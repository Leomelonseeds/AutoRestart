package com.leomelonseeds.autorestart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class AutoRestart extends JavaPlugin {
	
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	int i;
	boolean cancel = false;
	public BukkitTask runnable;
	Long interval = getConfig().getLong("checking-interval");
	List<String> list = getConfig().getStringList("times");
	DateFormat dateformat = new SimpleDateFormat(getConfig().getString("DateFormat"));
	
	@Override
    public void onEnable() {
		
		getLogger().info("Enabled Autorestart");

		
		this.saveDefaultConfig();
		
		this.getCommand("autorestart").setExecutor(new ARCommandHandler(this));
		
		new BukkitRunnable() {
			public void run(){
	
				Date date = new Date();
				
				for (String s : list){
					
						if (dateformat.format(date).equals(s)){
							Bukkit.dispatchCommand(AutoRestart.this.console, "autorestart " + Integer.toString(getConfig().getInt("time-to-autorestart")));
						}
				}
			}
		}.runTaskTimer(this, 0L, interval);
    }
    
    @Override
    public void onDisable() {
    	getLogger().info("Disabled Autorestart");
    }
}
