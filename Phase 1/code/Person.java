public abstract class Person implements IPerson {
    // attributes
    private final int id;
    private String name;
    private String phoneNumber;
    private LinkedList<IRide> rideHistory;
    private Node<PersonNameIndex<?>> nameIndexes;

    // constructor
    public Person(int id, String name, String phoneNumber){
        this.id = id;
        setName(name);
        setPhoneNumber(phoneNumber);
        rideHistory = new LinkedList<IRide>();
    }
    
    
    //return the unique ID of this person.
    public int getId(){
        return id;
    }

    //return the full name of this person.
    public String getName(){
        return name;
    }

    //Sets the full name of this person.
    public void setName(String name){
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name is required");
        this.name = name;
        for (Node<PersonNameIndex<?>> node = nameIndexes; node != null; node = node.next)
            node.data.nameChanged(this);
    }

    void attachNameIndex(PersonNameIndex<?> index) {
        Node<PersonNameIndex<?>> node = new Node<PersonNameIndex<?>>(index);
        node.next = nameIndexes;
        nameIndexes = node;
    }

    void detachNameIndex(PersonNameIndex<?> index) {
        Node<PersonNameIndex<?>> previous = null;
        Node<PersonNameIndex<?>> node = nameIndexes;
        while (node != null) {
            if (node.data == index) {
                if (previous == null) nameIndexes = node.next;
                else previous.next = node.next;
                return;
            }
            previous = node;
            node = node.next;
        }
    }

    //return the phone number of this person.
    public String getPhoneNumber(){
        return phoneNumber;
    }

    /**
     * Sets the phone number of this person.
     * The phone number must be exactly 10 digits (numeric characters only,
     * e.g., "0551234567"). Implementations must validate the format and
     * throw IllegalArgumentException if it does not match.
     */
    public void setPhoneNumber(String phoneNumber){
        if (phoneNumber == null || phoneNumber.length() != 10){
            throw new IllegalArgumentException("Phone number must be of length 10");
        }
        for(int i = 0; i < phoneNumber.length(); i++){
            if(phoneNumber.charAt(i) < '0' || phoneNumber.charAt(i) > '9'){
                throw new IllegalArgumentException("Phone number must be all numbers");
            } 
        }
        this.phoneNumber = phoneNumber;
    }

    //return the list of rides associated with this person (as a rider, or as an assigned driver).
    public LinkedList<IRide> getRideHistory(){
        return rideHistory;
    }

    // Returns a formatted string describing this person.
    @Override
    public String toString(){

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
