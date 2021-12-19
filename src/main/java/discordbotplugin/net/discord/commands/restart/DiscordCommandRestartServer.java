package discordbotplugin.net.discord.commands.restart;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.Launcher;

public class DiscordCommandRestartServer extends Command {

	public DiscordCommandRestartServer() {
		this.name = "server";
	}
	
	@Override
	protected void execute(CommandEvent event) {
		Plugin discordBotPluginStore = Bukkit.getPluginManager().getPlugin("MinecraftDiscordBotPluginStore");
		Bukkit.getPluginManager().disablePlugin(discordBotPluginStore);
		
		Bukkit.getScheduler().callSyncMethod(Launcher.getPlugin(Launcher.class), 
				() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spigot:restart"));
	}
}
