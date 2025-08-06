package utils;

import commands.*;

import java.util.Map;
import java.util.TreeMap;

/**
 * Класс представляет объект для работы со списком команд.
 */
public class CommandMap {
    /**
     * Статический метод по указанному объекту типа {@link commands.Executor} создает коллекцию {@link java.util.TreeMap} объектов-команд типа {@link commands.Command}, ключами которых являются названия команд.
     * @param executor Объект, который передается в качестве параметра в конструкторе объектов-команд.
     * Пример: {@code new Help(executor)}
     * @return Возвращает ссылку типа {@java.util.Map} (тип ключа String, тип значения {@link commands.Command}) на коллекцию типа {@link java.util.TreeMap}
     */
    public static Map<String, Command> createMapWithCommands(Executor executor) {
        Map<String, Command> commands = new TreeMap<>();

        Help help = new Help(executor);
        Info info = new Info(executor);
        Show show = new Show(executor);
        Clear clear = new Clear(executor);
        Save save = new Save(executor);
        Exit exit = new Exit(executor);
        Print_ascending print_ascending = new Print_ascending(executor);
        Print_descending print_descending = new Print_descending(executor);

        Remove_key remove_key = new Remove_key(executor);
        Remove_lower_key remove_lower_key = new Remove_lower_key(executor);
        Filter_starts_with_name filter_starts_with_name = new Filter_starts_with_name(executor);

        Insert insert = new Insert(executor);
        Update update = new Update(executor);
        Remove_lower remove_lower = new Remove_lower(executor);
        Replace_if_lower replace_if_lower = new Replace_if_lower(executor);

        Execute_script execute_script = new Execute_script(executor);

        commands.put(help.getCommandName(), help);
        commands.put(info.getCommandName(), info);
        commands.put(show.getCommandName(), show);
        commands.put(clear.getCommandName(), clear);
        commands.put(save.getCommandName(), save);
        commands.put(exit.getCommandName(), exit);
        commands.put(print_ascending.getCommandName(), print_ascending);
        commands.put(print_descending.getCommandName(), print_descending);

        commands.put(remove_key.getCommandName(), remove_key);
        commands.put(remove_lower_key.getCommandName(), remove_lower_key);
        commands.put(filter_starts_with_name.getCommandName(), filter_starts_with_name);

        commands.put(insert.getCommandName(), insert);
        commands.put(update.getCommandName(), update);
        commands.put(remove_lower.getCommandName(), remove_lower);
        commands.put(replace_if_lower.getCommandName(), replace_if_lower);

        commands.put(execute_script.getCommandName(), execute_script);
        return commands;
    }
}
