package utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import classes.Coordinates;
import classes.MusicBand;
import classes.MusicGenre;
import classes.Studio;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.TreeMap;

/**
 * Представляет класс для чтения объектов типа {@link classes.MusicBand} из CSV-файла.
 */
public class ReaderCSV {
    /**
     * Статический метод, возвращающий коллекцию {@link java.util.TreeMap} объектов типа {@link classes.MusicBand} считанных из файла.
     * @apiNote Для чтения данных используются методы:
     * <ul>
     *     <li>{@link ReaderCSV#parseCreationDate(String)}</li>>
     * </ul>
     * <p>
     * @param file_csv файл, хранящий объекты типа {@link classes.MusicBand}
     * @return коллекцию {@link java.util.TreeMap} объектов типа {@link classes.MusicBand} считанных из файла
     */
    public static TreeMap<Long, MusicBand> loadFromFile(File file_csv){
        TreeMap<Long, MusicBand> musicBands = new TreeMap<>();
        try(InputStream is = new FileInputStream(file_csv);
            InputStreamReader isr = new InputStreamReader(is);
            CSVReader reader = new CSVReader(isr)){

            reader.skip(1);

            List<String[]> rows = reader.readAll();

            for (String[] parts : rows) {
                try {
                    if (parts.length != 9) {
                        System.err.printf("Invalid line format '%s' in the file '%s'\n", String.join(",", parts), file_csv);
                        continue;
                    }

                    Long id = Long.parseLong(parts[0].trim());
                    String name = parts[1].trim();
                    Double x = Double.parseDouble(parts[2].trim());
                    Integer y = Integer.parseInt(parts[3].trim());
                    ZonedDateTime creationDate = parseCreationDate(parts[4].trim());
                    int numberOfParticipants = Integer.parseInt(parts[5].trim());
                    String description = parts[6].trim();
                    MusicGenre genre = MusicGenre.valueOf(parts[7].trim().toUpperCase());
                    String studioName = parts[8].trim();

                    Coordinates coordinates = new Coordinates(x, y);
                    Studio studio = new Studio(studioName);
                    MusicBand musicBand = new MusicBand(
                            id,
                            name,
                            coordinates,
                            creationDate,
                            numberOfParticipants,
                            description,
                            genre,
                            studio
                    );
                    musicBands.put(id, musicBand);
                }catch(NumberFormatException e){
                    System.err.println("Invalid number format in line:\n" + String.join(",", parts));
                }catch (IllegalArgumentException e){
                    System.err.println("Invalid data in line:\n" + String.join(",", parts) + "\nError: " + e.getMessage());
                }
            }
        }catch (IOException | CsvException e){
            System.err.println("Error: " + e.getMessage());
        }
        return musicBands;
    }

    /**
     * Статический метод, извлекающий значение типа {@link java.time.ZonedDateTime} из переданной строки.
     * <p>
     * @param date Строка, содержащая значение типа {@link java.time.ZonedDateTime}
     * @return значение типа {@link java.time.ZonedDateTime}
     */
    public static ZonedDateTime parseCreationDate(String date){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss z");
            return ZonedDateTime.parse(date, formatter);
        }catch(DateTimeParseException e){
            throw new IllegalArgumentException(
                    String.format("Invalid date format: '%s'. Expected format: 'dd-MM-yyyy HH-mm-ss z'", date),
                    e
            );
        }
    }
}
