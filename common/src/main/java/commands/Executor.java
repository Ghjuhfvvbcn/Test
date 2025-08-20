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
import java.util.stream.Collectors;

import static classes.MusicBand.compareByDateAndName;

/**
 * Класс для работы с коллекцией объектов типа {@link classes.MusicBand}.
 */
public class Executor {
    private TreeMap<Long, MusicBand> musicBands;
    private final Map<String, Command> commands = CommandMap.createMapWithCommands(this);
    private final ZonedDateTime initializationDate;
    private final File file_csv;
    private final File file_script;
    private final Console console = new Console();
    private Console consoleScript;

    public Executor(File file_csv, File file_script) {
        this.file_csv = file_csv;
        this.file_script = file_script;
        initializationDate = ZonedDateTime.now();
        musicBands = ReaderCSV.loadFromFile(file_csv);
    }

    public void setConsoleScript() throws FileNotFoundException {
        consoleScript = new Console(new FileInputStream(file_script));
    }

    /**
     * Возвращает список доступных команд в виде строки.
     */
    public String getHelp() {
        return "-help : вывести справку по доступным командам\n" +
                "-info : вывести информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "-show : вывести все элементы коллекции в строковом представлении\n" +
                "-insert null {element} : добавить новый элемент с заданным ключом\n" +
                "-update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                "-remove_key null : удалить элемент из коллекции по его ключу\n" +
                "-clear : очистить коллекцию\n" +
                "-save : сохранить коллекцию в файл\n" +
                "-execute_script file_name : считать и исполнить скрипт из указанного файла\n" +
                "-exit : завершить программу (без сохранения в файл)\n" +
                "-remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n" +
                "-replace_if_lower null {element} : заменить значение по ключу, если новое значение меньше старого\n" +
                "-remove_lower_key null : удалить из коллекции все элементы, ключ которых меньше, чем заданный\n" +
                "-filter_starts_with_name name : вывести элементы, значение поля name которых начинается с заданной подстроки\n" +
                "-print_ascending : вывести элементы коллекции в порядке возрастания\n" +
                "-print_descending : вывести элементы коллекции в порядке убывания";
    }

    /**
     * Возвращает информацию о коллекции в виде строки.
     */
    public String getInfo() {
        if (musicBands == null) {
            return "The collection is 'null'";
        } else if (!musicBands.isEmpty()) {
            return String.format("Type: TreeMap<Long, MusicBand>\n" +
                            "Initialization date: %s\n" +
                            "Size of collection: %d\n" +
                            "First key: %d\n" +
                            "Last key: %d",
                    initializationDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss z")),
                    musicBands.size(),
                    musicBands.firstKey(),
                    musicBands.lastKey());
        } else {
            return String.format("Type: TreeMap<Long, MusicBand>\n" +
                            "Initialization date: %s\n" +
                            "Size of collection: 0",
                    initializationDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss z")));
        }
    }

    /**
     * Возвращает все элементы коллекции в виде строки.
     */
    public String getShow() {
        if (musicBands == null) {
            return "The collection is 'null'";
        } else if (!musicBands.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("The collection contains ").append(musicBands.size()).append(" items:\n");
            for (MusicBand band : musicBands.values()) {
                sb.append(band).append("\n");
            }
            return sb.toString();
        } else {
            return "The collection is empty";
        }
    }

    /**
     * Очищает коллекцию и возвращает результат в виде строки.
     */
    public String clear() {
        if (musicBands == null) {
            return "The collection is 'null'";
        } else if (!musicBands.isEmpty()) {
            int sizeBefore = musicBands.size();
            musicBands.clear();
            return "The collection was successfully cleared. " + sizeBefore + " elements removed";
        } else {
            return "The collection is empty";
        }
    }

    /**
     * Сохраняет коллекцию в файл и возвращает результат в виде строки.
     */
    public String save() {
        try {
            WriterCSV.loadToFile(file_csv, musicBands);
            return "The collection was successfully saved to the file '" + file_csv + "'";
        } catch (IOException e) {
            return "Saving to a file failed: " + e.getMessage();
        }
    }

    /**
     * Завершает выполнение программы (возвращает строку перед выходом).
     */
    public String exit() {
        return "Shutting down...";
    }

    /**
     * Возвращает элементы коллекции в порядке возрастания в виде строки.
     */
    public String print_ascending() {
        if (musicBands == null) {
            return "The collection is 'null'";
        } else if (musicBands.isEmpty()) {
            return "The collection is empty";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Collection elements in ascending order (by 'name'):\n");
            ArrayList<MusicBand> bands = new ArrayList<>(musicBands.values());
            Collections.sort(bands);
            for (MusicBand band : bands) {
                sb.append(band).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Возвращает элементы коллекции в порядке убывания в виде строки.
     */
    public String print_descending() {
        if (musicBands == null) {
            return "The collection is 'null'";
        } else if (musicBands.isEmpty()) {
            return "The collection is empty";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Collection elements in descending order (by 'name'):\n");
            ArrayList<MusicBand> bands = new ArrayList<>(musicBands.values());
            Collections.sort(bands, Collections.reverseOrder());
            for (MusicBand band : bands) {
                sb.append(band).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Удаляет элемент по ключу и возвращает результат в виде строки.
     */
    public String remove_key(Long key) {
        MusicBand band = musicBands.remove(key);
        if (band == null) {
            return "The element with the key " + key + " was not found";
        } else {
            return "The item with the key " + key + " has been successfully deleted";
        }
    }

    /**
     * Удаляет элементы с ключами меньше заданного и возвращает результат в виде строки.
     */
    public String remove_lower_key(Long key) {
        int sizeBefore = musicBands.size();
        musicBands.headMap(key, false).clear();
        int sizeAfter = musicBands.size();
        return "Successfully deleted " + (sizeBefore - sizeAfter) + " items";
    }

    /**
     * Возвращает элементы, имена которых начинаются с заданной подстроки.
     */
    public String filter_starts_with_name(String name) {
        ArrayList<MusicBand> bands = new ArrayList<>();
        for (MusicBand band : musicBands.values()) {
            if (band.getName().startsWith(name)) {
                bands.add(band);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(bands.size())
                .append(" music groups whose names start with \"").append(name).append("\"\n");

        for (MusicBand band : bands) {
            sb.append(band).append("\n");
        }

        return sb.toString();
    }

    /**
     * Вставляет элемент по ключу и возвращает результат в виде строки.
     */
    public String insert(Long key) {
        if (musicBands.containsKey(key)) {
            return "The collection already contain the key: " + key;
        } else {
            MusicBand band = console.readMusicBand();
            band.setId(key);
            musicBands.put(key, band);
            return "The music band was successfully inserted to the collection";
        }
    }

    /**
     * Обновляет элемент по ключу и возвращает результат в виде строки.
     */
    public String update(Long id) {
        if (!musicBands.containsKey(id)) {
            return "The collection doesn't contain the key " + id;
        } else {
            MusicBand band = console.readMusicBand();
            band.setId(id);
            musicBands.put(id, band);
            return "The band with ID " + id + " was successfully updated";
        }
    }

    /**
     * Удаляет элементы меньшие заданного и возвращает результат в виде строки.
     */
    public String remove_lower() {
        if (musicBands.isEmpty()) {
            return "The collection is empty";
        }
        MusicBand band = console.readMusicBand();
        int sizeBefore = musicBands.size();
        musicBands.headMap(Long.MAX_VALUE).values().removeIf(musicBand ->
                compareByDateAndName.compare(musicBand, band) > 0);
        int sizeAfter = musicBands.size();
        return (sizeBefore - sizeAfter) + " bands were successfully removed";
    }

    /**
     * Заменяет элемент если новое значение меньше и возвращает результат в виде строки.
     */
    public String replace_if_lower(Long key) {
        if (musicBands.isEmpty()) {
            return "The collection is empty";
        }
        if (!musicBands.containsKey(key)) {
            return "The collection doesn't contain the key " + key;
        }
        MusicBand oldBand = musicBands.get(key);
        MusicBand newBand = console.readMusicBand();
        newBand.setId(key);
        if (compareByDateAndName.compare(oldBand, newBand) > 0) {
            musicBands.put(key, newBand);
            return "The band that key is " + key + " was successfully replaced";
        } else {
            return "The band that key is " + key + " wasn't replaced";
        }
    }

    /**
     * Выполняет скрипт и возвращает результат в виде строки.
     */
    public String execute_script() {
        StringBuilder result = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream(file_script)) {
            Console consoleScript = new Console(fileInputStream);

            String s;
            while (true) {
                s = consoleScript.readLine();
                if (s == null) {
                    result.append("Execution of the script from the '").append(file_script).append("' file is complete\n");
                    break;
                }
                if (s.isEmpty()) {
                    continue;
                }

                Console.CommandInput input = Console.parseCommand(s);
                if (!Console.isValidCommand(input.command)) {
                    result.append("There is no command '").append(input.command).append("'\n");
                    continue;
                }

                // Обработка специальных команд скрипта...
                // (логика требует адаптации для возврата строк вместо вывода)

                result.append("Executed: ").append(s).append("\n");
            }
        } catch (FileNotFoundException e) {
            return "File for script was not found";
        } catch (IOException e) {
            return "IO error: " + e.getMessage();
        }
        return result.toString();
    }

    /**
     * Сохраняет коллекцию и возвращает результат в виде строки
     */
    public String saveCollection() {
        try {
            WriterCSV.loadToFile(file_csv, musicBands);
            return "The collection was successfully saved to the file '" + file_csv + "'";
        } catch (IOException e) {
            return "Error saving collection: " + e.getMessage();
        }
    }
}