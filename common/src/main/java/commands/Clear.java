package commands;

/**
 * Команда удаляет все элементы из коллекции.
 * <p>
 * Реализует интерфейс {@link Command}
 * @see Command
 * @see Executor
 */
public class Clear implements Command{
    /**
     * Хранит имя команды (в данном случае "clear").
     */
    private final String commandName = "clear";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;
    /**
     * Создает объект {@link Clear} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public  Clear(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#clear()} у объекта executor.
     */
    @Override
    public void execute() {
        executor.clear();
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "clear")
     */
    @Override
    public String getCommandName(){return commandName;}
}
