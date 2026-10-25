# MediCheck - Drug Interaction Checker

A Java desktop app that checks a list of medicines for known drug interactions and rates each pair as Safe, Caution or Dangerous.

## Problem
People who take several medicines at once can easily miss a dangerous combination (for example aspirin with warfarin). Caregivers and pharmacy staff need a quick, free second check.

## Features
- Add medicines and check every pair for known interactions
- Results sorted by severity with colour-coded cards
- Food and drink warnings (for example grapefruit with statins)
- Interaction graph (medicines are nodes, interactions are edges)
- Print report for a doctor or pharmacist
- Large text mode for easier reading
- Warning when a medicine name is not in the database (typo protection)
- Save and load a medicine list, and add new interactions from inside the app

## Requirements
- Java 17 (JDK)
- Maven (bundled with IntelliJ IDEA)

## How to run
**Option A - IntelliJ IDEA:** open the project folder, wait for Maven to finish loading, then run `Main.java`.

**Option B - jar file:** run `mvn package`, then `java -jar target/Medicheck-1.0-SNAPSHOT.jar`.

The SQLite database file `medicheck.db` is created automatically on the first run, with 34 starter interactions.

## Project structure (src/main/java/org/example)
- `Medicine`, `Interaction`, `Severity` - data model
- `InteractionRepository` (interface), `Database`, `InMemoryRepository` - data access
- `InteractionChecker` - pairwise checking logic
- `FoodWarnings` - HashMap of food and drink notes
- `InvalidMedicineException` - custom exception
- `MainFrame`, `GraphPanel` - Swing user interface
- `Main` - entry point

## Java concepts used
- **OOP:** inheritance (`InvalidMedicineException`, `MainFrame`, `GraphPanel`), interface and polymorphism (`InteractionRepository` with two implementations), exception handling, encapsulation
- **Collections and generics:** `ArrayList<Medicine>`, `HashMap<String, String>`, `List<Interaction>`, `Comparator`
- **Multithreading:** `SwingWorker` runs the check off the UI thread; database methods are `synchronized`
- **Database:** SQLite through JDBC (`Connection`, `PreparedStatement`, `ResultSet`)

## Data and disclaimer
The interaction data is a small starter set based on general references (Drugs.com, MedlinePlus). Educational use only. Not medical advice.