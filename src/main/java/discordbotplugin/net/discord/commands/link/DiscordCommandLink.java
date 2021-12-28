package discordbotplugin.net.discord.commands.link;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.database.JsonDB;
import discordbotplugin.net.database.documents.MCPlayer;
import discordbotplugin.net.discord.DiscordBot;
import discordbotplugin.net.discord.commands.CommandUtilities;

public class DiscordCommandLink extends Command {

	private final String feature = "discord_command_link";
	public DiscordCommandLink() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (!CommandUtilities.fullUsageCheck(event, feature)) {
			return;
		}
		
		if (event.getArgs().isBlank()) {
			event.reply("Invalid Arguments: !d link <minecraftname>");
		}
		
		String mcname = event.getArgs();
		String discordName = event.getAuthor().getAsTag();
		String discordId = event.getAuthor().getId();
		
		
		for (Player p: Bukkit.getServer().getOnlinePlayers()) {
			if (p.getName().equals(mcname)) {
				MCPlayer mcp = new MCPlayer();
				mcp.setDiscordEffName(discordName);
				mcp.setDiscordId(discordId);
				mcp.setMinecraftName(mcname);
				mcp.setUuid(p.getUniqueId().toString());
				mcp.setLinked(true);
				JsonDB.database.upsert(mcp);
				
				event.reply("Player Linked! " + mcp.getDiscordEffName() + ":" + mcp.getMinecraftName());
				return;
				
			}
		}
		
		event.reply("Player Link Failed... \nMake sure the player is logged into the server and the name is spelled correctly.");
	}

}
