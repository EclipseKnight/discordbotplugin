package discordbotplugin.net.discord.commands;

import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.discord.DiscordBot;
import discordbotplugin.net.discord.DiscordUtils;
import net.dv8tion.jda.api.entities.Role;

public class CommandUtilities {

	
	
	public static boolean fullUsageCheck(CommandEvent event, String feature) {
		boolean result = true;
		String reply = "";
		
		if (!CommandUtilities.isFeatureEnabled(feature)) {
			reply += "Command is disabled. ";
			result = false;
		}
		
		if (!CommandUtilities.correctChannel(event, feature)) {
			reply += "Command is disabled in this channel. ";
			result = false;
		}
		
		if (!CommandUtilities.canUseCommand(event, feature)) {
			reply += "Command is disabled for your role.";
			result = false;
		}
		
		if (!reply.isEmpty()) {
			DiscordUtils.sendTimedMessaged(event, reply, 3000, false);
		}
		
		return result;
	}
	
	public static boolean isFeatureEnabled(String feature) {
		return DiscordBot.configuration.getFeatures().get(feature).isEnabled();
	}
	
	public static boolean correctChannel(CommandEvent event, String feature) {
		List<String> channels = DiscordBot.configuration.getFeatures().get(feature).getChannels();
		
		if (channels == null || channels.get(0) == null) {
			return true;
		}
		
		if (channels.contains(event.getChannel().getId())) {
			return true;
		}
		
		return false;
	}
	
	public static boolean canUseCommand(CommandEvent event, String feature) {
		
		List<String> cmdRoles = DiscordBot.configuration.getFeatures().get(feature).getRoles();
		
		if (cmdRoles == null || cmdRoles.get(0) == null) {
			return true;
		}
		
		List<String> pRoles = new ArrayList<>();
		
		for (Role r : event.getMember().getRoles()) {
			pRoles.add(r.getId());
		}
		
		
		for (String pRole: pRoles) {
			for (String cmdRole: cmdRoles) {
				if (pRole.equals(cmdRole)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
