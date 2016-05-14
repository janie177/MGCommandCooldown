package net.minegusta.mgcommandcooldown.listeners;

import net.minegusta.mgcommandcooldown.cooldownhandler.CooldownHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class CooldownListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onEvent(PlayerCommandPreprocessEvent e)
	{
		Player p = e.getPlayer();

		if(e.isCancelled()) return;

		String command = e.getMessage().toLowerCase().replace("/", "");

		//Clear all tasks as soon as this is ran.
		CooldownHandler.clearTasks(p.getName());

		if(command.contains(":"))
		{
			String[] split = command.split(":");

			boolean pluginEnabled = false;
			for(Plugin pl : Bukkit.getPluginManager().getPlugins())
			{
				if(pl.getName().equalsIgnoreCase(split[0]))
				{
					pluginEnabled = true;
					break;
				}
			}

			if(pluginEnabled)
			{
				if(split.length == 1)
				{
					e.setCancelled(true);
					return;
				}

				String cmd = "";
				for(int i = 1; i < split.length; i++)
				{
					cmd = cmd + " " + split[i];
				}
				command = cmd;
			}
		}

		command = command.trim();

		String[] base = command.split(" ");

		//Alias detection
		PluginCommand pluginCommand;
		if((pluginCommand = Bukkit.getPluginCommand(base[0])) != null)
		{
			command = command.replace(base[0], pluginCommand.getName());
		}

		for(String s : CooldownHandler.getCommands())
		{
			if(command.startsWith(s))
			{
				String commandPermNode = s.replace(" ", "");

				//Check the cooldown and send a message if the player cannot run the command.
				long cooldown;

				//Player has cooldown
				if(!(p.hasPermission("minegusta.cooldown.bypass." + commandPermNode)) && (cooldown = CooldownHandler.getPlayerCooldown(p.getName(), s)) > 0)
				{
					p.sendMessage(ChatColor.RED + "[CMD] " + ChatColor.YELLOW + "You have to wait another " + ChatColor.LIGHT_PURPLE + cooldown + ChatColor.YELLOW + " seconds before you can use that command.");
					e.setCancelled(true);
					return;
				}
				long warmup;
				if(!(p.hasPermission("minegusta.warmup.bypass." + commandPermNode)) && (warmup = CooldownHandler.getWarmup(s)) != 0 && !CooldownHandler.isWarmedUp(p.getName(), s))
				{
					p.sendMessage(ChatColor.GREEN + "[CMD] " + ChatColor.GRAY + "Your command is warming up! It will run in " + ChatColor.YELLOW + warmup + ChatColor.GRAY + " seconds.");
					p.sendMessage(ChatColor.GREEN + "[CMD] " + ChatColor.GRAY + "Moving or taking damage will cancel the command warmup.");
					CooldownHandler.scheduleWarmup(p.getName(), s, command, CooldownHandler.getWarmup(s));
					e.setCancelled(true);
					return;
				}

				CooldownHandler.resetWarmup(p.getName(), s);
				CooldownHandler.clearTasks(p.getName());
				CooldownHandler.setPlayerCooldown(p.getName(), command, CooldownHandler.getCooldown(s));
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(EntityDamageEvent e)
	{
		if(!e.isCancelled() && e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if(CooldownHandler.hasTasks(p.getName()))
			{
				CooldownHandler.clearTasks(p.getName());
				p.sendMessage(ChatColor.RED + "[CMD] " + ChatColor.GRAY + "You took damage, which cancelled your command warm-ups.");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(EntityDamageByEntityEvent e)
	{
		if(!e.isCancelled() && e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if(CooldownHandler.hasTasks(p.getName()))
			{
				CooldownHandler.clearTasks(p.getName());
				p.sendMessage(ChatColor.RED + "[CMD] " + ChatColor.GRAY + "You took damage, which cancelled your command warm-ups.");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(EntityDamageByBlockEvent e)
	{
		if(!e.isCancelled() && e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			if(CooldownHandler.hasTasks(p.getName()))
			{
				CooldownHandler.clearTasks(p.getName());
				p.sendMessage(ChatColor.RED + "[CMD] " + ChatColor.GRAY + "You took damage, which cancelled your command warm-ups.");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(PlayerMoveEvent e)
	{
		if(!e.isCancelled())
		{
			Player p = e.getPlayer();
			if(e.getFrom().distance(e.getTo()) > 0.1 && CooldownHandler.hasTasks(p.getName()))
			{
				CooldownHandler.clearTasks(p.getName());
				p.sendMessage(ChatColor.RED + "[CMD] " + ChatColor.GRAY + "You moved, which cancelled your command warm-ups.");
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		CooldownHandler.createPlayerObject(e.getPlayer().getName());
	}
}
