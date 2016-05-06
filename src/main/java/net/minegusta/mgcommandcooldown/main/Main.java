package net.minegusta.mgcommandcooldown.main;

import net.minegusta.mgcommandcooldown.commands.ReloadCommand;
import net.minegusta.mgcommandcooldown.cooldownhandler.CooldownHandler;
import net.minegusta.mgcommandcooldown.listeners.CooldownListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Plugin PLUGIN;

	@Override
	public void onEnable()
	{
		//assign this plugin instance to the static value
		PLUGIN = this;

		//Save the default config if it does not exist yet.
		saveDefaultConfig();

		//Load the commands from config.
		CooldownHandler.loadFromConfig();

		//commands
		Bukkit.getPluginCommand("mgccreload").setExecutor(new ReloadCommand());

		//listeners
		Bukkit.getPluginManager().registerEvents(new CooldownListener(), this);


	}


	@Override
	public void onDisable()
	{

	}


	public static Plugin getPlugin()
	{
		return PLUGIN;
	}
}
