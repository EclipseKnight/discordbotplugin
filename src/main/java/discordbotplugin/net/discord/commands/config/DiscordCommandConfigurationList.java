package discordbotplugin.net.discord.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.discord.DiscordBot;

public class DiscordCommandConfigurationList extends Command {

	
	public DiscordCommandConfigurationList() {
		this.name = "list";
		this.aliases = new String[] {"ls"};
	}

	@Override
	protected void execute(CommandEvent event) {
		event.reply(DiscordBot.configuration.toString());
	}
}
