package com.leomelonseeds.autorestart;

import java.text.SimpleDateFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;



public class ARCommandHandler implements CommandExecutor {
	
	AutoRestart plugin;
	public BukkitTask runnable;
	
	public ARCommandHandler (AutoRestart pl){
		this.plugin = pl;
	}
	
	boolean tryParseInt(String value) {  
	     try {  
	         Integer.parseInt(value);  
	         return true;  
	      } catch (NumberFormatException e) {  
	         return false;  
	      }  
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if (commandLabel.equalsIgnoreCase("autorestart") && sender.hasPermission("autorestart.restart")){
			if (plugin.cancel == false) {
				
				if (args.length == 0){
					sender.sendMessage(ChatColor.RED + "Usage: /autorestart [time in seconds]");
					return false;
				}

				else if (args.length == 1){
					if (tryParseInt(args[0]) == false){
						if (args[0].equalsIgnoreCase("reload")){
							
							if ((sender instanceof Player)) {
							    if (sender.hasPermission("autorestart.reload")) {
							        plugin.reloadConfig();
							        sender.sendMessage(ChatColor.GREEN + "AutoRestart reloaded!");
							        plugin.interval = plugin.getConfig().getLong("checking-interval");
							        plugin.list = plugin.getConfig().getStringList("times");
							        plugin.dateformat = new SimpleDateFormat(plugin.getConfig().getString("DateFormat"));
							        return true;
							    } else {
							        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("no-perm-message")));
							        return false;
							    }
							 } else {
								 plugin.reloadConfig();
							     sender.sendMessage(ChatColor.GREEN + "AutoRestart reloaded!");
							     plugin.interval = plugin.getConfig().getLong("checking-interval");
							     plugin.list = plugin.getConfig().getStringList("times");
							     plugin.dateformat = new SimpleDateFormat(plugin.getConfig().getString("DateFormat"));
							     return true;
							 }
						}
						
						else {
							sender.sendMessage(ChatColor.RED + "Invalid argument: '" + args[0] + "'.");
							return false;
						}
					}
					else {
						plugin.i = Integer.parseInt(args[0]);
						sender.sendMessage(ChatColor.GREEN + "Restart scheduled to " + plugin.i + " seconds!");
						
						

						plugin.cancel = true;
				
						runnable = new BukkitRunnable() {
				        	
				        	@Override
							public void run(){
								for (Player p : Bukkit.getServer().getOnlinePlayers()){	
									for(String key : plugin.getConfig().getConfigurationSection("restart-options").getKeys(false)){
										
										if (plugin.i == Integer.parseInt(key)){
												
											if(plugin.getConfig().isSet("restart-options." + key + ".message")){
												p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("restart-options." + key + ".message")));
											}
											if(plugin.getConfig().isSet("restart-options." + key + ".title")){
												p.sendTitle(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("restart-options." + key + ".title.title")), getSubtitle("restart-options.", key), getFadeIn("restart-options.", key), getStay("restart-options.", key), getFadeOut("restart-options.", key));
											}
											if(plugin.getConfig().isSet("restart-options." + key + ".sound")){
												
												p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("restart-options." + key + ".sound.sound")), getVolume("restart-options.", key), getPitch("restart-options.", key));
											}
										}	
									}
								}
								
								if(plugin.i <= -1){
									Bukkit.dispatchCommand(plugin.console, "stop");	   
									runnable.cancel();
								}
								
								plugin.i--;
							}
						}.runTaskTimer(plugin, 0L, 20L);
						
						return true;
						
					}
				}
				
				else{
					sender.sendMessage(ChatColor.RED + "Too many arguments!");
					return false;
				}
				
			}
			
			else {
				
				sender.sendMessage(ChatColor.GREEN + "You canceled the server restart!");
				
				for (Player p : Bukkit.getServer().getOnlinePlayers()){
					
						if(plugin.getConfig().isSet("cancel-options.message")){
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("cancel-options.message")));
						}
						if(plugin.getConfig().isSet("cancel-options.title")){
							p.sendTitle(plugin.getConfig().getString("cancel-options.title.title"), getSubtitle("cancel-options.", null), getFadeIn("cancel-options.", null), getStay("cancel-options.", null), getFadeOut("cancel-options.", null));
						}
						if(plugin.getConfig().isSet("cancel-options.sound")){
							p.playSound(p.getLocation(), plugin.getConfig().getString("cancel-options.sound.sound"), getVolume("cancel-options.", null), getPitch("cancel-options.", null));
						}
						
						
					
				}
				plugin.cancel = false;
				runnable.cancel();	
				
				return true;
			}
		}
		
		return false;
	}
	
	private String getSubtitle(String node, String key){
		if (plugin.getConfig().isSet(node + key + ".title.subtitle")){
			return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(node + key + ".title.subtitle"));
		}
		else {
			return null;
		}
	}
	
	private int getFadeIn(String node, String key){
		if (plugin.getConfig().isSet(node + key + ".title.fade-in")){
			return plugin.getConfig().getInt(node + key + ".title.fade-in");
		}
		else {
			return 10;
		}
	}
	
	private int getStay(String node, String key){
		if (plugin.getConfig().isSet(node + key + ".title.stay")){
			return plugin.getConfig().getInt(node + key + ".title.stay");
		}
		else {
			return 70;
		}
	}
	
	private int getFadeOut(String node, String key){
		if (plugin.getConfig().isSet(node + key + ".title.fade-out")){
			return plugin.getConfig().getInt(node + key + ".title.fade-out");
		}
		else {
			return 20;
		}
	}
	
	private int getVolume(String node, String key){
		if (plugin.getConfig().isSet(node + key + ".sound.volume")){
			return plugin.getConfig().getInt(node + key + ".sound.volume");
		}
		else {
			return 1;
		}
	}
	
	private int getPitch(String node, String key){
		if (plugin.getConfig().isSet(node + key + ".sound.pitch")){
			return plugin.getConfig().getInt(node + key + ".sound.pitch");
		}
		else {
			return 1;
		}
	}

}
