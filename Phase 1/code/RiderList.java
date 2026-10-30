public class RiderList implements IRiderList {

    private LinkedList<IRider> riders;
    private int size;
    private final PersonNameIndex<IRider> names = new PersonNameIndex<IRider>();

    public RiderList() {
        riders = new LinkedList<IRider>();
        size = 0;
    }


    @Override
    public int size() { return size; }


    @Override
    public boolean add(IRider rider) {
        if (rider == null || rider.getName() == null || rider.getName().trim().isEmpty()
                || rider.getEmail() == null || rider.getEmail().trim().isEmpty()
                || rider.getHomeCity() == null || rider.getHomeCity().trim().isEmpty()
                || rider.getPhoneNumber() == null || !rider.getPhoneNumber().matches("[0-9]{10}")
                || findByEmail(rider.getEmail()) != null)
            return false;

        if (riders.empty()) {
            riders.insert(rider);
            size++;
            names.add(rider);
            return true;
        }

        riders.findFirst();

        while (true) {
            int cmp = rider.compareTo(riders.retrieve());

            if (cmp == 0)
                return false;

            if (cmp < 0) {
                IRider displaced = riders.retrieve();
                riders.update(rider);
                riders.insert(displaced);
                size++;
                names.add(rider);
                return true;
            }

            if (riders.last()) {
                riders.insert(rider);
                size++;
                names.add(rider);
                return true;
            }
            riders.findNext();
        }
    }

    @Override
    public LinkedList<IRider> findByName(String fullName) {
        LinkedList<IRider> result = new LinkedList<IRider>();
        if (riders.empty())
            return result;                     

        riders.findFirst();
        while (true) {
            IRider r = riders.retrieve();
            if (r.getName().equals(fullName))
                result.insert(r);
            if (riders.last())
                break;
            riders.findNext();
        }
        return result;
    }

    @Override
    public LinkedList<IRider> findByHomeCity(String Homecity) {
        LinkedList<IRider> result = new LinkedList<IRider>();
        if (riders.empty())
            return result;

        riders.findFirst();
        while (true) {
            IRider r = riders.retrieve();
            if (r.getHomeCity().equals(Homecity))
                result.insert(r);
            if (riders.last())
                break;
            riders.findNext();
        }
        return result;
    }



    public IRider findById(int id) {
        if (riders.empty())
            return null;

        riders.findFirst();
        while (true) {
            IRider current = riders.retrieve();

            if (current.getId() == id)
                return current;


            if (current.getId() > id)
                return null;

            if (riders.last())
                break;
            riders.findNext();
        }
        return null;
    }



    public IRider findByEmail(String email) {

        if (riders.empty())
            return null;

        riders.findFirst();

        while (true) {
            IRider r = riders.retrieve();

            if (r.getEmail().equals(email))
                return r;
            if (riders.last())
                break;
            riders.findNext();
        }
        return null;
    }





    // Copies a maintained name index, so alphabetical listing takes O(n).
    public LinkedList<IRider> getAllAlphabetically() {
        return names.getAll();
    }

    @Override
    public LinkedList<IRider> getAll() {
        LinkedList<IRider> result = new LinkedList<IRider>();
        if (riders.empty())
            return result;

        riders.findFirst();
        while (true) {
            result.insert(riders.retrieve());

            if (riders.last())
                break;
            riders.findNext();
        }
        return result;
    }

    @Override
    public boolean removeById(int riderId) {
        if (riders.empty())
            return false;

        riders.findFirst();
        while (true) {
            int currentId = riders.retrieve().getId();

            if (currentId == riderId) {
                names.remove(riders.retrieve());
                riders.remove();
                size--;
                return true;
            }
            if (currentId > riderId)
                return false;

            if (riders.last())
                break;
            riders.findNext();
        }
        return false;
    }


    @Override
    public boolean removeByEmail(String email) {
        if (riders.empty() || email == null)
            return false;

        riders.findFirst();
        while (true) {
            if (email.equals(riders.retrieve().getEmail())) {
                names.remove(riders.retrieve());
                riders.remove();
                size--;
                return true;
            }

            if (riders.last())
                break;
            riders.findNext();
        }
        return false;
    }


    @Override
    public int removeByName(String fullName) {
        int original = size;
        int removed = 0;
        if (original == 0 || fullName == null)
            return 0;

        riders.findFirst();
        for (int i = 0; i < original; i++) {
            if (fullName.equals(riders.retrieve().getName())) {
                names.remove(riders.retrieve());
                riders.remove();
                size--;
                removed++;
            } else if (i < original - 1) {
                riders.findNext();
            }
        }
        return removed;
    }


    @Override
    public int removeByHomeCity(String homeCity) {
        int original = size;
        int removed = 0;
        if (original == 0 || homeCity == null)
            return 0;

        riders.findFirst();
        for (int i = 0; i < original; i++) {
            if (homeCity.equals(riders.retrieve().getHomeCity())) {
                names.remove(riders.retrieve());
                riders.remove();
                size--;
                removed++;
            } else if (i < original - 1) {
                riders.findNext();
            }
        }
        return removed;
    }

}
