package org.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Food and drink notes for common medicines.
 * A HashMap gives fast lookup by medicine name (key -> warning text).
 */
public class FoodWarnings {
    private static final Map<String, String> WARNINGS = new HashMap<>();

    // Static block: runs once when the class is first loaded and fills the map.
    static {
        WARNINGS.put("atorvastatin", "Avoid grapefruit juice; it raises the drug level in the blood.");
        WARNINGS.put("simvastatin", "Avoid grapefruit juice; it raises the drug level in the blood.");
        WARNINGS.put("amlodipine", "Large amounts of grapefruit juice may raise the drug level.");
        WARNINGS.put("warfarin", "Keep leafy greens (vitamin K) steady from day to day; big changes alter its effect.");
        WARNINGS.put("levothyroxine", "Take on an empty stomach; coffee and soy can reduce absorption.");
        WARNINGS.put("ciprofloxacin", "Dairy and calcium-fortified juice reduce absorption; take hours apart.");
        WARNINGS.put("metronidazole", "Avoid alcohol during treatment and for a few days after.");
        WARNINGS.put("tramadol", "Avoid alcohol; it increases drowsiness and breathing risk.");
        WARNINGS.put("diazepam", "Avoid alcohol; it increases drowsiness and breathing risk.");
        WARNINGS.put("sertraline", "Limit alcohol; it can worsen drowsiness and mood effects.");
        WARNINGS.put("lisinopril", "Avoid potassium-based salt substitutes.");
        WARNINGS.put("metformin", "Limit alcohol; heavy drinking raises the risk of lactic acidosis.");
    }

    /** @return the warning for this medicine, or null if there is none */
    public static String get(Medicine m) {
        return WARNINGS.get(m.getName());
    }
}