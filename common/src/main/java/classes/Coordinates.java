package classes;

import java.io.Serializable;
import java.util.Objects;

/**
 * Неизменяемый класс для представления двумерных координат.
 * <p>
 * Хранит координаты х (Double) и y (Integer) с гарантией отсутствия null-значений.
 * Пример:
 * {@code new Coordinates(12.34, 56)}
 */
public final class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Координата x
     *
     * @apiNote Ограничения:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     * </ul>
     */
    private Double x;
    /**
     * Координата y
     *
     * @apiNote Ограничения:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     * </ul>
     */
    private Integer y;

    /**
     * Создает координаты по двум указанным параметрам.
     * @param x Первая координата (не может быть {@code null})
     * @param y Вторая координата (не может быть {@code null})
     * @throws IllegalArgumentException если x или y - {@code null}
     * @apiNote Пример: {@code new Coordinates(12.34, 56)}
     */
    public Coordinates(Double x, Integer y){
        setX(x);
        setY(y);
    }

    /**
     * Устанавливает указанное значение координаты x.
     * @param x координата x
     * @throws IllegalArgumentException если указанное значение - {@code null}
     */
    private void setX(Double x){
        if (x == null){
            throw new IllegalArgumentException("X coordinate value cannot be null");
        }else{
            this.x = x;
        }
    }

    /**
     * Возвращает значение координаты x.
     *
     * @return значение x (гарантированно не null)
     */
    public Double getX(){return x;}

    /**
     * Устанавливает указанное значение координаты y.
     * @param y координата y
     * @throws IllegalArgumentException если указанное значение - {@code null}
     */
    private void setY(Integer y){
        if (y == null){
            throw new IllegalArgumentException("Y coordinate value cannot be null");
        }else{
            this.y = y;
        }
    }

    /**
     * Возвращает значение координаты y.
     *
     * @return значение y (гарантированно не null)
     */
    public Integer getY(){return y;}

    /**
     * Возвращает строковое представление координат в формате:
     * "Coordinates[x={координата х}, y={координата y}]".
     * @return Непустую строку с основными полями объекта
     * @apiNote Формат может измениться в будущих версиях
     */
    @Override
    public String toString(){
        return String.format("Coordinates[x=%.2f, y=%d]", x, y);
    }

    /**
     * Сравнивает координаты по каждому из двух значений (координата x, координата y).
     *
     * @param other объект для сравнивания (может быть {@code null})
     * @implSpec Соответствует общему контракту {@link Object#equals(Object)}
     * @return true если объекты одинаковые
     */
    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(!(other instanceof Coordinates)) return false;
        Coordinates o = (Coordinates) other;
        return Double.compare(x, o.x) == 0 &&
               Objects.equals(y, o.y);
    }

    /**
     * Возвращает хэш-код на основе первой и второй координаты
     * @return значение хэш-кода
     */
    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }
}