package commands;

/**
 * Команда удаляет из коллекции все элементы, ключ которых меньше, чем заданный. Команда имеет аргумент.
 * <p>
 * Реализует интерфейс {@link CommandWithArgument}
 * @see Command
 * @see CommandWithArgument
 * @see Executor
 */
public class Remove_lower_key implements CommandWithArgument{
    /**
     * Хранит имя команды (в данном случае "update").
     */
    private final String commandName = "remove_lower_key";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private Executor executor;
    /**
     * Хранит аргумент команды.
     */
    private Long argument;

    /**
     * Создает объект {@link Remove_lower_key} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Remove_lower_key(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#remove_lower_key(Long)} у объекта executor.
     */
    @Override
    public void execute(){
        executor.remove_lower_key(argument);
    }


    /**
     * {@inheritDoc}
     * Реализация метода {@link CommandWithArgument#setArgument(String)}.
     * Устанавливает переданное значение в качестве аргумента команды.
     * <p>
     * @param argument Аргумент команды Remove_lower_key
     * @apiNote Ограничения для аргумента:
     * <ul>
     *     <li>Должен быть положительным числом типа Long</li>
     * </ul>
     * @throws IllegalArgumentException если переданное значение не соответствует ограничениям для аргумента.
     */
    @Override
    public void setArgument(String argument) throws IllegalArgumentException{
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
     * @return имя команды (в данном случае "remove_lower_key")
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
