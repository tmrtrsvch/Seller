package ch.tmrtrsv.seller.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ch.tmrtrsv.seller.Seller;
import ch.tmrtrsv.seller.utils.Utils;

public class ReloadCommand implements CommandExecutor {

    private final Seller plugin;

    public ReloadCommand(Seller plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            String reloadUsageMessage = plugin.getPluginConfig().getString("messages.reload_usage", "&cИспользование: /seller reload.");
            sender.sendMessage(Utils.color(reloadUsageMessage));
            return true;
        }

        if (!args[0].equalsIgnoreCase("reload")) {
            String reloadUsageMessage = plugin.getPluginConfig().getString("messages.reload_usage", "&cИспользование: /seller reload.");
            sender.sendMessage(Utils.color(reloadUsageMessage));
            return true;
        }

        if (!sender.hasPermission("seller.reload")) {
            String noPermissionMessage = plugin.getPluginConfig().getString("messages.no_permission", "&cУ вас нет прав для выполнения этой команды.");
            sender.sendMessage(Utils.color(noPermissionMessage));
            return true;
        }
        plugin.reloadConfig();

        String reloadSuccessMessage = plugin.getPluginConfig().getString("messages.config_reloaded", "&aКонфигурация успешно перезагружена.");
        sender.sendMessage(Utils.color(reloadSuccessMessage));
        return true;
    }
}