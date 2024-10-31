package ch.tmrtrsv.seller;

import ch.tmrtrsv.seller.commands.AutoSellCommand;
import ch.tmrtrsv.seller.commands.BuyCommand;
import ch.tmrtrsv.seller.commands.ReloadCommand;
import ch.tmrtrsv.seller.commands.SellCommand;
import ch.tmrtrsv.seller.tabcompleters.AutoSellTabCompleter;
import ch.tmrtrsv.seller.tabcompleters.BuyTabCompleter;
import ch.tmrtrsv.seller.tabcompleters.SellTabCompleter;
import ch.tmrtrsv.seller.tabcompleters.SellerTabCompleter;
import ch.tmrtrsv.seller.utils.Utils;
import org.bukkit.Bukkit;
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
            this.sendNoEconomyManager();
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
        Bukkit.getConsoleSender().sendMessage(Utils.color(""));
        Bukkit.getConsoleSender().sendMessage(Utils.color("&f &#FBB908S&#FBAE07e&#FCA206l&#FC9706l&#FD8C05e&#FD8104r &#FE6A02v&#FE5F021&#FF5302.&#FF48000"));
        Bukkit.getConsoleSender().sendMessage(Utils.color("&f Автор: &#FB3908Т&#FC2B06и&#FD1D04м&#FE0E02у&#FF0000р"));
        Bukkit.getConsoleSender().sendMessage(Utils.color("&f Телеграм: &#008DFF@&#0086FFt&#007FFFm&#0078FFr&#0071FFt&#006BFFr&#0064FFs&#005DFFv&#0056FFc&#004FFFh"));
        Bukkit.getConsoleSender().sendMessage(Utils.color(""));
    }

    private void sendNoEconomyManager() {
        Bukkit.getConsoleSender().sendMessage(Utils.color("&f Для работы плагина необходимо установить менеджер экономики"));
        Bukkit.getConsoleSender().sendMessage(Utils.color("&f Рекомендую &#FF8600E&#FF8900s&#FF8D00s&#FF9000e&#FF9400n&#FF9700t&#FF9B00i&#FF9E00a&#FFA200l&#FFA500s"));
        Bukkit.getConsoleSender().sendMessage(Utils.color(""));
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
        getCommand("sell").setExecutor(new SellCommand(this));
        getCommand("sell").setTabCompleter(new SellTabCompleter(this));
        getCommand("buy").setExecutor(new BuyCommand(this));
        getCommand("buy").setTabCompleter(new BuyTabCompleter(this));
        getCommand("seller").setExecutor(new ReloadCommand(this));
        getCommand("seller").setTabCompleter(new SellerTabCompleter(this));
        getCommand("autosell").setTabCompleter(new AutoSellTabCompleter(this));
        getCommand("autosell").setExecutor(new AutoSellCommand(this));
    }
}