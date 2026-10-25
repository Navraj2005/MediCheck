package org.example;

/**
 * A known interaction between two medicines (an edge in the interaction graph).
 * Immutable: all fields are final and set once in the constructor.
 */
public class Interaction {
    private final Medicine drugA;
    private final Medicine drugB;
    private final Severity severity;
    private final String description;

    public Interaction(Medicine drugA, Medicine drugB, Severity severity, String description) {
        this.drugA = drugA;
        this.drugB = drugB;
        this.severity = severity;
        this.description = description;
    }

    public Medicine getDrugA() { return drugA; }
    public Medicine getDrugB() { return drugB; }
    public Severity getSeverity() { return severity; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return drugA + " + " + drugB + " -> " + severity.getLabel() + ": " + description;
    }
}