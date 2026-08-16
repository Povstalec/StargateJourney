package net.povstalec.sgjourney.common.sgjourney.transporter;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.TransporterInit;

import java.util.Optional;

public class TransporterType<T extends Transporter>
{
	public static final String TRANSPORTER_TYPE = "transporter_type";
	public static final ResourceLocation TRANSPORTER_TYPE_LOCATION = new ResourceLocation(StargateJourney.MODID, TRANSPORTER_TYPE);
	public static final ResourceKey<Registry<TransporterType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(TRANSPORTER_TYPE_LOCATION);
	public static final Codec<ResourceKey<TransporterType<?>>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResourceKey.createRegistryKey(TRANSPORTER_TYPE_LOCATION));
	
	private final TransporterConstructor<T> transporterConstructor;
	
	public TransporterType(TransporterConstructor<T> transporterConstructor)
	{
		this.transporterConstructor = transporterConstructor;
	}
	
	public T constructTransporter(MinecraftServer server)
	{
		return transporterConstructor.constructTransporter(this, server);
	}
	
	
	
	public static ResourceLocation getKey(TransporterType<?> type)
	{
		return TransporterInit.TRANSPORTER_TYPE.get().getKey(type);
	}
	
	public static TransporterType<?> getType(ResourceLocation key)
	{
		return TransporterInit.TRANSPORTER_TYPE.get().getValue(key);
	}
	
	public static boolean tagHasStargateType(CompoundTag tag)
	{
		return tag.contains(TRANSPORTER_TYPE, Tag.TAG_STRING);
	}
	
	public static Optional<TransporterType<?>> getTypeFromTag(CompoundTag tag)
	{
		if(tagHasStargateType(tag))
		{
			String typeName = tag.getString(TRANSPORTER_TYPE);
			TransporterType<?> type = getType(ResourceLocation.tryParse(typeName));
			if(type == null)
				StargateJourney.LOGGER.error("No entry found for Stargate Type \"{}\"", typeName);
			return Optional.ofNullable(type);
			
		}
		
		return Optional.empty();
	}
	
	public static void addTypeToTag(TransporterType<?> type, CompoundTag tag)
	{
		tag.putString(TRANSPORTER_TYPE, getKey(type).toString());
	}
	
	
	
	public interface TransporterConstructor<T extends Transporter>
	{
		T constructTransporter(TransporterType<?> type, MinecraftServer server);
	}
}
