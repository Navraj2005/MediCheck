package org.example;

/** Contract for any place that stores drug interactions (database, memory, file...). */
public interface InteractionRepository {
    void addInteraction(Interaction interaction);
    Interaction findInteraction(Medicine a, Medicine b);
    boolean isKnown(Medicine m);
}