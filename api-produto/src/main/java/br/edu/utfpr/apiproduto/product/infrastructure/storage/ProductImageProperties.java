package br.edu.utfpr.apiproduto.product.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.util.unit.DataSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app.product-images")
@Validated
public class ProductImageProperties {

    @NotNull
    private Path directory = Path.of("uploads", "products");

    @NotNull
    private DataSize maxSize = DataSize.ofMegabytes(5);

    @NotBlank
    private String publicPath = "/api/product-images";

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }

    public DataSize getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(DataSize maxSize) {
        this.maxSize = maxSize;
    }

    public String getPublicPath() {
        return publicPath;
    }

    public void setPublicPath(String publicPath) {
        this.publicPath = publicPath;
    }
}
