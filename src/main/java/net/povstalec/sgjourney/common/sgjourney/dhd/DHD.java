package net.povstalec.sgjourney.common.sgjourney.dhd;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.povstalec.sgjourney.common.misc.AutoCache;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.stargate.DHDControlledStargate;

import javax.annotation.Nullable;

public interface DHD extends AutoCache.IController<DHD, DHDControlledStargate>
{
	Capability<DHD> DHD_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
	
	/**
	 * @return Current Minecraft Server
	 */
	MinecraftServer getServer();
	
	/**
	 * @return Dimension the DHD is located in or null if it's not located in any Dimension
	 */
	@Nullable
	ResourceKey<Level> getDimension();
	
	/**
	 * @return Level the DHD is currently located in, null if it's not located in any Level
	 */
	@Nullable
	default ServerLevel getLevel()
	{
		ResourceKey<Level> dimension = getDimension();
		if(dimension == null)
			return null;
		
		return getServer().getLevel(dimension);
	}
	
	/**
	 * @return Returns true if this DHD is loaded (for example, in the case of DHDs placed in the world, if the Chunk the DHD is located in is loaded)
	 */
	boolean isLoaded();
	
	@Nullable
	default DHDControlledStargate getStargate()
	{
		return receiverCache().get();
	}
	
	double getMaxConnectionDistance();
	
	long getEnergyTarget();
	
	boolean enableAdvancedProtocols();
	
	int autocloseTicks();
	
	Address getAddress();
	
	boolean callForwardingEnabled();
	
	boolean hasNetworkRestrictions();
	
	void engageStargate();
	
	void encodeSymbol(int symbol);
}
