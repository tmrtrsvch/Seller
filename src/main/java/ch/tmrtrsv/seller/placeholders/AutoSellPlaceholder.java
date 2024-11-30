package ch.tmrtrsv.seller.placeholders;

import ch.tmrtrsv.seller.utils.Utils;
import ch.tmrtrsv.seller.Seller;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.Set;

public class AutoSellPlaceholder extends PlaceholderExpansion {

    private final Seller plugin;

    public AutoSellPlaceholder(Seller plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "seller";
    }

    @Override
    public String getAuthor() {
        return "tmrtrsvch";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "";

        if (identifier.startsWith("autosell_")) {
            String itemName = identifier.replace("autosell_", "").toLowerCase();

            Set<String> autoSellItems = plugin.getAutoSellItemsForPlayer(player);
            boolean isAutoSell = autoSellItems != null && autoSellItems.contains(itemName);

            String enabled = plugin.getPluginConfig().getString("autosell_placeholder.enabled", "&aДа");
            String disabled = plugin.getPluginConfig().getString("autosell_placeholder.disabled", "&cНет");

            return Utils.color(isAutoSell ? enabled : disabled);
        }

        return null;
    }
}