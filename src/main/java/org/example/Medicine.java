package org.example;

import java.util.Objects;

/**
 * A medicine, identified by its name (a node in the interaction graph).
 * Names are stored in lower case so "Aspirin" and "aspirin" are the same medicine.
 * The class is immutable: the name never changes after creation.
 */
public class Medicine {
    private final String name;

    /** @throws InvalidMedicineException if the name is null or blank */
    public Medicine(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidMedicineException("Medicine name cannot be empty");
        }
        this.name = name.trim().toLowerCase();
    }

    public String getName() {
        return name;
    }

    /** Two medicines are equal when their names are equal (needed for contains() and HashMap). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicine)) return false;
        return name.equals(((Medicine) o).name);
    }

    /** Must be overridden together with equals() so hash-based collections work. */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}