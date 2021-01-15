package discordbotplugin.net.discord.commands.whitelist;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.discord.DiscordBot;
import discordbotplugin.net.discord.commands.CommandUtilities;

public class DiscordCommandWhitelist extends Command {

	private String feature = "discord_command_whitelist";
	
	public DiscordCommandWhitelist() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
		this.children = new Command[] {new DiscordCommandWhitelistAdd(), new DiscordCommandWhitelistRemove()};
	}

	@Override
	protected void execute(CommandEvent event) {
		
		if (!CommandUtilities.fullUsageCheck(event, feature)) {
			return;
		}
		
		event.reply("Invalid Arguments: whitelist [add, remove] <player>");
	}
}
