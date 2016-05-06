package net.minegusta.mgcommandcooldown.cooldownhandler;

public class CooldownCommand {
	private String name;
	private long cooldown;
	private long warmup;

	private CooldownCommand(String name, long cooldown, long warmup)
	{
		this.name = name;
		this.cooldown = cooldown;
		this.warmup = warmup;
	}

	public static CooldownCommand create(String name, long cooldown, long warmup)
	{
		return new CooldownCommand(name, cooldown, warmup);
	}

	public String getName()
	{
		return name;
	}

	public long getCooldown()
	{
		return cooldown;
	}

	public long getWarmup()
	{
		return warmup;
	}
}
