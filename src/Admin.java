

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Admin {
    public static void root () throws IOException{
        Controller controller = new Controller();
        System.out.println("Вы вошли как администратор.");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        first:
        while (true){
            System.out.println("1- поиск пользователей по имени, 2- активность пользователей(забронированные номера)," +
                    " 3- удаление пользователей," +
                    "\n4- поиск отелей по названию, 5- поиск отелей по городу, 6- редактирование отелей, " +
                    "7- добавление отелей, 8- удаление отелей, " +
                    "\n9- поиск номеров, 10- редактирование номеров, 11- добавление номеров, 12- удаление номеров, " +
                    "\n0- выход из режима администрирования");
            String choice1 = br.readLine();
            if (choice1.equals("0")) break;

            // активность пользователей(забронированные номера)
            if (choice1.equals("2")){
                System.out.println("Активность пользователей. Забронированные номера:");
                List<Room> booked = controller.booked();
                if (booked.size() == 0) System.out.println("нет забронированных номеров");
                booked.stream().forEach(room -> System.out.println(room + " забронировал " + room.getUserReserved()));
                System.out.println();
                continue first;
            }


            //  1- поиск пользователей по имени
            if (choice1.equals("1")){
                while (true) {
                    System.out.println("Поиск пользователей. Введите имя пользователя, или часть имени;" +
                            " либо 9- предыдущее меню, 0- выход из режима администрирования");
                    String choice2 = br.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    controller.findUsersByName(choice2.trim()).forEach(System.out::println);
                    System.out.println("Продолжить поиск?");
                }
            }

            // 2- поиск отелей по названию
            if (choice1.equals("4")){
                while (true) {
                    System.out.println("Введите название отеля, или часть названия;" +
                            " либо 9- предыдущее меню, 0- выход из режима администрирования");
                    String choice2 = br.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    controller.findHotelByName(choice2.trim()).forEach(System.out::println);
                    System.out.println("Продолжить поиск?");
                }
            }

            // 3- поиск отелей по городу
            if (choice1.equals("5")){
                while (true) {
                    System.out.println("Введите название города, или часть названия;" +
                            " либо 9- предыдущее меню, 0- выход из режима администрирования");
                    String choice2 = br.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    controller.findHotelByCity(choice2.trim()).forEach(System.out::println);
                    System.out.println("Продолжить поиск?");
                }
            }

            // 4- поиск комнат
            if (choice1.equals("9")) {
                while (true) {
                    System.out.println("Для поиска номеров, введите параметры разделяя их символом / в таком формате:" +
                            " город/название отеля/кол-во персон/цена от/цена до, образец: Kiev/Desna/2/200/350 \n" +
                            "если параметр Вам не важен - поставьте вместо него символ *.   А если передумали искать," +
                            " то: 9- предыдущее меню, 0- выход из режима администрирования");
                    String choice2 = br.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    String s[] = choice2.split("/");
                    if (s.length !=5) {System.out.println("Некорректный ввод!"); continue;}
                    Map<String,String> map = new HashMap<>();
                    map.put(Controller.CITY, s[0].trim());
                    map.put(Controller.HOTEL_NAME, s[1].trim());
                    map.put(Controller.PERSONS, s[2].trim());
                    map.put(Controller.MIN_PRICE, s[3].trim());
                    map.put(Controller.MAX_PRICE, s[4].trim());
                    List<Room> rooms = controller.findRoom(map);
                    if (rooms.size() == 0) System.out.println("Ничего не найдено. Возможно параметры поиска слишком строгие.");
                    rooms.forEach(System.out::println);
                    System.out.println("Повторить поиск?");
                }
            }
            // 5- удаление пользователей
            if (choice1.equals("3")) {
                while (true){
                    System.out.println("Для удаления пользователя введите его id; либо: 9- предыдущее меню," +
                            " 0- выход из режима администрирования");
                    String choice2 = br.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    long id;
                    try {
                        id = Long.parseLong(choice2.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный ввод! Введите целое число.");
                        continue;
                    }
                    User user = controller.findUserById(id);
                    if (user == null){
                        System.out.println("Пользователь с таким id не найден.");
                        continue;
                    }
                    System.out.println("Вы уверены, что хотите удалить пользователя " + user + "? Введите 1 для подтверждения, " +
                            "или что-либо другое для отмены");
                    String ch = br.readLine();
                    if (ch.equals("1")){
                        boolean b = controller.removeUser(user);
                        if (b) System.out.println("Пользователь " + user + " удален.");
                        else System.out.println("Что-то пошло не так");
                    }
                    System.out.println("Продолжаем удалять пользователей?");
                }
            }

            // 6- удаление отелей
            if (choice1.equals("8")) {
                while (true) {
                    System.out.println("Для удаления отеля введите его id; " +
                            "либо: 9- предыдущее меню, 0- выход из режима администрирования");
                    String choice2 = br.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    long id;
                    try {
                        id = Long.parseLong(choice2.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный ввод! Введите целое число");
                        continue;
                    }
                    Hotel hotel = controller.findHotelById(id);
                    if (hotel == null) continue;

                    System.out.println("Вы уверены, что хотите удалить отель " + hotel +
                            "? Все его комнаты также будут удалены. \n Введите 1 для подтверждения, " +
                            "или что-либо другое для отмены");
                    String ch = br.readLine();
                    if (ch.equals("1")) {
                        boolean b = controller.removeHotel(hotel);
                        if (b) System.out.println("Отель " + hotel + " удален.");
                        else System.out.println("Что-то пошло не так");
                    }
                    System.out.println("Продолжаем удалять отели?");
                }
            }

            // 7- удаление комнат
            if (choice1.equals("12")) {
                while (true){
                    System.out.println("Для удаления комнаты введите ее id;" +
                            " либо: 9- предыдущее меню, 0- выход из режима администрирования");
                    String choice2 = br.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    long id;
                    try {
                        id = Long.parseLong(choice2.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный ввод! Введите целое число");
                        continue;
                    }
                    Room room = controller.findRoomById(id);
                    if (room == null) {
                        System.out.println("Комнаты с таким id не найдено");
                        continue;
                    }
                    System.out.println("Вы уверены, что хотите удалить комнату " + room +
                            "?  \n Введите 1 для подтверждения, или что-либо другое для отмены");
                    String ch = br.readLine();
                    if (ch.equals("1")) {
                        boolean b = controller.removeRoom(room);
                        if (b) System.out.println("Комната " + room + " удалена.");
                        else System.out.println("Что-то пошло не так");
                    }
                    System.out.println("Продолжаем удалять комнаты?");
                }
            }

            // 8- редактирование отелей
            if (choice1.equals("6")) {
                while (true) {
                    System.out.println("Для редактирования отеля введите его id;" +
                            " либо: 9- предыдущее меню, 0- выход из режима администрирования");
                    String choice2 = br.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    long id;
                    try {
                        id = Long.parseLong(choice2.trim());
                    }catch (NumberFormatException e){
                        System.out.println("Некорректный ввод! Введите целое число.");
                        continue;
                    }
                    Hotel hotel = controller.findHotelById(id);
                    if (hotel == null) continue;
                    while (true) {
                        System.out.println("Редактируем отель " + hotel +
                                "\nВведите новое имя отеля и город чрез разделитель /, пример: Radisson/Kiev ;  0- отмена");

                        String choce3 = br.readLine();
                        if (choce3.equals("0")) break;
                        String s[] = choce3.split("/");
                        if (s.length != 2) {
                            System.out.println("Некорректный ввод!");
                            continue;
                        }
                        Hotel newHotel = controller.editHotel(new Hotel(id, s[0].trim(), s[1].trim()));
                        if (newHotel != null) {
                            System.out.println("Отель отредактирован: " + newHotel +System.lineSeparator());
                            break;
                        }
                        System.out.println("Что-то пошло не так...");
                    }
                    System.out.println("Продолжаем редактировать отели?");
                }
            }

            // 9- редактирование комнат
            if (choice1.equals("10")) {
                while (true){
                    System.out.println("Для редактирования комнаты введите ее id; " +
                            "либо: 9- предыдущее меню, 0- выход из режима администрирования");
                    String choice2 = br.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    long id;
                    try {
                        id = Long.parseLong(choice2.trim());
                    }catch (NumberFormatException e){
                        System.out.println("Некорректный ввод! Введите целое число." + System.lineSeparator());
                        continue;
                    }
                    Room room = controller.findRoomById(id);
                    if (room == null) {
                        System.out.println("Комната с id " + id + " не найдена." + System.lineSeparator());
                        continue ;
                    }
                    while (true) {
                        System.out.println("Редактируем комнату " + room +
                                "\nВведите новые данные, цену и кол-во мест, через разделитель /, например 400/3 ;  " +
                                " 0- отмена");
                        String choce3 = br.readLine();
                        if (choce3.equals("0")) break;
                        String s[] = choce3.split("/");
                        if (s.length != 2) {
                            System.out.println("Некорректный ввод!");
                            continue;
                        }
                        int persons; int price;
                        try{
                            persons = Integer.parseInt(s[1].trim());
                            price = Integer.parseInt(s[0].trim());
                        }
                        catch (NumberFormatException e) {
                            System.out.println("Некорректный ввод! Вводить надо целые числа.");
                            continue ;
                        }
                        Room newRoom = controller.editRoom(
                                new Room(id, price, persons, room.getHotel(), room.getUserReserved()));
                        if (newRoom != null) {
                            System.out.println("Комната отредактирована: " + room + System.lineSeparator());
                            break ;
                        }
                        System.out.println("Что-то пошло не так...");
                    }
                    System.out.println("Продолжаем редактировать комнаты?");
                }
            }

            // 10- добавление отелей
            if (choice1.equals("7")) {
                while (true) {
                    System.out.println("Для добаления отеля введите его название и город таким образом:" +
                            " название/город, например Radisson/Kiev" +
                            "\nлибо: 9- предыдущее меню, 0- выход из режима администрирования");
                    String choice2 = br.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    String s[] = choice2.split("/");
                    if (s.length != 2) {
                        System.out.println("Некорректный ввод!");
                        continue;
                    }
                    Hotel hotel = controller.addHotel(new Hotel(0, s[0].trim(), s[1].trim()));
                    if (hotel != null) System.out.println("Отель добавлен:" + hotel);
                    System.out.println("Продолжить добавление отелей?");
                }
            }

            // 11- добавление комнат
            if (choice1.equals("11")) {
                while (true) {
                    System.out.println("Для добавления комнаты введите данные в таком формате:" +
                            " id отеля/кол-во мест/цена, например 999/2/450, " +
                            "\nлибо: 9- предыдущее меню, 0- выход из режима администрирования");
                    String choice2 = br.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    String s[] = choice2.split("/");
                    if (s.length !=3) {System.out.println("Некорректный ввод!"); continue;}
                    long hotelId; int persons; int price;
                    try {
                        hotelId = Long.parseLong(s[0].trim());
                        persons = Integer.parseInt(s[1].trim());
                        price = Integer.parseInt(s[2].trim());
                    }catch (NumberFormatException e){
                        System.out.println("Некорректный ввод! Вводите целые числа");
                        continue;
                    }
                    Hotel hotel = controller.findHotelById(hotelId);
                    if (hotel == null) continue;
                    Room room = controller.addRoom(new Room(0, price, persons, hotel));
                    if (room != null) System.out.println("Комната добавлена:" + room);
                    System.out.println("Продолжить добавление комнат?");
                }
            }

            System.out.println("Некорректный ввод! \n");
        }
    }
}