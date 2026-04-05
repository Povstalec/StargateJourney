package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.*;

import javax.annotation.Nullable;
import java.util.*;

public abstract class SGJourneyStargate implements Stargate
{
	public static final double MIN_TRAVELER_SPEED = 0.4;
	public static final double INNER_RADIUS = Wormhole.INNER_RADIUS;
	
	public static final int KAWOOSH_TICKS = 40;
	
	public static final int MAX_OPEN_TIME = CommonStargateConfig.max_wormhole_open_time.get() * 20;
	
	private final StargateType<?> type;
	
	protected Address.Immutable id9ChevronAddress;
	
	protected ResourceKey<Level> dimension;
	
	// Preferred Stargate decision
	protected boolean hasDHD;
	protected int timesOpened;
	protected int network;
	
	protected Wormhole wormhole = new Wormhole();
	
	public SGJourneyStargate(StargateType<?> type)
	{
		this.type = type;
	}
	
	public final StargateType<?> getStargateType()
	{
		return this.type;
	}
	
	@Override
	public Address.Immutable get9ChevronAddress()
	{
		return this.id9ChevronAddress;
	}
	
	
	@Override
	public @Nullable ResourceKey<Level> getDimension()
	{
		return this.dimension;
	}
	
	@Override
	public double getInnerRadius()
	{
		return INNER_RADIUS;
	}
	
	public Wormhole getWormhole()
	{
		return this.wormhole;
	}
	
	
	
	@Override
	public boolean hasDHD()
	{
		return this.hasDHD;
	}
	
	@Override
	public StargateInfo.Gen getGeneration()
	{
		return getStargateType().getGeneration();
	}
	
	@Override
	public int getTimesOpened()
	{
		return this.timesOpened;
	}
	
	@Override
	public int getNetwork()
	{
		return this.network;
	}
	
	// Energy
	
	@Override
	public boolean canPowerFromOtherSide(MinecraftServer server)
	{
		return CommonStargateConfig.can_draw_power_from_both_ends.get();
	}
	
	// Stargate Connection
	
	public abstract StargateInfo.ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh);
	
	@Override
	public int dialedEngageTime(MinecraftServer server, boolean doKawoosh)
	{
		return getChevronLockSpeed(doKawoosh).getKawooshStartTicks();
	}
	
	@Override
	public int wormholeEstablishTime(MinecraftServer server, boolean doKawoosh)
	{
		return KAWOOSH_TICKS;
	}
	
	@Override
	public StargateInfo.Feedback tryConnect(MinecraftServer server, Stargate dialingStargate, Address.Type addressType, boolean doKawoosh)
	{
		// If last Stargate is obstructed
		if(isObstructed(server))
			return StargateInfo.Feedback.TARGET_OBSTRUCTED;
		
		// If last Stargate is restricted
		if(isRestricted(server, dialingStargate.getNetwork()))
			return StargateInfo.Feedback.TARGET_RESTRICTED;
		
		// If last Stargate has a blacklist
		if(addressFilterInfo(server).getFilterType().isBlacklist() && addressFilterInfo(server).isAddressBlacklisted(dialingStargate.getConnectionAddress(server, getAddressRegion(server), addressType)))
			return StargateInfo.Feedback.BLACKLISTED_BY_TARGET;
		
		// If last Stargate has a whitelist
		if(addressFilterInfo(server).getFilterType().isWhitelist() && !addressFilterInfo(server).isAddressWhitelisted(dialingStargate.getConnectionAddress(server, getAddressRegion(server), addressType)))
			return StargateInfo.Feedback.NOT_WHITELISTED_BY_TARGET;
		
		return Dialing.connectStargates(server, dialingStargate, this, addressType, doKawoosh);
	}
	
	@Override
	public List<Stargate> getDialedStargates(MinecraftServer server, Stargate dialingStargate, StargateConnection.Type connectionType)
	{
		if(!callForward(server))
			return List.of(this);
		
		// Chooses a random Stargate to connect to
		RandomSource randomSource = new SingleThreadedRandomSource(server.getTickCount());
		
		AddressRegion addressRegion = this.getAddressRegion(server);
		
		if(addressRegion != null)
		{
			if(connectionType == StargateConnection.Type.SYSTEM_WIDE) // Picks a random Stargate from the same Address Region
			{
				for(Stargate reroutedStargate : addressRegion.getShuffledStargates(randomSource))
				{
					if(reroutedStargate != null && reroutedStargate != this && reroutedStargate != dialingStargate && !reroutedStargate.isConnected(server) && !reroutedStargate.callForward(server))
						return List.of(this, reroutedStargate);
				}
			}
			else // Picks a random Stargate from the same Galaxy
			{
				Universe universe = Universe.get(server);
				for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> entry : addressRegion.getGalacticAddresses().entrySet())
				{
					Galaxy galaxy = universe.getGalaxy(entry.getKey());
					if(galaxy != null)
					{
						for(AddressRegion randomAddressRegion : galaxy.getShuffledAddressRegions(randomSource))
						{
							for(Stargate reroutedStargate : randomAddressRegion.getShuffledStargates(randomSource))
							{
								if(reroutedStargate != null && reroutedStargate != this && reroutedStargate != dialingStargate && !reroutedStargate.isConnected(server) && !reroutedStargate.callForward(server))
									return List.of(this, reroutedStargate);
							}
						}
					}
				}
			}
		}
		
		return List.of(this);
	}
	
	@Override
	public boolean requiresEnergyBypass(MinecraftServer server, int openTime)
	{
		return openTime > MAX_OPEN_TIME;
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	@Override
	public void serializeNBT(CompoundTag tag)
	{
		tag.putString(DIMENSION, getDimension().location().toString());
		
		tag.putBoolean(HAS_DHD, hasDHD);
		tag.putInt(TIMES_OPENED, timesOpened);
		tag.putInt(NETWORK, network);
	}
	
	@Override
	public void deserializeNBT(MinecraftServer server, Address.Immutable id9ChevronAddress, CompoundTag tag)
	{
		this.id9ChevronAddress = id9ChevronAddress;
		
		this.dimension = Conversion.stringToDimension(tag.getString(DIMENSION));
		
		this.hasDHD = tag.getBoolean(HAS_DHD);
		this.timesOpened = tag.getInt(TIMES_OPENED);
		this.network = tag.getInt(NETWORK);
	}
	
	//============================================================================================
	//*******************************************Other********************************************
	//============================================================================================
	
	@Override
	public String toString()
	{
		return "[ " + get9ChevronAddress() + " | DHD: " + hasDHD() + " | Generation: " + getStargateType().getGeneration() + " | Times Opened: " + getTimesOpened() + " ]";
	}
}
