import java.util.*;
import java.util.stream.Collectors;
//import static finalProjectCore.Main.CITY;
//import static finalProjectCore.Main.HOTEL_NAME;

public class Controller {
    public static final String CITY = "city";
    public static final String HOTEL_NAME = "hotelName";
    public static final String PERSONS = "persons";
    public static final String MAX_PRICE = "maxPrice";
    public static final String MIN_PRICE = "minPrice";

    //Поиск зарезервированных юзером номеров
    public List<Room> bookedByUser (User user){
        List<Room> list = RoomDAO.getRoomDAO().getBase().stream().filter(room ->
                room.getUserReserved() != null). filter(room ->
                room.getUserReserved().equals(user)).collect(Collectors.toList());
        if (list.size() == 0) System.out.println("забронированных номеров не найдено");
        return list;
    }

    //Регистрация юзера в базе
    public boolean registerUser(User newUser) {
        long id;
        if (UserDAO.getUserDAO().getBase().size() != 0) {
            User userWithTheBigestId = UserDAO.getUserDAO().getBase().stream().max((u1, u2) ->
            {return Long.compare(u1.getId(), u2.getId()); }).get();
            id = userWithTheBigestId.getId() + 1;
        }
        else id = 100;
        newUser = new User(id, newUser.getName(), newUser.getPassword());
        boolean b = UserDAO.getUserDAO().add(newUser);
        return b;
    }

    //Вход в систему для юзера
    public User enter (String name, String pass){
        if (UserDAO.getUserDAO().getBase().size() == 0) {
            System.out.println("Пользователь не найден.");
            return null;
        }
        User user = null;
        try {
            user = UserDAO.getUserDAO().getBase().stream().filter(u ->
                    (u.getName().toLowerCase().equals(name))&& u.getPassword().equals(pass) ).findAny().get();
        } catch (Exception e) {
            System.out.println("Не верное имя пользователя, либо пароль");
            return null;
        }
        System.out.println("Вы вошли как " + name);
        return user;
    }

    public List<Hotel> findHotelByName(String name) {
        List<Hotel> list = HotelDAO.getHotelDAO().getBase().stream()
                .filter(hotel -> hotel.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
        if(list.size() == 0) System.out.println("Отели не найдены");
        return list;
    }

    public List<Hotel> findHotelByCity(String city){
        List<Hotel> list = HotelDAO.getHotelDAO().getBase().stream().
                filter(hotel -> hotel.getCity().toLowerCase().contains(city.toLowerCase())).collect(Collectors.toList());
        if(list.size() == 0) System.out.println("Отели не найдены");
        return list;
    }

    public boolean bookRoom(long roomId, long userId, long hotelId) {
        try {
            User userToRegister = UserDAO.getUserDAO().getBase().stream()
                    .filter(user -> user.getId() == userId)
                    .findAny()
                    .get();

            try {
                Hotel hotel = HotelDAO.getHotelDAO().getBase().stream()
                        .filter(hotel1 -> hotel1.getId() == hotelId)
                        .findAny()
                        .get();
                try {
                    Room roomToReservation = hotel.getRooms().stream()
                            .filter(room -> room.getId() == roomId)
                            .findAny()
                            .get();

                    if (roomToReservation.getUserReserved() == null) {
                        roomToReservation.setUserReserved(userToRegister);
                        System.out.printf("Номер %s успешно забронирован пользователем %s\n",
                                roomToReservation.toString(), userToRegister.toString() + System.lineSeparator());
                        RoomDAO.getRoomDAO().edit(roomToReservation);
                        return true;
                    } else
                        System.out.println("Комната недоступна для бронирования!");

                } catch (NoSuchElementException | NullPointerException e){
                    System.out.printf("Комната с ID %d в отеле с ID %d не найдена.\n", roomId, hotelId);
                }

            } catch (NoSuchElementException | NullPointerException e){
                System.out.printf("Отеля с ID %d нет базе.\n", hotelId);
            }
        } catch (NoSuchElementException e) {
            System.out.printf("Пользователя с ID %d нет базе. Просим зарегистрироваться!\n", userId);
        }
        return false;
    }

    public boolean cancelReservation(long roomId, long userId, long hotelId) {
        try {
            User userToRegister = UserDAO.getUserDAO().getBase().stream()
                    .filter(user -> user.getId() == userId)
                    .findAny()
                    .get();
            try {
                Hotel hotel = HotelDAO.getHotelDAO().getBase().stream()
                        .filter(hotel1 -> hotel1.getId() == hotelId)
                        .findAny()
                        .get();
                try {
                    Room roomToReservation = hotel.getRooms().stream()
                            .filter(room -> room.getId() == roomId)
                            .findAny()
                            .get();

                    if ((roomToReservation.getUserReserved() != null) &&
                            (userToRegister.equals(roomToReservation.getUserReserved()) )) {
                        roomToReservation.setUserReserved(null);
                        System.out.printf("Резерв пользователя %s с комнаты %s снят.\n",
                                userToRegister.toString(),roomToReservation.toString());
                        RoomDAO.getRoomDAO().edit(roomToReservation);
                        return true;
                    } else
                        System.out.println("Вы не бронировали такой номер.");

                } catch (NoSuchElementException | NullPointerException e){
                    System.out.printf("Комната с ID %d в отеле с ID %d не найдена.\n", roomId, hotelId);
                }

            } catch (NoSuchElementException | NullPointerException e){
                System.out.printf("Отеля с ID %d нет базе.\n", hotelId);
            }

        } catch (NoSuchElementException e) {
            System.out.printf("Пользователя с ID %d нет базе. Просим зарегистрироваться!\n", userId);
        }
        return false;
    }

    public List<Room> findRoom(Map <String,String> params){
        List<Room> rooms = RoomDAO.getRoomDAO().getBase().stream().filter(room ->
                room.getUserReserved() == null).collect(Collectors.toList());
        String city = params.get(CITY);
        String hotelName = params.get(HOTEL_NAME);
        String personsString = params.get(PERSONS);
        String maxPriceStr = params.get(MAX_PRICE);
        String minPriceStr = params.get(MIN_PRICE);
        int maxPriceInt = Integer.MAX_VALUE;
        int minPriceInt = Integer.MIN_VALUE;

        if (!"*".equals(city)) rooms = rooms.stream().filter(room ->
                room.getHotel().getCity().toLowerCase().contains(city.toLowerCase())).collect(Collectors.toList());

        if (!"*".equals(hotelName)) rooms = rooms.stream().filter(room ->
                room.getHotel().getName().toLowerCase().contains(hotelName.toLowerCase())).collect(Collectors.toList());

        if (!"*".equals(personsString)){
            try {
                rooms = rooms.stream().filter(room ->
                        room.getPersons() == Integer.parseInt(personsString)).collect(Collectors.toList());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка при вводе кол-ва персон. Требовалось целое число, либо символ *." +
                        " Поэтому при поиске этот фильтр не использовался");
            }
        }
        if (!"*".equals(maxPriceStr)) try {
            maxPriceInt = Integer.parseInt(maxPriceStr);
        } catch (NumberFormatException e){
            System.out.println("Ошибка при вводе максимальной цены. Требовалось целое число, либо символ *. " +
                    "Поэтому при поиске этот фильтр не использовался");
        }
        if (!"*".equals(minPriceStr)) try {
            minPriceInt = Integer.parseInt(minPriceStr);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка при вводе минимальной цены. Требовалось целое число, либо символ *. " +
                    "Поэтому при поиске этот фильтр не использовался");
        }
        if (maxPriceInt < minPriceInt) {
            int temp = maxPriceInt;
            maxPriceInt = minPriceInt;
            minPriceInt = temp;
            System.out.println("Максимальная цена была меньше минимальной, пришлось поменять их местами");
        }
        final int maxPrice = maxPriceInt;
        final int minPrice = minPriceInt;
        return rooms.stream().filter(room -> (room.getPrice() <= maxPrice) && (room.getPrice() >= minPrice))
                .collect(Collectors.toList());
    }

    public boolean editUser (User user){
        return UserDAO.getUserDAO().edit(user);
    }

    public User findUserById (long id){
        User user = null;
        try {
            user = UserDAO.getUserDAO().getBase().stream().filter(user1 -> user1.getId() == id).findAny().get();
        }catch (NoSuchElementException e){}
        return user;
    }

    public boolean removeUser (User user){
        return UserDAO.getUserDAO().remove(user);
    }

    public boolean removeHotel (Hotel hotel){
        return HotelDAO.getHotelDAO().remove(hotel);
    }

    public boolean removeRoom (Room room){
        return RoomDAO.getRoomDAO().remove(room);
    }

    public Room findRoomById (long id){
        Room room = null;
        try {
            room = RoomDAO.getRoomDAO().getBase().stream().filter(room1 -> room1.getId() == id).findAny().get();
        }catch (NoSuchElementException e){} //не обрабатываем
        return room;
    }

    public Hotel editHotel (Hotel hotel){
        if (HotelDAO.getHotelDAO().edit(hotel)) return hotel;
        return null;
    }

    public Room editRoom (Room room){
        if (RoomDAO.getRoomDAO().edit(room)) return room;
        return null;

    }
    public Hotel addHotel (Hotel hotel){
        long id;
        if (HotelDAO.getHotelDAO().getBase().size() != 0) {
            Hotel hotelWithTheBiggestId = HotelDAO.getHotelDAO().getBase().stream().max((r1, r2) ->
                    Long.compare(r1.getId(), r2.getId())).get();
            id = hotelWithTheBiggestId.getId() + 1;
        }
        else id = 200;
        hotel = new Hotel(id, hotel.getName(), hotel.getCity());

        if (HotelDAO.getHotelDAO().add(hotel)) return hotel;
        return null;
    }

    public Room addRoom (Room room) {
        long id;
        if (RoomDAO.getRoomDAO().getBase().size() != 0) {
            Room roomWithTheBiggestId = RoomDAO.getRoomDAO().getBase().stream().max((r1, r2) ->
                    Long.compare(r1.getId(), r2.getId())).get();
            id = roomWithTheBiggestId.getId() + 1;
        }
        else id = 300;
        room = new Room(id, room.getPrice(), room.getPersons(),room.getHotel(),room.getUserReserved());
        if (RoomDAO.getRoomDAO().add(room)) return room;
        return null;
    }

    public Hotel findHotelById (long id){
        Hotel hotel = null;
        try {
            hotel = HotelDAO.getHotelDAO().getBase().stream()
                    .filter(h -> h.getId()==id).findAny().get();
        } catch (NoSuchElementException e){
            System.out.println("Отель с таким ID не найден!");
        }
        return hotel;
    }

    public List<User> findUsersByName (String name){
        List <User> list = UserDAO.getUserDAO().getBase().stream().filter(user ->
                user.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
        if (list.size() == 0) System.out.println("Подходящие пользователи не найдены!");
        return list;
    }
    public List<Room> booked () {
        return RoomDAO.getRoomDAO().getBase().stream().filter(user ->
                user.getUserReserved() != null).collect(Collectors.toList());
    }
}