package discordbotplugin.net;

import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.AnsiConsole;

import discordbotplugin.net.database.JsonDB;
import discordbotplugin.net.discord.DiscordBot;
import discordbotplugin.net.discord.DiscordUtils;
import discordbotplugin.net.logger.Logger;
import discordbotplugin.net.logger.Logger.Level;
import discordbotplugin.net.relay.MessageRelay;

public class Launcher extends JavaPlugin {

	/**
	 * User working directory/Current working directory.
	 */
	public static String uwd = System.getProperty("user.dir");
	public static DiscordBot discordBot;

	public static void main(String[] args) {
		// allows ANSI escape sequences to format console output for loggers. a.k.a.
		// PRETTY COLORS
		AnsiConsole.systemInstall();

		// initialize the database
		Logger.log(Level.INFO, "Database initialized...");
		JsonDB.init();

		Logger.log(Level.INFO, "Discord Bot initialized...");
		discordBot = new DiscordBot();

	}

	@Override
	public void onEnable() {
		// allows ANSI escape sequences to format console output for loggers. a.k.a.
		// PRETTY COLORS
		AnsiConsole.systemInstall();

		// initialize the database
		Logger.log(Level.INFO, "Database initialized...");
		JsonDB.init();

		Logger.log(Level.INFO, "Discord Bot initialized...");
		discordBot = new DiscordBot();

		DiscordUtils.sendRelayMessage(DiscordUtils.format(MessageRelay.minecraftPrefix, "bold")
				+ " SERVER STARTED <:green_circle:690600732113502239>");
	}

	@Override
	public void onDisable() {
		DiscordUtils.sendRelayMessage(DiscordUtils.format(MessageRelay.minecraftPrefix, "bold")
				+ " SERVER SHUTDOWN <:red_circle:690600907666358314>");
		DiscordBot.jda.shutdownNow();
	}
}
