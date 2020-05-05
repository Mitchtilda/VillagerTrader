package me.mitch.mendingpricechanger.file;

import lombok.Getter;
import me.mitch.mendingpricechanger.PriceChangerPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    @Getter
    private FileConfiguration trades;

    private PriceChangerPlugin main;

    public FileManager(PriceChangerPlugin main)
    {
        this.main = main;

        trades = getYML(new File(main.getDataFolder(), "trades.yml"));
    }

    private FileConfiguration getYML(File file)
    {
        FileConfiguration cfg = new YamlConfiguration();

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            main.saveResource(file.getName(), false);
        }

        try {
            cfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return cfg;
    }
}
