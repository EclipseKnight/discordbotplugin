package discordbotplugin.net.discord.commands.ban;

import org.bukkit.Bukkit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.Launcher;
import discordbotplugin.net.discord.commands.CommandUtilities;

public class DiscordCommandUnban extends Command {

	public DiscordCommandUnban() {
		this.name = "unban";
		this.aliases = new String[] {"pardon"};
		this.hidden = true;
	
	}
	
	@Override
	protected void execute(CommandEvent event) {
		
		if (!CommandUtilities.fullUsageCheck(event, "discord_command_ban")) {
			return;
		}
		
		String[] args = event.getArgs().split("\\s+");
		
		if (args[0].isBlank()) {
			event.reply("Invalid Arguments; [unban, pardon] <player>");
			return;
		}
		
		final String p = new String(args[0]);
		
		Bukkit.getScheduler().callSyncMethod(Launcher.getPlugin(Launcher.class), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pardon " + p));
		event.reply(p + " is now unbanned.");
		
	}

}
