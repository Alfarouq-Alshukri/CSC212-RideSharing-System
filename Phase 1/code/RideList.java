public class RideList implements IRideList {

    private LinkedList<IRide> rides;
    private int size;

    public RideList() {
        rides = new LinkedList<IRide>();
        size = 0;
    }

    @Override
    public boolean addRide(IRide ride) {
        if (ride == null || containsId(ride.getRideId()))
            return false;

        if (rides.empty()) {
            rides.insert(ride);
            size++;
            return true;
        }

        rides.findFirst();
        while (true) {
            if (ride.compareTo(rides.retrieve()) < 0) {
                IRide displaced = rides.retrieve();
                rides.update(ride);
                rides.insert(displaced);
                size++;
                return true;
            }
            if (rides.last())
                break;
            rides.findNext();
        }

        rides.insert(ride);
        size++;
        return true;
    }

    @Override
    public boolean removeRideById(int rideId) {
        if (rides.empty())
            return false;

        rides.findFirst();
        while (true) {
            if (rides.retrieve().getRideId() == rideId) {
                rides.remove();
                size--;
                return true;
            }
            if (rides.last())
                break;
            rides.findNext();
        }
        return false;
    }

    @Override
    public LinkedList<IRide> getAllAlphabetically() {
        return rides;
    }

    @Override
    public LinkedList<IRide> findByPickupLocation(String pickupLocation) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        if (rides.empty() || pickupLocation == null)
            return result;

        rides.findFirst();
        while (true) {
            if (rides.retrieve().getPickupLocation().equalsIgnoreCase(pickupLocation))
                result.insert(rides.retrieve());
            if (rides.last())
                break;
            rides.findNext();
        }
        return result;
    }

    @Override
    public LinkedList<IRide> findByRiderName(String riderFullName) {
        LinkedList<IRide> result = new LinkedList<IRide>();
        if (rides.empty() || riderFullName == null)
            return result;

        rides.findFirst();
        while (true) {
            IRide ride = rides.retrieve();
            if (rideHasRiderNamed(ride, riderFullName))
                result.insert(ride);
            if (rides.last())
                break;
            rides.findNext();
        }
        return result;
    }

    @Override
    public int size() {
        return size;
    }

    private boolean containsId(int rideId) {
        if (rides.empty())
            return false;

        rides.findFirst();
        while (true) {
            if (rides.retrieve().getRideId() == rideId)
                return true;
            if (rides.last())
                break;
            rides.findNext();
        }
        return false;
    }

    private boolean rideHasRiderNamed(IRide ride, String fullName) {
        if (ride instanceof IPrivateRide) {
            IRider rider = ((IPrivateRide) ride).getRider();
            return rider != null && fullName.equalsIgnoreCase(rider.getName());
        }

        if (ride instanceof ISharedRide) {
            LinkedList<IRider> participants = ((ISharedRide) ride).getParticipants();
            if (participants.empty())
                return false;

            participants.findFirst();
            while (true) {
                if (fullName.equalsIgnoreCase(participants.retrieve().getName()))
                    return true;
                if (participants.last())
                    break;
                participants.findNext();
            }
        }
        return false;
    }
}
