# CSC212-RideSharing-System

Phase 1 ride-sharing application using the course's custom linked list. Requires
Java 17 or later; no external dependencies or preview features are needed.

## Run

From the repository root:

```sh
mkdir -p build
javac -d build "Phase 1/code/"*.java
java -cp build Main
```

Run the application from the repository root. `Main` loads the three CSV files
from their fixed location in `Phase 1` before showing options 1-12 and 0.

Dates use `MM/DD/YYYY HH:MM`. Shared ride scheduling requires at least two
distinct rider IDs, separated by semicolons in the menu. Rides that meet exactly
at a pickup/drop-off boundary do not conflict.

## Behavior

- Rider and driver storage stays sorted by ID. A second custom name index gives
  alphabetical system listings in O(n), including after a name changes.
- Ride storage stays sorted by pickup location, including after a location changes.
  Returned lists are independent copies of their list nodes.
- CSV loaders use the supplied paths and stop with `false` at the first invalid,
  duplicate, missing-reference, or conflicting row. Earlier valid rows remain
  loaded; the failing row is not added. Blank lines are ignored. Files use the
  assignment's headerless, comma-separated format.
- Multiple shared rides may have the same pickup location. Listing participants
  at that location returns each rider once across all matching shared rides.
- Scheduling and cascade removal update both rider and driver histories. All
  console input/output belongs to `Main`; system searches return their results.

The assignment report will be prepared separately.
