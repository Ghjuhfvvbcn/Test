package commands;

import classes.MusicBand;

/**
 * Команда для обновления элемента коллекции по ключу. Команда имеет аргумент.
 * <p>
 * Реализует интерфейс {@link CommandWithArgument}
 * @see Command
 * @see CommandWithArgument
 * @see Executor
 */
public class Update implements CommandWithArgument<Long>{
    /**
     * Хранит имя команды (в данном случае "update").
     */
    private final String commandName = "update";
    /**
     * Хранит объект типа {@link Executor}, содержащий реализацию команды.
     */
    private final Executor executor;
    /**
     * Хранит аргумент команды.
     */
    private Long argument;

    private MusicBand musicBand;

    /**
     * Создает объект {@link Update} по указанному аргументу типа {@link Executor}.
     * @param executor Приемник команд
     */
    public Update(Executor executor){
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link Command#execute()}.
     * Вызывает метод {@link Executor#update(Long)} у объекта executor.
     */
    @Override
    public String execute(){
        return executor.update(argument);
    }

//    public String executeWithMusicBand(MusicBand musicBand) {
//        return executor.update_server(argument, musicBand); // Новый метод
//    }
    public String executeWithMusicBand(MusicBand musicBand) {
        if (argument == null) {
            return "Error: No key specified for update";
        }
        return executor.update_server(argument, musicBand);
    }

    /**
     * {@inheritDoc}
     * Реализация метода {@link CommandWithArgument#setArgument(String)}.
     * Устанавливает переданное значение в качестве аргумента команды.
     * <p>
     * @param argument Аргумент команды Update
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
     * @return имя команды (в данном случае "update")
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
