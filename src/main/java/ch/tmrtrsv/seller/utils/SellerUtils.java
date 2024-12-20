package ch.tmrtrsv.seller.utils;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;

public class SellerUtils {

    public static ConfigurationSection getItemConfig(ConfigurationSection config, String itemName) {
        ConfigurationSection goodsSection = config.getConfigurationSection("goods");
        if (goodsSection == null) {
            return null;
        }

        if (goodsSection.contains(itemName.toLowerCase())) {
            return goodsSection.getConfigurationSection(itemName.toLowerCase());
        }

        for (String key : goodsSection.getKeys(false)) {
            String name = goodsSection.getString(key + ".name");
            if (name != null && name.equalsIgnoreCase(itemName)) {
                return goodsSection.getConfigurationSection(key);
            }
        }
        return null;
    }

    public static int countItem(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }

    public static void removeItem(Player player, Material material, int amount) {
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() != material) continue;

            int itemAmount = item.getAmount();
            if (amount >= itemAmount) {
                inventory.clear(i);
                amount -= itemAmount;
            } else {
                item.setAmount(itemAmount - amount);
                break;
            }

            if (amount <= 0) break;
        }
    }
}