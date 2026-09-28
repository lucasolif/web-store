package br.edu.utfpr.apiproduto.product.infrastructure.storage;

import br.edu.utfpr.apiproduto.shared.exception.ResourceNotFoundException;
import br.edu.utfpr.apiproduto.shared.exception.StorageException;
import br.edu.utfpr.apiproduto.shared.exception.ValidationException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ProductImageStorage {

    private static final Logger LOG = LoggerFactory.getLogger(ProductImageStorage.class);
    private static final Pattern STORED_FILE_NAME = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png|webp)$"
    );
    private static final Set<String> JPEG_TYPES = Set.of("image/jpeg", "image/jpg");
    private static final Map<String, String> EXTENSION_BY_TYPE = Map.of(
            "image/jpeg", "jpg",
            "image/jpg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final ProductImageProperties properties;
    private Path storageDirectory;

    public ProductImageStorage(ProductImageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void initialize() {
        if (properties.getMaxSize().toBytes() <= 0) {
            throw new StorageException("O tamanho máximo configurado para imagens deve ser maior que zero.", new IllegalArgumentException("app.product-images.max-size"));
        }
        storageDirectory = properties.getDirectory().toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageDirectory);
        } catch (IOException exception) {
            throw new StorageException("Não foi possível inicializar o armazenamento de imagens.", exception);
        }
    }

    public String store(MultipartFile image) {
        validate(image);

        String mediaType = normalizeContentType(image.getContentType());
        String extension = EXTENSION_BY_TYPE.get(mediaType);
        String storedFileName = UUID.randomUUID() + "." + extension;
        Path temporaryFile = null;

        try {
            temporaryFile = Files.createTempFile(storageDirectory, ".upload-", ".tmp");
            try (InputStream input = image.getInputStream()) {
                Files.copy(input, temporaryFile, StandardCopyOption.REPLACE_EXISTING);
            }

            Path destination = resolveStoredFile(storedFileName);
            moveToDestination(temporaryFile, destination);
            return normalizedPublicPath() + "/" + storedFileName;
        } catch (IOException exception) {
            deleteQuietly(temporaryFile);
            throw new StorageException("Não foi possível armazenar a imagem do produto.", exception);
        }
    }

    public Resource load(String storedFileName) {
        validateStoredFileName(storedFileName);
        Path file = resolveStoredFile(storedFileName);

        try {
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw imageNotFound();
            }
            return resource;
        } catch (MalformedURLException exception) {
            throw imageNotFound();
        }
    }

    public void deleteOnTransactionRollback(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
            if (status != STATUS_COMMITTED) {
                deleteByImagePathQuietly(imagePath);
            }
            }
        });
    }

    public void deleteAfterTransactionCommit(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            deleteByImagePathQuietly(imagePath);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteByImagePathQuietly(imagePath);
            }
        });
    }

    private void validate(MultipartFile image) {
        if (image.isEmpty()) {
            throw new ValidationException("INVALID_PRODUCT_IMAGE", "A imagem do produto está vazia.");
        }
        if (image.getSize() > properties.getMaxSize().toBytes()) {
            throw new ValidationException(
                    "PRODUCT_IMAGE_TOO_LARGE",
                    "A imagem do produto deve ter no máximo " + properties.getMaxSize().toBytes() + " bytes."
            );
        }

        String mediaType = normalizeContentType(image.getContentType());
        if (!EXTENSION_BY_TYPE.containsKey(mediaType)) {
            throw unsupportedImageType();
        }

        try (InputStream input = image.getInputStream()) {
            byte[] header = input.readNBytes(12);
            if (!matchesSignature(mediaType, header)) {
                throw new ValidationException(
                        "INVALID_PRODUCT_IMAGE_CONTENT",
                        "O conteúdo do arquivo não corresponde a uma imagem válida."
                );
            }
        } catch (IOException exception) {
            throw new StorageException("Não foi possível ler a imagem do produto.", exception);
        }
    }

    private void moveToDestination(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, destination);
        }
    }

    private boolean matchesSignature(String mediaType, byte[] header) {
        if (JPEG_TYPES.contains(mediaType)) {
            return header.length >= 3
                    && unsigned(header[0]) == 0xFF
                    && unsigned(header[1]) == 0xD8
                    && unsigned(header[2]) == 0xFF;
        }
        if ("image/png".equals(mediaType)) {
            int[] signature = {0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            if (header.length < signature.length) {
                return false;
            }
            for (int index = 0; index < signature.length; index++) {
                if (unsigned(header[index]) != signature[index]) {
                    return false;
                }
            }
            return true;
        }
        return "image/webp".equals(mediaType)
                && header.length >= 12
                && asciiEquals(header, 0, "RIFF")
                && asciiEquals(header, 8, "WEBP");
    }

    private boolean asciiEquals(byte[] bytes, int offset, String value) {
        for (int index = 0; index < value.length(); index++) {
            if (bytes[offset + index] != (byte) value.charAt(index)) {
                return false;
            }
        }
        return true;
    }

    private int unsigned(byte value) {
        return value & 0xFF;
    }

    private String normalizeContentType(String contentType) {
        return contentType == null ? "" : contentType.toLowerCase(Locale.ROOT).trim();
    }

    private ValidationException unsupportedImageType() {
        return new ValidationException(
                "UNSUPPORTED_PRODUCT_IMAGE_TYPE",
                "A imagem deve estar no formato JPEG, PNG ou WebP."
        );
    }

    private void validateStoredFileName(String storedFileName) {
        if (storedFileName == null || !STORED_FILE_NAME.matcher(storedFileName).matches()) {
            throw imageNotFound();
        }
    }

    private Path resolveImagePath(String imagePath) {
        int separator = imagePath.lastIndexOf('/');
        String storedFileName = separator >= 0 ? imagePath.substring(separator + 1) : imagePath;
        validateStoredFileName(storedFileName);
        return resolveStoredFile(storedFileName);
    }

    private Path resolveStoredFile(String storedFileName) {
        Path resolved = storageDirectory.resolve(storedFileName).normalize();
        if (!resolved.getParent().equals(storageDirectory)) {
            throw imageNotFound();
        }
        return resolved;
    }

    private String normalizedPublicPath() {
        String value = properties.getPublicPath().trim();
        if (!value.startsWith("/")) {
            value = "/" + value;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private ResourceNotFoundException imageNotFound() {
        return new ResourceNotFoundException("PRODUCT_IMAGE_NOT_FOUND", "Imagem do produto não encontrada.");
    }

    private void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            LOG.warn("Não foi possível excluir o arquivo de imagem {}.", path.getFileName(), exception);
        }
    }

    private void deleteByImagePathQuietly(String imagePath) {
        try {
            deleteQuietly(resolveImagePath(imagePath));
        } catch (RuntimeException exception) {
            LOG.warn("O caminho persistido da imagem é inválido e não pôde ser removido.");
        }
    }
}
