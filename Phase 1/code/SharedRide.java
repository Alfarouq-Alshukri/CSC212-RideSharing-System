public class SharedRide extends Ride implements ISharedRide {

    private LinkedList<IRider> participants;

    public SharedRide(int rideId, String pickupLocation, IDateTime pickupTime, IDateTime dropoffTime,
                      String dropoffLocation, IDriver driver) {
        super(rideId, pickupLocation, pickupTime, dropoffTime, dropoffLocation, driver);
        participants = new LinkedList<IRider>();
    }

    @Override
    public LinkedList<IRider> getParticipants() {
        LinkedList<IRider> copy = new LinkedList<IRider>();
        if (participants.empty())
            return copy;

        participants.findFirst();
        while (true) {
            copy.insert(participants.retrieve());
            if (participants.last())
                break;
            participants.findNext();
        }
        return copy;
    }

    @Override
    public boolean addParticipant(IRider rider) {
        if (rider == null || hasRider(rider.getId()))
            return false;

        if (!participants.empty()) {
            participants.findFirst();
            while (!participants.last())
                participants.findNext();
        }
        participants.insert(rider);
        return true;
    }

    @Override
    public boolean removeParticipantById(int riderId) {
        if (participants.empty())
            return false;

        participants.findFirst();
        while (true) {
            if (participants.retrieve().getId() == riderId) {
                participants.remove();
                return true;
            }
            if (participants.last())
                break;
            participants.findNext();
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return participants.empty();
    }

    @Override
    public boolean hasRider(int riderId) {
        if (participants.empty())
            return false;

        participants.findFirst();
        while (true) {
            if (participants.retrieve().getId() == riderId)
                return true;
            if (participants.last())
                break;
            participants.findNext();
        }
        return false;
    }

    @Override
    public String toString() {
        String ridersText = "";
        if (participants.empty()) {
            ridersText = "none";
        } else {
            participants.findFirst();
            while (true) {
                IRider r = participants.retrieve();
                ridersText += r.getName() + " (ID " + r.getId() + ")";
                if (participants.last())
                    break;
                ridersText += ", ";
                participants.findNext();
            }
        }
        return "[Shared] " + super.toString() + " | Riders: " + ridersText;
    }
}
