package me.mitch.mendingpricechanger;

import lombok.Getter;
import me.mitch.mendingpricechanger.data.TradeManager;
import me.mitch.mendingpricechanger.file.FileManager;
import me.mitch.mendingpricechanger.listener.InteractListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PriceChangerPlugin extends JavaPlugin {

    @Getter
    private TradeManager tradeManager;
    @Getter
    private FileManager fileManager;

    @Override
    public void onEnable() {
        fileManager = new FileManager(this);
        tradeManager = new TradeManager(this);

        Bukkit.getPluginManager().registerEvents(new InteractListener(this), this);

        getLogger().info("Loaded");
    }
}
