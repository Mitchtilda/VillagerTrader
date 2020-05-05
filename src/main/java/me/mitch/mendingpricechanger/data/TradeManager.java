package me.mitch.mendingpricechanger.data;

import lombok.Getter;
import me.mitch.mendingpricechanger.PriceChangerPlugin;
import me.mitch.mendingpricechanger.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeManager {

    private PriceChangerPlugin main;

    @Getter
    private Map<Villager.Profession, List<Trade>> trades = new HashMap<>();

    public TradeManager(PriceChangerPlugin main) {
        this.main = main;

        load();
    }

    public void patchVillager(Villager v)
    {
        if (!trades.containsKey(v.getProfession()))
            return;

        Villager.Profession profession = v.getProfession();

        //Prevents the professions from bugging
        if (v.getVillagerExperience() == 0)
            v.setVillagerExperience(1);

        //Loops through all villager trades
        for (int i = 0; i < v.getRecipeCount(); i++) {
            MerchantRecipe recipe = v.getRecipe(i); //Gets the trade

            ItemStack one = null; //Creates new variables for the items
            ItemStack two = null;

            int tradeCount = recipe.getMaxUses(); //Gets the trade limit

            ItemStack result = recipe.getResult(); //Gets the received item

            for (Trade trade : trades.get(v.getProfession())) { //Loops through all parsed trades
                //Checks if the trade matches amount (if specified) and item
                if (!((trade.isAmountSpecific() && trade.getItem().getAmount() == result.getAmount()) || ItemUtil.isSimilar(trade.getItem(), result)))
                    continue;

                //If one is not assigned and it matches the first item on the trade, assign one
                if (one == null && recipe.getIngredients().get(0).isSimilar(new ItemStack(trade.getFromOne())))
                    one = new ItemStack(trade.getItemOne(), (int) (recipe.getIngredients().get(0).getAmount() * trade.getMultiplierOne()));

                //If one is assigned, two isn't, there is a second item and it matches, assign two
                if (one != null && two == null && recipe.getIngredients().size() > 1 && recipe.getIngredients().get(1).isSimilar(new ItemStack(trade.getFromTwo())))
                    two = new ItemStack(trade.getItemTwo(), (int) (recipe.getIngredients().get(1).getAmount() * trade.getMultiplierTwo()));

            }
            //Creates a new recipe
            MerchantRecipe newRecipe = new MerchantRecipe(result, tradeCount);

            //Adds the assigned ingredients
            if (one != null)
                newRecipe.addIngredient(one);
            if (two != null)
                newRecipe.addIngredient(two);

            //Skip if no ingredients are assigned
            if (newRecipe.getIngredients().isEmpty())
                continue;

            //Sets the new trade in place of the old one
            v.setRecipe(i, newRecipe);

        }

        //Sets the profession just in case
        v.setProfession(profession);
    }

    private void load() //I'm sorry
    {
        FileConfiguration cfg = main.getFileManager().getTrades(); //config

        ConfigurationSection trades = cfg.getConfigurationSection("trades"); //trades section

        for (String type : trades.getKeys(false)) // profession
        {
            Villager.Profession profession = Villager.Profession.valueOf(type.toUpperCase()); //parsed profession

            List<Trade> list = new ArrayList<>(); //new list of trades for professional

            for (String index : trades.getConfigurationSection(type).getKeys(false)) //Iterating through trades
            {
                ConfigurationSection itemsection = trades.getConfigurationSection(type + "." + index + ".item"); //item section

                ItemStack item = new ItemStack(Material.valueOf(itemsection.getString("type"))); //parses material into a new itemstack

                boolean amountspecific = false; //wheter or not the trade is amount specific

                if (itemsection.contains("amount")) //if it contains a specific amount
                {
                    item.setAmount(itemsection.getInt("amount")); //sets the item amount
                    amountspecific = true; //lists the trade as amount specific
                }

                if (itemsection.contains("enchantments")) //sets the enchantments
                    for (String enchantname : itemsection.getConfigurationSection("enchantments").getKeys(false))
                        item.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchantname)), itemsection.getInt("enchantments." + enchantname));


                ConfigurationSection pricingsection = trades.getConfigurationSection(type + "." + index + ".pricing");

                Material fromOne = Material.valueOf(pricingsection.getString("1.from")); //loading values 1
                Material toOne = Material.valueOf(pricingsection.getString("1.to"));
                float multOne = (float) pricingsection.getDouble("1.multiplier");

                Material fromTwo = Material.valueOf(pricingsection.getString("2.from", "AIR")); //loading values 2
                Material toTwo = Material.valueOf(pricingsection.getString("2.to", "AIR"));
                float multTwo = (float) pricingsection.getDouble("2.multiplier", 1D);


                list.add(new Trade(amountspecific, item, fromOne, toOne, multOne, fromTwo, toTwo, multTwo)); //adds the trade to the profession's list
            }


            this.trades.put(profession, list);
        }
    }
}
