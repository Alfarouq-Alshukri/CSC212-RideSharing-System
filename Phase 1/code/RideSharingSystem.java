import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RideSharingSystem implements IRideSharingSystem {
    private final RiderList riderList;
    private final DriverList driverList;
    private final RideList rideList;
    private long nextRideId = 0;

    public RideSharingSystem() {
        riderList = new RiderList();
        driverList = new DriverList();
        rideList = new RideList();
    }

    public boolean loadRidersFromCSV(String ridersFilePath) {
        if (ridersFilePath == null)
            return false;
        BufferedReader reader = null;
        boolean loaded = true;
        try {
            reader = new BufferedReader(new FileReader(ridersFilePath));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] info = columns(line, 5);
                if (!addRider(new Rider(Integer.parseInt(info[0]), info[1], info[3], info[2], info[4])))
                    return false;
            }
        } catch (IOException | IllegalArgumentException e) {
            loaded = false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    loaded = false;
                }
            }
        }
        return loaded;
    }

    public boolean loadDriversFromCSV(String driversFilePath) {
        if (driversFilePath == null)
            return false;
        BufferedReader reader = null;
        boolean loaded = true;
        try {
            reader = new BufferedReader(new FileReader(driversFilePath));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] info = columns(line, 5);
                if (!addDriver(new Driver(Integer.parseInt(info[0]), info[1], info[2], info[3], VehicleType.valueOf(info[4]))))
                    return false;
            }
        } catch (IOException | IllegalArgumentException e) {
            loaded = false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    loaded = false;
                }
            }
        }
        return loaded;
    }

    public boolean loadRidesFromCSV(String ridesFilePath) {
        if (ridesFilePath == null)
            return false;
        BufferedReader reader = null;
        boolean loaded = true;
        try {
            reader = new BufferedReader(new FileReader(ridesFilePath));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
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
                if (!added)
                    return false;
            }
        } catch (IOException | IllegalArgumentException e) {
            loaded = false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    loaded = false;
                }
            }
        }
        return loaded;
    }

    // Reject a bad row before adding it; earlier rows stay loaded.
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

    public boolean addRider(IRider rider) {
        return riderList.add(rider);
    }

    public boolean addDriver(IDriver driver) {
        return driverList.add(driver);
    }

    public IRider searchRiderById(int riderId) {
        return riderList.findById(riderId);
    }

    public IRider searchRiderByEmail(String email) {
        return riderList.findByEmail(email);
    }

    public LinkedList<IRider> searchRidersByName(String fullName) {
        return riderList.findByName(fullName);
    }

    public LinkedList<IRider> searchRidersByHomeCity(String homeCity) {
        return riderList.findByHomeCity(homeCity);
    }

    public LinkedList<IRider> getAllRiders() {
        return riderList.getAllAlphabetically();
    }

    public IDriver searchDriverById(int driverId) {
        return driverList.findById(driverId);
    }

    public IDriver searchDriverByVehiclePlate(String vehiclePlate) {
        return driverList.findByVehiclePlate(vehiclePlate);
    }

    public LinkedList<IDriver> searchDriversByVehicleType(VehicleType vehicleType) {
        return driverList.findByVehicleType(vehicleType);
    }

    public LinkedList<IDriver> getAllDrivers() {
        return driverList.getAllAlphabetically();
    }

    public boolean removeRider(int riderId) {
        IRider rider = riderList.findById(riderId);
        if (rider == null)
            return false;
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
                        if (shared.isEmpty())
                            deleteRide(ride);
                    }
                }
                if (rides.last())
                    break;
                rides.findNext();
            }
        }
        return riderList.removeById(riderId);
    }

    public boolean removeDriver(int driverId) {
        if (driverList.findById(driverId) == null)
            return false;
        LinkedList<IRide> rides = rideList.getAllAlphabetically();
        if (!rides.empty()) {
            rides.findFirst();
            while (true) {
                IRide ride = rides.retrieve();
                if (ride.getDriver().getId() == driverId)
                    deleteRide(ride);
                if (rides.last())
                    break;
                rides.findNext();
            }
        }
        return driverList.removeById(driverId);
    }

    public boolean schedulePrivateRide(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                                       String dropoffLocation, int riderId, int driverId) {
        IRider rider = riderList.findById(riderId);
        IDriver driver = driverList.findById(driverId);
        if (rider == null || driver == null || nextRideId > Integer.MAX_VALUE
                || !validRideDetails(pickupLocation, pickupTime, dropoffTime, dropoffLocation)
                || conflicts(pickupTime, dropoffTime, new int[] { riderId }, driverId))
            return false;
        try {
            PrivateRide ride = new PrivateRide((int) nextRideId, pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver, rider);
            if (!rideList.addRide(ride))
                return false;
            nextRideId++;
            addHistory(rider, ride);
            addHistory(driver, ride);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean scheduleSharedRide(String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                                      String dropoffLocation, int[] riderIds, int driverId) {
        IDriver driver = driverList.findById(driverId);
        if (driver == null || riderIds == null || riderIds.length < 2 || nextRideId > Integer.MAX_VALUE
                || !validRideDetails(pickupLocation, pickupTime, dropoffTime, dropoffLocation))
            return false;
        LinkedList<IRider> participants = new LinkedList<IRider>();
        for (int i = 0; i < riderIds.length; i++) {
            IRider rider = riderList.findById(riderIds[i]);
            if (rider == null)
                return false;
            for (int j = 0; j < i; j++)
                if (riderIds[j] == riderIds[i])
                    return false;
            participants.insert(rider);
        }
        if (conflicts(pickupTime, dropoffTime, riderIds, driverId))
            return false;
        try {
            SharedRide ride = new SharedRide((int) nextRideId, pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
            participants.findFirst();
            while (true) {
                ride.addParticipant(participants.retrieve());
                if (participants.last())
                    break;
                participants.findNext();
            }
            if (!rideList.addRide(ride))
                return false;
            nextRideId++;
            participants.findFirst();
            while (true) {
                addHistory(participants.retrieve(), ride);
                if (participants.last())
                    break;
                participants.findNext();
            }
            addHistory(driver, ride);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public LinkedList<IRide> searchRidesByPickupLocation(String pickupLocation) {
        return rideList.findByPickupLocation(pickupLocation);
    }

    public LinkedList<IRide> searchRidesByRiderName(String riderName) {
        return rideList.findByRiderName(riderName);
    }

    public LinkedList<IRider> getSharedRideParticipants(String pickupLocation) {
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
                            // Several rides may have the same pickup location.
                            addParticipantOnce(participants, riders.retrieve());
                            if (riders.last())
                                break;
                            riders.findNext();
                        }
                    }
                }
                if (rides.last())
                    break;
                rides.findNext();
            }
        }
        return participants;
    }

    public LinkedList<IRide> getAllRidesAlphabetically() {
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
                if (participants.retrieve().getId() == rider.getId())
                    return;
                if (participants.last())
                    break;
                participants.findNext();
            }
        }
        participants.insert(rider);
    }

    private boolean conflicts(IDateTime start, IDateTime end, int[] riderIds, int driverId) {
        LinkedList<IRide> rides = rideList.getAllAlphabetically();
        if (rides.empty())
            return false;
        rides.findFirst();
        while (true) {
            IRide ride = rides.retrieve();

            // A pickup exactly at another drop-off does not overlap.
            if (start.compareTo(ride.getDropoffTime()) < 0 && ride.getPickupTime().compareTo(end) < 0) {
                if (ride.getDriver().getId() == driverId)
                    return true;
                for (int i = 0; i < riderIds.length; i++)
                    if (ride.hasRider(riderIds[i]))
                        return true;
            }
            if (rides.last())
                break;
            rides.findNext();
        }
        return false;
    }

    private static void addHistory(IPerson person, IRide ride) {
        LinkedList<IRide> history = person.getRideHistory();
        if (!history.empty()) {
            history.findFirst();
            while (!history.last())
                history.findNext();
        }
        history.insert(ride);
    }

    private static void removeHistory(IPerson person, int rideId) {
        LinkedList<IRide> history = person.getRideHistory();
        if (history.empty())
            return;
        history.findFirst();
        while (true) {
            if (history.retrieve().getRideId() == rideId) {
                history.remove();
                return;
            }
            if (history.last())
                break;
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
                    if (participants.last())
                        break;
                    participants.findNext();
                }
            }
        }
    }
}
