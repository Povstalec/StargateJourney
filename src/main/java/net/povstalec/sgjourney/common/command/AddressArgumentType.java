package net.povstalec.sgjourney.common.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ResourceLocationException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.common.sgjourney.Address;

import java.util.Arrays;
import java.util.Collection;

public class AddressArgumentType implements ArgumentType<Address.Immutable>
{
	private static final Collection<String> EXAMPLE_9_CHEVRON = Arrays.asList("-1-2-3-4-5-6-7-8-");
	private static final Collection<String> EXAMPLE_8_CHEVRON = Arrays.asList("-1-2-3-4-5-6-7-");
	private static final Collection<String> EXAMPLE_7_CHEVRON = Arrays.asList("-1-2-3-4-5-6-");
	private static final Collection<String> EXAMPLE_INVALID = Arrays.asList("-");
	public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.translatable("sgjourney.argument.address.incomplete"));
	
	private Address.Type addressType;
	
	public AddressArgumentType(Address.Type addressType)
	{
		this.addressType = addressType;
	}
	
	public Address.Type type()
	{
		return addressType;
	}
	
	@Override
	public Address.Immutable parse(StringReader reader) throws CommandSyntaxException
	{
		Address.Immutable address = Address.Immutable.read(reader);
		
		if(address.getType() == addressType)
			return address;
		
		throw ERROR_NOT_COMPLETE.createWithContext(reader);
	}
	
	public static Address.Immutable getAddress(CommandContext<CommandSourceStack> context, String string)
	{
		return context.getArgument(string, Address.Immutable.class);
	}
	
	public Collection<String> getExamples()
	{
		return switch(addressType)
		{
			case ADDRESS_7_CHEVRON -> EXAMPLE_7_CHEVRON;
			case ADDRESS_8_CHEVRON -> EXAMPLE_8_CHEVRON;
			case ADDRESS_9_CHEVRON -> EXAMPLE_9_CHEVRON;
			
			default -> EXAMPLE_INVALID;
		};
	}
}
