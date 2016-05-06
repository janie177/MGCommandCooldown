package net.minegusta.mgcommandcooldown.commands;

import net.minegusta.mgcommandcooldown.cooldownhandler.CooldownHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if(s.isOp() || s.hasPermission("minegusta.cooldown.reload"))
		{
			s.sendMessage(ChatColor.GREEN + "Minegusta Cooldowns Reloaded.");
			CooldownHandler.loadFromConfig();
		}
		else
		{
			s.sendMessage(ChatColor.RED + "You do not have permission to use this command. B-Baka.");
		}
		return true;
	}
}
