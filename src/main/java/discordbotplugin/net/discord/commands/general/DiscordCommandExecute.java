package discordbotplugin.net.discord.commands.general;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import discordbotplugin.net.Launcher;
import discordbotplugin.net.discord.DiscordBot;
import discordbotplugin.net.discord.commands.CommandUtilities;

public class DiscordCommandExecute extends Command {
	
	private String feature = "discord_command_execute";
	
	public DiscordCommandExecute() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		
		if (!CommandUtilities.fullUsageCheck(event, feature)) {
			return;
		}
		
		Future<Boolean> result = Bukkit.getScheduler().callSyncMethod(Launcher.getPlugin(Launcher.class), new ExecuteTask(event.getArgs()));

		try {
			if (!result.get()) {
				event.reply("Command failed to execute. /n" + "CMD: " + event.getArgs());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	
	private class ExecuteTask implements Callable<Boolean> {

		private String args;
		
		public ExecuteTask(String args) {
			this.args = args;
		}
		
		@Override
		public Boolean call() throws Exception {
			return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), args);	
		}
		
	}
}
