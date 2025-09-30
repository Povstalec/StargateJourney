package net.povstalec.sgjourney.common.command;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.povstalec.sgjourney.common.sgjourney.Address;

public class AddressArgumentInfo implements ArgumentTypeInfo<AddressArgumentType, AddressArgumentInfo.Template>
{
	@Override
	public void serializeToNetwork(Template template, FriendlyByteBuf buf)
	{
		buf.writeByte(template.type.byteValue());
	}
	
	@Override
	public Template deserializeFromNetwork(FriendlyByteBuf buf)
	{
		return new AddressArgumentInfo.Template(Address.Type.fromLength(buf.readByte()));
	}
	
	@Override
	public void serializeToJson(Template template, JsonObject json)
	{
		json.addProperty("type", template.type.byteValue());
	}
	
	@Override
	public Template unpack(AddressArgumentType addressArgumentType)
	{
		return new AddressArgumentInfo.Template(addressArgumentType.type());
	}
	
	
	
	public final class Template implements ArgumentTypeInfo.Template<AddressArgumentType>
	{
		final Address.Type type;
		
		Template(Address.Type type)
		{
			this.type = type;
		}
		
		@Override
		public AddressArgumentType instantiate(CommandBuildContext context)
		{
			return new AddressArgumentType(type);
		}
		
		@Override
		public ArgumentTypeInfo<AddressArgumentType, ?> type()
		{
			return AddressArgumentInfo.this;
		}
	}
}
