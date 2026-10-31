import java.util.*;

public class Main {
    private static final RideSharingSystem system = new RideSharingSystem();
    private static final Scanner input = new Scanner(System.in);

    private static void printMenu() {
        System.out.println("\n1. List all riders.");
        System.out.println("2. Search for riders by home city.");
        System.out.println("3. Search for riders by name.");
        System.out.println("4. Add a rider.");
        System.out.println("5. List all drivers.");
        System.out.println("6. Search for drivers by vehicle type.");
        System.out.println("7. Add a driver.");
        System.out.println("8. List all rides alphabetically by pickup location.");
        System.out.println("9. Search for rides by pickup location.");
        System.out.println("10. Search for rides by rider name.");
        System.out.println("11. List the participants of a shared ride by pickup location.");
        System.out.println("12. Add a ride (ask the user whether it is private or shared, then prompt accordingly).");
        System.out.println("0. Exit the program.");
    }

    private static String read(String prompt) {
        System.out.print(prompt);
        String s = input.nextLine().trim();
            return s;
    }

    private static String required(String prompt) {
        String value = read(prompt);
        if (value == "")
            throw new IllegalArgumentException("A value is required");
        return value;
    }

    private static int number(String prompt) {
        return Integer.parseInt(required(prompt));
    }

    private static VehicleType vehicleType() {
        return VehicleType.valueOf(required("Vehicle type (SEDAN, LUXURY_SEDAN, SUV, VAN): ").toUpperCase());
    }

    private static void addRider() {
        int id = number("Rider ID: ");
        String name = required("Full name: ");
        String phone = required("Phone number (10 digits): ");
        String email = required("Email: ");
        String city = required("Home city: ");
        printResult(system.addRider(new Rider(id, name, phone, email, city)));
    }

    private static void addDriver() {
        int id = number("Driver ID: ");
        String name = required("Full name: ");
        String phone = required("Phone number (10 digits): ");
        String plate = required("Vehicle plate (ABC1234): ");
        VehicleType type = vehicleType();
        printResult(system.addDriver(new Driver(id, name, phone, plate, type)));
    }

    private static void addRide() {
        String type = required("Ride type (PRIVATE or SHARED): ").toUpperCase();
        if (!type.equals("PRIVATE") && !type.equals("SHARED"))
            throw new IllegalArgumentException("Ride type must be PRIVATE or SHARED");
        String pickup = required("Pickup location: ");
        DateTime start = new DateTime(required("Pickup date/time (MM/DD/YYYY HH:MM): "));
        DateTime end = new DateTime(required("Drop-off date/time (MM/DD/YYYY HH:MM): "));
        String dropoff = required("Drop-off location: ");
        if (type.equals("PRIVATE")) {
            int riderId = number("Rider ID: ");
            int driverId = number("Driver ID: ");
            printResult(system.schedulePrivateRide(pickup, start, end, dropoff, riderId, driverId));
        } else {
            String[] ids = required("Rider IDs (at least two, separated by semicolons): ").split(";");
            int[] riderIds = new int[ids.length];
            for (int i = 0; i < ids.length; i++)
                riderIds[i] = Integer.parseInt(ids[i].trim());
            int driverId = number("Driver ID: ");
            printResult(system.scheduleSharedRide(pickup, start, end, dropoff, riderIds, driverId));
        }
    }

    private static void printResult(boolean success) {
        if (success)
            System.out.println("Operation succeeded.");
        else
            System.out.println("Operation failed (invalid, duplicate, missing, or conflicting data).");
    }

    private static <T> void printList(LinkedList<T> list) {
        if (list.empty()) {
            System.out.println("No results.");
            return;
        }
        list.findFirst();
        while (true) {
            System.out.println(list.retrieve().toString());
            if (list.last())
                break;
            list.findNext();
        }
    }
    public static void main(String[] args) {
        System.out.println("Welcome to KSU Ride Sharing System: KSUBER");
        System.out.println("Loading data from csv files...");
        if (!system.loadRidersFromCSV("Phase 1/riders_100.csv")) {
            System.out.println("Failed to load riders_100.csv.");
            return;
        }
        if (!system.loadDriversFromCSV("Phase 1/drivers_30.csv")) {
            System.out.println("Failed to load drivers_30.csv.");
            return;
        }
        if (!system.loadRidesFromCSV("Phase 1/rides_40.csv")) {
            System.out.println("Failed to load rides_40.csv.");
            return;
        }
        System.out.println("Riders, drivers, and rides loaded successfully.");
        try {
            while (true) {
                printMenu();
                String choice = read("Choose an option: ");
                if (choice.equals("0")){
                    System.out.println("Thanks for using KSUBER, goodbye.");
                    return;
                }
                try {
                    switch (Integer.parseInt(choice)) {
                        case 1:
                            printList(system.getAllRiders());
                            break;
                        case 2:
                            printList(system.searchRidersByHomeCity(required("Home city: ")));
                            break;
                        case 3:
                            printList(system.searchRidersByName(required("Full name: ")));
                            break;
                        case 4:
                            addRider();
                            break;
                        case 5:
                            printList(system.getAllDrivers());
                            break;
                        case 6:
                            printList(system.searchDriversByVehicleType(vehicleType()));
                            break;
                        case 7:
                            addDriver();
                            break;
                        case 8:
                            printList(system.getAllRidesAlphabetically());
                            break;
                        case 9:
                            printList(system.searchRidesByPickupLocation(required("Pickup location: ")));
                            break;
                        case 10:
                            printList(system.searchRidesByRiderName(required("Rider full name: ")));
                            break;
                        case 11:
                            printList(system.getSharedRideParticipants(required("Pickup location: ")));
                            break;
                        case 12:
                            addRide();
                            break;
                        default:
                            System.out.println("Choose an option from 0 to 12.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Operation failed: " + e.getMessage());
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Input is empty.");
        }
    }

    
}
