package commands;

/**
 * Команда выводит информацию о коллекции.
 * <p>
 * Реализует интерфейс {@link Command}
 * @see Command
 * @see Executor
 */
public class Info implements Command{
    /**
     * Хранит имя команды (в данном случае "info")
     */
    private final String commandName = "info";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;
    /**
     * Создает объект {@link Info} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Info(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#info()} у объекта executor.
     */
    @Override
    public void execute(){
        executor.info();
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "info")
     */
    @Override
    public String getCommandName(){return commandName;}
}
