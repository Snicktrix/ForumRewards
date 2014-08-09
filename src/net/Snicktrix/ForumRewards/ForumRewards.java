package net.Snicktrix.ForumRewards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Luke on 8/5/14.
 */
public class ForumRewards extends JavaPlugin implements Listener{

    File creditFile;
    FileConfiguration creditConfig;
    ConfigData configData;

    @Override
    public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);

        creditFile = new File(getDataFolder(), "credits.yml");
        creditConfig = YamlConfiguration.loadConfiguration(creditFile);
        saveCreditConfig();

        this.configData = new ConfigData(this);
    }

    @Override
    public void onDisable() {
        this.configData.saveConfig();
    }

    public void saveCreditConfig(){
        try {
            creditConfig.save(creditFile);

        } catch( Exception e) {
            e.printStackTrace();
        }
    }

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final int credits = configData.getPlayerCredits(event.getPlayer());
		if (credits > 0) {

			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.YELLOW
							+ credits + ChatColor.GREEN + " credits. Trade for rewards them with " + ChatColor.AQUA + "/fr collect");
					player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
				}
			},2 * 20);
		}
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName();

        if (cmd.equalsIgnoreCase("fr")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("collect")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        this.configData.collect(player);
                    } else {
                        sender.sendMessage("Only players can use this command");
                    }
                }
            } else if (args.length == 2 && sender.isOp()) {
                if (args[0].equalsIgnoreCase("reward")) {
                    this.configData.addPlayerPoint(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Added credit to " + ChatColor.YELLOW + args[1]);
                }
            }
        }
        return true;
    }
}
