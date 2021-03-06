package net.minegusta.mgcommandcooldown.cooldownhandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class CooldownPlayer {
	private String name;
	private ConcurrentMap<String, Long> commands = Maps.newConcurrentMap();
	private ConcurrentMap<String, Boolean> warmUpCalls = Maps.newConcurrentMap();
	private ConcurrentMap<Integer, Boolean> tasks = Maps.newConcurrentMap();

	private CooldownPlayer(String name)
	{
		this.name = name;
	}

	public static CooldownPlayer create(String name)
	{
		return new CooldownPlayer(name);
	}

	public void addCooldown(String command, long expire)
	{
		commands.put(command.toLowerCase(), expire);
	}

	public boolean hasCooldown(String command)
	{
		return commands.containsKey(command.toLowerCase()) && commands.get(command.toLowerCase()) > System.currentTimeMillis();
	}

	public long getRemainingSeconds(String command)
	{
		if(commands.containsKey(command.toLowerCase()))
		{
			return TimeUnit.MILLISECONDS.toSeconds(commands.get(command.toLowerCase()) - System.currentTimeMillis());
		}
		return 0;
	}

	public boolean isWarmedUp(String command)
	{
		if(warmUpCalls.containsKey(command.toLowerCase()))
		{
			return warmUpCalls.get(command.toLowerCase());
		}
		return false;
	}

	public void setWarmUp(String command, boolean warmedUp)
	{
		warmUpCalls.put(command.toLowerCase(), warmedUp);
	}

	public void resetWarmUp(String command)
	{
		if(warmUpCalls.containsKey(command.toLowerCase()))
		{
			warmUpCalls.remove(command.toLowerCase());
		}
	}

	public void addTask(int id)
	{
		tasks.put(id, false);
	}

	public void removeTask(int id)
	{
		if(tasks.containsKey(id))
		{
			tasks.remove(id);
		}
	}

	public Set<Integer> getTasks()
	{
		return tasks.keySet();
	}

	public boolean hasTasks()
	{
		return !tasks.isEmpty();
	}
}
