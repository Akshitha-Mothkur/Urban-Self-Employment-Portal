public class Provider {

    private int id;
    private String name;
    private String service;
    private String location;
    private double price;
    private int experience;

    public Provider(int id, String name, String service,
                    String location, double price, int experience) {
        this.id = id;
        this.name = name;
        this.service = service;
        this.location = location;
        this.price = price;
        this.experience = experience;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getService() {
        return service;
    }

    public String getLocation() {
        return location;
    }

    public double getPrice() {
        return price;
    }

    public int getExperience() {
        return experience;
    }
}
