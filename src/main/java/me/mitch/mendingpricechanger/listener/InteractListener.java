package me.mitch.mendingpricechanger.listener;

import me.mitch.mendingpricechanger.PriceChangerPlugin;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class InteractListener implements Listener {

    private PriceChangerPlugin main;

    public InteractListener(PriceChangerPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onInteract(PlayerInteractAtEntityEvent e)
    {
        if (!(e.getRightClicked() instanceof Villager))
            return;

        Villager v = (Villager) e.getRightClicked();

        main.getTradeManager().patchVillager(v);


    }


}
