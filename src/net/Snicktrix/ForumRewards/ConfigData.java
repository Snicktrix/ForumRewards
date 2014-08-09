package net.Snicktrix.ForumRewards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by Luke on 8/5/14.
 */
public class ConfigData {
    // Main Class
    private ForumRewards forumRewards;

    // Plugin Specific Variables
    HashMap<String, Integer> credits = new HashMap<String, Integer>();

    //**************************************//

    public ConfigData (ForumRewards forumRewards) {
        this.forumRewards = forumRewards;

        this.loadConfig();
    }

	public int getPlayerCredits(Player player) {
		if (this.credits.containsKey(player.getName().toLowerCase())) {
			return this.credits.get(player.getName().toLowerCase());
		}
		return 0;
	}

    public void loadConfig() {
        // Set up the config
        this.forumRewards.creditConfig.options().copyDefaults(true);
        this.forumRewards.saveCreditConfig();

        //**************************************//

        //Load all the player credits into our hashmap
        for (String name : this.forumRewards.creditConfig.getKeys(false)) {
            int value = this.forumRewards.creditConfig.getInt(name);

            this.credits.put(name.toLowerCase(), value);
            System.out.println(name + " : " + Integer.toString(value));
        }
    }

    public void saveConfig() {
        for (String name : credits.keySet()) {
            this.forumRewards.creditConfig.set(name.toLowerCase(), credits.get(name));
        }
        this.forumRewards.saveCreditConfig();
    }

    public void addPlayerPoint(String name) {
        if (this.credits.containsKey(name.toLowerCase())) {
            this.credits.put(name.toLowerCase(), credits.get(name.toLowerCase()) + 1);
        } else {
            this.credits.put(name.toLowerCase(), 1);
        }

        //Check if player is online
		//Now non caps sensitive
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(name)) {
				player.sendMessage(ChatColor.GREEN + "Thank you for helping the server by bumping the forums!" +
						" You have been rewarded " + ChatColor.YELLOW + ChatColor.BOLD.toString() + "1 "
						+ ChatColor.GREEN + "credit! Redeem with " + ChatColor.AQUA + "/fr collect");

				player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
			}
		}
    }

    public void collect(Player player) {
        //Has at least one credit to spend
        if (credits.containsKey(player.getName().toLowerCase()) && credits.get(player.getName().toLowerCase()) > 0) {

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(ChatColor.RED + "You do not have room in your inventory to collect your reward");
                return;
            }
            //Subtract credit
            credits.put(player.getName().toLowerCase(), credits.get(player.getName().toLowerCase()) - 1);
            ItemStack gold = new ItemStack(Material.GOLD_INGOT, 10);
            player.getInventory().addItem(gold);

            player.sendMessage(ChatColor.GREEN + "You traded " + ChatColor.AQUA + "1 credit"
                    + ChatColor.GREEN + " for " + ChatColor.GOLD + ChatColor.BOLD.toString() + "10 Gold Ingots");
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);

        } else {
            player.sendMessage(ChatColor.RED + "You do not have enough credits!");
        }
    }

}
