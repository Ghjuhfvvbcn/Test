package utils;

import classes.Coordinates;
import classes.MusicBand;
import classes.MusicGenre;
import classes.Studio;
import commands.Command;
import commands.CommandWithArgument;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Представляет класс для чтения объектов {@link classes.MusicBand} и {@link commands.Command}.
 */
public class Console {
    /**
     * Хранит список названий доступных команд.
     */
    private static ArrayList<String> commands = new ArrayList<>(Arrays.asList(
            "help",
            "info",
            "show",
            "clear",
            "save",
            "exit",
            "print_ascending",
            "print_descending",
            "remove_key",
            "remove_lower_key",
            "filter_starts_with_name",
            "insert",
            "update",
            "remove_lower",
            "replace_if_lower",
            "execute_script"
    ));

    /**
     * Хранит объект типа {@link java.io.BufferedReader} для чтения данных.
     */
    private BufferedReader reader;

    /**
     * Создает объект {@link java.io.BufferedReader} для чтения данных из консоли.
     */
    public Console(){
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }
    /**
     * Создает объект {@link java.io.BufferedReader} для чтения данных из объекта типа {@link java.io.InputStream}.
     */
    public Console(InputStream inputStream){
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    /**
     * Вспомогательный класс, представляющий собой две строки (название команды и аргумент).
     */
    public static class CommandInput {
        /**
         * Хранит строку - название команды.
         */
        public final String command;
        /**
         * Хранит строку - аргумент команды.
         */
        public final String argument;

        /**
         * По двум указанным объектам типа {@link String} создает объект, хранящий команду и ее аргумент.
         * <p>
         * @param command Название команды
         * @param argument Аргумент команды
         */
        public CommandInput(String command, String argument) {
            this.command = command;
            this.argument = argument;
        }
    }

    /**
     * Парсит название команды и ее аргумент из одной строки.
     * Если аргумент не указан - полю argument объекта {@link CommandInput} присваивается значение {@code null}.
     * <p>
     * @param s Строка формата {"название команды" "аргумент"} или {"название команды"}
     * @return Объект типа {@link CommandInput}
     */
    public static CommandInput parseCommand(String s){
        String[] parts = s.trim().split("\\s+", 2);
        return new CommandInput(
                parts[0],
                parts.length > 1 ? parts[1] : null
        );
    }

    /**
     * С помощью {@link Console#reader} считывает команду и аргумент (если есть).
     * @return Объект {@link CommandInput}
     */
    public CommandInput readCommand(){
        String s = null;
        try{
            s = reader.readLine();
        }catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }
        if(s == null){
            return null;
        }
        String[] parts = s.trim().split("\\s+", 2);
        return new CommandInput(
                parts[0],
                parts.length > 1 ? parts[1] : null
        );
    }

    /**
     * Принимает строку и проверяет, доступна ли команда с таким названием.
     * <p>
     * @param command Проверяемое название команды.
     * @return true если команда с указанным названием доступна
     */
    public static boolean isValidCommand(String command){
        return commands.contains(command);
    }

    /**
     * Принимает команду и проверяет, имеет ли указанная команда аргумент.
     * <p>
     * @param command Команда для проверки на наличие аргумента.
     * @return true если указанная команда имеет аргумент
     */
    public static boolean isCommandWithArgument(Command command){
        return command instanceof CommandWithArgument;
    }

    /**
     * С помощью {@link Console#reader} считывает объект типа {@link classes.MusicBand}.
     * @apiNote Для чтения полей объекта используются методы:
     * <ul>
     *     <li>{@link Console#readMusicBandName(String, String)}</li>
     *     <li>{@link Console#readCoordinates()}</li>
     *     <li>{@link Console#readNumberOfParticipants(String, String)}</li>
     *     <li>{@link Console#readDescription(String, String)}</li>
     *     <li>{@link Console#readMusicGenre()}</li>
     *     <li>{@link Console#readStudio()}</li>
     * </ul>
     * @return Созданный по указанным параметрам объект {@link classes.MusicBand} или {@code null} если один из переданных параметров не соответствует ограничениям.
     * Завершает программу в случае обнаружения конца ввода
     */
    public MusicBand readMusicBand(){
        try {
            String name = readMusicBandName("Enter the name of the music band (cannot be empty string): ", "The name of the music band cannot be empty. Please enter again: ");
            Coordinates coordinates = readCoordinates();
            int numberOfParticipants = readNumberOfParticipants("Enter number of participants (should be positive integer value): ", "The number of participants should be positive integer value. Please enter again: ");
            String description = readDescription("Enter the description of the music band (cannot be empty): ", "The description cannot be empty string. Please enter again: ");
            MusicGenre genre = readMusicGenre();
            Studio studio = readStudio();

            MusicBand musicBand = new MusicBand(name, coordinates, numberOfParticipants, description, genre, studio);
            return musicBand;
        }catch(EOFException e){
            System.out.println("Shutting down...");
            System.exit(0);
        }
        return null;
    }

    /**
     * С помощью {@link Console#reader} считывает объект типа {@link classes.MusicBand} из файла со скриптом.
     * @return Созданный по указанным параметрам объект {@link classes.MusicBand} или {@code null} если один из переданных параметров не соответствует ограничениям.
     */
    public MusicBand readMusicBandFromScript(){
        try{
            Double x;
            Integer y;
            int numberOfParticipants;
            MusicGenre genre;

            String name = read().trim();
            if(name.isEmpty()){
                throw new IllegalArgumentException("Name cannot be empty");
            }
            try{
                x = Double.parseDouble(read().trim());
            }catch (NumberFormatException e){
                throw new IllegalArgumentException("Coordinate X should be Double, not null value");
            }
            try{
                y = Integer.parseInt(read().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Coordinate Y should be integer, not null value");
            }
            try{
                numberOfParticipants = Integer.parseInt(read().trim());
                if(numberOfParticipants <= 0){
                    throw new NumberFormatException();
                }
            }catch (NumberFormatException e){
                throw new IllegalArgumentException("Number of participants should be positive, integer value");
            }
            String description = read().trim();
            if(description.isEmpty()){
                throw new IllegalArgumentException("Description cannot be empty");
            }
            try{
                genre = MusicGenre.valueOf(read().trim());
            }catch (IllegalArgumentException e){
                throw new IllegalArgumentException(
                        String.format("Music genre should be one of: %s", Arrays.toString(MusicGenre.values()))
                );
            }
            String studioName = read().trim();
            if(studioName.isEmpty()){
                throw new IllegalArgumentException("Name of studio cannot be empty");
            }
            Coordinates coordinates = new Coordinates(x, y);
            Studio studio = new Studio(studioName);

            MusicBand musicBand = new MusicBand(name, coordinates, numberOfParticipants, description, genre, studio);
            return musicBand;

        }catch(IOException e){
            System.err.println("IO error in the process of reading an object: " + e.getMessage());
        }catch(IllegalArgumentException e){
            System.out.println("Error in the process of reading an object: " + e.getMessage());
        }
        return null;
    }

    /**
     * С помощью {@link Console#reader} считывает строку.
     * <p>
     * @return Считанную строку
     * @throws IOException если источник, из которого происходит чтение недоступен для чтения
     */
    public String read() throws IOException{
        String s = reader.readLine();
        if(s == null){
            throw new IllegalArgumentException("Unexpected end of file");
        }else{
            return s;
        }
    }

    /**
     * С помощью {@link Console#reader} считывает строку.
     * <p>
     * @return Считанную строку
     * @throws IOException если источник, из которого происходит чтение недоступен для чтения
     */
    public String readLine() throws IOException{
        String s = reader.readLine();
        return s;
    }

    /**
     * С помощью {@link Console#reader} считывает название музыкальной группы предварительно выводя запрос ввода.
     * <p>
     * @param s1 Приглашение для ввода
     * @param s2 Приглашение для повторного ввода
     * @return Название музыкальной группы
     * @throws EOFException если источник, из которого происходит чтение недоступен для чтения
     */
    public String readMusicBandName(String s1, String s2) throws EOFException {
        String s;
        String name;
        System.out.print(s1);
        while (true) {
            try {
                s = reader.readLine();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                return null;
            }
            if (s == null) throw new EOFException();
            if (!s.trim().isEmpty()) {
                name = s;
                break;
            }
            System.out.print(s2);
        }
        return name;
    }

    /**
     * Считывает объект типа {@link classes.Coordinates}.
     * @apiNote Для чтения использует методы:
     * <ul>
     *     <li>{@link Console#readCoordinateX(String, String)}</li>
     *     <li>{@link Console#readCoordinateY(String, String)} o}</li>
     * </ul>
     * <p>
     * @return Объект {@link classes.Coordinates}
     * @throws EOFException если источник, из которого происходит чтение недоступен для чтения
     */
    public Coordinates readCoordinates() throws EOFException{
        Double x = readCoordinateX("Enter the x coordinate (Double, not null value): ", "The x coordinate should be Double, not null value. Please enter again: ");
        Integer y = readCoordinateY("Enter the y coordinate (should be integer value, cannot be null): ", "The y coordinate should be Integer, not null value. Please enter again: ");
        return new Coordinates(x, y);
    }

    /**
     * С помощью {@link Console#reader} считывает значение поля x объекта {@link classes.Coordinates}. Предварительно выводится приглашение для ввода.
     * <p>
     * @param s1 Приглашение для ввода
     * @param s2 Приглашение для повторного ввода
     * @return Значение поля x объекта {@link classes.Coordinates}
     * @throws EOFException если источник, из которого происходит чтение недоступен для чтения
     */
    public Double readCoordinateX(String s1, String s2) throws EOFException{
        String s;
        Double x;
        System.out.print(s1);
        while(true) {
            try {
                s = reader.readLine();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                return null;
            }
            if(s == null){
                throw new EOFException();
            }
            if(!s.trim().isEmpty()){
                try{
                    x = Double.parseDouble(s);
                    break;
                }catch (NumberFormatException e){
                    System.out.print(s2);
                }
            }else{
                System.out.print(s2);
            }
        }
        return x;
    }

    /**
     * С помощью {@link Console#reader} считывает значение поля y объекта {@link classes.Coordinates}. Предварительно выводится приглашение для ввода.
     * <p>
     * @param s1 Приглашение для ввода
     * @param s2 Приглашение для повторного ввода
     * @return Значение поля y объекта {@link classes.Coordinates}
     * @throws EOFException если источник, из которого происходит чтение недоступен для чтения
     */
    public Integer readCoordinateY(String s1, String s2) throws EOFException{
        String s;
        Integer y;
        System.out.print(s1);
        while(true) {
            try{
                s = reader.readLine();
            }catch (IOException e){
                System.err.println("Error: " + e.getMessage());
                return null;
            }
            if(s == null){
                throw new EOFException();
            }
            if(!s.trim().isEmpty()){
                try{
                    y = Integer.parseInt(s);
                    break;
                }catch (NumberFormatException e){
                    System.out.print(s2);
                }
            }else{
                System.out.print(s2);
            }
        }
        return y;
    }

    /**
     * С помощью {@link Console#reader} считывает значение поля numberOfParticipants объекта {@link classes.MusicBand}. Предварительно выводится приглашение для ввода.
     * <p>
     * @param s1 Приглашение для ввода
     * @param s2 Приглашение для повторного ввода
     * @return Значение поля numberOfParticipants объекта {@link classes.MusicBand}
     * @throws EOFException если источник, из которого происходит чтение недоступен для чтения
     */
    public int readNumberOfParticipants(String s1, String s2) throws EOFException{
        String s;
        int numberOfParticipants;
        System.out.print(s1);
        while(true){
            try{
                s = reader.readLine();
            }catch (IOException e){
                System.err.println("Error: " + e.getMessage());
                return 0;
            }
            if(s == null){
                throw new EOFException();
            }
            if(!s.trim().isEmpty()){
                try{
                    numberOfParticipants = Integer.parseInt(s);
                    if(numberOfParticipants <= 0){
                        throw new NumberFormatException();
                    }
                    break;
                }catch(NumberFormatException e){
                    System.out.print(s2);
                }
            }else{
                System.out.print(s2);
            }
        }
        return numberOfParticipants;
    }

    /**
     * С помощью {@link Console#reader} считывает значение поля description объекта {@link classes.MusicBand}. Предварительно выводится приглашение для ввода.
     * <p>
     * @param s1 Приглашение для ввода
     * @param s2 Приглашение для повторного ввода
     * @return Значение поля description объекта {@link classes.MusicBand}
     * @throws EOFException если источник, из которого происходит чтение недоступен для чтения
     */
    public String readDescription(String s1, String s2) throws EOFException{
        String s;
        String description;
        System.out.print(s1);
        while(true){
            try{
                s = reader.readLine();
            }catch (IOException e){
                System.err.println("Error: " + e.getMessage());
                return null;
            }
            if(s == null){
                throw new EOFException();
            }
            if(!s.trim().isEmpty()){
                description = s;
                break;
            }
            System.out.print(s2);
        }
        return description;
    }

    /**
     * С помощью {@link Console#reader} считывает значение поля genre объекта {@link classes.MusicBand}. Предварительно выводится приглашение для ввода.
     * <p>
     * @return Значение поля genre объекта {@link classes.MusicBand}
     * @throws EOFException если источник, из которого происходит чтение недоступен для чтения
     */
    public MusicGenre readMusicGenre() throws  EOFException{
        String s;
        MusicGenre genre;
        System.out.printf("Enter the music genre of the music band. Here the valid values: %s: ", Arrays.toString(MusicGenre.values()));
        while(true){
            try{
                s = reader.readLine();
            }catch(IOException e){
                System.err.println("Error: " + e.getMessage());
                return null;
            }
            if(s == null){
                throw new EOFException();
            }
            if(!s.trim().isEmpty()){
                try{
                    genre = MusicGenre.valueOf(s);
                    break;
                }catch(IllegalArgumentException e){
                    System.out.printf("There is no genre '%s'. Please enter again. Here the valid values: %s: ", s, Arrays.toString(MusicGenre.values()));
                }
            }else{
                System.out.printf("The genre cannot be empty string. Please enter again. Here the valid values: %s: ", Arrays.toString(MusicGenre.values()));
            }
        }
        return genre;
    }

    /**
     * Считывает объект типа {@link classes.Studio}.
     * @apiNote Для чтения использует методы:
     * <ul>
     *     <li>{@link Console#readStudio()}</li>
     * </ul>
     * <p>
     * @return Объект {@link classes.Studio}
     * @throws EOFException если источник, из которого происходит чтение недоступен для чтения
     */
    public Studio readStudio() throws EOFException{
        String studioName = readStudioName("Enter the name of studio (not empty string): ", "The name of studio cannot be empty. Please enter again: ");
        return new Studio(studioName);
    }

    /**
     * С помощью {@link Console#reader} считывает значение поля name объекта {@link classes.Studio}. Предварительно выводится приглашение для ввода.
     * <p>
     * @param s1 Приглашение для ввода
     * @param s2 Приглашение для повторного ввода
     * @return значение поля name объекта {@link classes.Studio}
     * @throws EOFException если источник, из которого происходит чтение недоступен для чтения
     */
    public String readStudioName(String s1, String s2) throws EOFException{
        String s;
        String studioName;
        System.out.print(s1);
        while(true){
            try{
                s = reader.readLine();
            }catch (IOException e){
                System.err.println("Error: " + e.getMessage());
                return null;
            }
            if(s == null){
                throw new EOFException();
            }
            if(!s.trim().isEmpty()){
                studioName = s;
                break;
            }else{
                System.out.print(s2);
            }
        }
        return studioName;
    }
}