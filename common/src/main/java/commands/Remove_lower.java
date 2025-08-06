package commands;

/**
 * Команда удаляет из коллекции все меньшие, чем заданный, элементы
 * <p>
 * Реализует интерфейс {@link Command}
 * @see Command
 * @see Executor
 */
public class Remove_lower implements Command{
    /**
     * Хранит имя команды (в данном случае "remove_lower")
     */
    private final String commandName = "remove_lower";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;

    /**
     * Создает объект {@link Remove_lower} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Remove_lower(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#remove_lower()} у объекта executor.
     */
    @Override
    public void execute(){
        executor.remove_lower();
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "remove_lower")
     */
    @Override
    public String getCommandName(){return commandName;}
}
