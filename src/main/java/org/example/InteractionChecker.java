package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Business logic of the app: checks every pair of medicines against a repository.
 * It depends on the InteractionRepository interface, so it works with any
 * implementation (polymorphism), e.g. Database or InMemoryRepository.
 */
public class InteractionChecker {
    private final InteractionRepository db;

    public InteractionChecker(InteractionRepository db) {
        this.db = db;
    }

    /** A medicine is "known" if it appears in an interaction or has a food warning. */
    public boolean isKnown(Medicine m) {
        return db.isKnown(m) || FoodWarnings.get(m) != null;
    }

    /**
     * Tests all n(n-1)/2 pairs (the inner loop starts at i+1, so each pair is
     * tested once). Returns the interactions found, most dangerous first.
     */
    public List<Interaction> check(List<Medicine> medicines) {
        List<Interaction> found = new ArrayList<>();
        for (int i = 0; i < medicines.size(); i++) {
            for (int j = i + 1; j < medicines.size(); j++) {
                Interaction result = db.findInteraction(medicines.get(i), medicines.get(j));
                if (result != null) {
                    found.add(result);
                }
            }
        }
        // Enums compare by declaration order, so reversed() puts DANGEROUS first.
        found.sort(Comparator.comparing(Interaction::getSeverity).reversed());
        return found;
    }
}