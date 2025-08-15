// ClientMain.java
package org.example;

import classes.MusicBand;
import utils.Console;
import newClasses.CommandWrapper;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ClientMain {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        Console console = new Console();
        System.out.println("Client started. Type 'help' for available commands.");

        while (true) {
            /*
            Получаем объект команда+аргумент(если есть) в строковом представлении
             */
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
                /*
                Вызываем метод sendCommandToServer(input), где параметр - это объект команда+аргумент
                 */
                Object response = sendCommandToServer(input);
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

    private static Object sendCommandToServer(Console.CommandInput input) throws IOException {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            if (!channel.isConnected()) {
                throw new IOException("Не удалось зафиксировать адрес");
            }
            // Try to connect with timeout
//            if (!channel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT))) {
//                while (!channel.finishConnect()) {
//                    System.out.print(".");
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                        throw new IOException("Connection interrupted");
//                    }
//                }
//            }
            System.out.println();

            /*
            Вызывает метод createCommandWrapper(input), где параметр - это объект команда+аргумент,
            полученный в методе main в начале цикла, и переданный в метод sendCommandToServer
             */
            CommandWrapper commandWrapper = createCommandWrapper(input);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(commandWrapper);
            oos.flush();

            byte[] requestData = baos.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(requestData);
            channel.write(buffer);

            // Wait for response
            ByteBuffer responseBuffer = ByteBuffer.allocate(65536);
            int bytesRead;
            int attempts = 0;

            while ((bytesRead = channel.read(responseBuffer)) == 0 && attempts < 10) {
                attempts++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Response wait interrupted");
                }
            }

            if (bytesRead == -1) {
                throw new IOException("No response from server");
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

    private static CommandWrapper createCommandWrapper(Console.CommandInput input) {
        CommandWrapper wrapper = new CommandWrapper();
        wrapper.setCommandName(input.command);

        switch (input.command) {
            case "remove_lower":
                wrapper.setMusicBand(new Console().readMusicBand());
                break;
            case "insert":
            case "update":
            case "replace_if_lower":
                wrapper.setKey(Long.parseLong(input.argument));
                wrapper.setMusicBand(new Console().readMusicBand());
                break;
            case "remove_key":
            case "remove_lower_key":
                wrapper.setKey(Long.parseLong(input.argument));
                break;
            case "filter_starts_with_name":
                wrapper.setArgument(input.argument);
                break;
        }

        return wrapper;
    }
}