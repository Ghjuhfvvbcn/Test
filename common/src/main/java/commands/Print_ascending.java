package commands;

/**
 * Команда выводит элементы коллекции в порядке возрастания.
 * <p>
 * Реализует интерфейс {@link Command}
 * @see Command
 * @see Executor
 */
public class Print_ascending implements Command{
    /**
     * Хранит имя команды (в данном случае "print_ascending")
     */
    private final String commandName = "print_ascending";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;
    /**
     * Создает объект {@link Print_ascending} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Print_ascending(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#print_ascending()} у объекта executor.
     */
    @Override
    public void execute(){
        executor.print_ascending();
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "print_ascending")
     */
    @Override
    public String getCommandName(){return commandName;}
}
