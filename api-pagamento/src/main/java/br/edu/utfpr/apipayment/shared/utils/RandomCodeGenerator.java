package br.edu.utfpr.apipayment.shared.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomCodeGenerator {

    private static final char[] NUMERIC_ALPHABET = "0123456789".toCharArray();
    private static final char[] ALPHANUMERIC_ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private final SecureRandom secureRandom;

    public RandomCodeGenerator() {
        this(new SecureRandom());
    }

    RandomCodeGenerator(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public String numeric(int length) {
        return generate(length, NUMERIC_ALPHABET);
    }

    public String alphanumeric(int length) {
        return generate(length, ALPHANUMERIC_ALPHABET);
    }

    private String generate(int length, char[] alphabet) {
        if (length <= 0) {
            throw new IllegalArgumentException("O tamanho do código deve ser maior que zero.");
        }

        char[] code = new char[length];
        for (int index = 0; index < length; index++) {
            code[index] = alphabet[secureRandom.nextInt(alphabet.length)];
        }
        return new String(code);
    }
}
