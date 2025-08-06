package commands;

import classes.MusicBand;
import utils.*;
import utils.Console;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static classes.MusicBand.compareByDateAndName;


/**
 * Класс для работы с коллекцией объектов типа {@link classes.MusicBand}.
 * <p>
 * @see classes.MusicBand
 * @see utils.CommandMap
 * @see utils.Console
 */
public class Executor {
    /**
     * Хранит коллекцию типа {@link java.util.TreeMap} значений типа {@link classes.MusicBand}.
     */
    private TreeMap<Long, MusicBand> musicBands;
    /**
     * Хранит коллекцию типа {@link java.util.TreeMap}. Ключ - имя команды, значение - команда.
     */
    private final Map<String, Command> commands = CommandMap.createMapWithCommands(this);
    /**
     * Хранит дату создания коллекции.
     */
    private final ZonedDateTime initializationDate;
    /**
     * Хранит ссылку на CSV-файл с коллекцией.
     */
    private final File file_csv;
    /**
     * Хранит ссылку на файл со скриптом.
     */
    private final File file_script;
    /**
     * Хранит объект типа {@link utils.Console} для чтения команд из консоли.
     */
    private final Console console = new Console();
    /**
     * Хранит объект типа {@link utils.Console} для чтения команд из скрипта.
     */
    private Console consoleScript;

    /**
     * Создает объект типа {@link Executor} по указанным параметрам.
     * Присваивает значение переменной initializationDate.
     * Считывает данные из файла file_csv и сохраняет их в переменную musicBands.
     * <p>
     * @param file_csv Ссылка типа {@link java.io.File} на CSV-файл для хранения коллекции.
     * @param file_script Ссылка типа {@link java.io.File} на файл, содержащий скрипт.
     */
    public Executor(File file_csv, File file_script){
        this.file_csv = file_csv;
        this.file_script = file_script;
        initializationDate = ZonedDateTime.now();
        musicBands = ReaderCSV.loadFromFile(file_csv);
    }

    /**
     * Присваивает значение переменной consoleScript.
     * <p>
     * @throws FileNotFoundException если файл file_script не найден или недоступен для чтения
     */
    public void setConsoleScript() throws FileNotFoundException{
        consoleScript = new Console(new FileInputStream(file_script));
    }

    /**
     * Выводит на консоль список доступных команд.
     */
    public void help(){
        System.out.println("-help : вывести справку по доступным командам\n" +
                "-info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "-show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "-insert null {element} : добавить новый элемент с заданным ключом\n" +
                "-update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                "-remove_key null : удалить элемент из коллекции по его ключу\n" +
                "-clear : очистить коллекцию\n" +
                "-save : сохранить коллекцию в файл\n" +
                "-execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                "-exit : завершить программу (без сохранения в файл)\n" +
                "-remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный (по дате создания и имени)\n" +
                "-replace_if_lower null {element} : заменить значение по ключу, если новое значение меньше старого (по дате создания и имени)\n" +
                "-remove_lower_key null : удалить из коллекции все элементы, ключ которых меньше, чем заданный\n" +
                "-filter_starts_with_name name : вывести элементы, значение поля name которых начинается с заданной подстроки\n" +
                "-print_ascending : вывести элементы коллекции в порядке возрастания\n" +
                "-print_descending : вывести элементы коллекции в порядке убывания");
    }

    /**
     * Выводит на консоль информацию о коллекции musicBands.
     */
    public void info() {
        if(musicBands == null){
            System.out.println("The collection is 'null'");
        }
        else if(!musicBands.isEmpty()) {
            System.out.printf("Type: TreeMap<Long, MusicBand>\n" +
                    "Initialization date: %s\n" +
                    "Size of collection: %d\n" +
                    "First key: %d\n" +
                    "Last key: %d\n", initializationDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss z")), musicBands.size(), musicBands.firstKey(), musicBands.lastKey());
        }else{
            System.out.printf("Type: TreeMap<Long, MusicBand>\n" +
                    "Initialization date: %s\n" +
                    "Size of collection: 0\n", initializationDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss z")));
        }
    }

    /**
     * Выводит на консоль все элементы коллекции musicBands в строковом представлении.
     */
    public void show(){
        if(musicBands == null){
            System.out.println("The collection is 'null'");
        }
        else if(!musicBands.isEmpty()) {
            System.out.printf("The collection contains %d items:\n", musicBands.size());
            for (MusicBand band : musicBands.values()) {
                System.out.println(band);
            }
        }else{
            System.out.println("The collection is empty");
        }
    }

    /**
     * Удаляет все элементы коллекции musicBands.
     */
    public void clear(){
        if(musicBands == null){
            System.out.println("The collection is 'null'");
        }
        else if(!musicBands.isEmpty()){
            int sizeBefore = musicBands.size();
            musicBands.clear();
            System.out.printf("The collection was successfully cleared. %d elements removed\n", sizeBefore);
        }else{
            System.out.println("The collection is empty");
        }
    }

    /**
     * Сохраняет все элементы коллекции musicBands в файл CSV-файл.
     */
    public void save(){
        try {
            WriterCSV.loadToFile(file_csv, musicBands);
            System.out.printf("The collection was successfully saved to the file '%s'\n", file_csv);
        }catch (IOException e){
            System.err.printf("Saving to a file failed: %s", e.getMessage());
        }
    }

    /**
     * Завершает выполнение программы.
     */
    public void exit(){
        System.out.println("Shutting down...");
        System.exit(0);
    }

    /**
     * Выводит все элементы коллекции {@link Executor#musicBands} на консоль в порядке возрастания.
     */
    public void print_ascending(){
        if(musicBands == null){
            System.out.println("The collection is 'null'");
        }else if(musicBands.isEmpty()){
            System.out.println("The collection is empty");
        }else{
            System.out.println("Collection elements in ascending order (by 'name'):");
            ArrayList<MusicBand> bands = new ArrayList<>(musicBands.values());
            Collections.sort(bands);
            for(MusicBand band : bands){
                System.out.println(band);
            }
        }
    }

    /**
     * Выводит все элементы коллекции {@link Executor#musicBands} на консоль в порядке убывания.
     */
    public void print_descending(){
        if(musicBands == null){
            System.out.println("The collection is 'null'");
        }else if(musicBands.isEmpty()){
            System.out.println("The collection is empty");
        }else{
            System.out.println("Collection elements in descending order (by 'name'):");
            ArrayList<MusicBand> bands = new ArrayList<>(musicBands.values());
            Collections.sort(bands, Collections.reverseOrder());
            for(MusicBand band : bands){
                System.out.println(band);
            }
        }
    }

    /**
     * Удаляет из коллекции {@link Executor#musicBands} элемент по указанному ключу.
     * @param key Ключ удаляемого элемента
     */
    public void remove_key(Long key){
        MusicBand band = musicBands.remove(key);
        if(band == null){
            System.out.printf("The element with the key %d to was not found\n", key);
        }else{
            System.out.printf("The item with the key %d to has been successfully deleted\n", key);
        }
    }

    /**
     * Удаляет из коллекции {@link Executor#musicBands} все элементы, ключ которых меньше, чем заданный.
     * @param key Граничное значение ключа
     */
    public void remove_lower_key(Long key){
        int sizeBefore = musicBands.size();
        musicBands.headMap(key, false).clear();
        int sizeAfter = musicBands.size();
        System.out.printf("Successfully deleted %d items\n", sizeBefore-sizeAfter);
    }

    /**
     * Выводит на консоль все элементы коллекции {@link Executor#musicBands}, значение поля name которых начинается с заданной подстроки.
     * @param name Подстрока, с которой начинаются названия искомых групп из коллекции {@link Executor#musicBands}
     */
    public void filter_starts_with_name(String name){
        ArrayList<MusicBand> bands = new ArrayList<>();
        for(MusicBand band : musicBands.values()){
            if(band.getName().startsWith(name)){
                bands.add(band);
            }
        }
        System.out.printf("Found %d music groups whose names start with \"%s\"\n", bands.size(), name);
        for(MusicBand band : bands){
            System.out.println(band);
        }
    }

    /**
     * Вставляет элемент по указанному ключу.
     * @param key Ключ для нового элемента
     */
    public void insert(Long key){
        if(musicBands.containsKey(key)){
            System.out.println("The collection already contain the key: " + key);
        }else{
            MusicBand band = console.readMusicBand();
            band.setId(key);
            musicBands.put(key, band);
            System.out.println("The music band was successfully inserted to the collection");
        }
    }

    /**
     * Заменяет в коллекции {@link Executor#musicBands} старый элемент по указанному ключу на новый.
     * @param id Идентификатор элемента, который нужно заменить
     */
    public void update(Long id){
        if(!musicBands.containsKey(id)){
            System.out.println("The collection doesn't contain the key " + id);
        }else{
            MusicBand band = console.readMusicBand();
            band.setId(id);
            musicBands.put(id, band);
            System.out.printf("The band with ID %d was successfully updated\n", id);
        }
    }

    /**
     * Удаляет из коллекции {@link Executor#musicBands} все элементы меньшие, чем заданный.
     */
    public void remove_lower(){
        if(musicBands.isEmpty()){
            System.out.println("The collection is empty");
            return;
        }
        MusicBand band = console.readMusicBand();
        Long sizeBefore = Long.valueOf(musicBands.size());
        musicBands.headMap(Long.MAX_VALUE).values().removeIf(musicBand -> compareByDateAndName.compare(musicBand, band) > 0);
        Long sizeAfter = Long.valueOf(musicBands.size());
        System.out.printf("%d bands were successfully removed\n", sizeBefore-sizeAfter);
    }

    /**
     * Заменяет элемент коллекции {@link Executor#musicBands} по ключу, если новое значение меньше старого.
     * @param key Ключ элемента, который нужно перезаписать
     */
    public void replace_if_lower(Long key){
        if(musicBands.isEmpty()){
            System.out.println("The collection is empty");
            return;
        }
        if(!musicBands.containsKey(key)){
            System.out.println("The collection doesn't contain the key " + key);
            return;
        }
        MusicBand oldBand = musicBands.get(key);
        MusicBand newBand = console.readMusicBand();
        newBand.setId(key);
        if(compareByDateAndName.compare(oldBand, newBand) > 0){
            musicBands.put(key, newBand);
            System.out.printf("The band that key is %d was successfully replaced\n", key);
        }else{
            System.out.printf("The band that key is %d wasn't replaced\n", key);
        }
    }

    /**
     * Выполняет команды из файла со скриптом.
     */
    public void execute_script(){
        try(FileInputStream fileInputStream = new FileInputStream(file_script)){
            Console consoleScript = new Console(fileInputStream);

            String s;
            while (true){
                s = consoleScript.readLine();
                if(s == null){
                    System.out.printf("Execution of the script from the '%s' file is complete\n", file_script);
                    break;
                }
                if(s.isEmpty()){
                    continue;
                }

                Console.CommandInput input = Console.parseCommand(s);
                if(!Console.isValidCommand(input.command)){
                    System.out.printf("There is no command '%s'\n", input.command);
                    continue;
                }

                if(input.command.equals("remove_lower")){
                    if(musicBands.isEmpty()){
                        System.out.println("The collection is empty");
                        continue;
                    }
                    MusicBand band = consoleScript.readMusicBandFromScript();
                    Long sizeBefore = Long.valueOf(musicBands.size());
                    musicBands.headMap(Long.MAX_VALUE).values().removeIf(musicBand -> compareByDateAndName.compare(musicBand, band) > 0);
                    Long sizeAfter = Long.valueOf(musicBands.size());
                    System.out.printf("%d bands were successfully removed\n", sizeBefore-sizeAfter);
                }

                Command command = commands.get(input.command);
                if(Console.isCommandWithArgument(command)){
                    CommandWithArgument commandWithArg = (CommandWithArgument) command;
                    try{
                        commandWithArg.setArgument(input.argument);
                    }catch (IllegalArgumentException e){
                        System.out.println("Error occurred during script execution: " + e.getMessage());
                        continue;
                    }
                    if(input.command.equals("insert")){
                        Long key = (Long) commandWithArg.getArgument();
                        if(musicBands.containsKey(key)){
                            System.out.println("The collection already contain the key: " + key);
                        }else{
                            MusicBand band = consoleScript.readMusicBandFromScript();
                            if(band == null){
                                System.out.println("The read music band is null. Command \"insert\" failed");
                                continue;
                            }
                            band.setId(key);
                            musicBands.put(key, band);
                            System.out.println("The music band was successfully inserted to the collection");
                        }
                        continue;
                    }
                    if(input.command.equals("update")){
                        Long id = (Long) commandWithArg.getArgument();
                        if(!musicBands.containsKey(id)){
                            System.out.println("The collection doesn't contain the key " + id);
                        }else{
                            MusicBand band = consoleScript.readMusicBand();
                            if(band == null){
                                System.out.println("The read music band is null. Command \"update\" failed");
                                continue;
                            }
                            band.setId(id);
                            musicBands.put(id, band);
                            System.out.printf("The band with ID %d was successfully updated\n", id);
                        }
                        continue;
                    }
                    if(input.command.equals("replace_if_lower")){
                        Long key = (Long) commandWithArg.getArgument();
                        if(musicBands.isEmpty()){
                            System.out.println("The collection is empty");
                            continue;
                        }
                        if(!musicBands.containsKey(key)){
                            System.out.println("The collection doesn't contain the key " + key);
                            continue;
                        }
                        MusicBand oldBand = musicBands.get(key);
                        MusicBand newBand = consoleScript.readMusicBandFromScript();
                        if(newBand == null){
                            System.out.println("The read music band is null. Command \"replace_if_lower\" failed");
                            continue;
                        }
                        newBand.setId(key);
                        if(compareByDateAndName.compare(oldBand, newBand) > 0){
                            musicBands.put(key, newBand);
                            System.out.printf("The band that key is %d was successfully replaced\n", key);
                        }else{
                            System.out.printf("The band that key is %d wasn't replaced\n", key);
                        }
                        continue;
                    }
                    commandWithArg.execute();
                }else{
                    command.execute();
                }
            }
        }catch(FileNotFoundException e){
            System.err.println("File for script was not found");
        }catch(IOException e){
            System.err.println("IO error:" + e.getMessage());
        }
    }
}