package commands;

import classes.MusicBand;
import utils.*;
import utils.Console;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    private final Stack<FileInputStream> scriptStack = new Stack<>();

    public void setConsoleScript(FileInputStream scriptStream) {
        this.consoleScript = new Console(scriptStream);
    }

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
//    public String insert(Long key, MusicBand band) {
//        if (musicBands.containsKey(key)) {
//            return "The collection already contain the key: " + key;
//        } else {
//            band.setId(key);
//            musicBands.put(key, band);
//            saveCollection(); // ← СОХРАНЯЕМ!
//            return "Music band inserted successfully.";
//        }
//    }

    /**
     * Обновляет элемент по ключу и возвращает результат в виде строки.
     */
//    public String update(Long id, MusicBand band) {
//        if (!musicBands.containsKey(id)) {
//            return "The collection doesn't contain the key " + id;
//        } else {
//            band.setId(id);
//            musicBands.put(id, band);
//            saveCollection(); // ← СОХРАНЯЕМ!
//            return "Music band updated successfully.";
//        }
//    }
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
     * Вставляет элемент (для сервера) - уже полученный MusicBand
     */
    public String insert_server(Long key, MusicBand band) {
        if (musicBands.containsKey(key)) {
            return "The collection already contain the key: " + key;
        } else {
            band.setId(key);
            musicBands.put(key, band);
            saveCollection();
            return "Music band inserted successfully.";
        }
    }

    /**
     * Обновляет элемент (для сервера) - уже полученный MusicBand
     */
    public String update_server(Long id, MusicBand band) {
        if (!musicBands.containsKey(id)) {
            return "The collection doesn't contain the key " + id;
        } else {
            band.setId(id);
            musicBands.put(id, band);
            saveCollection();
            return "Music band updated successfully.";
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

    public String remove_lower_server(MusicBand band) {
        if (musicBands.isEmpty()) {
            return "The collection is empty";
        }
        int sizeBefore = musicBands.size();
        musicBands.values().removeIf(musicBand ->
                MusicBand.compareByDateAndName.compare(musicBand, band) > 0);
        int sizeAfter = musicBands.size();
        saveCollection();
        return (sizeBefore - sizeAfter) + " bands were successfully removed";
    }

    /**
     * Заменяет элемент если новое значение меньше и возвращает результат в виде строки.
     */
    public String replace_if_lower_server(Long key, MusicBand newBand) {
        if (musicBands.isEmpty()) {
            return "The collection is empty";
        }
        if (!musicBands.containsKey(key)) {
            return "The collection doesn't contain the key " + key;
        }
        MusicBand oldBand = musicBands.get(key);
        newBand.setId(key);
        if (compareByDateAndName.compare(oldBand, newBand) > 0) {
            musicBands.put(key, newBand);
            saveCollection();
            return "Music band replaced successfully.";
        } else {
            return "New value is not lower than existing value.";
        }
    }
//    public String replace_if_lower(Long key, MusicBand newBand) {
//        if (musicBands.isEmpty()) {
//            return "The collection is empty";
//        }
//        if (!musicBands.containsKey(key)) {
//            return "The collection doesn't contain the key " + key;
//        }
//        MusicBand oldBand = musicBands.get(key);
//        newBand.setId(key);
//        if (compareByDateAndName.compare(oldBand, newBand) > 0) {
//            musicBands.put(key, newBand);
//            saveCollection(); // ← СОХРАНЯЕМ!
//            return "Music band replaced successfully.";
//        } else {
//            return "New value is not lower than existing value.";
//        }
//    }
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

    public TreeMap<Long, MusicBand> getMusicBandsCollection() {
        return musicBands;
    }

    public String execute_script(String filename) {
        try {
            File scriptFile = new File(filename);

            // Проверка существования файла
            if (!scriptFile.exists()) {
                return "Error: Script file not found: " + filename;
            }
            if (!scriptFile.canRead()) {
                return "Error: Cannot read script file: " + filename;
            }
            if (scriptFile.isDirectory()) {
                return "Error: Specified path is a directory: " + filename;
            }

            // Проверка на рекурсию
            if (isScriptAlreadyInStack(scriptFile)) {
                return "Error: Recursive script execution detected for file: " + filename;
            }

            FileInputStream scriptStream = new FileInputStream(scriptFile);
            scriptStack.push(scriptStream);
            setConsoleScript(scriptStream);

            StringBuilder result = new StringBuilder();
            result.append("Executing script: ").append(filename).append("\n");

            String line;
            int lineNumber = 0;

            while ((line = consoleScript.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;

                try {
                    Console.CommandInput input = Console.parseCommand(line);

                    // Проверка существования команды
                    if (!Console.isValidCommand(input.command)) {
                        result.append("Line ").append(lineNumber).append(": Unknown command: '")
                                .append(input.command).append("'\n");
                        continue;
                    }

                    // Проверка обязательных аргументов
                    if (input.command.equals("execute_script")) {
                        if (input.argument == null || input.argument.trim().isEmpty()) {
                            result.append("Line ").append(lineNumber)
                                    .append(": Error: execute_script requires filename argument\n");
                            continue;
                        }
                    }

                    if (input.command.equals("insert") || input.command.equals("update") ||
                            input.command.equals("replace_if_lower") || input.command.equals("remove_key")) {

                        if (input.argument == null || input.argument.trim().isEmpty()) {
                            result.append("Line ").append(lineNumber)
                                    .append(": Error: Command '").append(input.command)
                                    .append("' requires a key argument\n");
                            continue;
                        }
                    }

                    // Выполнение команды
                    if (input.command.equals("execute_script")) {
                        result.append(execute_script(input.argument)).append("\n");
                    } else {
                        result.append(processScriptCommand(input, lineNumber)).append("\n");
                    }

                } catch (Exception e) {
                    result.append("Line ").append(lineNumber).append(": Error: ")
                            .append(e.getMessage()).append("\n");
                }
            }

            scriptStack.pop();
            if (!scriptStack.isEmpty()) {
                setConsoleScript(scriptStack.peek());
            }

            return result.toString();

        } catch (FileNotFoundException e) {
            return "Error: Script file not found: " + filename;
        } catch (SecurityException e) {
            return "Error: Access denied to script file: " + filename;
        } catch (IOException e) {
            return "Error: IO error reading script: " + e.getMessage();
        } catch (Exception e) {
            return "Error: Unexpected error: " + e.getMessage();
        }
    }

    private boolean isScriptAlreadyInStack(File scriptFile) {
        try {
            String currentPath = scriptFile.getCanonicalPath();
            for (FileInputStream stream : scriptStack) {
                if (new File(stream.toString()).getCanonicalPath().equals(currentPath)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // Если не можем проверить, лучше пропустить
        }
        return false;
    }

    private String processScriptCommand(Console.CommandInput input, int lineNumber) {
        try {
            Command command = commands.get(input.command);
            if (command == null) {
                return "Line " + lineNumber + ": Unknown command: " + input.command;
            }

            // Обработка аргументов для команд с аргументами
            if (command instanceof CommandWithArgument) {
                CommandWithArgument<?> cmdWithArg = (CommandWithArgument<?>) command;

                try {
                    cmdWithArg.setArgument(input.argument);
                } catch (IllegalArgumentException e) {
                    return "Line " + lineNumber + ": Invalid argument for '" + input.command + "': " + e.getMessage();
                }
            }

            // Для команд, требующих MusicBand
            if (input.command.equals("insert") || input.command.equals("update") ||
                    input.command.equals("remove_lower") || input.command.equals("replace_if_lower")) {

                try {
                    MusicBand band = consoleScript.readMusicBandFromScript();
                    if (band == null) {
                        return "Line " + lineNumber + ": Error reading MusicBand data";
                    }

                    // Дополнительная валидация
                    if (band.getNumberOfParticipants() <= 0) {
                        return "Line " + lineNumber + ": Error: Number of participants must be positive";
                    }
                    if (band.getName() == null || band.getName().trim().isEmpty()) {
                        return "Line " + lineNumber + ": Error: Band name cannot be empty";
                    }

                    if (command instanceof Insert) {
                        return ((Insert) command).executeWithMusicBand(band);
                    } else if (command instanceof Update) {
                        return ((Update) command).executeWithMusicBand(band);
                    } else if (command instanceof Remove_lower) {
                        return ((Remove_lower) command).executeWithMusicBand(band);
                    } else if (command instanceof Replace_if_lower) {
                        return ((Replace_if_lower) command).executeWithMusicBand(band);
                    }

                } catch (IOException e) {
                    return "Line " + lineNumber + ": Error: Unexpected end of file while reading MusicBand";
                } catch (IllegalArgumentException e) {
                    return "Line " + lineNumber + ": Error in MusicBand data: " + e.getMessage();
                }
            }

            return command.execute();

        } catch (Exception e) {
            return "Line " + lineNumber + ": Error executing command: " + e.getMessage();
        }
    }
}