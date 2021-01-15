package discordbotplugin.net.discord.commands.whitelist;

import org.bukkit.Bukkit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.Launcher;

public class DiscordCommandWhitelistRemove extends Command {

	public DiscordCommandWhitelistRemove() {
		this.name = "remove";
		this.hidden = true;
	}
	
	@Override
	protected void execute(CommandEvent event) {
		String[] args = event.getArgs().split("\\s+");
		
		if (args[0].isBlank()) {
			event.reply("Invalid Arguments; whitelist remove <player>");
			return;
		}
		
		final String p = new String(args[0]);
		
		Bukkit.getScheduler().callSyncMethod(Launcher.getPlugin(Launcher.class), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist remove " + p));
		event.reply(p + " was removed from the whitelist.");
	}
}
