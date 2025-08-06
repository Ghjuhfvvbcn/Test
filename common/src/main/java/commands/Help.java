package commands;

/**
 * Команда выводит список доступных команд.
 * <p>
 * Реализует интерфейс {@link Command}
 * @see Command
 * @see Executor
 */
public class Help implements Command{
    /**
     * Хранит имя команды (в данном случае "help")
     */
    private final String commandName = "help";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private Executor executor;

    /**
     * Создает объект {@link Help} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Help(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#help()} у объекта executor.
     */
    @Override
    public void execute(){
        executor.help();
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "help")
     */
    @Override
    public String getCommandName(){return commandName;}
}
