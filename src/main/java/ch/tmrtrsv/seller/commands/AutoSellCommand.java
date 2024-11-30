package ch.tmrtrsv.seller.commands;

import ch.tmrtrsv.seller.Seller;
import ch.tmrtrsv.seller.utils.SellerUtils;
import ch.tmrtrsv.seller.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AutoSellCommand implements CommandExecutor {
    private final Seller plugin;
    private final Map<Player, Set<String>> autoSellItems = new HashMap<>();

    public AutoSellCommand(Seller plugin) {
        this.plugin = plugin;
        startAutoSellTask();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("seller.autosell")) {
            String noPermissionMessage = plugin.getPluginConfig().getString("messages.no_permission");
            player.sendMessage(Utils.color(noPermissionMessage));
            return true;
        }

        if (args.length == 0) {
            String specifyItemMessage = plugin.getPluginConfig().getString("messages.specify_autosell_item");
            player.sendMessage(Utils.color(specifyItemMessage));
            return true;
        }

        String itemName = args[0].toLowerCase();
        ConfigurationSection itemConfig = SellerUtils.getItemConfig(plugin.getPluginConfig(), itemName);

        if (itemConfig == null) {
            String itemNotFoundMessage = plugin.getPluginConfig().getString("messages.item_not_found");
            player.sendMessage(Utils.color(itemNotFoundMessage.replace("{item}", itemName)));
            return true;
        }

        if (!itemConfig.isConfigurationSection("price") || !itemConfig.getConfigurationSection("price").isSet("sell")) {
            String cannotSellMessage = plugin.getPluginConfig().getString("messages.cannot_sell_item");
            player.sendMessage(Utils.color(cannotSellMessage.replace("{item}", itemName)));
            return true;
        }

        if (autoSellItems.containsKey(player) && autoSellItems.get(player).contains(itemName)) {
            autoSellItems.get(player).remove(itemName);
            String autoSellItemRemoved = plugin.getPluginConfig().getString("messages.autosell_item_removed");
            player.sendMessage(Utils.color(autoSellItemRemoved.replace("{item}", itemName)));
        } else {
            autoSellItems.computeIfAbsent(player, k -> new HashSet<>()).add(itemName);
            String autoSellItemAdded = plugin.getPluginConfig().getString("messages.autosell_item_added");
            player.sendMessage(Utils.color(autoSellItemAdded.replace("{item}", itemName)));
        }
        return true;
    }

    private void startAutoSellTask() {
        int delay = plugin.getPluginConfig().getInt("autosell-delay", 30) * 20;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Set<String>> entry : autoSellItems.entrySet()) {
                    Player player = entry.getKey();
                    Set<String> items = entry.getValue();

                    for (String itemName : items) {
                        ConfigurationSection itemConfig = SellerUtils.getItemConfig(plugin.getPluginConfig(), itemName);

                        if (itemConfig == null || !itemConfig.isSet("price.sell")) continue;

                        Material material = Material.getMaterial(itemConfig.getName().toUpperCase());
                        if (material == null) continue;

                        int itemCount = SellerUtils.countItem(player, material);
                        if (itemCount > 0) {
                            double price = itemConfig.getDouble("price.sell");
                            double totalPrice = price * itemCount;

                            plugin.getEconomy().depositPlayer(player, totalPrice);
                            SellerUtils.removeItem(player, material, itemCount);

                            String sellMessage = plugin.getPluginConfig().getString("messages.sell");
                            player.sendMessage(Utils.color(sellMessage.replace("{item}", itemConfig.getString("name"))
                                    .replace("{price}", String.valueOf(totalPrice))
                                    .replace("{amount}", String.valueOf(itemCount))));

                        }
                    }
                }
            }
        }.runTaskTimer(plugin, delay, delay);
    }

    public Set<String> getAutoSellItemsForPlayer(Player player) {
        return autoSellItems.getOrDefault(player, new HashSet<>());
    }
}