package discordbotplugin.net.relay;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import discordbotplugin.net.discord.DiscordBot;
import discordbotplugin.net.discord.DiscordUtilities;
import discordbotplugin.net.logger.Logger;
import discordbotplugin.net.logger.Logger.Level;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;

public class MessageRelay extends ListenerAdapter implements Listener {

	public static String discordPrefix = "Discord:";
	public static String minecraftPrefix = "Minecraft:";
	public static int numOfPlayers = 0;
	
	private String feature = "discord_message_relay";
	
	// Discord events
	public void onMessageReceived(MessageReceivedEvent event) {
		
		if (!event.isFromGuild()) {
			return;
		}
		
		List<String> channels = DiscordBot.configuration.getFeatures().get(feature).getChannels();
		
		if (channels == null || channels.get(0) == null) {
			return;
		}
		
		
		for (String id: channels) {
			if (id.equals(event.getTextChannel().getId()) && !event.getMessage().getContentDisplay().contains(minecraftPrefix)) {
				String message = "&9" 
						+ discordPrefix 
						+ " &3" 
						+ event.getAuthor().getName() 
						+ " ->" 
						+ " &f" 
						+ event.getMessage().getContentDisplay();
				
				String message2 = ChatColor.translateAlternateColorCodes('&', message);
				Bukkit.broadcastMessage(message2);
				
				
				if (event.getMessage().getAttachments().size() > 0) {
					String attachment = "&2Attachments(s): ";
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', attachment));
					
					
					for (Attachment at: event.getMessage().getAttachments()) {
						String url = "&a&n" + at.getUrl();
						Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', url));
					}
				} 
				
				
			}
		}
		
	}
	
	// Minecraft Events
	
	// Player Events
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (!e.getMessage().startsWith(discordPrefix)) {
			String preMessage = DiscordUtilities.format(minecraftPrefix, "bold") 
					+ " " 
					+ "<:speech_balloon:691393503124520980> " 
					+ DiscordUtilities.format(e.getPlayer().getName(), "italics") 
					+ " " 
					+ DiscordUtilities.format("->", "bold") 
					+ " ";
			String message = e.getMessage();
			
			Pattern pattern = Pattern.compile("(:\\w*:)");
			Matcher matcher = pattern.matcher(message);
			
			
			while (matcher.find()) {
				Emote emote = DiscordUtilities.getGuildEmote(matcher.group().replaceAll(":", ""), false);
				
				if (emote != null) {
					message = message.replaceAll(matcher.group(), emote.getAsMention());
//					System.out.println(emote.getAsMention());
				} else {
					Logger.log(Level.ERROR, "Emote not found");
				}
			}
			
			
			DiscordUtilities.sendRelayMessage(preMessage + message + " ");
			
			e.setFormat(ChatColor.GOLD + e.getPlayer().getDisplayName() + ": " + ChatColor.GRAY + e.getMessage());
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		String message = DiscordUtilities.format(minecraftPrefix, "bold") 
				+ " " 
				+ DiscordUtilities.format(e.getEntity().getName(), "italics") 
				+ " " 
				+ DiscordUtilities.format("->", "bold") 
				+ " " 
				+ DiscordUtilities.format(e.getDeathMessage(), "bold")
				+ " <:skull_crossbones:690567471618457631>";
		DiscordUtilities.sendRelayMessage(message);
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		String message = DiscordUtilities.format(minecraftPrefix, "bold") 
				+ " " 
				+ DiscordUtilities.format("<:inbox_tray:690567153371447327> Login -> " + e.getPlayer().getName(), "italics") 
				+ " " 
				+ DiscordUtilities.format("->", "bold") + " " + e.getResult();
		
		DiscordUtilities.sendRelayMessage(message);
		DiscordUtilities.setBotStatus("Alpha Server: " + (Bukkit.getOnlinePlayers().size()+1) + " player(s)");
	}
	
	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent e) {
		String message = DiscordUtilities.format(minecraftPrefix, "bold") 
				+ " " 
				+ DiscordUtilities.format("<:outbox_tray:690566847774457857> Logout -> " + e.getPlayer().getName(), "italics") 
				+ " " 
				+ DiscordUtilities.format("->", "bold") + " " + "left the game";
		
		DiscordUtilities.sendRelayMessage(message);
		DiscordUtilities.setBotStatus("Alpha Server: " + (Bukkit.getOnlinePlayers().size()-1) + " player(s)");
	}
	
	public void OnPlayerKick(PlayerKickEvent e) {
		String message = DiscordUtilities.format(minecraftPrefix, "bold") 
				+ " " 
				+ DiscordUtilities.format("<:boot:798924891961163817> Kicked -> " + e.getPlayer().getName(), "italics") 
				+ " " 
				+ DiscordUtilities.format("->", "bold") + " " + "left the game";
		
		DiscordUtilities.sendRelayMessage(message);
		DiscordUtilities.setBotStatus("Alpha Server: " + (Bukkit.getOnlinePlayers().size()-1) + " player(s)");
	}
	
	@EventHandler
	public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent e) {
		if(e.getAdvancement().getKey().getKey().startsWith("story")) {
			String message = DiscordUtilities.format(minecraftPrefix, "bold") 
					+ " " 
					+ DiscordUtilities.format("<:trophy:690738524659646485> Advancement -> " + e.getPlayer().getName(), "italics") 
					+ " " 
					+ DiscordUtilities.format("->", "bold") + " " + e.getAdvancement().getKey().getKey();
			DiscordUtilities.sendRelayMessage(message);
		}
		
	}
	/*
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent e) {
		String message = format(minecraftPrefix, "bold") + " " + format("Entered Bed -> " + e.getPlayer().getName(), "italics") + " " + format("->", "bold") + " " + e.getBedEnterResult();
		String guildId = (String) ConfigurationManager.getProperty("guildId", "bot/bot");
		String relayId = (String) ConfigurationManager.getProperty("relayChannelId", guildId);
		
		Bot.jda.getGuildById(guildId).getTextChannelById(relayId).sendTyping().queue();
		Bot.jda.getGuildById(guildId).getTextChannelById(relayId).sendMessage(message).queue();
	}
	
	@EventHandler
	public void onPlayerBedLeave(PlayerBedLeaveEvent e) {
		String message = format(minecraftPrefix, "bold") + " " + format("Left Bed -> " + e.getPlayer().getName(), "italics") + " " + format("->", "bold") + " " + "left bed";
		String guildId = (String) ConfigurationManager.getProperty("guildId", "bot/bot");
		String relayId = (String) ConfigurationManager.getProperty("relayChannelId", guildId);
		
		Bot.jda.getGuildById(guildId).getTextChannelById(relayId).sendTyping().queue();
		Bot.jda.getGuildById(guildId).getTextChannelById(relayId).sendMessage(message).queue();
	}
	*/
	
	
	// World Events
	
	// Raids
	@EventHandler
	public void onRaidTrigger(RaidTriggerEvent e) {
		Raid r = e.getRaid();
		Location loc = r.getLocation();
		
		String message = DiscordUtilities.format(minecraftPrefix, "bold") 
				+ " " 
				+ DiscordUtilities.format("<:crossed_swords:698730737045864448> Raid -> " + e.getPlayer().getName(), "italics")
				+ " " 
				+ DiscordUtilities.format("->", "bold") + " " + e.getPlayer().getName() + "has triggered a " + r.getBadOmenLevel() + " raid at (" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ").";
		
		String p1 = "&6" + e.getPlayer().getName() + "&4&lhas triggered a level " + (r.getBadOmenLevel()+1) + " raid at &c(" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ")&4&l.";
		String p2 = "&4Totals -> &cGroups: " + r.getTotalGroups() + ", Health: " + (r.getTotalHealth()+1) + ", Waves: " + r.getTotalWaves();
		
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', p1));
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', p2));
		
		DiscordUtilities.sendRelayMessage(message);
	}
	
	@EventHandler
	public void onRaidSpawnWave(RaidSpawnWaveEvent e) {
		Raid r = e.getRaid();
		
		r.getHeroes().forEach(p -> {
			String p1 = "&4&lA new wave is spawning... &cSpawned Groups: " + r.getSpawnedGroups();
			
			if (Bukkit.getPlayer(p) != null) {
				Bukkit.getPlayer(p).sendMessage(ChatColor.translateAlternateColorCodes('&', p1));
			}
			
		});
	}
	
	@EventHandler
	public void onRaidFinish(RaidFinishEvent e) {
		Raid r = e.getRaid();
		
		String message = DiscordUtilities.format(minecraftPrefix, "bold") 
				+ " <:crossed_swords:698730737045864448>" 
				+ " " 
				+ DiscordUtilities.format("Raid", "italics") 
				+ " " 
				+ DiscordUtilities.format("->", "bold") + " The raid has been completed <:trophy:690738524659646485>";
		
		String p1 = "&6&lThe raid has been completed! These heroes reign victorious...";
		String p2 = "&a&n";
		
		
		for (UUID id : r.getHeroes()) {
			if (Bukkit.getPlayer(id) != null) {
				p2 = Bukkit.getPlayer(id).getName() + ", ";
			}
		}
		p2 = p2.substring(0, p2.lastIndexOf(',')) + ".";
		
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', p1));
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', p2));
		
		DiscordUtilities.sendRelayMessage(message);
	}
	
	@EventHandler
	public void onRaidStop(RaidStopEvent e) {
		Raid r = e.getRaid();
		
		String message = DiscordUtilities.format(minecraftPrefix, "bold") 
				+ " <:crossed_swords:698730737045864448>" 
				+ " " 
				+ DiscordUtilities.format("Raid", "italics") 
				+ " " 
				+ DiscordUtilities.format("->", "bold") + " The raid has been lost <:skull_crossbones:690567471618457631>";
		
		String p1 = "&6&lThe raid has been lost. These heroes were defeated...";
		String p2 = "&a&n";
		
		for (UUID id : r.getHeroes()) {
			if (Bukkit.getPlayer(id) != null) {
				p2 = Bukkit.getPlayer(id).getName() + ", ";
			}
		}
		p2 = p2.substring(0, p2.lastIndexOf(',')) + ".";
		
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', p1));
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', p2));
		
		DiscordUtilities.sendRelayMessage(message);
	}
	
	
	// Block Events
	public static Multimap<UUID, Block> furnaceOwners = ArrayListMultimap.create();
	
	private boolean isNotNullOrAir(ItemStack stack) {
		if (stack != null && stack.getType() != Material.AIR)
			return true;
		return false;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory inven = e.getView().getTopInventory();
		Block block = inven.getLocation().getBlock();
		//check if its a furnace
		if (inven != null && e.getWhoClicked() instanceof Player 
				&& (inven.getType() == InventoryType.FURNACE || inven.getType() == InventoryType.BLAST_FURNACE || inven.getType() == InventoryType.SMOKER)) {
		
			//check if slot is crafting
			//checks if player click drops item into furnace or shift clicks.
			if (e.getSlotType() == SlotType.CRAFTING || (e.isShiftClick() && e.getSlotType() == SlotType.CONTAINER)) {
				Player p = (Player) e.getWhoClicked();
				UUID id = p.getUniqueId();
				
				//if player puts item in to cook
				if ((e.isShiftClick() && e.getSlotType() == SlotType.CONTAINER && isNotNullOrAir(e.getCurrentItem()) ) || isNotNullOrAir(e.getCursor())) {
					
					if (!furnaceOwners.containsEntry(id, block)) {
						if (furnaceOwners.put(id, block)) {
							p.sendMessage("You will be notified once the furnace is finished.");
						} else {
							p.sendMessage("This block has already been cached. Report this message to Admin.");
						}
					}
					
				} else if(e.getSlotType() == SlotType.CRAFTING && isNotNullOrAir(e.getCurrentItem()) && !e.isRightClick()) { //removing item
					
					if (furnaceOwners.containsEntry(id, block)) {
						// if the player is in the keys, check if the furnace they are extracting from is one of theirs.
						// Then remove if so.
						furnaceOwners.remove(id, block);
						p.sendMessage("You will no longer be notified.");
						return;
						
					} else {
						// If not, then go through all of the furnace owners and find the block
						Iterator<UUID> keys = furnaceOwners.keySet().iterator();
						while (keys.hasNext()) {
							UUID key = keys.next();
							if (furnaceOwners.containsEntry(key, block)) {
								furnaceOwners.remove(id, block);
								p.sendMessage("You will no longer be notified.");
								return;
							}
						}
					} 
				}
			}
		}
	}
	
	@EventHandler
	public void onFurnaceSmelt(FurnaceSmeltEvent e) {
		Furnace f = (Furnace) e.getBlock().getState();
		int smelting = f.getInventory().getSmelting().getAmount();
		
		/*
		if (smelting <= 1) {
			Bukkit.getServer().getOnlinePlayers().forEach(p -> {
				if (e.getBlock().getLocation().distanceSquared(p.getLocation()) <= 400) {
					String message ="&fThe &7" + e.getBlock().getType().toString().toLowerCase().replaceAll("_", " ") 
							+ " &ffinished making &a" + f.getInventory().getResult() + "&f " 
							+ e.getResult().getType().toString().toLowerCase().replaceAll("_", " ") +".";
					
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .5f, .5f);
				}
			});
		}
		*/
		
		// WIP
		if (smelting <= 1) {
			//Check all of the key-value pairs for this block then send the owner a message.
			for (UUID key : furnaceOwners.keySet()) {
				if (furnaceOwners.containsEntry(key, e.getBlock())) {
					int amount = 0;
					if (f.getInventory().getResult() == null) {
						amount = 1;
					} else {
						amount = f.getInventory().getResult().getAmount() + 1;
					}
					
					String message ="&fThe &7" + e.getBlock().getType().toString().toLowerCase().replaceAll("_", " ") 
							+ " &ffinished making &a" + amount + "&f " 
							+ e.getResult().getType().toString().toLowerCase().replaceAll("_", " ") +".";
					
					Player p = Bukkit.getServer().getPlayer(key);
					if (p != null) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
						p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .5f, .5f);
					}
					
					furnaceOwners.remove(key, e.getBlock());
				}
			}
		}
	}
	
	@EventHandler
	public void onFurnaceExtract(FurnaceExtractEvent e) {
		// Check if the the keys contain the player
		if (furnaceOwners.containsEntry(e.getPlayer().getUniqueId(), e.getBlock())) {
			furnaceOwners.remove(e.getPlayer().getUniqueId(), e.getBlock());
			return;
		} else {
			
			// If not, then go through all of the furnace owners and find the block
			for (UUID key : furnaceOwners.keySet()) {
				if (furnaceOwners.containsEntry(key, e.getBlock())) {
					furnaceOwners.remove(key, e.getBlock());
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		
		if (e.getBlock().getType() == Material.FURNACE || e.getBlock().getType() == Material.BLAST_FURNACE || e.getBlock().getType() == Material.SMOKER) {
			//iterate through each key and check if the values contain the broken furnace block.
			for (UUID key : furnaceOwners.keySet()) {
				if (furnaceOwners.containsEntry(key, e.getBlock())) {
					furnaceOwners.remove(key, e.getBlock());
				}
			}
		}
	}
}
