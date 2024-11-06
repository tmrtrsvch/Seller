package ch.tmrtrsv.seller.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ch.tmrtrsv.seller.Seller;
import ch.tmrtrsv.seller.utils.SellerUtils;
import ch.tmrtrsv.seller.utils.Utils;

public class BuyCommand implements CommandExecutor {

    private final Seller plugin;

    public BuyCommand(Seller plugin) {
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
            String specifyItemMessage = plugin.getPluginConfig().getString("messages.specify_item", "Пожалуйста, укажите предмет для покупки.");
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

        if (!itemConfig.isSet("price.buy")) {
            String cannotBuyMessage = plugin.getPluginConfig().getString("messages.cannot_buy_item", "Этот предмет нельзя купить.");
            player.sendMessage(Utils.color(cannotBuyMessage.replace("{item}", itemName)));
            return true;
        }

        Material material = Material.getMaterial(itemConfig.getName().toUpperCase());
        String displayName = itemConfig.getString("name");
        double price = itemConfig.getDouble("price.buy");
        int buyQuantity;

        try {
            buyQuantity = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            String specifyAmountMessage = plugin.getPluginConfig().getString("messages.specify_amount", "Пожалуйста, укажите количество предметов.");
            player.sendMessage(Utils.color(specifyAmountMessage));
            return true;
        }

        double totalPrice = price * buyQuantity;
        if (!plugin.getEconomy().has(player, totalPrice)) {
            String notEnoughMoneyMessage = plugin.getPluginConfig().getString("messages.not_enough_money", "У вас недостаточно средств.");
            player.sendMessage(Utils.color(notEnoughMoneyMessage));
            return true;
        }

        ItemStack itemStack = new ItemStack(material, buyQuantity);
        PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            String inventoryFullMessage = plugin.getPluginConfig().getString("messages.inventory_full", "Ваш инвентарь полон.");
            player.sendMessage(Utils.color(inventoryFullMessage));
            return true;
        }

        plugin.getEconomy().withdrawPlayer(player, totalPrice);
        inventory.addItem(itemStack);

        String buyMessage = plugin.getPluginConfig().getString("messages.buy", "Вы успешно купили {amount} {item} за {price}.");
        player.sendMessage(Utils.color(buyMessage.replace("{item}", displayName)
                .replace("{price}", String.valueOf(totalPrice))
                .replace("{amount}", String.valueOf(buyQuantity))));

        return true;
    }
}