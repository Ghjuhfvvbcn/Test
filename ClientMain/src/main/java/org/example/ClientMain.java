// ClientMain.java
package org.example;

import classes.MusicBand;
import utils.Console;
import newClasses.CommandWrapper;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class ClientMain {
    private static String SERVER_HOST = "localhost";
    private static int SERVER_PORT = 12345;

    public static void main(String[] args) {
        Console console = new Console();
        if (args.length >= 1) {
            SERVER_HOST = args[0];
        }
        if (args.length >= 2) {
            try {
                SERVER_PORT = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number, using default: " + SERVER_PORT);
            }
        }

        System.out.println("Connecting to server: " + SERVER_HOST + ":" + SERVER_PORT);
        System.out.println("Client started. Type 'help' for available commands.");

        while (true) {
            Console.CommandInput input = console.readCommand();
            if (input == null) {
                System.out.println("Input is complete");
                break;
            }

            if (!Console.isValidCommand(input.command)) {
                System.out.printf("There is no command '%s'\n", input.command);
                continue;
            }

            if (input.command.equals("exit")) {
                System.out.println("Input the command 'exit', try later");
                break;
            }

            try {
                // Проверяем наличие обязательных аргументов перед отправкой на сервер
                String validationError = validateCommandInput(input);
                if (validationError != null) {
                    System.out.println(validationError);
                    continue;
                }

                // ==================== ИЗМЕНЕНИЕ: Чтение MusicBand ТОЛЬКО ЗДЕСЬ ====================
                MusicBand musicBand = null;
                if (input.command.equals("remove_lower") ||
                        input.command.equals("insert") ||
                        input.command.equals("update") ||
                        input.command.equals("replace_if_lower")) {

                    System.out.println("Please enter MusicBand details:");
                    musicBand = console.readMusicBand();
                    if (musicBand == null) {
                        System.out.println("Error: Failed to read MusicBand");
                        continue;
                    }
                }
                // =================================================================================

                Object response = sendCommandToServer(input, musicBand); // Передаем musicBand
                if (response instanceof Object[]) {
                    Arrays.stream((Object[]) response).forEach(System.out::println);
                } else {
                    System.out.println(response);
                }
            } catch (IOException e) {
                System.err.println("Server communication error: " + e.getMessage());
                System.out.println("Retrying in 3 seconds...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private static String validateCommandInput(Console.CommandInput input) {
        switch (input.command) {
            case "insert":
            case "update":
            case "replace_if_lower":
            case "remove_key":
            case "remove_lower_key":
                if (input.argument == null || input.argument.trim().isEmpty()) {
                    return "Error: Command '" + input.command + "' requires a numeric argument (key)";
                }
                try {
                    Long.parseLong(input.argument.trim());
                } catch (NumberFormatException e) {
                    return "Error: Argument for '" + input.command + "' must be a valid number";
                }
                break;

            case "filter_starts_with_name":
                if (input.argument == null || input.argument.trim().isEmpty()) {
                    return "Error: Command '" + input.command + "' requires a string argument";
                }
                break;
        }
        return null;
    }

    // ==================== ИЗМЕНЕННЫЙ МЕТОД: добавлен параметр musicBand ====================
    private static Object sendCommandToServer(Console.CommandInput input, MusicBand musicBand) throws IOException {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            if (!channel.isConnected()) {
                throw new IOException("Не удалось зафиксировать адрес");
            }

            // ==================== ПЕРЕДАЕМ musicBand в createCommandWrapper ====================
            CommandWrapper commandWrapper = createCommandWrapper(input, musicBand);
            if (commandWrapper == null) {
                return "Error: Failed to create command wrapper";
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(commandWrapper);
            oos.flush();

            byte[] requestData = baos.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(requestData);
            channel.write(buffer);

            ByteBuffer responseBuffer = ByteBuffer.allocate(65536);
            int bytesRead;
            int attempts = 0;

            while ((bytesRead = channel.read(responseBuffer)) == 0 && attempts < 10) {
                attempts++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Response wait interrupted");
                }
            }

            if (bytesRead == 0) {
                throw new IOException("No response from server (timeout)");
            }

            responseBuffer.flip();
            byte[] responseData = new byte[responseBuffer.remaining()];
            responseBuffer.get(responseData);

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(responseData));
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Error deserializing response", e);
        }
    }

    // ==================== ИЗМЕНЕННЫЙ МЕТОД: принимает musicBand ====================
    private static CommandWrapper createCommandWrapper(Console.CommandInput input, MusicBand musicBand) {
        try {
            CommandWrapper wrapper = new CommandWrapper();
            wrapper.setCommandName(input.command);

            switch (input.command) {
                case "remove_lower":
                    // ==================== ИСПОЛЬЗУЕМ ПЕРЕДАННЫЙ musicBand ====================
                    wrapper.setMusicBand(musicBand);
                    break;

                case "insert":
                case "update":
                case "replace_if_lower":
                    if (input.argument == null || input.argument.trim().isEmpty()) {
                        System.out.println("Error: Command '" + input.command + "' requires a key argument");
                        return null;
                    }
                    try {
                        long key = Long.parseLong(input.argument);
                        wrapper.setKey(key);
                        // ==================== ИСПОЛЬЗУЕМ ПЕРЕДАННЫЙ musicBand ====================
                        wrapper.setMusicBand(musicBand);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Key must be a valid number for command '" + input.command + "'");
                        return null;
                    }
                    break;

                case "remove_key":
                case "remove_lower_key":
                    if (input.argument == null || input.argument.trim().isEmpty()) {
                        System.out.println("Error: Command '" + input.command + "' requires a key argument");
                        return null;
                    }
                    try {
                        long key = Long.parseLong(input.argument);
                        wrapper.setKey(key);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Key must be a valid number for command '" + input.command + "'");
                        return null;
                    }
                    break;

                case "filter_starts_with_name":
                case "execute_script":
                    if (input.argument == null || input.argument.trim().isEmpty()) {
                        System.out.println("Error: Command '" + input.command + "' requires a string argument");
                        return null;
                    }
                    wrapper.setArgument(input.argument);
                    break;
            }

            return wrapper;

        } catch (Exception e) {
            System.out.println("Error creating command: " + e.getMessage());
            return null;
        }
    }
}