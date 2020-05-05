package me.mitch.mendingpricechanger.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;

public class ItemUtil {

    public static boolean isSimilar(ItemStack one, ItemStack two)
    {
        if (one == null || two == null) //If either of the items is null
            return false;


        if (one.getType() != two.getType()) //If the item materials do not match
            return false;

        Map<Enchantment, Integer> enchantsOne = getTrueEnchants(one); //Obtains the true enchants
        Map<Enchantment, Integer> enchantsTwo = getTrueEnchants(two);

        if (enchantsOne.size() != enchantsTwo.size()) //Compares enchantment count
            return false;

        for (Enchantment enchantment : enchantsOne.keySet()) { //Loops through all enchantments
            int lvl = one.getEnchantmentLevel(enchantment); //Obtains enchantment level

            if (!enchantsTwo.containsKey(enchantment) || enchantsTwo.get(enchantment) != lvl) //Compares enchantments and levels
                return false;
        }

        return true;
    }

    private static Map<Enchantment, Integer> getTrueEnchants(ItemStack item) //Obtain the real enchantments from an item (API was being weird)
    {
        Map<Enchantment, Integer> map = new HashMap<>(item.getEnchantments()); //Creates a clone of the item's enchantments

        if (item.getItemMeta() instanceof EnchantmentStorageMeta) { //If the item has stored enchants (In case of books)
            EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) item.getItemMeta(); //Obtains the stored meta

            for (Enchantment enchantment : enchantMeta.getStoredEnchants().keySet()) //Puts the stored enchants if absent
                map.putIfAbsent(enchantment, enchantMeta.getStoredEnchantLevel(enchantment));
        }

        return map;
    }
}
