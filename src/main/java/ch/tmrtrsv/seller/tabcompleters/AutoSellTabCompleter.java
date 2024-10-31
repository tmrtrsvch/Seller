package ch.tmrtrsv.seller.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ch.tmrtrsv.seller.Seller;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AutoSellTabCompleter implements TabCompleter {

    private final Seller plugin;

    public AutoSellTabCompleter(Seller plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("autosell") && args.length == 1) {
            return getItemSuggestions();
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
}