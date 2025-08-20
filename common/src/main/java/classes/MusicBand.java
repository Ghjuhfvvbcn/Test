package classes;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;

/**
 * Класс, представляющий музыкальную группу с различными характеристиками.
 *<p>
 * Реализует интерфейс {@link Comparable<MusicBand>} для сравнения по названию группы.
 * Содержит компаратор {@link MusicBand#compareByDateAndName} для сортировки по дате создания и названию группы.
 */
public final class MusicBand implements Comparable<MusicBand>, Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Уникальный идентификатор группы.
     *
     * @apiNote Ограничения:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     *     <li>Значение должно быть больше 0</li>
     *     <li>Значение должно быть уникальным</li>
     *     <li>Значение должно генерироваться автоматически</li>
     * </ul>
     * @see GeneratorId#generateId()
     */
    private Long id;
    /**
     * Название группы.
     *
     * @apiNote Ограничения:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     *     <li>Не может быть пустой строкой</li>
     * </ul>
     */
    private String name;
    /**
     * Координаты группы.
     * <p>
     * @apiNote Ограничения:
     * <ul>
     *     <li>Не может быть null</li>
     * </ul>
     */
    private Coordinates coordinates;
    /**
     * Дата создания группы.
     * <p>
     * @apiNote Ограничения:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     *     <li>Значение генерируется автоматически</li>
     * </ul>
     */
    private java.time.ZonedDateTime creationDate;
    /**
     * Количество участников группы.
     * <p>
     * @apiNote Ограничения:
     * <ul>
     *     <li>Значение должно быть положительным целым числом</li>
     * </ul>
     */
    private int numberOfParticipants;
    /**
     * Описание группы.
     * <p>
     * @apiNote Ограничения:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     *     <li>Не может быть пустой строкой</li>
     * </ul>
     */
    private String description;
    /**
     * Музыкальный жанр группы.
     * <p>
     * @apiNote Особенности:
     * <ul>
     *   <li>Не может быть {@code null}</li>
     *   <li>Допустимые значения:
     *     <ul>
     *       <li>{@link MusicGenre#ROCK} - рок-музыка</li>
     *       <li>{@link MusicGenre#PSYCHEDELIC_CLOUD_RAP} - психоделический клауд-рэп</li>
     *       <li>{@link MusicGenre#JAZZ} - джаз</li>
     *       <li>{@link MusicGenre#SOUL} - соул-музыка</li>
     *       <li>{@link MusicGenre#POST_ROCK} - пост-рок</li>
     *     </ul>
     *   </li>
     * </ul>
     * @see MusicGenre
     */
    private MusicGenre genre;
    /**
     * Студия музыкальной группы.
     * <p>
     * @apiNote Ограничения:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     * </ul>
     */
    private Studio studio;

    /**
     * Создает новую музыкальную группу по указанным параметрам. Значение id и дата создания генерируется автоматически.
     * <p>
     * @param name название группы (непустая строка, не может быть {@code null})
     * @param coordinates Координаты группы (не {@code null})
     * @param numberOfParticipants Количество участников группы (положительное целое число)
     * @param description Описание группы (непустая строка, не может быть {@code null})
     * @param genre Музыкальный жанр группы (не может быть {@code null}), допустимые значения: ROCK, PSYCHEDELIC_CLOUD_RAP, JAZZ, SOUL, POST_ROCK
     * @param studio Студия музыкальной группы (не может быть {@code null})
     * @throws IllegalArgumentException если значение хотя бы одного из полей не соответствует требованиям
     */
    public MusicBand(String name, Coordinates coordinates, int numberOfParticipants, String description, MusicGenre genre, Studio studio){
        this.id = GeneratorId.generateId();
        setName(name);
        setCoordinates(coordinates);
        creationDate = ZonedDateTime.now();
        setNumberOfParticipants(numberOfParticipants);
        setDescription(description);
        setGenre(genre);
        setStudio(studio);
    }

    /**
     * Создает новую музыкальную группу по указанным параметрам (в том числе по указанным id и дате создания).
     * <p>
     * @param id Уникальный идентификатор группы (целое положительное число)
     * @param name название группы (непустая строка, не может быть {@code null})
     * @param coordinates Координаты группы (не {@code null})
     * @param creationDate Дата создания музыкальной группы в формате "dd-MM-yyyy HH-mm-ss z"
     * @param numberOfParticipants Количество участников группы (положительное целое число)
     * @param description Описание группы (непустая строка, не может быть {@code null})
     * @param genre Музыкальный жанр группы (не может быть {@code null}), допустимые значения: ROCK, PSYCHEDELIC_CLOUD_RAP, JAZZ, SOUL, POST_ROCK
     * @param studio Студия музыкальной группы (не может быть {@code null})
     * @throws IllegalArgumentException если значение хотя бы одного из полей не соответствует требованиям
     */
    public MusicBand(Long id, String name, Coordinates coordinates, ZonedDateTime creationDate, int numberOfParticipants, String description, MusicGenre genre, Studio studio){
        if(id == null){
            throw new IllegalArgumentException("Id value cannot be null");
        }else if(id <= 0){
            throw new IllegalArgumentException("Id should be a positive number");
        }else{
            this.id = id;
            GeneratorId.setId(id);
        }
        setName(name);
        setCoordinates(coordinates);
        if(creationDate == null){
            throw new IllegalArgumentException("Creation date value cannot be null");
        }else{
            this.creationDate = creationDate;
        }
        setNumberOfParticipants(numberOfParticipants);
        setDescription(description);
        setGenre(genre);
        setStudio(studio);
    }

    public MusicBand(){
        this.creationDate = ZonedDateTime.now();
    }

    /**
     * Устанавливает дату создания музыкальной группы.
     * <p>
     * @param creationDate Дата создания группы
     * @apiNote Ограничения для переданного значения:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     * </ul>
     * @throws IllegalArgumentException если переданное значение является {@code null}
     */
    public void setCreationDate(ZonedDateTime creationDate) {
        if (creationDate == null) {
            throw new IllegalArgumentException("Creation date value cannot be null");
        } else {
            this.creationDate = creationDate;
        }
    }


    /**
     * Устанавливает переданное значение в качестве id группы.
     * <p>
     * @param id Значение идентификатора группы
     */
    public void setId(Long id){
        this.id = id;
    }

    /**
     * Возвращает значение id музыкальной группы.
     * <p>
     * @return id идентификатор группы
     */
    public Long getId(){return id;}

    /**
     * Устанавливает переданное значение в качестве названия группы.
     * <p>
     * @param name Название группы
     * @apiNote Ограничения для переданного параметра:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     *     <li>Не может быть пустой строкой</li>
     * </ul>
     * @throws IllegalArgumentException если переданный параметр не соответствует хотя бы одному ограничению
     */
    public void setName(String name){
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Name value cannot be empty or null");
        }else{
            this.name = name;
        }
    }

    /**
     * Возвращает название музыкальной группы.
     * @return name Название музыкальной группы (гарантированно не {@code null})
     */
    public String getName(){return name;}

    /**
     * Устанавливает переданное значение в качестве координат группы.
     * <p>
     * @param coordinates Координаты группы
     * @apiNote Ограничения для переданного значения:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     * </ul>
     * @throws IllegalArgumentException если переданное значение является {@code null}
     */
    public void setCoordinates(Coordinates coordinates){
        if(coordinates == null){
            throw new IllegalArgumentException("Coordinates value cannot be null");
        }else{
            this.coordinates = coordinates;
        }
    }

    /**
     * Возвращает координаты музыкальной группы.
     * <p>
     * @return coordinates Координаты группы (гарантированно не {@code null})
     */
    public Coordinates getCoordinates(){return coordinates;}

    /**
     * Возвращает дату создания музыкальной группы в формате "dd-MM-yyyy HH-mm-ss z".
     * <p>
     * @return creationDate Дата создания группы (гарантированно не {@code null})
     */
    public ZonedDateTime getCreationDate(){return creationDate;}

    /**
     * Устанавливает указанный параметр в качестве количества участников группы.
     * <p>
     * @param numberOfParticipants Количество участников группы
     * @apiNote Ограничения для переданного значения:
     * <ul>
     *     <li>Должно быть целым положительным числом</li>
     * </ul>
     * @throws IllegalArgumentException если переданное значение не соответствует ограничениям.
     */
    public void setNumberOfParticipants(int numberOfParticipants){
        if(numberOfParticipants <= 0){
            throw new IllegalArgumentException("Number of participants should be a positive number");
        }else{
            this.numberOfParticipants = numberOfParticipants;
        }
    }

    /**
     * Возвращает значение типа int - количество участников группы.
     * <p>
     * @return numberOfParticipants Количество участников группы (гарантированно целое положительное число)
     */
    public int getNumberOfParticipants(){return  numberOfParticipants;}

    /**
     * Устанавливает переданную строку в качестве описания музыкальной группы.
     * <p>
     * @param description Описание музыкальной группы
     * @apiNote Ограничения для переданного значения:
     * <ul>
     *     <li>Не может быть пустой строкой</li>
     *     <li>Не может быть {@code null}</li>
     * </ul>
     * @throws IllegalArgumentException если переданное значение не соответствует ограничениям
     */
    public void setDescription(String description){
        if(description == null || description.trim().isEmpty()){
            throw new IllegalArgumentException("Description cannot be empty or null");
        }else{
            this.description = description;
        }
    }

    /**
     * Возвращает строковое описание музыкальной группы.
     * <p>
     * @return description Описание музыкальной группы (гарантированно не {@code null})
     */
    public String getDescription(){return description;}

    /**
     * Устанавливает переданное значение в качестве жанра музыкальной группы.
     * <p>
     * @param genre Жанр музыкальной группы.
     * @apiNote Допустимые варианты для переданного значения:
     * <ul>
     *       <li>{@link MusicGenre#ROCK} - рок-музыка</li>
     *       <li>{@link MusicGenre#PSYCHEDELIC_CLOUD_RAP} - психоделический клауд-рэп</li>
     *       <li>{@link MusicGenre#JAZZ} - джаз</li>
     *       <li>{@link MusicGenre#SOUL} - соул-музыка</li>
     *       <li>{@link MusicGenre#POST_ROCK} - пост-рок</li>
     * </ul>
     * @throws IllegalArgumentException если переданное значение не является одним из допустимых.
     */
    public void setGenre(MusicGenre genre){
        if(genre == null){
            throw new IllegalArgumentException("Music genre cannot be null");
        }else{
            this.genre = genre;
        }
    }

    /**
     * Возвращает жанр музыкальной группы.
     * <p>
     * @return genre Жанр музыкальной группы (гарантированно одно из следующих значений: ROCK, PSYCHEDELIC_CLOUD_RAP, JAZZ, SOUL, POST_ROCK)
     */
    public MusicGenre getGenre(){return genre;}

    /**
     * Устанавливает переданное значение в качестве студии музыкальной группы.
     * <p>
     * @param studio Студия музыкальной группы
     * @apiNote Ограничения для переданного значения:
     * <ul>
     *     <li>Не должно быть {@code null}</li>
     * </ul>
     * @throws IllegalArgumentException если переданное значение является {@code null}
     */
    public void setStudio(Studio studio){
        if(studio == null){
            throw new IllegalArgumentException("Studio cannot be null");
        }else{
            this.studio = studio;
        }
    }

    /**
     * Возвращает объект типа Studio - студию музыкальной группы.
     * <p>
     * @return studio Студия музыкальной группы (гарантированно не {@code null})
     */
    public Studio getStudio(){return studio;}

    /**
     * Компаратор для сортировки коллекций {@link MusicBand} по дате создания и имени
     * <p>
     * Сравнение выполняется в следующем порядке:
     * <ol>
     *     <li>По полю {@link MusicBand#getCreationDate()} (в хронологическом порядке)</li>
     *     <li>При совпадении дат - по {@link MusicBand#getName()} (лексикографический порядок)</li>
     * </ol>
     * @see Comparator
     * @see MusicBand#getCreationDate()
     * @see MusicBand#getName()
     */
    public static final Comparator<MusicBand> compareByDateAndName = Comparator
            .comparing(MusicBand::getCreationDate)
            .thenComparing(MusicBand::getName);

    /**
     * Переопределение метода {@link #compareTo(MusicBand)} для реализации интерфейса {@link java.lang.Comparable<MusicBand>},
     * <p>
     * Сравнение происходит по названию группы.
     * @param other_band Объект для сравнения
     * @return -1 если название текущей группы меньше названия переданной группы, 0 - если название текущей группы равно названию переданной группы, 1 - если название текущей группы больше названия переданной группы.
     */
    @Override
    public int compareTo(MusicBand other_band){
        return name.compareTo(other_band.getName());
    }

    /**
     * Возвращает строковое представление музыкальной группы в многострочном формате.
     * <p>
     * Формат вывода:
     * <pre>
     * MusicBand[
     *   id={идентификатор группы}
     *   name={название группы}
     *   coordinates={координаты группы}
     *   creationDate={дата в формате дд-мм-гггг чч-мм-сс таймзона}
     *   numberOfParticipants={колличество участников группы}
     *   description={описание группы}
     *   genre={жанр музыкальной группы}
     *   studio={студия группы}
     * ]
     * </pre>
     *
     * @return строка, содержащая все основные поля объекта в читаемом формате
     * @apiNote Особенности:
     * <ul>
     *   <li>Дата форматируется по шаблону "dd-MM-yyyy HH-mm-ss z"</li>
     *   <li>Поля-объекты выводятся через их собственные {@code toString()}</li>
     * </ul>
     */
    @Override
    public String toString(){
        return String.format("MusicBand[\n" +
                "id=%d\n" +
                "name=%s\n" +
                "coordinates=%s\n" +
                "creationDate=%s\n" +
                "numberOfParticipants=%d\n" +
                "description=%s\n" +
                "genre=%s\n" +
                "studio=%s\n" +
                "]",
                id, name, coordinates, creationDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss z")), numberOfParticipants, description, genre, studio);
    }

    /**
     * Сравнивает две музыкальные группы по каждому параметру.
     * <p>
     * @param other Группа для сравнения (может быть {@code null})
     * @implSpec Соответствует общему контракту {@link Object#equals(Object)}
     * @return true уесли объекты одинаковые
     */
    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(!(other instanceof MusicBand)) return false;
        MusicBand o = (MusicBand) other;
        return Objects.equals(id, o.id) &&
                Objects.equals(name, o.name) &&
                Objects.equals(coordinates, o.coordinates) &&
                Objects.equals(creationDate, o.creationDate) &&
                numberOfParticipants == o.numberOfParticipants &&
                Objects.equals(description, o.description) &&
                Objects.equals(genre, o.genre) &&
                Objects.equals(studio, o.studio);
    }

    /**
     * Возвращает значение хэш-кода группы на основе всех ее параметров.
     * <p>
     * @return Значение хэш-кода музыкальной группы
     */
    @Override
    public int hashCode(){
        return Objects.hash(id,
                name,
                coordinates,
                creationDate,
                numberOfParticipants,
                description,
                genre,
                studio);
    }
}