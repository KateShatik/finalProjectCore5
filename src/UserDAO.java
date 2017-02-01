import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class UserDAO implements DAO<User> {
    private static UserDAO userDAO;
    private List<User> usersBase = new ArrayList<>();
    private File file;


    //Singleton
    public static UserDAO getUserDAO (){
        if (userDAO == null) userDAO = new UserDAO();
        return userDAO;
    }


    private UserDAO() {
        //создаем файл для хранения пользователей
        try {
            file = new File("src/finalProjectCore/userBase.txt");
            if(file.createNewFile()) System.out.println("Файл базы создан!");
        } catch (IOException e) {
            System.out.println("Не далось создать базу!");
        }

        // формируем коллекцию пользователей из файла при создании єкз. UserDAO()
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            if (br.readLine()==null)
                System.out.println("База пользователей пуста!");
            else {
                Stream<String> streamFromFiles = Files.lines(Paths.get(file.getAbsolutePath()));
                streamFromFiles.forEach(line -> {
                    String fields[] = line.split(" ");

                    if (fields.length != 3) throw new RuntimeException("База юзеров повреждена!");
                    usersBase.add(new User(Long.parseLong(fields[0]), fields[1], fields[2]));
                });}
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException("База юзеров повреждена!");

        }

    }

    // метод заносит информацию в файл txt
    private boolean writerToFile(File file, List<User> list){
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(file)))){
            for (User user : list) {

                bufferedWriter.write(user.getId()+" ");
                bufferedWriter.write(user.getName()+" ");
                bufferedWriter.write(user.getPassword()+System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Не удалось сохранить данные в базу!");
            return false;
        }
        return true;
    }

    // проверка на вилидность данных
    private boolean validInspect (User user){
        boolean notValid = false;
        if ((user.getName() == null || user.getName().equals("")) ||
                (user.getPassword() == null || user.getPassword().equals("")))
            notValid = true;
        return notValid;
    }


    @Override
    public boolean add(User user) {
        try {
            if (validInspect(user)) {
                System.out.println("Поле имя и пароль должны быть заполнены!");
                return false;}
            else {
                if (usersBase.stream().anyMatch(u -> (u.getId() ==
                        user.getId())||u.getName().toLowerCase().equals(user.getName().toLowerCase()))) {
                    System.out.println("Пользователь уже существует!");
                    return false;
                } else {
                    usersBase.add(user);
                    writerToFile(file, usersBase);
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Внесите корректную информацию о новом пользователе!");
            return false;
        }
        System.out.println("Пользователь добавлен в базу");
        return true;
    }



    @Override
    public boolean edit(User user) {

        try {
            User userToEdit = usersBase.stream()
                    .filter(user1 -> user1.getId() == user.getId())
                    .findAny()
                    .get();
            if (validInspect(user)) {
                System.out.println("Поле имя и пароль должны быть заполнены!");
                return false;
            } else {
                userToEdit.setName(user.getName());
                userToEdit.setPassword(user.getPassword());
                writerToFile(file,usersBase);
            }
        } catch (NoSuchElementException e) {
            System.out.printf("Пользователя с ID %d нет в базе." + "\n", user.getId());
            return false;
        } catch (NullPointerException e){
            System.out.println("Внесите корректную информацию о пользователе!");
            return false;
        }
        return true;
    }



    @Override
    public boolean remove(User user) {
        if (usersBase.contains(user)) {
            try{
                RoomDAO roomDAO = RoomDAO.getRoomDAO();
                roomDAO.getBase().forEach( r -> {
                    if (r.getUserReserved() != null && r.getUserReserved().equals(user)) r.setUserReserved(null);
                });
                roomDAO.writerToFile(roomDAO.getFile(),roomDAO.getBase());
                usersBase.remove(user);
                writerToFile(file,usersBase);
                return true;

            } catch (NullPointerException e){
                System.out.println("База пуста!");
            }
        }
        else System.out.println("Такого пользователя нет в базе данных!");
        return false;
    }

    @Override
    public  List<User> getBase() {
        return usersBase;
    }

}