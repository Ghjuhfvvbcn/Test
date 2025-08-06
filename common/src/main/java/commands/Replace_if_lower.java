package commands;

/**
 * Команда заменяет значение по ключу, если новое значение меньше старого. Команда имеет аргумент.
 * <p>
 * Реализует интерфейс {@link CommandWithArgument}
 * @see Command
 * @see CommandWithArgument
 * @see Executor
 */
public class Replace_if_lower implements CommandWithArgument{
    /**
     * Хранит имя команды (в данном случае "replace_if_lower").
     */
    private static final String commandName = "replace_if_lower";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;
    /**
     * Хранит аргумент команды.
     */
    private Long argument;

    /**
     * Создает объект {@link Replace_if_lower} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Replace_if_lower(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#replace_if_lower(Long)} у объекта executor.
     */
    @Override
    public void execute(){
        executor.replace_if_lower(argument);
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link CommandWithArgument#setArgument(String)}.
     * Устанавливает переданное значение в качестве аргумента команды.
     * <p>
     * @param argument Аргумент команды Replace_if_lower
     * @apiNote Ограничения для аргумента:
     * <ul>
     *     <li>Должен быть положительным числом типа Long</li>
     * </ul>
     * @throws IllegalArgumentException если переданное значение не соответствует ограничениям для аргумента.
     */
    @Override
    public void setArgument(String argument){
        Long arg;
        try{
            arg = Long.parseLong(argument.trim());
            if(arg <= 0) throw new NumberFormatException();
        }catch(NumberFormatException | NullPointerException e){
            throw new IllegalArgumentException("Command '" + commandName + "' failed: '" + argument + "' is not a valid Long number.");
        }
        this.argument = arg;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#getCommandName()}.
     * Возвращает имя команды.
     * <p>
     * @return имя команды (в данном случае "replace_if_lower")
     */
    @Override
    public String getCommandName(){return commandName;}

    /**
     * Возвращает аргумент команды (значение типа {@link Long}
     * <p>
     * @return аргумент команды (значение типа {@link Long}
     */
    @Override
    public Long getArgument(){return argument;}
}
