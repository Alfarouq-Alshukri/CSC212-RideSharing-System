public class Driver extends Person implements IDriver {

    private String vehiclePlate;
    private VehicleType vehicleType;

    public Driver(int id, String name, String phoneNumber, String vehiclePlate, VehicleType vehicleType) {
        super(id, name, phoneNumber);
        setVehiclePlate(vehiclePlate);
        setVehicleType(vehicleType);
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        if (vehiclePlate == null || vehiclePlate.length() != 7)
            throw new IllegalArgumentException("Vehicle plate must be 3 uppercase letters followed by 4 digits");

        for (int i = 0; i < 3; i++) {
            char c = vehiclePlate.charAt(i);
            if (c < 'A' || c > 'Z')
                throw new IllegalArgumentException("First 3 characters must be uppercase letters");
        }
        for (int i = 3; i < 7; i++) {
            char c = vehiclePlate.charAt(i);
            if (c < '0' || c > '9')
                throw new IllegalArgumentException("Last 4 characters must be digits");
        }
        this.vehiclePlate = vehiclePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        if (vehicleType == null)
            throw new IllegalArgumentException("Vehicle type is required");
        this.vehicleType = vehicleType;
    }

    @Override
    public int compareTo(IDriver other) {
        return Integer.compare(getId(), other.getId());
    }

    @Override
    public String toString() {
        return super.toString() + "\nVehicle Plate: " + vehiclePlate + "\nVehicle Type: " + vehicleType;
    }
}
