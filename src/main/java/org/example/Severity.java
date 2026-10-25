package org.example;

/**
 * Risk level of a drug interaction.
 * An enum is used because the values are fixed, and the compiler catches typos.
 * The declaration order is also the order of risk (SAFE < CAUTION < DANGEROUS),
 * which InteractionChecker uses when sorting results.
 */
public enum Severity {
    SAFE("Safe"),
    CAUTION("Caution"),
    DANGEROUS("Dangerous");

    private final String label;   // text shown to the user

    Severity(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}