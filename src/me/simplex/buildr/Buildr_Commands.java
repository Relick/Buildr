package me.simplex.buildr;

import java.util.HashMap;

import me.simplex.buildr.util.Buildr_WoolConv;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Buildr_Commands {
	Buildr plugin;
	
	/**
	 * 
	 * @param plugin
	 */
	public Buildr_Commands(Buildr plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * 
	 * @param sender
	 */
	protected void cmd_globalbuild(CommandSender sender,World world){
		if (plugin.getWorldbuildmode().contains(world)) {
			plugin.leaveGlobalbuildmode(world);
			sender.sendMessage("Globalbuildmode disabled");
			for (Player inhab : world.getPlayers()) {
				inhab.sendMessage(ChatColor.AQUA+((Player)sender).getName()+" disabled the Globalbuildmode on the world you are currently in");
			}
			plugin.log("Globalbuildmode disabled in World "+world.getName());
		}
		else {
			plugin.enterGlobalbuildmode(world);
			sender.sendMessage("Globalbuildmode enabled");
			for (Player inhab : world.getPlayers()) {
				inhab.sendMessage(ChatColor.AQUA+((Player)sender).getName()+" ensabled the Globalbuildmode on the world you are currently in");
			}
			plugin.log("Globalbuildmode enabled in World "+world.getName());
		}
	}
	
	/**
	 * 
	 * @param sender
	 */
	protected void cmd_build(CommandSender sender){
		if (plugin.getPlayerbuildmode().contains((Player)sender)) {
			
			sender.sendMessage(ChatColor.BLUE+"Buildmode disabled");
			plugin.leaveBuildmode((Player)sender);
		}
		else {
			
			sender.sendMessage(ChatColor.BLUE+"Buildmode enabled");
			plugin.enterBuildmode((Player)sender);
		}
	}
	
	/**
	 * 
	 * @param sender
	 */
	protected void cmd_top(CommandSender sender) {
		Player player = (Player)sender;
		if (player.teleport(new Location(player.getWorld(), player.getLocation().getX(), player.getWorld().getHighestBlockYAt(player.getLocation()), player.getLocation().getZ(),player.getLocation().getYaw(),player.getLocation().getPitch()))) 
		{
			player.sendMessage("Ported to Top.");
		}
		else {
			player.sendMessage("Something went wrong");
		}
	}
	
	/**
	 * 
	 * @param sender
	 * @param material
	 * @param height
	 * @param size
	 */
	protected void cmd_airfloor(CommandSender sender, int material, int height, int size){
		Player player = (Player)sender;
		int blockheight = player.getLocation().getBlockY()+height-1;
		Location location = new Location(player.getWorld(), player.getLocation().getBlockX(), blockheight, player.getLocation().getBlockZ());
		HashMap<Block, Material> UnDoList = new HashMap<Block, Material>();		
		if (blockheight > 128) {
			blockheight = 128;
		}
		if (size == 0 || size == 1) {
			UnDoList.put(player.getWorld().getBlockAt(location),player.getWorld().getBlockAt(location).getType());
			player.getWorld().getBlockAt(location).setTypeId(material);
		}
		else {
			int offset;
			
			if ((size % 2) == 0) {offset = size/2;}
			else {offset = (size-1)/2;}
			
			int xstart= location.getBlockX()-offset;
			int zstart= location.getBlockZ()-offset;
			
			int x= xstart;
			int z= zstart;
			
			for (int i = 0; i < size; i++) { //x
				for (int j = 0; j < size; j++) {//z
					UnDoList.put(player.getWorld().getBlockAt(x, blockheight, z), player.getWorld().getBlockAt(x, blockheight, z).getType());
					player.getWorld().getBlockAt(x, blockheight, z).setTypeId(material);
					z++;
				}
				z=zstart;
				x++;
			}
		}
		sender.sendMessage("Block(s) placed");
		plugin.getUndoList().addToStack(UnDoList, player);
		plugin.log(player.getName()+" used /airfloor: "+UnDoList.size()+" blocks affected");
	}
	/**
	 * 
	 * @param sender
	 */
	protected void cmd_undo(CommandSender sender){
		Player player = (Player)sender;
		HashMap<Block, Material> undos = plugin.getUndoList().getAndDeleteFromStack(player);
		if (undos != null) {
			for (Block block : undos.keySet()) {
				block.setType(undos.get(block));
			}
			plugin.log(player.getName()+" used /bu: "+undos.size()+" blocks affected");
			sender.sendMessage(undos.size()+" blocks restored");
		}
		else {
			sender.sendMessage("Nothing to undo");
		}
	}
	/**
	 * 
	 * @param sender
	 * @param material
	 * @param amount
	 */
	public void cmd_give(CommandSender sender, String material, int amount, String player) {
		Material give_mat = null;
		try {
			give_mat = Material.getMaterial(Integer.parseInt(material));
		} catch (NumberFormatException e) {
			give_mat = Material.matchMaterial(material);
		}
		if (give_mat == null) {
			sender.sendMessage(ChatColor.RED+"No such Item found");
			return;
		}
		if (amount ==-1 || amount == 0) {
			amount = 64;
		}
		Player giveto = ((Player)sender);
		if (player !=null) {
			giveto = sender.getServer().getPlayer(player);
		}
		if (giveto == null) {
			sender.sendMessage(ChatColor.RED+"Player not found");
			return;
		}
		if (give_mat.getId() == 0) {
			sender.sendMessage(ChatColor.RED+"You can't give air");
			return;
		}
		giveto.getInventory().addItem(new ItemStack(give_mat,amount));
		sender.sendMessage("Gave "+giveto.getName()+" a stack of "+amount+" "+give_mat.toString()+"(ID: "+give_mat.getId()+")");
	}
	
	protected void cmd_wool(CommandSender sender, String args) {
		args.toUpperCase();
		Buildr_WoolConv woolcolor;
		try {
			woolcolor = Enum.valueOf(Buildr_WoolConv.class, args);
		} catch (IllegalArgumentException e) {
			sender.sendMessage("No such color");
			return;
		}
		((Player)sender).getInventory().addItem(woolcolor.giveStack());
		String ret = "Gave yourself a stack of "+woolcolor.toString().toLowerCase()+" wool";
		sender.sendMessage(ret);
	}
}