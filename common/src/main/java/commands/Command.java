package commands;

/**
 * Интерфейс для реализации команд без аргументов.
 */
public interface Command {
    /**
     * Выполняет команду.
     */
    void execute();

    /**
     * Возвращает имя команды.
     * @return имя команды.
     */
    String getCommandName();
}
