package com.extendedclip.giveallhand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.coloredcarrot.mcapi.json.JSONComponent;
import com.coloredcarrot.mcapi.json.JSONHoverAction;

public class GAH extends JavaPlugin implements CommandExecutor {

	@Override
	public void onEnable() {
		getCommand("gah").setExecutor(this);
		loadConfig();
	}
	
	private void loadConfig() {
		FileConfiguration c = getConfig();
		c.options().header("GAH v " + getDescription().getVersion()
				+ "\nCreated by extended_clip");
		saveConfig();
		reloadConfig();
	}
	
	public void sms(CommandSender p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
		
		String msg = getConfig().getString("message");
		
		if (!(s instanceof Player)) {
			if(args.length > 1){
				String item = args[0].toUpperCase();
				int amount = Integer.valueOf(args[1]);
				try {
					Material.valueOf(item);
				} catch (IllegalArgumentException e) {
					getLogger().severe("Unknown material: " + item); // if it's not a material
					sms(s, "&cInvalid Item: &6"+item);
					return true;
				}
				ItemStack i = new ItemStack(Material.valueOf(item), amount);
				JSONComponent itemjson = new JSONComponent(i.getItemMeta().getDisplayName());
				JSONHoverAction hover = new JSONHoverAction.ShowItemStack(i);
				itemjson.setHoverAction(hover);
				msg = replace(itemjson, "Console", msg, amount);
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
				i.setAmount(amount);
				for(Player user : Bukkit.getOnlinePlayers()){
					user.getInventory().addItem(i);
				}
			}
			return false;
		}
		
		Player p = (Player) s;
		
		if (!p.hasPermission("gah.give")) {
			sms(s, "&cYou don't have permission to do that!");
			return true;
		}
		
		ItemStack i = p.getInventory().getItemInMainHand();
		i = new ItemStack(i);
		Material item = i.getType();
		
		if (item == Material.AIR) {
			sms(s, "&cYou don't have an item in your hand to give!");
			return true;
		}
		
		JSONComponent itemjson = new JSONComponent(i.getItemMeta().getDisplayName());
		JSONHoverAction hover = new JSONHoverAction.ShowItemStack(i);
		itemjson.setHoverAction(hover);
		
		if (args.length > 0) {
			if(isInt(args[0])){
				msg = replace(itemjson, p.getName(), msg, Integer.valueOf(args[0]));
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
				i.setAmount(Integer.valueOf(args[0]));
				for(Player user : Bukkit.getOnlinePlayers()){
					user.getInventory().addItem(i);
				}
			}
		}else{
			msg = replace(itemjson, p.getName(), msg, 1);
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
			i.setAmount(1);
			for(Player user : Bukkit.getOnlinePlayers()){
				user.getInventory().addItem(i);
			}
		}
		
		return true;
	}
	
	private String replace(JSONComponent itemjson, String p, String s, int n) {
		return s.replace("%item%", itemjson.toString())
                .replace("%player%", p)
                .replace("%amount%", n+"");
    }
	
	private boolean isInt(String arg) {
        try {
            Integer.parseInt(arg);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
