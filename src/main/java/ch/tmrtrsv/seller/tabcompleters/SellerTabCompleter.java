package ch.tmrtrsv.seller.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ch.tmrtrsv.seller.Seller;

import java.util.Collections;
import java.util.List;

public class SellerTabCompleter implements TabCompleter {

    private final Seller plugin;

    public SellerTabCompleter(Seller plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("seller")) {
            if (args.length == 1 && sender.hasPermission("seller.reload")) {
                return Collections.singletonList("reload");
            }
        }
        return null;
    }
}