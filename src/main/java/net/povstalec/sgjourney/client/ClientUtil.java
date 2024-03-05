package net.povstalec.sgjourney.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class ClientUtil
{
	public static final PointOfOrigin getPointOfOrigin(String pointOfOrigin)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<PointOfOrigin> registry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		if(pointOfOrigin != null)
			return registry.get(new ResourceLocation(pointOfOrigin));
		
		return registry.get(new ResourceLocation(StargateJourney.MODID + ":universal"));
	}
	
	public static final Symbols getSymbols(String symbols)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<Symbols> registry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
		
		if(symbols != null)
			return registry.get(new ResourceLocation(symbols));
		
		return registry.get(new ResourceLocation(StargateJourney.MODID + ":universal"));
	}
}
