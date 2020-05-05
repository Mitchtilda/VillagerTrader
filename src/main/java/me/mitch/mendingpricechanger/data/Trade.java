package me.mitch.mendingpricechanger.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
class Trade { //No need to be public (can change later)

    private boolean amountSpecific;

    private ItemStack item;

    private Material fromOne;
    private Material itemOne;
    private float multiplierOne;

    private Material fromTwo;
    private Material itemTwo;
    private float multiplierTwo;

}
