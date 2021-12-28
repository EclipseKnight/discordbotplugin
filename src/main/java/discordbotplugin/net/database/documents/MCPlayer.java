package discordbotplugin.net.database.documents;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "players", schemaVersion = "1.0")
public class MCPlayer {

	@Id
	private String uuid;
	
	private String minecraftName;
	private String discordId;
	private String discordEffName;
	
	private boolean isLinked;
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getMinecraftName() {
		return minecraftName;
	}
	
	public void setMinecraftName(String minecraftName) {
		this.minecraftName = minecraftName;
	}
	
	public String getDiscordId() {
		return discordId;
	}
	
	public void setDiscordId(String discordId) {
		this.discordId = discordId;
	}
	
	public String getDiscordEffName() {
		return discordEffName;
	}
	
	public void setDiscordEffName(String discordEffName) {
		this.discordEffName = discordEffName;
	}
	
	public boolean isLinked() {
		return isLinked;
	}
	
	public void setLinked(boolean isLinked) {
		this.isLinked = isLinked;
	}
	
}
