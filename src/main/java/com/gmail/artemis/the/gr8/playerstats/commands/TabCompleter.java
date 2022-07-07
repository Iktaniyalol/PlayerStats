package com.gmail.artemis.the.gr8.playerstats.commands;

import com.gmail.artemis.the.gr8.playerstats.commands.cmdutils.TabCompleteHelper;
import com.gmail.artemis.the.gr8.playerstats.utils.EnumHandler;
import com.gmail.artemis.the.gr8.playerstats.utils.OfflinePlayerHandler;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private final List<String> commandOptions;
    private final TabCompleteHelper tabCompleteHelper;

    //TODO add "example" to the list
    public TabCompleter() {
        commandOptions = new ArrayList<>();
        commandOptions.add("top");
        commandOptions.add("player");
        commandOptions.add("server");
        commandOptions.add("me");

        tabCompleteHelper = new TabCompleteHelper();
    }

    //args[0] = statistic                                                                        (length = 1)
    //args[1] = commandOption (top/player/me)   OR substatistic (block/item/entitytype)          (length = 2)
    //args[2] = playerName                      OR commandOption (top/player/me)                 (length = 3)
    //args[3] =                                    playerName                                    (length = 4)

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> tabSuggestions = new ArrayList<>();

        //after typing "stat", suggest a list of viable statistics
        if (args.length >= 1) {
            String currentArg = args[args.length -1];

            if (args.length == 1) {
                tabSuggestions = getTabSuggestions(EnumHandler.getStatNames(), args[0]);
            }

            //after checking if args[0] is a viable statistic, suggest substatistic OR commandOptions
            else {
                String previousArg = args[args.length -2];

                if (EnumHandler.isStatistic(previousArg)) {
                    Statistic stat = EnumHandler.getStatEnum(previousArg);

                    if (stat != null) {
                        tabSuggestions = getTabSuggestions(getRelevantList(stat), currentArg);
                    }
                }

                //if previous arg = "player", suggest playerNames
                else if (previousArg.equalsIgnoreCase("player")) {
                    if (args.length >= 3 && EnumHandler.getEntityTypeStatNames().contains(args[args.length-3].toLowerCase())) {
                        tabSuggestions = commandOptions;
                    }
                    else {
                        tabSuggestions = getTabSuggestions(OfflinePlayerHandler.getOfflinePlayerNames(), currentArg);
                    }
                }

                //after a substatistic, suggest commandOptions
                else if (EnumHandler.isSubStatEntry(previousArg)) {
                    tabSuggestions = commandOptions;
                }
            }
        }
        return tabSuggestions;
    }

    private List<String> getTabSuggestions(List<String> completeList, String currentArg) {
        return completeList.stream()
                .filter(item -> item.toLowerCase().contains(currentArg.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<String> getRelevantList(Statistic stat) {
        switch (stat.getType()) {
            case BLOCK -> {
                return tabCompleteHelper.getAllBlockNames();
            }
            case ITEM -> {
                if (stat == Statistic.BREAK_ITEM) {
                    return tabCompleteHelper.getItemBrokenSuggestions();
                } else if (stat == Statistic.CRAFT_ITEM) {
                    return tabCompleteHelper.getAllItemNames();  //TODO fix
                } else {
                    return tabCompleteHelper.getAllItemNames();
                }
            }
            case ENTITY -> {
                return tabCompleteHelper.getEntityKilledSuggestions();
            }
            default -> {
                return commandOptions;
            }
        }
    }
}