// ServerMain.java
package org.example;

import classes.MusicBand;
import newClasses.CommandWrapper;
import utils.ReaderCSV;
import utils.WriterCSV;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.TreeMap;
import java.util.Collections;

public class ServerMain {
    private static final int PORT = 12345;
    private static TreeMap<Long, MusicBand> musicBands;
    private static File file_csv;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ServerMain <csv_file>");
            System.exit(1);
        }

        file_csv = new File(args[0]);
        musicBands = ReaderCSV.loadFromFile(file_csv);
        System.out.println("Server started. Loaded " + musicBands.size() + " music bands.");

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(PORT));
            channel.configureBlocking(false);

            ByteBuffer buffer = ByteBuffer.allocate(65536);

            while (true) {
                buffer.clear();
                SocketAddress clientAddress = channel.receive(buffer);

                if (clientAddress != null) {
                    buffer.flip();
                    try {
                        ObjectInputStream ois = new ObjectInputStream(
                                new ByteArrayInputStream(buffer.array(), 0, buffer.limit()));
                        CommandWrapper command = (CommandWrapper) ois.readObject();
                        System.out.println("Received command: " + command.getCommandName());

                        Object response = processCommand(command);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(response);
                        oos.flush();

                        byte[] responseData = baos.toByteArray();
                        ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
                        channel.send(responseBuffer, clientAddress);
                    } catch (Exception e) {
                        System.err.println("Error processing command: " + e.getMessage());
                    }
                }

                try {
                    Thread.sleep(100); // Prevent CPU overuse
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static Object processCommand(CommandWrapper command) {
        try {
            switch (command.getCommandName()) {
                case "help":
                    return "Available commands: help, info, show, insert, update, remove_key, clear, " +
                            "execute_script, exit, remove_lower, replace_if_lower, remove_lower_key, " +
                            "filter_starts_with_name, print_ascending, print_descending";

                case "info":
                    return getCollectionInfo();

                case "show":
                    return musicBands.values().stream()
                            .sorted(MusicBand::compareTo)
                            .toArray();

                case "insert":
                    musicBands.put(command.getKey(), command.getMusicBand());
                    saveCollection();
                    return "Music band inserted successfully.";

                case "update":
                    if (musicBands.containsKey(command.getKey())) {
                        musicBands.put(command.getKey(), command.getMusicBand());
                        saveCollection();
                        return "Music band updated successfully.";
                    }
                    return "Error: Key not found.";

                case "remove_key":
                    if (musicBands.remove(command.getKey()) != null) {
                        saveCollection();
                        return "Music band removed successfully.";
                    }
                    return "Error: Key not found.";

                case "clear":
                    musicBands.clear();
                    saveCollection();
                    return "Collection cleared successfully.";

                case "print_ascending":
                    return musicBands.values().stream()
                            .sorted()
                            .toArray();

                case "print_descending":
                    return musicBands.values().stream()
                            .sorted(Collections.reverseOrder())
                            .toArray();

                case "remove_lower_key":
                    int count = musicBands.headMap(command.getKey(), false).size();
                    musicBands.headMap(command.getKey(), false).clear();
                    saveCollection();
                    return "Removed " + count + " music bands.";

                case "filter_starts_with_name":
                    return musicBands.values().stream()
                            .filter(b -> b.getName().startsWith(command.getArgument().toString()))
                            .toArray();

                default:
                    return "Error: Unknown command.";
            }
        } catch (Exception e) {
            return "Error processing command: " + e.getMessage();
        }
    }

    private static String getCollectionInfo() {
        return String.format(
                "Type: TreeMap<Long, MusicBand>\n" +
                        "Size: %d\n" +
                        "First key: %d\n" +
                        "Last key: %d",
                musicBands.size(),
                musicBands.isEmpty() ? 0 : musicBands.firstKey(),
                musicBands.isEmpty() ? 0 : musicBands.lastKey()
        );
    }

    private static void saveCollection() {
        try {
            WriterCSV.loadToFile(file_csv, musicBands);
        } catch (IOException e) {
            System.err.println("Error saving collection: " + e.getMessage());
        }
    }
}