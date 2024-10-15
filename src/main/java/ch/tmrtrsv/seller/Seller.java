package ch.tmrtrsv.seller;

import ch.tmrtrsv.seller.commands.BuyCommand;
import ch.tmrtrsv.seller.commands.ReloadCommand;
import ch.tmrtrsv.seller.commands.SellCommand;
import ch.tmrtrsv.seller.tabcompleters.SellerTabCompleter;
import ch.tmrtrsv.seller.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Seller extends JavaPlugin {

    private Economy economy;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        if (!this.setupEconomy()) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.loadConfig();
        this.registerCommands();
        this.sendCredit();
    }

    @Override
    public void onDisable() {
        this.sendCredit();
    }

    private void sendCredit() {
        getLogger().info(Utils.color(""));
        getLogger().info(Utils.color("&f &#FBB908S&#FBAE07e&#FCA206l&#FC9706l&#FD8C05e&#FD8104r &#FE6A02v&#FE5F021&#FF5301.&#FF48000"));
        getLogger().info(Utils.color("&f Автор: &#FB3908Т&#FC2B06и&#FD1D04м&#FE0E02у&#FF0000р"));
        getLogger().info(Utils.color("&f Телеграм: &#008DFF@&#0086FFt&#007FFFm&#0078FFr&#0071FFt&#006BFFr&#0064FFs&#005DFFv&#0056FFc&#004FFFh"));
        getLogger().info(Utils.color(""));
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.economy = rsp.getProvider();
        return this.economy != null;
    }

    private void loadConfig() {
        this.saveDefaultConfig();
        this.reloadConfig();
        this.config = this.getConfig();
    }

    public Economy getEconomy() {
        return economy;
    }

    public FileConfiguration getPluginConfig() {
        return config;
    }

    private void registerCommands() {
        this.getCommand("sell").setExecutor(new SellCommand(this));
        this.getCommand("sell").setTabCompleter(new SellerTabCompleter(this));
        this.getCommand("buy").setExecutor(new BuyCommand(this));
        this.getCommand("buy").setTabCompleter(new SellerTabCompleter(this));
        this.getCommand("seller").setExecutor(new ReloadCommand(this));
        this.getCommand("seller").setTabCompleter(new SellerTabCompleter(this));
    }
}