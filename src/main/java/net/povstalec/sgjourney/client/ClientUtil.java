package net.povstalec.sgjourney.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public class ClientUtil
{
	public static final PointOfOrigin getPointOfOrigin(ResourceLocation pointOfOrigin)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<PointOfOrigin> registry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		if(pointOfOrigin != null)
			return registry.get(pointOfOrigin);
		
		return registry.get(new ResourceLocation(StargateJourney.MODID + ":universal"));
	}
	
	public static final Symbols getSymbols(ResourceLocation symbols)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<Symbols> registry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
		
		if(symbols != null)
			return registry.get(symbols);
		
		return registry.get(new ResourceLocation(StargateJourney.MODID + ":universal"));
	}
}
