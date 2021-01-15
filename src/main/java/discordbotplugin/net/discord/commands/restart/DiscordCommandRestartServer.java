package discordbotplugin.net.discord.commands.restart;

import org.bukkit.Bukkit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.Launcher;

public class DiscordCommandRestartServer extends Command {

	public DiscordCommandRestartServer() {
		this.name = "server";
		this.hidden = true;
		this.ownerCommand = true;
		
		
	}
	
	@Override
	protected void execute(CommandEvent event) {
		Bukkit.getScheduler().callSyncMethod(Launcher.getPlugin(Launcher.class), 
				() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spigot:restart"));
	}
}
