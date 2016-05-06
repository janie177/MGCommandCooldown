package net.minegusta.mgcommandcooldown.cooldownhandler;

import com.google.common.collect.Maps;
import net.minegusta.mgcommandcooldown.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentMap;

public class CooldownHandler {

	private static ConcurrentMap<String, CooldownCommand> commandCooldowns = Maps.newConcurrentMap();
	private static ConcurrentMap<String, CooldownPlayer> playerCooldowns = Maps.newConcurrentMap();

	public static void loadFromConfig()
	{
		commandCooldowns.clear();
		FileConfiguration conf = Main.getPlugin().getConfig();
		for(String s : conf.getKeys(false))
		{
			long cooldown = conf.getLong(s + ".cooldown", 0);
			long warmup = conf.getLong(s + ".warmup", 0);
			String name = conf.getString(s + ".name", s);

			commandCooldowns.put(s.toLowerCase(), CooldownCommand.create(name.toLowerCase(), cooldown, warmup));
		}
	}

	public static String[] getCommands()
	{
		return (String[]) commandCooldowns.keySet().toArray();
	}

	public static long getWarmup(String command)
	{
		if(commandCooldowns.containsKey(command.toLowerCase()))
		{
			return commandCooldowns.get(command.toLowerCase()).getWarmup();
		}
		return 0;
	}

	public static long getCooldown(String command)
	{
		if(commandCooldowns.containsKey(command.toLowerCase()))
		{
			return commandCooldowns.get(command.toLowerCase()).getCooldown();
		}
		return 0;
	}

	public static long getPlayerCooldown(String playername, String command)
	{
		CooldownPlayer p = playerCooldowns.get(playername.toLowerCase());
		if(p.hasCooldown(command.toLowerCase()))
		{
			return p.getRemainingSeconds(command.toLowerCase());
		}
		return 0;
	}

	public static void setPlayerCooldown(String playername, String command, long cooldown)
	{
		CooldownPlayer p = playerCooldowns.get(playername.toLowerCase());
		p.addCooldown(command.toLowerCase(), System.currentTimeMillis() + (cooldown * 1000));
	}

	public boolean cooldownExpired(String playername, String command)
	{
		return getPlayerCooldown(command, playername) <= 0;
	}

	public static void createPlayerObject(String playername)
	{
		playerCooldowns.putIfAbsent(playername.toLowerCase(), CooldownPlayer.create(playername.toLowerCase()));
	}

	public static void scheduleWarmup(String playerName, String command, long delay)
	{
		CooldownPlayer p = playerCooldowns.get(playerName.toLowerCase());
		p.setWarmUp(command, delay * 1000);
		p.addTask(Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
			try {
				Player player;
				if((player = Bukkit.getPlayerExact(playerName)) != null) {
					player.performCommand(command);
				}
			} catch (Exception ignored) {}
		}, (delay * 20) + 1));
	}

	public static boolean isWarmedUp(String player, String command)
	{
		CooldownPlayer p = playerCooldowns.get(player.toLowerCase());
		return p.isWarmedUp(command.toLowerCase());
	}

	public static void resetWarmup(String playername, String command)
	{
		CooldownPlayer p = playerCooldowns.get(playername.toLowerCase());
		p.resetWarmUp(command.toLowerCase());
	}

	public static void clearTasks(String player)
	{
		CooldownPlayer p = playerCooldowns.get(player.toLowerCase());
		p.getTasks().stream().forEach(t -> {
			if(Bukkit.getScheduler().isQueued(t))
			{
				Bukkit.getScheduler().cancelTask(t);
			}
			p.removeTask(t);
		});
	}

	public static boolean hasTasks(String player)
	{
		CooldownPlayer p = playerCooldowns.get(player.toLowerCase());
		return p.hasTasks();
	}
}
