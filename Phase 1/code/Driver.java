public class Driver extends Person implements IDriver {
    // attributes
    private String vehiclePlate;
    private VehicleType vehicleType;
    // constructor
    public Driver(int id, String name, String phoneNember, String vehiclePlate, VehicleType vehicleType){
        super(id, name, phoneNember);
        setVehiclePlate(vehiclePlate);
        this.vehicleType = vehicleType;
    }
     /**
     * return the driver's vehicle plate number.
     */
    public String getVehiclePlate(){
        return vehiclePlate;
    }

    /**
     * Sets the driver's vehicle plate number.
     * The plate must follow the format of exactly 3 uppercase letters
     * followed by 4 digits (e.g., "ABC1234"). Implementations must
     * validate the format and throw IllegalArgumentException if it does
     * not match. Vehicle plate numbers must be unique across all drivers
     * in the system; enforcing that uniqueness is the responsibility of
     * IDriverList/IRideSharingSystem when a driver is added.
     */
    public void setVehiclePlate(String vehiclePlate) {
    if (vehiclePlate.length() != 7) {
        throw new IllegalArgumentException("Vehicle plate must be 3 uppercase letters followed by 4 digits");
    }

    for (int i = 0; i < 3; i++) {
        char c = vehiclePlate.charAt(i);
        if (c < 'A' || c > 'Z') {
            throw new IllegalArgumentException("First 3 characters must be uppercase letters");
            }
    }

    for (int i = 3; i < 7; i++) {
        if (!(Character.isDigit(vehiclePlate.charAt(i)))) {
            throw new IllegalArgumentException("Last 4 characters must be digits");
        }
    }
    this.vehiclePlate = vehiclePlate;
}

    //return the driver's vehicle type.
    public VehicleType getVehicleType(){
        return vehicleType;
    }

    //Sets the driver's vehicle type.
    public void setVehicleType(VehicleType vehicleType){
        this.vehicleType = vehicleType;
    }

    /**
     * Compares this driver with another driver based on driver ID.
     * Returns a negative integer, zero, or a positive integer as this
     * driver's ID is less than, equal to, or greater than the other
     * driver's ID. This ordering must be consistent with equality by ID.
     */
    @Override
    public int compareTo(IDriver other){
        return this.getId() - other.getId();
    }

    @Override
    public String toString() {
        return super.toString() + "\nVehicle Plate: " + vehiclePlate + "\nVehicle Type: " + vehicleType;
    }


}
