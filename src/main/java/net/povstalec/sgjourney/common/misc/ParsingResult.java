package net.povstalec.sgjourney.common.misc;

import net.minecraft.network.chat.Component;

public class ParsingResult
{
	public static final ParsingResult SUCCESS = new ParsingResult();
	
	private final boolean isSuccess;
	private final Component message;
	private final Runnable doThrow;
	
	private ParsingResult()
	{
		this.isSuccess = true;
		this.doThrow = () -> {};
		this.message = Component.empty();
	}
	
	private ParsingResult(Component message, Runnable doThrow)
	{
		this.isSuccess = false;
		this.message = message;
		this.doThrow = doThrow;
	}
	
	public boolean isSuccess()
	{
		return isSuccess;
	}
	
	public Component getMessage()
	{
		return message;
	}
	
	public void doThrow()
	{
		doThrow.run();
	}
	
	public static ParsingResult success()
	{
		return SUCCESS;
	}
	
	public static ParsingResult failure(Component message, Runnable doThrow)
	{
		return new ParsingResult(message, doThrow);
	}
}
