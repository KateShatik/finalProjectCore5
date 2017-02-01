public class Room {
    private long id;
    private int price;
    private int persons;
    private Hotel hotel;
    private User userReserved=null;

    public Room(long id, int price, int persons, Hotel hotel) {
        this.id = id;
        this.price = price;
        this.persons = persons;
        this.hotel = hotel;
    }

    public Room(long id, int price, int persons, Hotel hotel, User userReserved) {
        this.id = id;
        this.price = price;
        this.persons = persons;
        this.hotel = hotel;
        this.userReserved = userReserved;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public User getUserReserved() {
        return userReserved;
    }

    public void setUserReserved(User userReserved) {
        this.userReserved = userReserved;
    }

//

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        if (id != room.id) return false;
        if (price != room.price) return false;
        if (persons != room.persons) return false;
        return hotel.equals(room.hotel);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + price;
        result = 31 * result + persons;
        result = 31 * result + hotel.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", price=" + price +
                ", persons=" + persons +
                ", hotel=" + hotel +
                '}';
    }
}