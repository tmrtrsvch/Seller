package ch.tmrtrsv.seller.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ch.tmrtrsv.seller.Seller;
import ch.tmrtrsv.seller.utils.SellerUtils;
import ch.tmrtrsv.seller.utils.Utils;

public class SellCommand implements CommandExecutor {

    private final Seller plugin;

    public SellCommand(Seller plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("seller.use")) {
            String noPermissionMessage = plugin.getPluginConfig().getString("messages.no_permission", "&cУ вас нет прав для выполнения этой команды.");
            player.sendMessage(Utils.color(noPermissionMessage));
            return true;
        }

        if (args.length == 0) {
            String specifyItemMessage = plugin.getPluginConfig().getString("messages.specify_item", "Пожалуйста, укажите предмет для продажи.");
            player.sendMessage(Utils.color(specifyItemMessage));
            return true;
        }

        if (args.length < 2) {
            String specifyAmountMessage = plugin.getPluginConfig().getString("messages.specify_amount", "Пожалуйста, укажите количество предметов.");
            player.sendMessage(Utils.color(specifyAmountMessage));
            return true;
        }

        String itemName = args[0];
        ConfigurationSection itemConfig = SellerUtils.getItemConfig(plugin.getPluginConfig(), itemName);

        if (itemConfig == null) {
            String itemNotFoundMessage = plugin.getPluginConfig().getString("messages.item_not_found", "Item не найден.");
            player.sendMessage(Utils.color(itemNotFoundMessage.replace("{item}", itemName)));
            return true;
        }

        Material material = Material.getMaterial(itemConfig.getName().toUpperCase());
        String displayName = itemConfig.getString("name");
        double price = itemConfig.getDouble("price.sell");
        int sellQuantity;

        try {
            sellQuantity = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            String specifyAmountMessage = plugin.getPluginConfig().getString("messages.specify_amount", "Пожалуйста, укажите количество предметов.");
            player.sendMessage(Utils.color(specifyAmountMessage));
            return true;
        }

        int itemCount = SellerUtils.countItem(player, material);
        if (itemCount < sellQuantity) {
            String noItemMessage = plugin.getPluginConfig().getString("messages.no_item", "У вас недостаточно предметов для продажи.");
            player.sendMessage(Utils.color(noItemMessage));
            return true;
        }

        double totalPrice = price * sellQuantity;
        plugin.getEconomy().depositPlayer(player, totalPrice);
        SellerUtils.removeItem(player, material, sellQuantity);

        String sellMessage = plugin.getPluginConfig().getString("messages.sell", "Вы успешно продали {amount} {item} за {price}.");
        player.sendMessage(Utils.color(sellMessage.replace("{item}", displayName)
                .replace("{price}", String.valueOf(totalPrice))
                .replace("{amount}", String.valueOf(sellQuantity))));

        return true;
    }
}