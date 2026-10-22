import java.io.*;

public class RideSharingSystem implements IRideSharingSystem {
    public RiderList riderList;
    public DriverList driverList;
    public RideList rideList;

    
    public RideSharingSystem(){
        riderList = new RiderList();
        driverList = new DriverList();
        rideList = new RideList();
        
    }
    // Loads riders from a CSV file.
	// Returns true if loading succeeds; false otherwise.
	public boolean loadRidersFromCSV(String ridersFilePath){
        
        try{
            File ridersFile = new File("riders_100.csv");
            FileReader FR = new FileReader(ridersFile);
            BufferedReader BFR = new BufferedReader(FR);
            String s = BFR.readLine();
            while(s != null){
                String[] info = s.split(",");
                if (info.length == 5){
                    Rider rider = new Rider(Integer.parseInt(info[0]), info[1], info[3], info[2], info[4]);
                    riderList.add(rider);
                }
                s = BFR.readLine();
            }
            BFR.close();
            return true;
        }
       catch (IOException e){
            System.out.println(e.getMessage());
            return false;
       }
      
    }

	// Loads drivers from a CSV file.
	// Returns true if loading succeeds; false otherwise.
	public boolean loadDriversFromCSV(String driversFilePath){
 
        try{
            File driversFile = new File("drivers_30.csv");
            FileReader FR = new FileReader(driversFile);
            BufferedReader BFR = new BufferedReader(FR);
            String s = BFR.readLine();
            while(s != null){
                String[] info = s.split(",");
                if (info.length == 5){
                    Driver driver = new Driver(Integer.parseInt(info[0]), info[1], info[2], info[3], VehicleType.valueOf(info[4]));
                    driverList.add(driver);
                }
                s = BFR.readLine();
            }
            BFR.close();
            return true;
        }
       catch (IOException e){
            System.out.println(e.getMessage());
            return false;
       }
          }

	// Loads rides from a CSV file. All referenced riders and the assigned driver must already exist. It must enforce conflict rules
	// Returns true if loading succeeds; false otherwise.
	public boolean loadRidesFromCSV(String ridesFilePath){
 
        try{
            int rideID = 0;
            File ridesFile = new File("rides_40.csv");
            FileReader FR = new FileReader(ridesFile);
            BufferedReader BFR = new BufferedReader(FR);
            String s = BFR.readLine();
            while(s != null){
                String[] info = s.split(",");
                if (info.length == 7){
                    if (driverList.findById(Integer.parseInt(info[5])) == null){
                        continue;
                    }
                    String[] riders = info[6].split(";");
                    for(int i=0; i < riders.length; i++){
                        if (riderList.findById(Integer.parseInt(riders[i])) == null){
                            continue;
                        }
                    }
                    // Enforce conflect rules

                    if(info[0].equals("PRIVATE")){
                        PrivateRide ride = new PrivateRide(rideID++,
                                            info[1], new DateTime(info[2]), new DateTime(info[3]), info[4],
                                            driverList.findById(Integer.parseInt(info[5])),
                                            riderList.findById(Integer.parseInt(info[6]))  );
                    rideList.addRide(ride);
                    }

                    else {
                        SharedRide ride = new SharedRide(rideID++,
                                            info[1], new DateTime(info[2]), new DateTime(info[3]), info[4],
                                            driverList.findById(Integer.parseInt(info[5]))     );
                        for (int i=0; i < riders.length; i++){
                            IRider rider = riderList.findById(Integer.parseInt(riders[i]));
                            ride.addParticipant(rider);
                        }
                        rideList.addRide(ride);
                    }                    
                }
                s = BFR.readLine();
            }
            BFR.close();
            return true;
        }
       catch (IOException e){
            System.out.println(e.getMessage());
            return false;
       }
        }

    //Adds a rider to the system after enforcing uniqueness by ID. Returns true if added; false if duplicate ID or invalid
     public boolean addRider(IRider rider){
        return false;
     }

    /**
     * Adds a driver to the system after enforcing uniqueness by ID and by
     * vehicle plate number (no two drivers may share the same plate).
     * Returns true if added; false if duplicate ID, duplicate plate, or invalid
     */
     public boolean addDriver(IDriver driver){
        return false;
     }

    //Searches for a rider by ID.
    public IRider searchRiderById(int riderId){
        return null;
    }

    //Searches for a rider by email.
    public IRider searchRiderByEmail(String email){
        return null;
    }

    //Searches for all riders whose full name exactly matches the given name.
    public LinkedList<IRider> searchRidersByName(String fullName){
        return null;
    }

    //Searches for all riders from the specified home city.
    public LinkedList<IRider> searchRidersByHomeCity(String homeCity){
        return null;
    }

    //Returns all riders stored in the system.
    public LinkedList<IRider> getAllRiders(){
        return null;
    }

    //Searches for a driver by ID.
    public IDriver searchDriverById(int driverId){
        return null;
    }

    //Searches for a driver by vehicle plate.
    public IDriver searchDriverByVehiclePlate(String vehiclePlate){
        return null;
    }

    //Searches for all drivers with the specified vehicle type.
    public LinkedList<IDriver> searchDriversByVehicleType(VehicleType vehicleType){
        return null;
    }

    //Returns all drivers stored in the system.
    public LinkedList<IDriver> getAllDrivers(){
        return null;
    }

    /**
     * Removes a rider and performs cascade deletion of associated rides:
     * - All private rides involving the rider are deleted.
     * - The rider is removed from any shared rides.
     * - Shared rides with no remaining participants are deleted.
     * It returns true if the rider was found and removed; false otherwise
     */
    public boolean removeRider(int riderId){
        return false;
    }

    /**
     * Removes a driver and performs cascade deletion of their assigned rides:
     * - All private rides assigned to the driver are deleted.
     * - All shared rides assigned to the driver are deleted.
     * It returns true if the driver was found and removed; false otherwise
     */
    public boolean removeDriver(int driverId){
        return false;
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
        return false;
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
        return false;
    }

    //Searches for all rides whose pickup location matches the given location (may return multiple if pickup locations repeat).
    public LinkedList<IRide> searchRidesByPickupLocation(String pickupLocation){
        return null;
    }

    //Searches for all rides that involve a rider with the given name.
    public LinkedList<IRide> searchRidesByRiderName(String riderName){
        return null;
    }

    //Returns all riders registered on a shared ride identified by its pickup location.
    public LinkedList<IRider> getSharedRideParticipants(String pickupLocation){
        return null;

    }

    //Returns all rides (private and shared) alphabetically ordered by pickup location.
    public LinkedList<IRide> getAllRidesAlphabetically(){
        return null;

    }
}
