package commands;

/**
 * Команда выводит в консоль все элементы коллекции.
 * <p>
 * Реализует интерфейс {@link Command}
 * @see Command
 * @see Executor
 */
public class Show implements Command{
    /**
     * Хранит имя команды (в данном случае "show").
     */
    private final String commandName = "show";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;

    /**
     * Создает объект {@link Show} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Show(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#show()} у объекта executor.
     */
    @Override
    public void execute(){
        executor.show();
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "show")
     */
    @Override
    public String getCommandName(){return commandName;}
}
