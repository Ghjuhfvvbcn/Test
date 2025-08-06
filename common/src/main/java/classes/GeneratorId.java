package classes;

/**
 * Класс, представляющий генератор уникальных id типа {@code Long}.
 * <p>
 * Значения начинаются с 1 и увеличиваются на 1 при каждом вызове {@ling #generatId()}
 */
class GeneratorId{
    /**
     * Счётчик id.
     */
    private static Long id = 1L;

    /**
     * Возвращает текущее значение счетчика и увеличивает его на 1.
     * <p>
     * @return id положительное целое значение
     */
    public static Long generateId(){
        return id++;
    }

    /**
     * Устанавливает переданное значение в качестве значения счетчика, если переданное значение больше текущего.
     * <p>
     * @param newId целое число
     */
    public static void setId(Long newId){
        if (id < newId){
            id = newId;
        }
    }

}