package org.example;

public class InvalidMedicineException extends IllegalArgumentException {
    public InvalidMedicineException(String message) {
        super(message);
    }
}