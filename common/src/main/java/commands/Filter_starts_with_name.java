package commands;

/**
 * Команда выводит элементы, значение поля name которых начинается с указанной подстроки. Команда имеет аргумент.
 * <p>
 * Реализует интерфейс {@link CommandWithArgument}
 * @see Command
 * @see CommandWithArgument
 * @see Executor
 */
public class Filter_starts_with_name implements CommandWithArgument{
    /**
     * Хранит имя команды (в данном случае "filter_starts_with_argument")
     */
    private final String commandName = "filter_starts_with_name";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;
    /**
     * Хранит аргумент команды.
     */
    private String argument;

    /**
     * Создает объект {@link Filter_starts_with_name} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Filter_starts_with_name(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#filter_starts_with_name(String)} у объекта executor.
     */
    @Override
    public void execute(){
        executor.filter_starts_with_name(argument);
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link CommandWithArgument#setArgument(String)}.
     * Устанавливает переданное значение в качестве аргумента команды.
     * <p>
     * @param argument Аргумент команды Filter_starts_with_name
     * @apiNote Ограничения для аргумента:
     * <ul>
     *     <li>Не может быть {@code null}</li>
     *     <li>Не может быть пустой строкой</li>
     * </ul>
     * @throws IllegalArgumentException если переданное значение не соответствует ограничениям для аргумента.
     */
    @Override
    public void setArgument(String argument) throws IllegalArgumentException{
        if(argument == null || argument.trim().isEmpty()){
            throw new IllegalArgumentException("Command '" + commandName + "' failed: Argument cannot be empty or null");
        }
        this.argument = argument;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "filter_starts_with_name")
     */
    @Override
    public String getCommandName(){return commandName;}

    /**
     * Возвращает аргумент команды (значение типа {@link String}
     * <p>
     * @return аргумент команды (значение типа {@link String}
     */
    @Override
    public String getArgument(){return argument;}
}
