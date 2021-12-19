package discordbotplugin.net.discord.commands.general;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.discord.DiscordBot;
import discordbotplugin.net.discord.commands.CommandUtilities;

public class DiscordCommandListPlayers extends Command {

	private String feature = "discord_command_list";
	
	public DiscordCommandListPlayers() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		
		if (!CommandUtilities.fullUsageCheck(event, feature)) {
			return;
		}
		
		if (Bukkit.getServer().getOnlinePlayers().size() <= 0) {
			event.reply("No players currently online.");
			return;
		}
		
		String msg = "Players Online: \n";
		
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			msg += p.getName() + ", ";
		}
		
		event.reply(msg.substring(0, msg.lastIndexOf(", ")));
	}

}
