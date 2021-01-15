package discordbotplugin.net.discord;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordConfiguration {

	private Map<String, String> bot;

	private Map<String, String> api;
	
	private String ownerId;
	
	private List<String> coOwnerIds;

	private String guildId;
	
	private Map<String, DiscordFeature> features;

	public Map<String, String> getBot() {
		return bot;
	}

	public void setBot(Map<String, String> bot) {
		this.bot = bot;
	}

	public Map<String, String> getApi() {
		return api;
	}

	public void setApi(Map<String, String> api) {
		this.api = api;
	}
	
	public String getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	public List<String> getCoOwnerIds() {
		return coOwnerIds;
	}

	public void setGuildId(String guildId) {
		this.guildId = guildId;
	}
	
	public String getGuildId() {
		return guildId;
	}
	
	public Map<String, DiscordFeature> getFeatures() {
		return features;
	}
	
	public void setFeatures(Map<String, DiscordFeature> features) {
		this.features = features;
		
	}
	

	@Override
	public String toString() {
		return String.format("""
				Configuration:
					bot = %s
					
					owner_id = %s
					
					co_owner_ids = %s
					
					guild_id = %s
					
					features:
						discord_command_test:
					 		enabled = %s
					 		channels = %s
					 		roles = %s
					 	
					 	discord_command_execute:
					 		enabled = %s
					 		channels = %s
					 		roles = %s
					 	
					 	discord_command_restart:
					 		enabled = %s
					 		channels = %s
					 		roles = %s
					 
					 	discord_command_whitelist:
					 		enabled = %s
					 		channels = %s
					 		roles = %s
					 		
					 	discord_command_ban:
					 		enabled = %s
					 		channels = %s
					 		roles = %s
					 	
					 	discord_command_list:
					 		enabled = %s
					 		channels = %s
					 		roles = %s
					 	
					 	discord_message_relay:
					 		enabled = %s,
					 		channels = %s
					 		
				""", bot, ownerId, coOwnerIds, guildId,
				features.get("discord_command_test").isEnabled(),
				features.get("discord_command_test").getChannels(),
				features.get("discord_command_test").getRoles(),
				features.get("discord_command_execute").isEnabled(),
				features.get("discord_command_execute").getChannels(),
				features.get("discord_command_execute").getRoles(),
				features.get("discord_command_restart").isEnabled(),
				features.get("discord_command_restart").getChannels(),
				features.get("discord_command_restart").getRoles(),
				features.get("discord_command_whitelist").isEnabled(),
				features.get("discord_command_whitelist").getChannels(),
				features.get("discord_command_whitelist").getRoles(),
				features.get("discord_command_ban").isEnabled(),
				features.get("discord_command_ban").getChannels(),
				features.get("discord_command_ban").getRoles(),
				features.get("discord_command_list").isEnabled(),
				features.get("discord_command_list").getChannels(),
				features.get("discord_command_list").getRoles(),
				features.get("discord_message_relay").isEnabled(),
				features.get("discord_message_relay").getChannels()
				);
	}
}
