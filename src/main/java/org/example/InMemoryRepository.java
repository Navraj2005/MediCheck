package org.example;

import java.util.ArrayList;
import java.util.List;

/** Second implementation of InteractionRepository: keeps data in memory, no database. */
public class InMemoryRepository implements InteractionRepository {
    private final List<Interaction> store = new ArrayList<>();

    @Override
    public synchronized void addInteraction(Interaction interaction) {
        store.add(interaction);
    }

    @Override
    public synchronized Interaction findInteraction(Medicine a, Medicine b) {
        for (Interaction i : store) {
            boolean forward = i.getDrugA().equals(a) && i.getDrugB().equals(b);
            boolean backward = i.getDrugA().equals(b) && i.getDrugB().equals(a);
            if (forward || backward) return i;
        }
        return null;
    }

    @Override
    public synchronized boolean isKnown(Medicine m) {
        for (Interaction i : store) {
            if (i.getDrugA().equals(m) || i.getDrugB().equals(m)) return true;
        }
        return false;
    }
}