package org.example;

import java.sql.*;

/**
 * Database class: all JDBC (SQLite) operations for the interactions table.
 * It implements InteractionRepository, so the rest of the app depends on the
 * interface and not on SQLite directly.
 * Public methods are synchronized because the UI thread and the SwingWorker
 * background thread share this one database file.
 */
public class Database implements InteractionRepository {
    private static final String URL = "jdbc:sqlite:medicheck.db";   // file is created automatically

    public Database() {
        createTable();
        seedIfEmpty();
    }

    /** Opens a new JDBC connection to the SQLite file. */
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /** Creates the interactions table on the first run. */
    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS interactions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "drug_a TEXT NOT NULL, drug_b TEXT NOT NULL, "
                + "severity TEXT NOT NULL, description TEXT NOT NULL)";
        // try-with-resources closes the Connection and Statement automatically
        try (Connection c = connect(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Could not create table", e);
        }
    }

    /** Inserts one interaction. PreparedStatement (?) prevents SQL injection. */
    @Override
    public synchronized void addInteraction(Interaction i) {
        String sql = "INSERT INTO interactions (drug_a, drug_b, severity, description) VALUES (?,?,?,?)";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, i.getDrugA().getName());
            p.setString(2, i.getDrugB().getName());
            p.setString(3, i.getSeverity().name());
            p.setString(4, i.getDescription());
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not add interaction", e);
        }
    }

    /** Finds the interaction for a pair, in either order (A,B) or (B,A); null if unknown. */
    @Override
    public synchronized Interaction findInteraction(Medicine a, Medicine b) {
        String sql = "SELECT * FROM interactions WHERE (drug_a=? AND drug_b=?) OR (drug_a=? AND drug_b=?)";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, a.getName());
            p.setString(2, b.getName());
            p.setString(3, b.getName());
            p.setString(4, a.getName());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return new Interaction(
                        new Medicine(rs.getString("drug_a")),
                        new Medicine(rs.getString("drug_b")),
                        Severity.valueOf(rs.getString("severity")),
                        rs.getString("description"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Could not search interactions", e);
        }
    }

    /** True if the medicine appears in at least one stored interaction (used to catch typos). */
    @Override
    public synchronized boolean isKnown(Medicine m) {
        String sql = "SELECT 1 FROM interactions WHERE drug_a=? OR drug_b=? LIMIT 1";
        try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, m.getName());
            p.setString(2, m.getName());
            return p.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Could not check medicine", e);
        }
    }

    /** Fills the table with starter data, but only if the table is empty. */
    private void seedIfEmpty() {
        try (Connection c = connect(); Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM interactions")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Dangerous
        add("aspirin", "warfarin", Severity.DANGEROUS, "Increased bleeding risk. Consult a doctor.");
        add("warfarin", "ibuprofen", Severity.DANGEROUS, "Higher risk of serious bleeding.");
        add("warfarin", "naproxen", Severity.DANGEROUS, "Higher risk of serious bleeding.");
        add("warfarin", "diclofenac", Severity.DANGEROUS, "Higher risk of serious bleeding.");
        add("warfarin", "fluconazole", Severity.DANGEROUS, "Raises warfarin effect; bleeding risk.");
        add("warfarin", "metronidazole", Severity.DANGEROUS, "Raises warfarin effect; bleeding risk.");
        add("atorvastatin", "clarithromycin", Severity.DANGEROUS, "Raises statin levels; risk of muscle damage.");
        add("simvastatin", "clarithromycin", Severity.DANGEROUS, "Raises statin levels; risk of muscle damage.");
        add("sildenafil", "nitroglycerin", Severity.DANGEROUS, "Can cause a severe drop in blood pressure.");
        add("sildenafil", "isosorbide", Severity.DANGEROUS, "Can cause a severe drop in blood pressure.");
        add("tramadol", "sertraline", Severity.DANGEROUS, "Risk of serotonin syndrome and seizures.");
        add("tramadol", "fluoxetine", Severity.DANGEROUS, "Risk of serotonin syndrome and seizures.");
        add("linezolid", "sertraline", Severity.DANGEROUS, "Risk of serotonin syndrome.");
        add("diazepam", "tramadol", Severity.DANGEROUS, "Risk of slowed breathing and heavy sedation.");
        add("diazepam", "codeine", Severity.DANGEROUS, "Risk of slowed breathing and heavy sedation.");
        add("digoxin", "amiodarone", Severity.DANGEROUS, "Can raise digoxin to toxic levels.");
        add("methotrexate", "trimethoprim", Severity.DANGEROUS, "Severe drop in blood cell counts possible.");
        add("allopurinol", "azathioprine", Severity.DANGEROUS, "Azathioprine toxicity; dose must be reduced by a doctor.");

        // Caution
        add("aspirin", "ibuprofen", Severity.CAUTION, "Ibuprofen may reduce aspirin's heart protection; stomach bleeding risk.");
        add("aspirin", "clopidogrel", Severity.CAUTION, "Higher bleeding risk.");
        add("clopidogrel", "omeprazole", Severity.CAUTION, "Omeprazole may reduce clopidogrel's effect.");
        add("warfarin", "paracetamol", Severity.CAUTION, "Regular high doses of paracetamol can raise bleeding risk.");
        add("warfarin", "ciprofloxacin", Severity.CAUTION, "May raise warfarin effect; monitor INR.");
        add("simvastatin", "amlodipine", Severity.CAUTION, "Raises simvastatin levels; dose limit applies.");
        add("lisinopril", "spironolactone", Severity.CAUTION, "Can raise potassium to unsafe levels.");
        add("lisinopril", "ibuprofen", Severity.CAUTION, "Can weaken blood pressure control and strain the kidneys.");
        add("lisinopril", "potassium", Severity.CAUTION, "Can raise potassium to unsafe levels.");
        add("losartan", "spironolactone", Severity.CAUTION, "Can raise potassium to unsafe levels.");
        add("sertraline", "ibuprofen", Severity.CAUTION, "Higher risk of stomach bleeding.");
        add("sertraline", "aspirin", Severity.CAUTION, "Higher risk of bleeding.");
        add("digoxin", "furosemide", Severity.CAUTION, "Low potassium can make digoxin toxic.");
        add("methotrexate", "ibuprofen", Severity.CAUTION, "May raise methotrexate levels.");
        add("levothyroxine", "calcium", Severity.CAUTION, "Calcium reduces absorption; take 4 hours apart.");
        add("ciprofloxacin", "antacid", Severity.CAUTION, "Antacids reduce absorption; take hours apart.");
    }

    /** Small helper that keeps the seed list short and readable. */
    private void add(String a, String b, Severity sev, String desc) {
        addInteraction(new Interaction(new Medicine(a), new Medicine(b), sev, desc));
    }
}