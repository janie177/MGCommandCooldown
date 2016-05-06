package net.minegusta.mgcommandcooldown.listeners;

import net.minegusta.mgcommandcooldown.cooldownhandler.CooldownHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class CooldownListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEvent(PlayerCommandPreprocessEvent e)
	{
		Player p = e.getPlayer();

		if(e.isCancelled()) return;

		String command = e.getMessage().toLowerCase().replace("/", "");

		for(String s : CooldownHandler.getCommands())
		{
			if(command.startsWith(s))
			{
				String commandPermNode = s.replace(" ", "");

				//Check the cooldown and send a message if the player cannot run the command.
				long cooldown;

				//Player has cooldown
				if(!(p.hasPermission("minegusta.cooldown.bypass." + commandPermNode)) && (cooldown = CooldownHandler.getPlayerCooldown(p.getName(), s)) != 0)
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
					CooldownHandler.scheduleWarmup(p.getName(), s, CooldownHandler.getWarmup(s));
					e.setCancelled(true);
					return;
				}

				CooldownHandler.resetWarmup(p.getName(), s);
				CooldownHandler.clearTasks(p.getName());
				CooldownHandler.setPlayerCooldown(p.getName(), s, CooldownHandler.getCooldown(s));
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
