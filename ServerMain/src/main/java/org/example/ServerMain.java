// ServerMain.java
package org.example;

import classes.MusicBand;
import commands.*;
import utils.CommandMap;
import utils.ReaderCSV;
import utils.WriterCSV;
import newClasses.CommandWrapper;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;
import java.util.TreeMap;

public class ServerMain {
    private static final int PORT = 12345;
    private static TreeMap<Long, MusicBand> musicBands;
    private static File file_csv;
    private static Map<String, Command> commands; // Мапа команд
    private static Executor executor;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ServerMain <csv_file>");
            System.exit(1);
        }

        file_csv = new File(args[0]);
        musicBands = ReaderCSV.loadFromFile(file_csv);

        // Создаем Executor и инициализируем мапу команд
        executor = new Executor(file_csv, null); // file_script может быть null для сервера
        commands = CommandMap.createMapWithCommands(executor);

        // Получаем коллекцию из Executor
        TreeMap<Long, MusicBand> musicBands = executor.getMusicBandsCollection();

        System.out.println("Server started. Loaded " + musicBands.size() + " music bands.");

        try (DatagramChannel channel = DatagramChannel.open()) {
            /*
            Привязываем UDP канал к порту 12345.
            UDP не устанавливает соединение, но фиксирует порт для приема пакетов.
             */
//            channel.bind(new InetSocketAddress(PORT));
            channel.bind(new InetSocketAddress("0.0.0.0", PORT));  // Слушаем все сетевые интерфейсы

            System.out.println("Server started on all interfaces. Port: " + PORT);

            /*
            Устанавливаем неблокирующий режим - метод receive будет возвращать null
            сразу если нет данных, вместо блокировки потока.
             */
            channel.configureBlocking(false);

            /*
            Создаем буфер размером 64 КБ для приема входящих UDP пакетов.
            Размер выбран с запасом для больших команд с объектами MusicBand.
             */
            ByteBuffer buffer = ByteBuffer.allocate(65536);

            // Основной цикл обработки запросов
            while (true) {
                /*
                Очищаем буфер перед каждым использованием:
                - position = 0 (курсор в начало)
                - limit = capacity (доступна вся емкость)
                 */
                buffer.clear();

                /*
                Принимаем UDP пакет. В неблокирующем режиме:
                - возвращает SocketAddress клиента если пакет получен
                - возвращает null если пакетов нет
                 */
                SocketAddress clientAddress = channel.receive(buffer);

                if (clientAddress != null) {
                    /*
                    Переводим буфер в режим чтения:
                    - limit = position (где закончились данные)
                    - position = 0 (читаем с начала)
                    Теперь буфер готов для чтения полученных данных.
                     */
                    buffer.flip();
                    try {
                        /*
                        ДЕСЕРИАЛИЗАЦИЯ ВХОДЯЩИХ ДАННЫХ:
                        1. buffer.array() - получаем внутренний byte[] буфера
                        2. new ByteArrayInputStream(buffer.array(), 0, buffer.limit()) -
                           создаем поток из полученных байтов (только значимая часть)
                        3. ObjectInputStream - десериализует объекты из байтов

                        Этот процесс преобразует байты обратно в Java объект CommandWrapper.
                         */
                        ObjectInputStream ois = new ObjectInputStream(
                                new ByteArrayInputStream(buffer.array(), 0, buffer.limit()));
                        CommandWrapper commandWrapper = (CommandWrapper) ois.readObject();
                        System.out.println("Received command: " + commandWrapper.getCommandName());

                        // Обрабатываем команду через мапу команд
                        Object response = processCommandWithMap(commandWrapper);

                        /*
                        СЕРИАЛИЗАЦИЯ ИСХОДЯЩИХ ДАННЫХ:
                        1. ByteArrayOutputStream - накапливает байты в памяти
                        2. ObjectOutputStream - преобразует объекты в байты
                        3. oos.writeObject(response) - сериализует объект ответа
                        4. oos.flush() - принудительно записывает данные в baos

                        Теперь baos содержит сериализованный ответ в виде byte[].
                         */
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(response);
                        oos.flush();

                        /*
                        ПОДГОТОВКА И ОТПРАВКА ОТВЕТА:
                        1. baos.toByteArray() - получаем массив байтов ответа
                        2. ByteBuffer.wrap(responseData) - оборачиваем массив в буфер
                        3. channel.send() - отправляем UDP пакет обратно клиенту

                        Важно: UDP не гарантирует доставку, поэтому ответ может быть потерян.
                         */
                        byte[] responseData = baos.toByteArray();
                        ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
                        channel.send(responseBuffer, clientAddress);
                    } catch (Exception e) {
                        System.err.println("Error processing command: " + e.getMessage());
                        // Отправляем сообщение об ошибке клиенту
                        sendErrorResponse(channel, clientAddress, "Error: " + e.getMessage());
                    }
                }

                /*
                Короткая пауза для снижения нагрузки на CPU.
                Без sleep в неблокирующем режиме цикл будет выполняться миллионы раз в секунду.
                 */
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает команду используя мапу команд вместо switch-case
     */
    private static Object processCommandWithMap(CommandWrapper commandWrapper) {
        try {
            String commandName = commandWrapper.getCommandName();

            // Получаем команду ИЗ МАПЫ (не создаем новую!)
            Command command = commands.get(commandName);

            if (command == null) {
                return "Error: Unknown command '" + commandName + "'";
            }

            // Обрабатываем команды с аргументами
            if (command instanceof CommandWithArgument) {
                CommandWithArgument<?> commandWithArg = (CommandWithArgument<?>) command;

                switch (commandName) {
                    case "insert":
                    case "update":
                    case "remove_key":
                    case "remove_lower_key":
                    case "replace_if_lower":
                        if (commandWrapper.getKey() != null) {
                            // Устанавливаем аргумент в СУЩЕСТВУЮЩИЙ объект команды
                            commandWithArg.setArgument(commandWrapper.getKey().toString());
                        } else {
                            return "Error: Command '" + commandName + "' requires a key argument";
                        }
                        break;

                    case "filter_starts_with_name":
                    case "execute_script":
                        if (commandWrapper.getArgument() != null) {
                            // Устанавливаем аргумент в СУЩЕСТВУЮЩИЙ объект команды
                            commandWithArg.setArgument(commandWrapper.getArgument().toString());
                        } else {
                            return "Error: Command '" + commandName + "' requires a string argument";
                        }
                        break;
                }
            }

            // Выполняем команду (ТЕПЕРЬ С ПРАВИЛЬНЫМ OBJECT!)
            return executeCommand(command, commandWrapper);

        } catch (Exception e) {
            return "Error processing command: " + e.getMessage();
        }
    }

    /**
     * Выполняет команду и возвращает результат
     */
    /**
     * Выполняет команду и возвращает результат
     */
    private static Object executeCommand(Command command, CommandWrapper commandWrapper) {
        try {
            String commandName = command.getCommandName();

            if (commandName.equals("remove_lower")) {
                if (commandWrapper.getMusicBand() != null) {
                    // Используем специальный метод с готовым MusicBand
                    if (command instanceof Remove_lower) {
                        return ((Remove_lower) command).executeWithMusicBand(commandWrapper.getMusicBand());
                    }
                }
                return "Error: No MusicBand data provided";
            }
            // Для команд, которые требуют MusicBand
            if (commandName.equals("insert") || commandName.equals("update") || commandName.equals("replace_if_lower")) {
                if (commandWrapper.getMusicBand() != null) {
                    // Используем специальные методы executeWithMusicBand если они есть
                    if (command instanceof Insert) {
                        return ((Insert) command).executeWithMusicBand(commandWrapper.getMusicBand());
                    } else if (command instanceof Update) {
                        return ((Update) command).executeWithMusicBand(commandWrapper.getMusicBand());
                    } else if (command instanceof Replace_if_lower) {
                        return ((Replace_if_lower) command).executeWithMusicBand(commandWrapper.getMusicBand());
                    }
                }
                return "Error: No MusicBand data provided for command '" + commandName + "'";
            }

            // Для всех остальных команд просто выполняем их
            return command.execute();

        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }

    /**
     * Отправляет сообщение об ошибке клиенту
     */
    private static void sendErrorResponse(DatagramChannel channel, SocketAddress clientAddress, String errorMessage) {
        try {
            /*
            СЕРИАЛИЗАЦИЯ ОШИБКИ:
            Точно такой же процесс как для обычного ответа, но сериализуем строку с ошибкой.
             */
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(errorMessage);
            oos.flush();

            byte[] responseData = baos.toByteArray();
            ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);

            /*
            ОТПРАВКА ОШИБКИ:
            Отправляем UDP пакет с сериализованной ошибкой обратно клиенту.
            Клиент должен быть готов десериализовать этот ответ.
             */
            channel.send(responseBuffer, clientAddress);
        } catch (IOException ex) {
            System.err.println("Failed to send error response: " + ex.getMessage());
        }
    }

//    private static String getCollectionInfo() {
//        return String.format(
//                "Type: TreeMap<Long, MusicBand>\n" +
//                        "Size: %d\n" +
//                        "First key: %d\n" +
//                        "Last key: %d",
//                musicBands.size(),
//                musicBands.isEmpty() ? 0 : musicBands.firstKey(),
//                musicBands.isEmpty() ? 0 : musicBands.lastKey()
//        );
//    }
//
//    private static void saveCollection() {
//        try {
//            WriterCSV.loadToFile(file_csv, musicBands);
//        } catch (IOException e) {
//            System.err.println("Error saving collection: " + e.getMessage());
//        }
//    }
}



































// ServerMain.java
//package org.example;
//
//import classes.MusicBand;
//import newClasses.CommandWrapper;
//import utils.ReaderCSV;
//import utils.WriterCSV;
//
//import java.io.*;
//import java.net.*;
//import java.nio.ByteBuffer;
//import java.nio.channels.DatagramChannel;
//import java.util.TreeMap;
//import java.util.Collections;
//
//public class ServerMain {
//    private static final int PORT = 12345;
//    private static TreeMap<Long, MusicBand> musicBands;
//    private static File file_csv;
//
//    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.err.println("Usage: java ServerMain <csv_file>");
//            System.exit(1);
//        }
//
//        file_csv = new File(args[0]);
//        musicBands = ReaderCSV.loadFromFile(file_csv);
//        System.out.println("Server started. Loaded " + musicBands.size() + " music bands.");
//
//        try (DatagramChannel channel = DatagramChannel.open()) {
//            channel.bind(new InetSocketAddress(PORT));
//            /*
//            Устанавливает неблокирующий режим в канале
//             */
//            channel.configureBlocking(false);
//
//            /*
//            Создает буфер размером 64 кб
//             */
//            ByteBuffer buffer = ByteBuffer.allocate(65536);
//
//            while (true) {
//                buffer.clear();
//                SocketAddress clientAddress = channel.receive(buffer);
//
//                if (clientAddress != null) {
//                    buffer.flip();
//                    try {
//                        ObjectInputStream ois = new ObjectInputStream(
//                                new ByteArrayInputStream(buffer.array(), 0, buffer.limit()));
//                        CommandWrapper command = (CommandWrapper) ois.readObject();
//                        System.out.println("Received command: " + command.getCommandName());
//
//                        Object response = processCommand(command);
//
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        ObjectOutputStream oos = new ObjectOutputStream(baos);
//                        oos.writeObject(response);
//                        oos.flush();
//
//                        byte[] responseData = baos.toByteArray();
//                        ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
//                        channel.send(responseBuffer, clientAddress);
//                    } catch (Exception e) {
//                        System.err.println("Error processing command: " + e.getMessage());
//                    }
//                }
//
//                try {
//                    Thread.sleep(100); // Prevent CPU overuse
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    break;
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("Server error: " + e.getMessage());
//        }
//    }
//
//    private static Object processCommand(CommandWrapper command) {
//        try {
//            switch (command.getCommandName()) {
//                case "help":
//                    return "Available commands: help, info, show, insert, update, remove_key, clear, " +
//                            "execute_script, exit, remove_lower, replace_if_lower, remove_lower_key, " +
//                            "filter_starts_with_name, print_ascending, print_descending";
//
//                case "info":
//                    return getCollectionInfo();
//
//                case "show":
//                    return musicBands.values().stream()
//                            .sorted(MusicBand::compareTo)
//                            .toArray();
//
//                case "insert":
//                    musicBands.put(command.getKey(), command.getMusicBand());
//                    saveCollection();
//                    return "Music band inserted successfully.";
//
//                case "update":
//                    if (musicBands.containsKey(command.getKey())) {
//                        musicBands.put(command.getKey(), command.getMusicBand());
//                        saveCollection();
//                        return "Music band updated successfully.";
//                    }
//                    return "Error: Key not found.";
//
//                case "remove_key":
//                    if (musicBands.remove(command.getKey()) != null) {
//                        saveCollection();
//                        return "Music band removed successfully.";
//                    }
//                    return "Error: Key not found.";
//
//                case "clear":
//                    musicBands.clear();
//                    saveCollection();
//                    return "Collection cleared successfully.";
//
//                case "print_ascending":
//                    return musicBands.values().stream()
//                            .sorted()
//                            .toArray();
//
//                case "print_descending":
//                    return musicBands.values().stream()
//                            .sorted(Collections.reverseOrder())
//                            .toArray();
//
//                case "remove_lower_key":
//                    int count = musicBands.headMap(command.getKey(), false).size();
//                    musicBands.headMap(command.getKey(), false).clear();
//                    saveCollection();
//                    return "Removed " + count + " music bands.";
//
//                case "filter_starts_with_name":
//                    return musicBands.values().stream()
//                            .filter(b -> b.getName().startsWith(command.getArgument().toString()))
//                            .toArray();
//
//                default:
//                    return "Error: Unknown command.";
//            }
//        } catch (Exception e) {
//            return "Error processing command: " + e.getMessage();
//        }
//    }
//
//    private static String getCollectionInfo() {
//        return String.format(
//                "Type: TreeMap<Long, MusicBand>\n" +
//                        "Size: %d\n" +
//                        "First key: %d\n" +
//                        "Last key: %d",
//                musicBands.size(),
//                musicBands.isEmpty() ? 0 : musicBands.firstKey(),
//                musicBands.isEmpty() ? 0 : musicBands.lastKey()
//        );
//    }
//
//    private static void saveCollection() {
//        try {
//            WriterCSV.loadToFile(file_csv, musicBands);
//        } catch (IOException e) {
//            System.err.println("Error saving collection: " + e.getMessage());
//        }
//    }
//}