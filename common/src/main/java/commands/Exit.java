package commands;

/**
 * Команда завершает работу программы без сохранения коллекции в файл.
 * <p>
 * Реализует интерфейс {@link Command}
 * @see Command
 * @see Executor
 */
public class Exit implements Command{
    /**
     * Хранит имя команды (в данном случае "exit").
     */
    private final String commandName = "exit";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;
    /**
     * Создает объект {@link Exit} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public  Exit(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#exit()} у объекта executor.
     */
    @Override
    public void execute(){
        executor.exit();
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "exit")
     */
    @Override
    public String getCommandName(){return commandName;}
}
