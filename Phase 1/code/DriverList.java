public class DriverList implements IDriverList {

    private LinkedList<IDriver> drivers;
    private final PersonNameIndex<IDriver> names = new PersonNameIndex<IDriver>();

    public DriverList() {
        drivers = new LinkedList<IDriver>();
    }

    public boolean add(IDriver driver) {
        if (driver == null || driver.getName() == null || driver.getName().trim().isEmpty()
                || driver.getPhoneNumber() == null || !driver.getPhoneNumber().matches("[0-9]{10}")
                || driver.getVehiclePlate() == null || !driver.getVehiclePlate().matches("[A-Z]{3}[0-9]{4}")
                || driver.getVehicleType() == null)
            return false;
        if (drivers.empty()) {
            drivers.insert(driver);
        } else {
            if (findByVehiclePlate(driver.getVehiclePlate()) != null)
                return false;
            drivers.findFirst();
            while (true) {
                IDriver current = drivers.retrieve();
                int comparison = driver.compareTo(current);
                if (comparison == 0)
                    return false;
                if (comparison < 0) {
                    drivers.update(driver);
                    drivers.insert(current);
                    break;
                }
                if (drivers.last()) {
                    drivers.insert(driver);
                    break;
                }
                drivers.findNext();
            }
        }
        names.add(driver);
        return true;
    }

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

    // The name index lets alphabetical listing take O(n).
    public LinkedList<IDriver> getAllAlphabetically() {
        return names.getAll();
    }

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

    public boolean removeById(int driverId) {
        if (drivers.empty()) {
            return false;
        }
        drivers.findFirst();
        while (true) {
            IDriver driver = drivers.retrieve();
            if (driver.getId() == driverId) {
                names.remove(driver);
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

    public boolean removeByVehiclePlate(String vehiclePlate) {
        if (drivers.empty()) {
            return false;
        }
        drivers.findFirst();
        while (true) {
            IDriver driver = drivers.retrieve();
            if (driver.getVehiclePlate().equals(vehiclePlate)) {
                names.remove(driver);
                drivers.remove();
                return true;
            }
            if (drivers.last()) {
                return false;
            }
            drivers.findNext();
        }
    }

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
                names.remove(driver);
                drivers.remove();
                count++;
                if (wasLast || drivers.empty()) {
                    break;
                }
            } else {
                if (drivers.last()) {
                    break;
                }
                drivers.findNext();
            }
        }
        return count;
    }

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
                names.remove(driver);
                drivers.remove();
                count++;
                if (wasLast || drivers.empty()) {
                    break;
                }
            } else {
                if (drivers.last()) {
                    break;
                }
                drivers.findNext();
            }
        }
        return count;
    }

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
