package discordbotplugin.net.discord;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Emote;

public class DiscordUtils {

	
	public static void sendTimedMessaged(CommandEvent event, String message, int ms, boolean isPrivate) {
		
		if (isPrivate) {
			event.getMember().getUser().openPrivateChannel().queue(channel -> {
				channel.sendMessage(message).queue( m -> {
					m.delete().queueAfter(ms, TimeUnit.MICROSECONDS);
				});
			});
			
			return;
		}
		
		event.getChannel().sendMessage(message).queue( m -> {
			m.delete().queueAfter(ms, TimeUnit.MILLISECONDS);
		});
	}
	
	public static void sendMessage(CommandEvent event, String message, boolean isPrivate) {
		
		if (isPrivate) {
			event.getMember().getUser().openPrivateChannel().queue(channel -> {
				channel.sendMessage(message).queue();
			});
			
			return;
		}
		
		event.getChannel().sendMessage(message).queue();
	}
	
	public static void sendRelayMessage(String message) {

		String guildId = DiscordBot.configuration.getGuildId();
		
		for (String id: DiscordBot.configuration.getFeatures().get("discord_message_relay").getChannels()) {
			DiscordBot.jda.getGuildById(guildId).getTextChannelById(id).sendTyping().queue();
			DiscordBot.jda.getGuildById(guildId).getTextChannelById(id).sendMessage(message).queue();
		}
	}
	
	public static void setBotStatus(String status) {
		DiscordBot.jda.getPresence().setActivity(Activity.of(ActivityType.DEFAULT, status));
	}
	
	public static Emote getGuildEmote(String name, boolean ignoreCase) {
		String guildId = DiscordBot.configuration.getGuildId();
		
		List<Emote> emotes = DiscordBot.jda.getGuildById(guildId).getEmotesByName(name, ignoreCase);
		
		if (!emotes.isEmpty()) {
			return emotes.get(0);
		}
		return null;
		
	}
	
	public static String format(String message, String form) {

		if ("bold".equals(form)) {
			return "**" + message + "**";
		}

		if ("italics".equals(form)) {
			return "*" + message + "*";
		}
		return message;
	}
}
