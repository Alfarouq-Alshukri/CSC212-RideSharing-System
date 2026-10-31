public class Rider extends Person implements IRider {

    private String email;
    private String homeCity;

    public Rider(int id, String name, String phoneNumber, String email, String homeCity) {
        super(id, name, phoneNumber);
        setEmail(email);
        setHomeCity(homeCity);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Email is required");
        this.email = email;
    }

    public String getHomeCity() {
        return homeCity;
    }

    public void setHomeCity(String homeCity) {
        if (homeCity == null || homeCity.trim().isEmpty())
            throw new IllegalArgumentException("Home city is required");
        this.homeCity = homeCity;
    }

    @Override
    public int compareTo(IRider other) {
        return Integer.compare(getId(), other.getId());
    }

    @Override
    public String toString() {
        return super.toString() + "\n, Email: " + email + ",\n Home City: " + homeCity;
    }
}
