public abstract class Person implements IPerson {

    private final int id;
    private String name;
    private String phoneNumber;
    private LinkedList<IRide> rideHistory;
    // Keep every name index sorted when this person is renamed.
    private Node<PersonNameIndex> nameIndexes;

    public Person(int id, String name, String phoneNumber) {
        this.id = id;
        setName(name);
        setPhoneNumber(phoneNumber);
        rideHistory = new LinkedList<IRide>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name is required");
        this.name = name;
        Node<PersonNameIndex> node = nameIndexes;
        while (node != null) {
            node.data.nameChanged(this);
            node = node.next;
        }
    }

    void attachNameIndex(PersonNameIndex index) {
        Node<PersonNameIndex> node = new Node<PersonNameIndex>(index);
        node.next = nameIndexes;
        nameIndexes = node;
    }

    void detachNameIndex(PersonNameIndex index) {
        Node<PersonNameIndex> previous = null;
        Node<PersonNameIndex> node = nameIndexes;
        while (node != null) {
            if (node.data == index) {
                if (previous == null)
                    nameIndexes = node.next;
                else
                    previous.next = node.next;
                return;
            }
            previous = node;
            node = node.next;
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 10) {
            throw new IllegalArgumentException("Phone number must be of length 10");
        }
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (phoneNumber.charAt(i) < '0' || phoneNumber.charAt(i) > '9') {
                throw new IllegalArgumentException("Phone number must be all numbers");
            }
        }
        this.phoneNumber = phoneNumber;
    }

    public LinkedList<IRide> getRideHistory() {
        return rideHistory;
    }

    @Override
    public String toString() {

        String s = "ID: " + id + "\nName: " + name + "\nPhone Number: " + phoneNumber + "\nRide History: ";
        if (rideHistory.empty()) {
            return s;
        }
        rideHistory.findFirst();
        while (true) {
            s += "\n" + rideHistory.retrieve().toString();
            if (rideHistory.last()) {
                break;
            }
            rideHistory.findNext();
        }

        return s;
    }
}
