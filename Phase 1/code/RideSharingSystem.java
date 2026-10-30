import java.io.*;

public class RideSharingSystem implements IRideSharingSystem {
    private final RiderList riderList;
    private final DriverList driverList;
    private final RideList rideList;
    private long nextRideId = 0;

    
    public RideSharingSystem(){
        riderList = new RiderList();
        driverList = new DriverList();
        rideList = new RideList();
        
    }
    // Loads riders from a CSV file.
	// Returns true if loading succeeds; false otherwise.
	public boolean loadRidersFromCSV(String ridersFilePath){

        if (ridersFilePath == null) return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(ridersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] info = columns(line, 5);
                if (!addRider(new Rider(Integer.parseInt(info[0]), info[1], info[3], info[2], info[4])))
                    return false;
            }
            return true;
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    // A failed import retains earlier valid rows; the failing row is not added.
    public boolean loadDriversFromCSV(String driversFilePath) {
        if (driversFilePath == null) return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(driversFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] info = columns(line, 5);
                if (!addDriver(new Driver(Integer.parseInt(info[0]), info[1], info[2], info[3], VehicleType.valueOf(info[4]))))
                    return false;
            }
            return true;
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    // Imported rides go through exactly the same checks as menu scheduling.
    public boolean loadRidesFromCSV(String ridesFilePath) {
        if (ridesFilePath == null) return false;
        try (BufferedReader reader = new BufferedReader(new FileReader(ridesFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] info = columns(line, 7);
                DateTime pickup = new DateTime(info[2]);
                DateTime dropoff = new DateTime(info[3]);
                int driverId = Integer.parseInt(info[5]);
                String[] ids = info[6].split(";", -1);
                int[] riderIds = new int[ids.length];
                for (int i = 0; i < ids.length; i++)
                    riderIds[i] = Integer.parseInt(ids[i].trim());
                boolean added;
                if (info[0].equals("PRIVATE") && riderIds.length == 1)
                    added = schedulePrivateRide(info[1], pickup, dropoff, info[4], riderIds[0], driverId);
                else if (info[0].equals("SHARED"))
                    added = scheduleSharedRide(info[1], pickup, dropoff, info[4], riderIds, driverId);
                else
                    return false;
                if (!added) return false;
            }
            return true;
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    private static String[] columns(String line, int expected) {
        String[] values = line.split(",", -1);
        if (values.length != expected)
            throw new IllegalArgumentException("Incorrect CSV column count");
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();
            if (values[i].isEmpty())
                throw new IllegalArgumentException("CSV fields cannot be empty");
        }
        return values;
    }

    //Adds a rider to the system after enforcing uniqueness by ID. Returns true if added; false if duplicate ID or invalid
     public boolean addRider(IRider rider){
        return riderList.add(rider);
    }

    /**
     * Adds a driver to the system after enforcing uniqueness by ID and by
     * vehicle plate number (no two drivers may share the same plate).
     * Returns true if added; false if duplicate ID, duplicate plate, or invalid
     */
     public boolean addDriver(IDriver driver){
        return driverList.add(driver);
    }

    //Searches for a rider by ID.
    public IRider searchRiderById(int riderId){
        return riderList.findById(riderId);
    }

    //Searches for a rider by email.
    public IRider searchRiderByEmail(String email){
        return riderList.findByEmail(email);
    }

    //Searches for all riders whose full name exactly matches the given name.
    public LinkedList<IRider> searchRidersByName(String fullName){
        return riderList.findByName(fullName);
    }

    //Searches for all riders from the specified home city.
    public LinkedList<IRider> searchRidersByHomeCity(String homeCity){
        return riderList.findByHomeCity(homeCity);
    }

    //Returns all riders stored in the system.
    public LinkedList<IRider> getAllRiders(){
        return riderList.getAllAlphabetically();
    }

    //Searches for a driver by ID.
    public IDriver searchDriverById(int driverId){
        return driverList.findById(driverId);
    }

    //Searches for a driver by vehicle plate.
    public IDriver searchDriverByVehiclePlate(String vehiclePlate){
        return driverList.findByVehiclePlate(vehiclePlate);
    }

    //Searches for all drivers with the specified vehicle type.
    public LinkedList<IDriver> searchDriversByVehicleType(VehicleType vehicleType){
        return driverList.findByVehicleType(vehicleType);
    }

    //Returns all drivers stored in the system.
    public LinkedList<IDriver> getAllDrivers(){
        return driverList.getAllAlphabetically();
    }

    /**
     * Removes a rider and performs cascade deletion of associated rides:
     * - All private rides involving the rider are deleted.
     * - The rider is removed from any shared rides.
     * - Shared rides with no remaining participants are deleted.
     * It returns true if the rider was found and removed; false otherwise
     */
    public boolean removeRider(int riderId){
        IRider rider = riderList.findById(riderId);
        if (rider == null) return false;
        LinkedList<IRide> rides = rideList.getAllAlphabetically();
        if (!rides.empty()) {
            rides.findFirst();
            while (true) {
                IRide ride = rides.retrieve();
                if (ride.hasRider(riderId)) {
                    if (ride instanceof IPrivateRide) {
                        deleteRide(ride);
                    } else {
                        ISharedRide shared = (ISharedRide) ride;
                        shared.removeParticipantById(riderId);
                        removeHistory(rider, ride.getRideId());
                        if (shared.isEmpty()) deleteRide(ride);
                    }
                }
                if (rides.last()) break;
                rides.findNext();
            }
        }
        return riderList.removeById(riderId);
    }

    /**
     * Removes a driver and performs cascade deletion of their assigned rides:
     * - All private rides assigned to the driver are deleted.
     * - All shared rides assigned to the driver are deleted.
     * It returns true if the driver was found and removed; false otherwise
     */
    public boolean removeDriver(int driverId){
        if (driverList.findById(driverId) == null) return false;
        LinkedList<IRide> rides = rideList.getAllAlphabetically();
        if (!rides.empty()) {
            rides.findFirst();
            while (true) {
                IRide ride = rides.retrieve();
                if (ride.getDriver().getId() == driverId) deleteRide(ride);
                if (rides.last()) break;
                rides.findNext();
            }
        }
        return driverList.removeById(driverId);
    }

    /**
     * Schedules a private ride for one rider with one driver.
     * Requirements:
     * - The rider must exist.
     * - The driver must exist.
     * - The ride must not conflict with the rider's current schedule.
     * - The ride must not conflict with the driver's current schedule.
     * It returns true if scheduled; false otherwise
     */
    public boolean schedulePrivateRide(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime, String dropoffLocation, int riderId, int driverId){
        IRider rider = riderList.findById(riderId);
        IDriver driver = driverList.findById(driverId);
        if (rider == null || driver == null || nextRideId > Integer.MAX_VALUE
                || !validRideDetails(pickupLocation, pickupTime, dropoffTime, dropoffLocation)
                || conflicts(pickupTime, dropoffTime, new int[] { riderId }, driverId))
            return false;
        try {
            PrivateRide ride = new PrivateRide((int) nextRideId, pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver, rider);
            if (!rideList.addRide(ride)) return false;
            nextRideId++;
            addHistory(rider, ride);
            addHistory(driver, ride);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Schedules a shared ride for multiple riders with one driver.
     * Requirements:
     * - All riders must exist.
     * - The driver must exist.
     * - The ride must not conflict with any listed rider's schedule.
     * - The ride must not conflict with the driver's current schedule.
     * It returns true if scheduled; false otherwise
     */
    public boolean scheduleSharedRide(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime, String dropoffLocation, int[] riderIds, int driverId){
        IDriver driver = driverList.findById(driverId);
        if (driver == null || riderIds == null || riderIds.length < 2 || nextRideId > Integer.MAX_VALUE
                || !validRideDetails(pickupLocation, pickupTime, dropoffTime, dropoffLocation))
            return false;
        LinkedList<IRider> participants = new LinkedList<IRider>();
        for (int i = 0; i < riderIds.length; i++) {
            IRider rider = riderList.findById(riderIds[i]);
            if (rider == null) return false;
            for (int j = 0; j < i; j++)
                if (riderIds[j] == riderIds[i]) return false;
            participants.insert(rider);
        }
        if (conflicts(pickupTime, dropoffTime, riderIds, driverId)) return false;
        try {
            SharedRide ride = new SharedRide((int) nextRideId, pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
            participants.findFirst();
            while (true) {
                ride.addParticipant(participants.retrieve());
                if (participants.last()) break;
                participants.findNext();
            }
            if (!rideList.addRide(ride)) return false;
            nextRideId++;
            participants.findFirst();
            while (true) {
                addHistory(participants.retrieve(), ride);
                if (participants.last()) break;
                participants.findNext();
            }
            addHistory(driver, ride);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //Searches for all rides whose pickup location matches the given location (may return multiple if pickup locations repeat).
    public LinkedList<IRide> searchRidesByPickupLocation(String pickupLocation){
        return rideList.findByPickupLocation(pickupLocation);
    }

    //Searches for all rides that involve a rider with the given name.
    public LinkedList<IRide> searchRidesByRiderName(String riderName){
        return rideList.findByRiderName(riderName);
    }

    //Returns all riders registered on a shared ride identified by its pickup location.
    public LinkedList<IRider> getSharedRideParticipants(String pickupLocation){
        LinkedList<IRider> participants = new LinkedList<IRider>();
        LinkedList<IRide> rides = rideList.findByPickupLocation(pickupLocation);
        if (!rides.empty()) {
            rides.findFirst();
            while (true) {
                if (rides.retrieve() instanceof ISharedRide) {
                    LinkedList<IRider> riders = ((ISharedRide) rides.retrieve()).getParticipants();
                    if (!riders.empty()) {
                        riders.findFirst();
                        while (true) {
                            // Repeated pickup locations return each participant once.
                            addParticipantOnce(participants, riders.retrieve());
                            if (riders.last()) break;
                            riders.findNext();
                        }
                    }
                }
                if (rides.last()) break;
                rides.findNext();
            }
        }
        return participants;
    }

    //Returns all rides (private and shared) alphabetically ordered by pickup location.
    public LinkedList<IRide> getAllRidesAlphabetically(){
        return rideList.getAllAlphabetically();
    }

    private boolean validRideDetails(String pickup, IDateTime start, IDateTime end, String dropoff) {
        return pickup != null && !pickup.trim().isEmpty() && dropoff != null && !dropoff.trim().isEmpty()
                && start != null && end != null && start.compareTo(end) < 0;
    }

    private static void addParticipantOnce(LinkedList<IRider> participants, IRider rider) {
        if (!participants.empty()) {
            participants.findFirst();
            while (true) {
                if (participants.retrieve().getId() == rider.getId()) return;
                if (participants.last()) break;
                participants.findNext();
            }
        }
        participants.insert(rider);
    }

    private boolean conflicts(IDateTime start, IDateTime end, int[] riderIds, int driverId) {
        LinkedList<IRide> rides = rideList.getAllAlphabetically();
        if (rides.empty()) return false;
        rides.findFirst();
        while (true) {
            IRide ride = rides.retrieve();
            // Half-open intervals allow one ride to start at another's drop-off.
            if (start.compareTo(ride.getDropoffTime()) < 0 && ride.getPickupTime().compareTo(end) < 0) {
                if (ride.getDriver().getId() == driverId) return true;
                for (int riderId : riderIds)
                    if (ride.hasRider(riderId)) return true;
            }
            if (rides.last()) break;
            rides.findNext();
        }
        return false;
    }

    private static void addHistory(IPerson person, IRide ride) {
        LinkedList<IRide> history = person.getRideHistory();
        if (!history.empty()) {
            history.findFirst();
            while (!history.last()) history.findNext();
        }
        history.insert(ride);
    }

    private static void removeHistory(IPerson person, int rideId) {
        LinkedList<IRide> history = person.getRideHistory();
        if (history.empty()) return;
        history.findFirst();
        while (true) {
            if (history.retrieve().getRideId() == rideId) {
                history.remove();
                return;
            }
            if (history.last()) break;
            history.findNext();
        }
    }

    private void deleteRide(IRide ride) {
        rideList.removeRideById(ride.getRideId());
        removeHistory(ride.getDriver(), ride.getRideId());
        if (ride instanceof IPrivateRide) {
            removeHistory(((IPrivateRide) ride).getRider(), ride.getRideId());
        } else {
            LinkedList<IRider> participants = ((ISharedRide) ride).getParticipants();
            if (!participants.empty()) {
                participants.findFirst();
                while (true) {
                    removeHistory(participants.retrieve(), ride.getRideId());
                    if (participants.last()) break;
                    participants.findNext();
                }
            }
        }
    }
}
