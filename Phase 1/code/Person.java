public abstract class Person implements IPerson {
    // attributes
    private int id;
    private String name;
    private String phoneNumber;
    private LinkedList<IRide> rideHistory; // should be changed to <Ride> when the class is finished
    // constructor
    public Person(int id, String name, String phoneNember){
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNember;
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
        this.name = name;
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
        if (phoneNumber.length() != 10){
            throw new IllegalArgumentException("Phone number must be of length 10");
        }
        for(int i = 0; i < phoneNumber.length(); i++){
            if(!(Character.isDigit(phoneNumber.charAt(i)))){
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
        rideHistory.findFirst();
        IRide tmp = rideHistory.retrieve();
        while (tmp != null) {
            s += "\n" + tmp.toString();
            rideHistory.findNext();
            tmp = rideHistory.retrieve();
        }

        return s;
    }
}
