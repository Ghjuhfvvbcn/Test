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
            /*
            Установка неблокирующего режима
             */
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

            /*
            Создает расширяющийся буфер для хранения байтов
             */
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            /*
            Создает объект, способный сериализовать (преобразовать в байты) объект и записать полученные байты в буфер baos
             */
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            /*
            Преобразует в байты объект commandWrapper (полученный методом createCommandWrapper) и записывает их в поток oos
             */
            oos.writeObject(commandWrapper);
            /*
            Переносит данные из потока oos в поток baos
            Теперь в baos храниться сериализованные команда+аргумент+группа
             */
            oos.flush();

            /*
            Записывает команда+аргумент+группа в массив байтов
             */
            byte[] requestData = baos.toByteArray();
            /*
            Буфер данных в памяти для чтения, записи и навигации. ByteBuffer оборачивает requestData.
             */
            ByteBuffer buffer = ByteBuffer.wrap(requestData);
            /*
            Отправляет buffer, хранящий команда+аргумент+группа, в DatagramChannel, т.е. на сервер
             */
            channel.write(buffer);

            // Wait for response
            /*
            Создает в куче буфер емкостью 64 кб
             */
            ByteBuffer responseBuffer = ByteBuffer.allocate(65536);
            int bytesRead;
            int attempts = 0;

            /*
            Цикл повторяется пока попыток меньше 10 и получено 0 байтов
             */
            while ((bytesRead = channel.read(responseBuffer)) == 0 && attempts < 10) {
                attempts++;
                /*
                Ждем 100 миллисекунд
                Если поток прервался, то отмечаем поток как "прерванный" и вызываем IOException из-за прерывания потока
                 */
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Response wait interrupted");
                }
            }

            /*
            Если ответа нет
             */
//            if (bytesRead == -1) {
//                throw new IOException("No response from server");
//            }
            if (bytesRead == 0) {
                // Превышено количество попыток, ответ так и не пришел
                throw new IOException("No response from server (timeout)");
            }

            /*
            Переводим буфер в режим чтения (сбрасываем курсор в начало буфера)
             */
            responseBuffer.flip();
            /*
            Создается массив байтов, размер которого равен количеству байтов в ответе от сервера
            В созданный массив копируются данные из буфера
             */
            byte[] responseData = new byte[responseBuffer.remaining()];
            responseBuffer.get(responseData);

            /*
            ois оборачивается вокруг bais и сериализует данные из него, bais, будучи расширяющимся буфером, оборачивается вокруг массива байтов
            Из массива байтов собирается объект типа Object. Может выбросить CNFE
             */
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