import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class HotelDAO implements DAO<Hotel> {

    private static HotelDAO hotelDAO;
    private List<Hotel> hotelList = new ArrayList<>();
    private File file;

    public static HotelDAO getHotelDAO (){
        if (hotelDAO == null) hotelDAO = new HotelDAO();
        return hotelDAO;
    }

    private HotelDAO() {
        //создаем файл для хранения отелей
        try {
            file = new File("src/finalProjectCore/hotelBase.txt");
            if(file.createNewFile()) System.out.println("Файл базы создан!");
        } catch (IOException e) {
            System.out.println("Не далось создать базу!");
        }
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            if (br.readLine() == null)
                System.out.println("База отелей пуста!");
            else {
                Stream<String> streamFromFiles = Files.lines(Paths.get(file.getAbsolutePath()));
                streamFromFiles.forEach(line -> {
                    String fields[] = line.split("@");
                    if (fields.length != 3) throw new RuntimeException("База отелей повреждена");
                    if (hotelList.stream().anyMatch(hotel -> hotel.getId() ==
                            Long.parseLong(fields[0]))) throw new RuntimeException("База отелей повреждена");
                    hotelList.add(new Hotel(Long.parseLong(fields[0]), fields[1], fields[2]));

                });
                streamFromFiles.close();}
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException("База отелей повреждена");
        }
    }

    @Override
    public boolean add(Hotel hotel) {

        try {
            if (validInspect(hotel)) {
                System.out.println("Поле ID, название отеля и город должны быть заполнены!");
                return false;}
            else {
                if (hotelList.stream().anyMatch(hotelFromBase -> hotelFromBase.getId() == hotel.getId()))  {
                    System.out.println("Отель с таким ID уже существует!");
                    return false;
                }
                if (hotelList.stream().anyMatch(hotel1 ->
                        ( hotel1.getCity().toLowerCase().trim().equals(hotel.getCity().toLowerCase().trim())
                                && hotel1.getName().trim().toLowerCase().equals(hotel.getName().trim().toLowerCase())))){
                    System.out.println("Мы добавляем отель, но хотим обратить Ваше внимание, " +
                            "в базе уже был отель в этом городе именем!");

                }
                hotelList.add(hotel);
                writerToFile(file,hotelList);
            }
        } catch (NullPointerException e) {
            System.out.println("Внесите корректную информацию о новой комнате!");
            return false;
        }
        return true;
    }

    @Override
    public boolean edit(Hotel hotel) {

        try {
            if (validInspect(hotel)) {
                System.out.println("Поле ID, название отеля и город должны быть заполнены!");
                return false;
            } else {
                Hotel oldHotel = hotelDAO.getBase().stream()
                        .filter(hotel1 -> hotel1.getId() == hotel.getId())
                        .findAny()
                        .get();

                oldHotel.setCity(hotel.getCity());
                oldHotel.setName(hotel.getName());
                writerToFile(file, hotelList);
            }
        } catch (NoSuchElementException e) {
            System.out.printf("Отель с ID %d нет в базе." + "\n", hotel.getId());
            return false;
        } catch (NullPointerException e){
            System.out.println("Внесите корректную информацию об отеле!");
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(Hotel hotel) {
        if (!hotelList.contains(hotel)) {
            System.out.println("Такого отеля нет в базе данных!");
            return false;
        }else{
            try {
                if (hotelList.stream()
                        .anyMatch(h -> h.getId() == hotel.getId())) {

                    RoomDAO roomDAO = RoomDAO.getRoomDAO();
                    for (int i = roomDAO.getBase().size()-1; i >=0 ; i--) {
                        if (roomDAO.getBase().get(i).getHotel().getId()==hotel.getId()) {
                            roomDAO.remove(roomDAO.getBase().get(i));
                        }
                    }
                    hotelList.remove(hotel);
                    writerToFile(file, hotelList);
                }
            } catch (NullPointerException e) {
                System.out.println("База пуста или такого отеля нет в базе данных!");
                return false;
            }
            return true;
        }

    }
    // проверка на валидность
    private boolean validInspect (Hotel hotel){
        boolean notValid = false;
        if ((hotel.getId() == 0) || (hotel.getCity()==null||hotel.getCity().equals("")) ||
                (hotel.getName()==null||hotel.getName().equals("")))
            notValid = true;
        return notValid;
    }



    private boolean writerToFile (File file, List<Hotel> list){
        StringBuilder stringBuilder = new StringBuilder();
        list.stream().forEach(hotelFromList -> stringBuilder.append(hotelFromList.getId() + "@"
                + hotelFromList.getName() + "@" + hotelFromList.getCity() + "\n"));

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))){
            bufferedWriter.append(stringBuilder);
            bufferedWriter.flush();

        } catch (IOException e) {
            System.out.println("Не удалось сохранить данные в базу!");
            return false;
        }
        return true;
    }

    @Override
    public List<Hotel> getBase() {
        return hotelList;
    }

}