public class Rider extends Person implements IRider{

    private String Email;
    private String HomeCity;

    public Rider(int ID ,String name, String phoneNember, String email, String City){
      super(ID,name,phoneNember);
        this.Email = email;
        this.HomeCity = City;
    }

    //return the rider's email address.
    public String getEmail(){
        return Email;
    }

    //Sets the rider's email address.
    public void setEmail(String email){
        this.Email = email;
    }

    //return the rider's home city.
    public String getHomeCity(){
        return HomeCity;
    }

    //Sets the rider's home city.
    public void setHomeCity(String homeCity){
        this.HomeCity = homeCity;
    }

    @Override
   public int compareTo(IRider other){
       return Integer.compare(this.getId(), other.getId());
}

    @Override
    public String toString() {
        return super.toString() + "\n, Email: " + Email + ",\n Home City: " + HomeCity;
    }

}
