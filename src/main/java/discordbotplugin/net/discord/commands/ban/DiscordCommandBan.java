package discordbotplugin.net.discord.commands.ban;

import org.bukkit.Bukkit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.Launcher;
import discordbotplugin.net.discord.commands.CommandUtilities;

public class DiscordCommandBan extends Command {

	private String feature = "discord_command_ban";
	
	public DiscordCommandBan() {
		this.name = "ban";
	}
	
	@Override
	protected void execute(CommandEvent event) {
		
		if (!CommandUtilities.fullUsageCheck(event, feature)) {
			return;
		}
		
		String[] args = event.getArgs().split("\\s+");
		
		if (args[0].isBlank()) {
			event.reply("Invalid Arguments; ban <player>");
			return;
		}
		
		final String p = new String(args[0]);
		
		Bukkit.getScheduler().callSyncMethod(Launcher.getPlugin(Launcher.class), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + p));
		event.reply(p + " is now banned.");
		
	}

}
