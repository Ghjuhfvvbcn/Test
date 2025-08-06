package classes;

import java.io.Serializable;

/**
 * Перечисление музыкальных жанров.
 */
public enum MusicGenre implements Serializable {
    ROCK("Rock"),
    PSYCHEDELIC_CLOUD_RAP("Psychedelic cloud rap"),
    JAZZ("Jazz"),
    SOUL("Soul"),
    POST_ROCK("Post rock");

    private String genre;

    /**
     * Создает элемент перечисления с указанным отображаемым именем.
     * @param genre название жанра
     */
    MusicGenre(String genre){
        this.genre = genre;
    }

    /**
     * Возвращает удобно-читаемое название музыкального жанра.
     * <p>
     * @return genre удобно-читаемое название жанра.
     */
    public String getGenre(){return genre;}
}