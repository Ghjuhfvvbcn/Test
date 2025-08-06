package commands;

/**
 * Интерфейс для реализации команд, имеющих аргументы.
 * Расширяет интерфейс {@link Command}
 * @see Command
 */
public interface CommandWithArgument<T> extends Command{
    /**
     * Если переданное значение соответствует ограничениям для аргумента, то устанавливает его в качестве аргумента команды.
     * <p>
     * @param argument аргумент команды
     * @throws IllegalArgumentException если переданное значение не соответствует ограничениям для аргумента.
     */
    void setArgument(String argument);

    /**
     * Возвращает аргумент команды.
     * <p>
     * @return аргумент команды
     */
    T getArgument();
}
