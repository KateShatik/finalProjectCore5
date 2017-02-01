import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {

    public static void main(String[] args) throws IOException {
        Controller controller = new Controller();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        first:
        while (true) {
            System.out.println("1- поиск номеров, 2- поиск отеля по названию, 3- поиск отеля по городу, \n" +
                    "Авторизируйтесь для бронирования: 4- войти, 5- зарегистрироваться. 0- завершить работу");
            String choice1 = bufferedReader.readLine();

            if (choice1.equals("0")) break;

            if (choice1.equals("1")){
                while (true) {
                    System.out.println("Для поиска номеров, введите параметры разделяя их символом / в таком формате:" +
                            " город/название отеля/кол-во персон/цена от/цена до, образец: Киев/Десна/2/200/350 \n" +
                            "если параметр Вам не важен - поставьте вместо него символ *.   А если передумали искать, то: " +
                            " 9- главное меню; 0- завершить работу");
                    String choice4 = bufferedReader.readLine();
                    if (choice4.equals("0")) break first;
                    if (choice4.equals("9")) continue first;
                    String s[] = choice4.split("/");
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
                    System.out.println("Авторизируйтесь в главном меню для бронирования. \nПовторить поиск?");
                }
            }

            if (choice1.equals("2")) {
                while (true){
                    System.out.println("Введите название отеля, или его часть;" +
                            " получить весь список отелей- просто нажмите Enter; 9- предыдущее меню; 0- завершить работу");
                    String choice4 = bufferedReader.readLine();
                    if (choice4.equals("0")) break first;
                    if (choice4.equals("9")) continue first;
                    controller.findHotelByName(choice4.trim()).forEach(System.out::println);
                    System.out.println("Продолжить поиск?");
                }
            }
            /*поиск отеля по городу*/
            if (choice1.equals("3")) {
                while (true) {
                    System.out.println("Введите название города, или его часть;" +
                            " получить весь список отелей- просто нажмите Enter; 9- предыдущее меню; 0- завершить работу");
                    String choice2 = bufferedReader.readLine();
                    if (choice2.equals("0")) break first;
                    if (choice2.equals("9")) continue first;
                    controller.findHotelByCity(choice2.trim()).forEach(System.out::println);
                    System.out.println("Продолжить поиск?");

                }
            }

            //Вход
            if (choice1.equals("4")) {
                User user;
                while (true){
                    System.out.println("Вход. Введите имя и пароль через разделитель /, например Vova/12345 ;" +
                            " либо: 9- выйти в главное меню, 0- завершить работу");
                    String choice2 = bufferedReader.readLine();
                    if (choice2.equals("root/777")){
                        Admin.root();
                        continue first;
                    }
                    if (choice2.length() == 1) {
                        if (choice2.equals("0")) break first;
                        if (choice2.equals("9")) continue first;
                    }
                    String nameAndPass[] = choice2.split("/");
                    if (nameAndPass.length != 2) {
                        System.out.println("Некорректный ввод");
                        continue;
                    }
                    user = controller.enter(nameAndPass[0].toLowerCase().trim(), nameAndPass[1].trim());

                    //юзер вошел под своим именем
                    if (user != null) {
                        third:
                        while (true){
                            System.out.println("\n1- поиск номеров/бронирование, 2- поиск отеля по названию," +
                                    " 3- поиск отеля по городу," +
                                    "\n5- список забронированных Вами номеров/снятие брони," +
                                    "\n7- редактировать имя/пароль, 9- выход из учетной записи, 0- завершить работу");
                            String choice3 = bufferedReader.readLine();
                            if (choice3.equals("0")) break first;
                            if (choice3.equals("9")) { user = null; continue first;}

                            //редактировать имя/пароль
                            if (choice3.equals("7")) {
                                while (true) {
                                    System.out.println("Для начала редактирования имени и пароля ведите текущие имя " +
                                            "и пароль через разделитель /, например Vova/12345 ; либо: 1- отмена, 0- завершить работу");
                                    String choice4 = bufferedReader.readLine();
                                    if (choice4.equals("0")) break first;
                                    if (choice4.equals("1")) continue third;
                                    String split[] = choice4.split("/");
                                    if (split.length != 2) {
                                        System.out.println("Некорректный ввод");
                                        continue;
                                    }
                                    if (split[0].trim().trim().equals(user.getName().toLowerCase())
                                            && split[1].trim().equals(user.getPassword())){

                                        while (true) {
                                            System.out.println("Введите новое имя и пароль через разделитель /," +
                                                    " например Vova/12345 ; либо: 1- отмена, 0- завершить работу");
                                            String choice5 = bufferedReader.readLine();
                                            if (choice5.equals("0")) break first;
                                            if (choice5.equals("1")) continue third;
                                            String splitNewNameAndPass[] = choice5.split("/");
                                            if (splitNewNameAndPass.length != 2){
                                                System.out.println("Некорректный ввод");
                                                continue;
                                            }
                                            if (controller.editUser(new User(user.getId(), splitNewNameAndPass[0].trim(),
                                                    splitNewNameAndPass[1].trim()))) {
                                                System.out.println("Профиль успешно изменен. Ваш текущий профиль: " + user);
                                                continue third;
                                            }
                                            else {
                                                System.out.println("Что-то пошло не так! Попробуйте в другой раз.");
                                                continue third;
                                            }
                                        }
                                    }
                                    System.out.println("Неверный логин или пароль!");
                                    user = null;
                                    continue first;
                                }
                            }

                            /*поиск отеля по названию*/
                            if (choice3.equals("2")) {
                                while (true){
                                    System.out.println("Введите название отеля, или его часть;" +
                                            " получить весь список отелей- просто нажмите Enter;" +
                                            " 9- предыдущее меню; 0- завершить работу");
                                    String choice4 = bufferedReader.readLine();
                                    if (choice4.equals("0")) break first;
                                    if (choice4.equals("9")) continue third;
                                    controller.findHotelByName(choice4.trim()).forEach(System.out::println);
                                    System.out.println("Продолжить поиск?");
                                }
                            }

                            /*поиск отеля по городу*/
                            if (choice3.equals("3")) {
                                while (true) {
                                    System.out.println("Введите название города, или его часть;" +
                                            " получить весь список отелей- просто нажмите Enter;" +
                                            " 9- предыдущее меню; 0- завершить работу");
                                    String choice4 = bufferedReader.readLine();
                                    if (choice4.equals("0")) break first;
                                    if (choice4.equals("9")) continue third;
                                    controller.findHotelByCity(choice4.trim()).forEach(System.out::println);
                                    System.out.println("Продолжить поиск?");
                                }
                            }

                            //отмена бронирования
                            if (choice3.equals("5")) {
                                while (true) {
                                    System.out.println("Забронированные Вами номера:");
                                    List<Room> booked = controller.bookedByUser(user);
                                    if (booked.size() == 0 ) continue third;
                                    booked.forEach(System.out::println);
                                    System.out.println("\nДля снятия брони введите id номера и id отеля через разделитель /," +
                                            " например 5555/7777; 9- предыдущее меню; 0- завершить работу");
                                    String choice4 = bufferedReader.readLine();
                                    if (choice4.equals("0")) break first;
                                    if (choice4.equals("9")) continue third;
                                    String s[] = choice4.split("/");
                                    if (s.length != 2) {System.out.println("Некорректный ввод id"); continue;}
                                    try {
                                        long roomId = Long.parseLong(s[0].trim());
                                        long hotelId = Long.parseLong(s[1].trim());
                                        controller.cancelReservation(roomId, user.getId(), hotelId);
                                    } catch (NumberFormatException e){
                                        System.out.println("Надо вводить целые числа!");
                                    }
                                }
                            }

                            //поиск номеров
                            if (choice3.equals("1")){
                                second:
                                while (true) {
                                    System.out.println("Для поиска номеров, введите параметры разделяя их символом / в таком формате:" +
                                            " город/название отеля/кол-во персон/цена от/цена до, образец: Киев/Десна/2/200/350 \n" +
                                            "если параметр Вам не важен - поставьте вместо него символ *.   А если передумали искать, то: " +
                                            " 9- предыдущее меню; 0- завершить работу");
                                    String choice4 = bufferedReader.readLine();
                                    if (choice4.equals("0")) break first;
                                    if (choice4.equals("9")) continue third;
                                    String s[] = choice4.split("/");
                                    if (s.length !=5) {System.out.println("Некорректный ввод!"); continue;}
                                    Map<String,String> map = new HashMap<>();
                                    map.put(Controller.CITY, s[0].trim());
                                    map.put(Controller.HOTEL_NAME, s[1].trim());
                                    map.put(Controller.PERSONS, s[2].trim());
                                    map.put(Controller.MIN_PRICE, s[3].trim());
                                    map.put(Controller.MAX_PRICE, s[4].trim());
                                    List<Room> rooms = controller.findRoom(map);
                                    if (rooms.size() == 0) {
                                        System.out.println("Ничего не найдено. Возможно параметры поиска слишком строгие." +
                                                " Повторить поиск?");
                                        continue ;
                                    }
                                    rooms.forEach(System.out::println);
                                    System.out.println();
                                    forth:
                                    while (true){
                                        System.out.println("1- повторить поиск, 2- бронировать, 9- главное меню, 0- выход");
                                        String choice5 = bufferedReader.readLine();
                                        if (choice5.equals("0")) break first;
                                        if (choice5.equals("9")) continue third;
                                        if (choice5.equals("1")) break ;
                                        if (choice5.equals("2")){
                                            while (true) {
                                                System.out.println("Для бронирования введите id номера и id отеля через" +
                                                        " разделитель /, например 5555/7777; 9- главное меню; 0- завершить работу");
                                                String choice6 = bufferedReader.readLine();
                                                if (choice6.equals("0")) break first;
                                                if (choice6.equals("9")) continue third;
                                                String s1[] = choice6.split("/");
                                                if (s1.length != 2) {System.out.println("Некорректный ввод id"); continue;}
                                                try {
                                                    long roomId = Long.parseLong(s1[0].trim());
                                                    long hotelId = Long.parseLong(s1[1].trim());
                                                    if (controller.bookRoom(roomId, user.getId(), hotelId))  continue forth;
                                                } catch (NumberFormatException e){
                                                    System.out.println("Надо вводить целые числа!");
                                                }
                                            }

                                        }
                                        System.out.println("Некорректный ввод!");

                                    }
                                }
                            }

                            System.out.println("Некорректный ввод");
                        }
                    }//юзер под своим именем тут заканчивается
                }

            }

            //регистрация
            if (choice1.equals("5")) {
                while (true) {
                    System.out.println("Регистрация. Введите имя и пароль через разделитель /, например Vova/12345 ;" +
                            "  либо: 1- выйти в главное меню, 0- завершить работу");
                    String choice2 = bufferedReader.readLine();
                    if (choice2.length() == 1) {
                        if (choice2.equals("0")) break first;
                        if (choice2.equals("1")) continue first;
                    }
                    String nameAndPass[] = choice2.split("/");
                    if (nameAndPass.length != 2) {
                        System.out.println("Некорректный ввод");
                        continue;
                    }
                    boolean b = controller.registerUser(new User(0, nameAndPass[0].trim(), nameAndPass[1].trim()));
                    if (b) continue first;
                }
            }

            System.out.println("Некорректный ввод");
        }

        System.out.println("До встречи!");
        bufferedReader.close();
    }
}