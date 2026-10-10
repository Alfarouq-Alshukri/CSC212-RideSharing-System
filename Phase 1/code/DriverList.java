public class DriverList implements IDriverList {
    // attributes
    private LinkedList<IDriver> drivers;
    // constructor
    public DriverList() {
        drivers = new LinkedList<IDriver>();
    }

    // Inserts a driver into the list in sorted order by ID. If a driver with the same ID already exists, insertion fails.
    public boolean add(IDriver driver) {
        if (drivers.empty()) {
            drivers.insert(driver);
            return true;
        }
        if (findByVehiclePlate(driver.getVehiclePlate()) != null) {
            return false;
        }
        drivers.findFirst();
        while (true) {
            IDriver currentDriver = drivers.retrieve();
            int comparison = driver.compareTo(currentDriver);
            if (comparison == 0) {
                return false;
            }
            if (comparison < 0) {
                drivers.update(driver);
                drivers.insert(currentDriver);
                return true;
            }
            if (drivers.last()) {
                drivers.insert(driver);
                return true;
            }
            drivers.findNext();
        }
    }

    //Searches for a driver by ID.
    public IDriver findById(int driverId) {
        if (drivers.empty()) {
            return null;
        }
        drivers.findFirst();
        while (true) {
            IDriver driver = drivers.retrieve();

            if (driver.getId() == driverId) {
                return driver;
            }

            if (driver.getId() > driverId) {
                return null;
            }

            if (drivers.last()) {
                return null;
            }

            drivers.findNext();
        }
    }

    // Searches for all drivers with the given full name.
    public LinkedList<IDriver> findByName(String fullName) {
        LinkedList<IDriver> result = new LinkedList<IDriver>();
        if (drivers.empty()) {
            return result;
        }
        drivers.findFirst();
        while (true) {
            IDriver driver = drivers.retrieve();
            if (driver.getName().equals(fullName)) {
                result.insert(driver);
            }
            if (drivers.last()) {
                break;
            }
            drivers.findNext();
        }
        return result;
    }

    //Searches for a driver by vehicle plate number.
    public IDriver findByVehiclePlate(String vehiclePlate) {
        if (drivers.empty()) {
            return null;
        }
        drivers.findFirst();
        while (true) {
            IDriver driver = drivers.retrieve();
            if (driver.getVehiclePlate().equals(vehiclePlate)) {
                return driver;
            }
            if (drivers.last()) {
                break;
            }
            drivers.findNext();
        }
        return null;
    }

    //Returns all drivers with the specified vehicle type.
    public LinkedList<IDriver> findByVehicleType(VehicleType vehicleType) {
        LinkedList<IDriver> result = new LinkedList<IDriver>();
        if (drivers.empty()) {
            return result;
        }
        drivers.findFirst();
        while (true) {
            IDriver driver = drivers.retrieve();
            if (driver.getVehicleType() == vehicleType) {
                result.insert(driver);
            }
            if (drivers.last()) {
                break;
            }
            drivers.findNext();
        }
        return result;
    }

    //Returns all drivers in the linked list.
    public LinkedList<IDriver> getAll() {
        LinkedList<IDriver> result = new LinkedList<IDriver>();
        if (drivers.empty()) {
            return result;
        }
        drivers.findFirst();
        while (true) {
            result.insert(drivers.retrieve());
            if (drivers.last()) {
                break;
            }
            drivers.findNext();
        }
        return result;
    }

    //Removes a driver by ID. Returns true if a driver with that ID was found and removed; false otherwise.
    public boolean removeById(int driverId) {
        if (drivers.empty()) {
            return false;
        }
        drivers.findFirst();
        while (true) {
            IDriver driver = drivers.retrieve();
            if (driver.getId() == driverId) {
                drivers.remove();
                return true;
            }
            if (driver.getId() > driverId) {
                return false;
            }
            if (drivers.last()) {
                return false;
            }
            drivers.findNext();
        }
    }

    //Removes a driver by vehicle plate number. Returns true if a driver with that plate was found and removed; false otherwise.
    public boolean removeByVehiclePlate(String vehiclePlate) {
        if (drivers.empty()) {
            return false;
        }
        drivers.findFirst();
        while (true) {
            IDriver driver = drivers.retrieve();
            if (driver.getVehiclePlate().equals(vehiclePlate)) {
                drivers.remove();
                return true;
            }
            if (drivers.last()) {
                return false;
            }
            drivers.findNext();
        }
    }

    /**
     * Name and vehicle type are not unique, so more than one driver may match.
     * Removes every driver whose full name exactly matches the given name
     * (the same full-name equality used by findByName), not a first-name-only
     * or partial match.
     * Returns the number of drivers removed (0 if none matched).
     */
    public int removeByName(String fullName) {
        int count = 0;
        if (drivers.empty()) {
            return count;
        }
        drivers.findFirst();
        while (true) {
            IDriver driver = drivers.retrieve();
            if (driver.getName().equals(fullName)) {
                boolean wasLast = drivers.last();
                drivers.remove();
                count++;
                if (wasLast || drivers.empty()) {
                    break;
                }
            }
            else {
                if (drivers.last()) {
                    break;
                }
                drivers.findNext();
            }
        }
        return count;
    }

    /**
     * Removes every driver with the specified vehicle type.
     * Returns the number of drivers removed (0 if none matched).
     */
    public int removeByVehicleType(VehicleType vehicleType) {
        int count = 0;
        if (drivers.empty()) {
            return count;
        }
        drivers.findFirst();
        while (true) {
            IDriver driver = drivers.retrieve();
            if (driver.getVehicleType() == vehicleType) {
                boolean wasLast = drivers.last();
                drivers.remove();
                count++;
                if (wasLast || drivers.empty()) {
                    break;
                }
            }
            else {
                if (drivers.last()) {
                    break;
                }
                drivers.findNext();
            }
        }
        return count;
    }

    //return the total number of drivers stored.
    public int size() {
        if (drivers.empty()) {
            return 0;
        }
        int count = 0;
        drivers.findFirst();
        while (true) {
            count++;
            if (drivers.last()) {
                break;
            }
            drivers.findNext();
        }
        return count;
    }
}
