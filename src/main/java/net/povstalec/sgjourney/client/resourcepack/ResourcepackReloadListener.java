package net.povstalec.sgjourney.client.resourcepack;

import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.MilkyWayStargateVariant;
import net.povstalec.stellarview.StellarView;

public class ResourcepackReloadListener
{
	public static final String PATH = StargateJourney.MODID;
	
	public static final String STARGATE_VARIANT = "stargate_variant";
	
	@Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ReloadListener extends SimpleJsonResourceReloadListener
	{
		public ReloadListener()
		{
			super(new GsonBuilder().create(), PATH);
		}
		
		@Override
		protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager manager, ProfilerFiller filler)
		{
    		ClientStargateVariants.clear();
			System.out.println("---------- Loading Stargate Variants ----------"); // TODO Remove this
    		
			for(Map.Entry<ResourceLocation, JsonElement> jsonEntry : jsonMap.entrySet())
			{
				ResourceLocation location = jsonEntry.getKey();
				JsonElement element = jsonEntry.getValue();
				
				if(canShortenPath(location, STARGATE_VARIANT))
					addMilkyWayStargateVariant(shortenPath(location, STARGATE_VARIANT), element);
			}
		}
		
		private static void addMilkyWayStargateVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, STARGATE_VARIANT);
				MilkyWayStargateVariant stargateVariant = MilkyWayStargateVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StellarView.LOGGER.error("Failed to parse Stargate Variant", msg));
				
				ClientStargateVariants.addMilkyWayStargateVariant(location, stargateVariant);
				System.out.println("-------------------- Loaded " + location); // TODO Remove this
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load " + location.toString());
			}
		}
		
		
		
		public static void registerReloadListener(RegisterClientReloadListenersEvent event)
		{
			event.registerReloadListener(new ReloadListener());
		}
		
		private static boolean canShortenPath(ResourceLocation location, String shortenBy)
		{
			return location.getPath().startsWith(shortenBy) && location.getPath().length() > shortenBy.length(); // If it starts with the string and isn't empty after getting shortened
		}
		
		private static ResourceLocation shortenPath(ResourceLocation location, String shortenBy)
		{
			return location.withPath(location.getPath().substring(shortenBy.length() + 1)); // Magical 1 because there's also the / symbol
		}
	}
}
