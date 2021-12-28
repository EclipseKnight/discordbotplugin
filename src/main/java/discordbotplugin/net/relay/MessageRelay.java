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

import discordbotplugin.net.database.JsonDB;
import discordbotplugin.net.database.documents.MCPlayer;
import discordbotplugin.net.discord.DiscordBot;
import discordbotplugin.net.discord.DiscordUtils;
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
		// Checks if message is from guild and not private channel.
		if (!event.isFromGuild()) {
			return;
		}
		
		// Checks if message is from optional store bot plugin.
		if (event.getMember() != null && event.getMember().getId().equals(DiscordBot.configuration.getStoreBotId())) {
			return;
		}
		
		// Checks if message is from appropriate channel.
		List<String> channels = DiscordBot.configuration.getFeatures().get(feature).getChannels();
		if (channels == null || channels.get(0) == null) {
			return;
		}
		
		String jxQuery = String.format("/.[discordId='%s']", event.getAuthor().getId());
		List<MCPlayer> mcps = JsonDB.database.find(jxQuery, MCPlayer.class);
		MCPlayer mcp = null;
		if (mcps.size() > 0) {
			mcp = mcps.get(0);
		}
		
		
		
		// send in all the relay channels.
		for (String id: channels) {
			if (id.equals(event.getTextChannel().getId()) && !event.getMessage().getContentDisplay().contains(minecraftPrefix)) {
				
				
				
				String message = String.format("&9%s &3%s -> &f%s", 
						discordPrefix,
						event.getAuthor().getName(),
						event.getMessage().getContentDisplay());
				
				if (mcp.isLinked()) {
					message = String.format("&9%s &3%s[%s] -> &f%s", 
							discordPrefix,
							event.getAuthor().getName(),
							mcp.getMinecraftName(),
							event.getMessage().getContentDisplay());
				}
				
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
			
			MCPlayer mcp = JsonDB.database.findById(e.getPlayer().getUniqueId().toString(), MCPlayer.class);
			
			if (mcp == null) {
				mcp = new MCPlayer();
				mcp.setMinecraftName(e.getPlayer().getName());
				mcp.setUuid(e.getPlayer().getUniqueId().toString());
				mcp.setLinked(false);
				JsonDB.database.upsert(mcp);
			}
			
			
			String preMessage = String.format("**%s** <:speech_balloon:691393503124520980> *%s* **->** ", 
					minecraftPrefix,
					mcp.getMinecraftName());
			
			if (mcp.isLinked()) {
				preMessage = String.format("**%s** <:speech_balloon:691393503124520980> *%s[%s]* **->** ", 
					minecraftPrefix,
					mcp.getMinecraftName(),
					mcp.getDiscordEffName());
			}
			
			
			String message = e.getMessage();
			
			Pattern pattern = Pattern.compile("(:\\w*:)");
			Matcher matcher = pattern.matcher(message);
			
			while (matcher.find()) {
				Emote emote = DiscordUtils.getGuildEmote(matcher.group().replaceAll(":", ""), false);
				
				if (emote != null) {
					message = message.replaceAll(matcher.group(), emote.getAsMention());
				} else {
					Logger.log(Level.ERROR, "Emote not found");
				}
			}
			
			DiscordUtils.sendRelayMessage(preMessage + message);
			
			e.setFormat(ChatColor.GOLD + e.getPlayer().getDisplayName() + ": " + ChatColor.GRAY + e.getMessage());
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		MCPlayer mcp = JsonDB.database.findById(e.getEntity().getUniqueId().toString(), MCPlayer.class);
		
		if (mcp == null) {
			mcp = new MCPlayer();
			mcp.setMinecraftName(e.getEntity().getName());
			mcp.setUuid(e.getEntity().getUniqueId().toString());
			mcp.setLinked(false);
			JsonDB.database.upsert(mcp);
		}
		
		
		String message = String.format("""
				**%s** *%s* **->** **%s** <:skull_crossbones:690567471618457631>
				""", 
				minecraftPrefix,
				mcp.getMinecraftName(),
				e.getDeathMessage());
		
		if (mcp.isLinked()) {
			message = String.format("""
				**%s** *%s[%s]* **->** **%s** <:skull_crossbones:690567471618457631>
				""", 
				minecraftPrefix,
				mcp.getMinecraftName(),
				mcp.getDiscordEffName(),
				e.getDeathMessage());
		}
		
		DiscordUtils.sendRelayMessage(message);
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		MCPlayer mcp = JsonDB.database.findById(e.getPlayer().getUniqueId().toString(), MCPlayer.class);
		
		if (mcp == null) {
			mcp = new MCPlayer();
			mcp.setMinecraftName(e.getPlayer().getName());
			mcp.setUuid(e.getPlayer().getUniqueId().toString());
			mcp.setLinked(false);
			JsonDB.database.upsert(mcp);
		}
		
		String message = String.format("""
				**%s** *<:inbox_tray:690567153371447327> Login -> %s* **->** %s
				""",
				minecraftPrefix,
				mcp.getMinecraftName(),
				e.getResult());
		
		if (mcp.isLinked()) {
			message = String.format("""
					**%s** *<:inbox_tray:690567153371447327> Login -> %s[%s]* **->** %s
					""",
					minecraftPrefix,
					mcp.getMinecraftName(),
					mcp.getDiscordEffName(),
					e.getResult());
		}
		
		
		DiscordUtils.sendRelayMessage(message);
		DiscordUtils.setBotStatus("Alpha Server: " + (Bukkit.getOnlinePlayers().size()+1) + " player(s)");
		
		if (!mcp.isLinked()) {
			String message3 = "&4Discord Account Not Linked.";
			String message4 = "&4To have your discord and minecraft names show up together in chat,"
			+ "&r&4 use command \"!d link " + e.getPlayer().getName() +" \" in the #minecraft discord channel to link accounts.";
			
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message3));
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message4));
		}
		
	}
	
	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent e) {
		MCPlayer mcp = JsonDB.database.findById(e.getPlayer().getUniqueId().toString(), MCPlayer.class);
		
		if (mcp == null) {
			mcp = new MCPlayer();
			mcp.setMinecraftName(e.getPlayer().getName());
			mcp.setUuid(e.getPlayer().getUniqueId().toString());
			mcp.setLinked(false);
			JsonDB.database.upsert(mcp);
		}
		
		String message = String.format("""
				**%s** *<:outbox_tray:690566847774457857> Logout -> %s* **->** left the game
				""",
				minecraftPrefix,
				mcp.getMinecraftName());
		
		if (mcp.isLinked()) {
			message = String.format("""
				**%s** *<:outbox_tray:690566847774457857> Logout -> %s[%s]* **->** left the game
				""",
				minecraftPrefix,
				mcp.getMinecraftName(),
				mcp.getDiscordEffName());
		}
		
		DiscordUtils.sendRelayMessage(message);
		DiscordUtils.setBotStatus("Alpha Server: " + (Bukkit.getOnlinePlayers().size()-1) + " player(s)");
	}
	
	@EventHandler
	public void OnPlayerKick(PlayerKickEvent e) {
		MCPlayer mcp = JsonDB.database.findById(e.getPlayer().getUniqueId().toString(), MCPlayer.class);
		
		if (mcp == null) {
			mcp = new MCPlayer();
			mcp.setMinecraftName(e.getPlayer().getName());
			mcp.setUuid(e.getPlayer().getUniqueId().toString());
			mcp.setLinked(false);
			JsonDB.database.upsert(mcp);
		}
		
		String message = String.format("""
				**%s** *<:boot:798924891961163817> Kicked -> %s* **->** left the game
				""",
				minecraftPrefix,
				mcp.getMinecraftName());
		
		if (mcp.isLinked()) {
			message = String.format("""
				**%s** *<:boot:798924891961163817> Kicked -> %s[%s]* **->** left the game
				""",
				minecraftPrefix,
				mcp.getMinecraftName(),
				mcp.getDiscordEffName());
		}
		
		
		DiscordUtils.sendRelayMessage(message);
		DiscordUtils.setBotStatus("Alpha Server: " + (Bukkit.getOnlinePlayers().size()-1) + " player(s)");
	}
	
	@EventHandler
	public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent e) {
		if(e.getAdvancement().getKey().getKey().startsWith("story")) {
			
			MCPlayer mcp = JsonDB.database.findById(e.getPlayer().getUniqueId().toString(), MCPlayer.class);
			
			if (mcp == null) {
				mcp = new MCPlayer();
				mcp.setMinecraftName(e.getPlayer().getName());
				mcp.setUuid(e.getPlayer().getUniqueId().toString());
				mcp.setLinked(false);
				JsonDB.database.upsert(mcp);
			}
			
			String message = String.format("""
					**%s** *<:trophy:690738524659646485> Advancement -> %s* **%s**
					""",
					minecraftPrefix,
					mcp.getMinecraftName(),
					e.getAdvancement().getKey().getKey());
			
			if (mcp.isLinked()) {
				message = String.format("""
					**%s** *<:trophy:690738524659646485> Advancement -> %s[%s]* **%s**
					""",
					minecraftPrefix,
					mcp.getMinecraftName(),
					mcp.getDiscordEffName(),
					e.getAdvancement().getKey().getKey());
			}
		
			DiscordUtils.sendRelayMessage(message);
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
		MCPlayer mcp = JsonDB.database.findById(e.getPlayer().getUniqueId().toString(), MCPlayer.class);
		
		if (mcp == null) {
			mcp = new MCPlayer();
			mcp.setMinecraftName(e.getPlayer().getName());
			mcp.setUuid(e.getPlayer().getUniqueId().toString());
			mcp.setLinked(false);
			JsonDB.database.upsert(mcp);
		}
		
		Raid r = e.getRaid();
		Location loc = r.getLocation();
		
		String message = " ";
		
		if (mcp.isLinked()) {
			message = DiscordUtils.format(minecraftPrefix, "bold") 
				+ " " 
				+ DiscordUtils.format("<:crossed_swords:698730737045864448> Raid -> " + mcp.getMinecraftName() + "[" + mcp.getDiscordEffName() +"]", "italics")
				+ " " 
				+ DiscordUtils.format("->", "bold") + " " + mcp.getMinecraftName() + "[" + mcp.getDiscordEffName() +"]" + "has triggered a " + r.getBadOmenLevel() + " raid at (" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ").";
			
		} else {
			message = DiscordUtils.format(minecraftPrefix, "bold") 
				+ " " 
				+ DiscordUtils.format("<:crossed_swords:698730737045864448> Raid -> " + e.getPlayer().getName(), "italics")
				+ " " 
				+ DiscordUtils.format("->", "bold") + " " + e.getPlayer().getName() + "has triggered a " + r.getBadOmenLevel() + " raid at (" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ").";
			
		}

		String p1 = "&6" + e.getPlayer().getName() + "&4&lhas triggered a level " + (r.getBadOmenLevel()+1) + " raid at &c(" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ")&4&l.";
		String p2 = "&4Totals -> &cGroups: " + r.getTotalGroups() + ", Health: " + (r.getTotalHealth()+1) + ", Waves: " + r.getTotalWaves();
		
		
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', p1));
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', p2));
		
		DiscordUtils.sendRelayMessage(message);
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
		
		String message = DiscordUtils.format(minecraftPrefix, "bold") 
				+ " <:crossed_swords:698730737045864448>" 
				+ " " 
				+ DiscordUtils.format("Raid", "italics") 
				+ " " 
				+ DiscordUtils.format("->", "bold") + " The raid has been completed <:trophy:690738524659646485>";
		
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
		
		DiscordUtils.sendRelayMessage(message);
	}
	
	@EventHandler
	public void onRaidStop(RaidStopEvent e) {
		if (e.getReason() == RaidStopEvent.Reason.FINISHED) {
			return;
		}
		
		Raid r = e.getRaid();
		
		String message = DiscordUtils.format(minecraftPrefix, "bold") 
				+ " <:crossed_swords:698730737045864448>" 
				+ " " 
				+ DiscordUtils.format("Raid", "italics") 
				+ " " 
				+ DiscordUtils.format("->", "bold") + " The raid has stopped. Reason: " + e.getReason() + " <:skull_crossbones:690567471618457631>";
		
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
		
		DiscordUtils.sendRelayMessage(message);
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
		
		if (inven == null) return; 
		
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
