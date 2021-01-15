package discordbotplugin.net.discord.commands.restart;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.Launcher;
import discordbotplugin.net.discord.DiscordBot;

public class DiscordCommandRestartBot extends Command {

	public DiscordCommandRestartBot() {
		this.name = "bot";
		this.hidden = true;
		this.ownerCommand = true;
		
		
	}
	
	@Override
	protected void execute(CommandEvent event) {
		event.reply("Restarting Discord Bot...");
		DiscordBot.jda.shutdown();
		Launcher.discordBot = new DiscordBot();
	}
}
