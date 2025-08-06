package commands;

/**
 * Команда выводит элементы коллекции в порядке убывания.
 * <p>
 * Реализует интерфейс {@link Command}
 * @see Command
 * @see Executor
 */
public class Print_descending implements Command{
    /**
     * Хранит имя команды (в данном случае "print_descending")
     */
    private final String commandName = "print_descending";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;
    /**
     * Создает объект {@link Print_descending} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Print_descending(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#print_descending()} у объекта executor.
     */
    @Override
    public void execute(){
        executor.print_descending();
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "print_descending")
     */
    @Override
    public String getCommandName(){return commandName;}
}