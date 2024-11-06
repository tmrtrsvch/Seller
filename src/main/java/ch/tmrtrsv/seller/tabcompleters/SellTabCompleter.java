package ch.tmrtrsv.seller.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import ch.tmrtrsv.seller.Seller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SellTabCompleter implements TabCompleter {

    private final Seller plugin;

    public SellTabCompleter(Seller plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("sell")) {
            return handleItemCommandTabComplete(args);
        }
        return null;
    }

    private List<String> handleItemCommandTabComplete(String[] args) {
        if (args.length == 1) {
            return getItemSuggestions();
        } else if (args.length == 2) {
            return getAmountSuggestions();
        }
        return Collections.emptyList();
    }

    private List<String> getItemSuggestions() {
        List<String> suggestions = new ArrayList<>();
        ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("goods");

        if (itemsSection == null || !plugin.getConfig().getBoolean("tab-completer.goods.enabled", true)) {
            return suggestions;
        }

        Set<String> items = itemsSection.getKeys(false);
        int mode = plugin.getConfig().getInt("tab-completer.goods.mode", 1);

        for (String itemKey : items) {
            if (!itemsSection.isSet(itemKey + ".price.sell")) {
                continue;
            }

            if (mode == 1) {
                suggestions.add(itemKey.toLowerCase());
            } else if (mode == 2) {
                String itemName = itemsSection.getString(itemKey + ".name");
                if (itemName != null) {
                    suggestions.add(itemName);
                }
            }
        }
        return suggestions;
    }

    private List<String> getAmountSuggestions() {
        if (!plugin.getConfig().getBoolean("tab-completer.amount.enabled", true)) {
            return Collections.emptyList();
        }

        List<String> amounts = plugin.getConfig().getStringList("tab-completer.amount.numbers");
        List<String> suggestions = new ArrayList<>(amounts);

        suggestions.add("all");

        return suggestions;
    }
}