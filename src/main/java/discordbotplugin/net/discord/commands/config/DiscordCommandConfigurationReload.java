package discordbotplugin.net.discord.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.discord.DiscordBot;

public class DiscordCommandConfigurationReload extends Command {

	
	public DiscordCommandConfigurationReload() {
		this.name = "reload";
	}

	@Override
	protected void execute(CommandEvent event) {
		DiscordBot.loadConfiguration();
		event.reply("Configuration reloaded...");
	}
}
