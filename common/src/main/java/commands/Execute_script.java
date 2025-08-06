package commands;

/**
 * Команда выполняет скрипт из файла.
 * <p>
 * Реализует интерфейс {@link Command}
 * @see Command
 * @see Executor
 */
public class Execute_script implements Command{
    /**
     * Хранит имя команды (в данном случае "execute_script").
     */
    private final String commandName = "execute_script";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;
    /**
     * Создает объект {@link Execute_script} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Execute_script(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#execute_script()} у объекта executor.
     */
    @Override
    public void execute(){
        executor.execute_script();
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "execute_script")
     */
    @Override
    public String getCommandName(){return commandName;}
}
