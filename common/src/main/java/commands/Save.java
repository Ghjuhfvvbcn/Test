package commands;

/**
 * Команда сохраняет в файл все элементы коллекции.
 * <p>
 * Реализует интерфейс {@link Command}
 * @see Command
 * @see Executor
 */
public class Save implements Command{
    /**
     * Хранит имя команды (в данном случае "save").
     */
    private final String commandName = "save";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;

    /**
     * Создает объект {@link Show} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Save(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#save()} у объекта executor.
     */
    @Override
    public void execute(){
        executor.save();
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "save")
     */
    @Override
    public String getCommandName(){return commandName;}
}
