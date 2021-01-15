package discordbotplugin.net.discord.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.discord.DiscordUtilities;

public class DiscordCommandConfiguration extends Command {

	public DiscordCommandConfiguration() {
		this.name = "config";
		this.children = new Command[] {new DiscordCommandConfigurationList(), new DiscordCommandConfigurationReload()};
	}
	
	@Override
	protected void execute(CommandEvent event) {
		DiscordUtilities.sendTimedMessaged(event, "Invalid Arguments: config [list, ls] or [reload].", 5000, false);
	}

}
