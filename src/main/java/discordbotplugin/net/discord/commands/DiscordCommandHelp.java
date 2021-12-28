package discordbotplugin.net.discord.commands;

import java.util.function.Consumer;

import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.discord.DiscordBot;
import discordbotplugin.net.discord.DiscordUtils;

public class DiscordCommandHelp implements Consumer<CommandEvent> {

	@Override
	public void accept(CommandEvent event) {
		String[] commands = new String[] {
				"discord_command_test",
				"discord_command_execute",
				"discord_command_restart",
				"discord_command_whitelist",
				"discord_command_ban",
				"discord_command_list",
				"discord_command_link"
				};
		
		String message = "Commands you can use:\n";
		
		for (String c : commands) {
			
			if (canUse(event, c)) {
				message += DiscordBot.prefix
						+ DiscordBot.configuration.getFeatures().get(c).getDescription() + "\n";
			}
		}
		
		DiscordUtils.sendMessage(event, message, true);
	}
	
	private boolean canUse(CommandEvent event, String feature) {
		boolean result = true;
		
		if (!CommandUtilities.isFeatureEnabled(feature)) {
			result = false;
		}
		
		if (!CommandUtilities.canUseCommand(event, feature)) {
			result = false;
		}
		
		return result;
	}
}
