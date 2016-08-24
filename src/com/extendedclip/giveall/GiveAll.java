package com.extendedclip.giveall;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class GiveAll extends JavaPlugin implements CommandExecutor {

	@Override
	public void onEnable() {
		getCommand("giveall").setExecutor(this);
		loadConfig();
	}
	
	private void loadConfig() {
		if(getConfig().getString("message").equals("&c%player% &7has Given Everyone: %amount% &c%item%")){
			saveDefaultConfig();
			reloadConfig();
		}else{
			reloadConfig();
		}
	}
	
	public void sms(CommandSender p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
		
		String msg = getConfig().getString("message");
		
		if (!(s instanceof Player)) {
			if(args.length > 1){
				String item = args[0].toUpperCase();
				if(!isMat(item)){
					sms(s, "&cInvalid Item: &6"+item);
					return true;
				}
				int amount = Integer.valueOf(args[1]);
				ItemStack i = new ItemStack(Material.valueOf(item), amount);
				msg = replace(getName(i), "Console", msg, amount);
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
				i.setAmount(amount);
				for(Player user : Bukkit.getOnlinePlayers()){
					user.getInventory().addItem(i);
				}
			}else{
				sms(s, "&aUsage: &6/giveall <item/hand> (amount) (message)");
			}
			return true;
		}
		
		Player p = (Player) s;
		
		if (!p.hasPermission("giveall.give")) {
			sms(s, "&cYou don't have permission to do that!");
			return true;
		}
		
		ItemStack i;
		String item;
		
		if (args.length > 0) {
			if(args[0].equalsIgnoreCase("hand")){
				i = p.getInventory().getItemInHand();
				i = new ItemStack(i);
				item = getName(i);
				
				if (i.getType() == Material.AIR) {
					sms(s, "&cYou don't have an item in your hand to give!");
					return true;
				}
			}else{
				if(isMat(args[0].toUpperCase())){
					Material mat = Material.valueOf(args[0].toUpperCase());
					i = new ItemStack(mat, 1);
					item = getName(i);
				}else{
					sms(s, "&cInvalid Item: &6"+args[0].toUpperCase());
					return true;
				}
			}
			if (!(args.length > 2)){
				if(args.length > 1){
					if(isInt(args[1])){
						int amount = Integer.valueOf(args[1]);
						msg = replace(item, p.getName(), msg, amount);
						Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
						i.setAmount(amount);
						for(Player user : Bukkit.getOnlinePlayers()){
							user.getInventory().addItem(i);
						}
						return true;
					}else{
						sms(s, "&cInvalid amount!");
						return true;
					}
				}
				msg = replace(item, p.getName(), msg, 1);
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
				i.setAmount(1);
				for(Player user : Bukkit.getOnlinePlayers()){
					user.getInventory().addItem(i);
				}
				return true;
			}else{
				if(args.length > 1){
					if(isInt(args[1])){
						int amount = Integer.valueOf(args[1]);
						msg = StringUtils.join(args, " ", 2, args.length);
						msg = replace(item, p.getName(), msg, amount);
						Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
						for(Player user : Bukkit.getOnlinePlayers()){
							user.getInventory().addItem(i);
						}
						return true;
					}else{
						sms(s, "&cInvalid amount!");
						return true;
					}
				}
				msg = replace(item, p.getName(), msg, 1);
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
				i.setAmount(1);
				for(Player user : Bukkit.getOnlinePlayers()){
					user.getInventory().addItem(i);
				}
				return true;
			}
		}else{
			sms(s, "&aUsage: &6/giveall <item/hand> (amount) (message)");
		}
		
		return true;
	}
	
	private String replace(String item, String p, String s, int n) {
		return s.replace("%item%", item)
                .replace("%player%", p)
                .replace("%amount%", n+"");
    }
	
	private String getName(ItemStack i){
		String name;
		if(i.getItemMeta().getDisplayName() != null){
			name = i.getItemMeta().getDisplayName();
		}else{
			name = i.getType().toString();
		}
		return name;
	}
	
	private boolean isInt(String arg) {
        try {
            Integer.parseInt(arg);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
	
	private boolean isMat(String arg){
		try {
			Material.valueOf(arg);
			return true;
		} catch (IllegalArgumentException e) {
			getLogger().severe("Unknown material: " + arg); // if it's not a material
			return false;
		}
	}
}
