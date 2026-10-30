public abstract class Ride implements IRide {

    private final int rideId;
    private String pickupLocation;
    private final IDateTime pickupTime;
    private final IDateTime dropoffTime;
    private String dropoffLocation;
    private IDriver driver;
    private Node<RideList> lists;

    public Ride(int rideId, String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                String dropoffLocation, IDriver driver) {
        this.rideId = rideId;
        if (pickupTime == null || dropoffTime == null || pickupTime.compareTo(dropoffTime) >= 0)
            throw new IllegalArgumentException("Drop-off time must be after pickup time");
        setPickupLocation(pickupLocation);
        this.pickupTime = new DateTime(pickupTime.getYear(), pickupTime.getMonth(), pickupTime.getDay(), pickupTime.getHour(), pickupTime.getMinute());
        this.dropoffTime = new DateTime(dropoffTime.getYear(), dropoffTime.getMonth(), dropoffTime.getDay(), dropoffTime.getHour(), dropoffTime.getMinute());
        setDropoffLocation(dropoffLocation);
        this.driver = driver;
    }

    @Override
    public int getRideId() {
        return rideId;
    }

    @Override
    public String getPickupLocation() {
        return pickupLocation;
    }

    @Override
    public void setPickupLocation(String pickupLocation) {
        if (pickupLocation == null || pickupLocation.trim().isEmpty())
            throw new IllegalArgumentException("Pickup location is required");
        this.pickupLocation = pickupLocation;
        for (Node<RideList> node = lists; node != null; node = node.next)
            node.data.pickupLocationChanged(this);
    }

    void attachList(RideList list) {
        Node<RideList> node = new Node<RideList>(list);
        node.next = lists;
        lists = node;
    }

    void detachList(RideList list) {
        Node<RideList> previous = null;
        Node<RideList> node = lists;
        while (node != null) {
            if (node.data == list) {
                if (previous == null) lists = node.next;
                else previous.next = node.next;
                return;
            }
            previous = node;
            node = node.next;
        }
    }

    @Override
    public IDateTime getPickupTime() {
        return pickupTime;
    }

    @Override
    public IDateTime getDropoffTime() {
        return dropoffTime;
    }

    @Override
    public String getDropoffLocation() {
        return dropoffLocation;
    }

    @Override
    public void setDropoffLocation(String dropoffLocation) {
        if (dropoffLocation == null || dropoffLocation.trim().isEmpty())
            throw new IllegalArgumentException("Drop-off location is required");
        this.dropoffLocation = dropoffLocation;
    }

    @Override
    public IDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(IDriver driver) {
        this.driver = driver;
    }

    @Override
    public abstract boolean hasRider(int riderId);

    @Override
    public int compareTo(IRide other) {
        return pickupLocation.compareToIgnoreCase(other.getPickupLocation());
    }

    @Override
    public String toString() {
        String driverText;
        if (driver == null)
            driverText = "none";
        else
            driverText = driver.getName() + " (ID " + driver.getId() + ")";

        return "Ride #" + rideId
                + " | Pickup: " + pickupLocation + " at " + pickupTime
                + " | Drop-off: " + dropoffLocation + " at " + dropoffTime
                + " | Driver: " + driverText;
    }
}
