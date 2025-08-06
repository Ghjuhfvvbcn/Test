package classes;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс, представляющий музыкальную студию.
 */
public final class Studio implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Название музыкальной студии.
     * @apiNote Ограничения:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     *     <li>Не может быть пустой строкой</li>
     * </ul>
     */
    private String name; //Поле не может быть null

    /**
     * Создает студию по одному указанному параметру (названию студии).
     * <p>
     * @param name Название студии
     * @throws IllegalArgumentException если:
     * <ul>
     *     <li>name равен {@code null}</li>
     *     <li>name является пустой строкой</li>
     * </ul>
     * @apiNote Пример:
     * {@code new Studio("Название студии")}
     */
    public Studio(String name){
        setName(name);
    }

    /**
     * Устанавливает название студии
     * @param name Название студии
     * @throws IllegalArgumentException если:
     * <ul>
     *     <li>name является {@code null}</li>
     *     <li>name является пустой строкой</li>
     * </ul>
     */
    private void setName(String name){
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Name of studio value cannot be empty or null");
        }else{
            this.name = name;
        }
    }

    /**
     * Возвращает название студии
     * <p>
     * @return name не {@code null} и не пустая строка
     */
    public String getName(){return name;}

    /**
     * Возвращает строковое представление студии в формате:
     * "Studio[name={название студии}]"
     * @return непустую строку с основным параметром объекта
     * @apiNote формат может измениться в будущих версиях
     */
    @Override
    public String toString(){
        return String.format("Studio[name=%s]", name);
    }

    /**
     * Сравнивает две студии по их названиям.
     * <p>
     * @param other объект для сравнения (может быть {@code null})
     * @return true если объекты одинаковые
     */
    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(!(other instanceof Studio)) return false;
        Studio o = (Studio) other;
        return Objects.equals(name, o.name);
    }

    /**
     * Возвращает хэш-код на основе названия студии.
     * <p>
     * @return значение хэш-кода
     */
    @Override
    public int hashCode(){
        return Objects.hash(name);
    }
}
