package net.povstalec.sgjourney.common.stargate;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.misc.Conversion;

public class Connection
{
	private static final String DIMENSION = "Dimension";
	private static final String COORDINATES = "Coordinates";
	
	private static final String DIALING_STARGATE = "DialingStargate";
	private static final String DIALED_STARGATE = "DialedStargate";

	private static final String USED = "Used";
	private static final String TIME_SINCE_LAST_TRAVELER = "TimeSinceLastTraveler";
	private static final String OPEN_TIME = "OpenTime";
	private static final String CONNECTION_TIME = "ConnectionTime";
	private static final String CONNECTION_TYPE = "ConnectionType";
	
	public static final int KAWOOSH_TICKS = 40;
	public static final int VORTEX_TICKS = 20;
	
	protected static int maxOpenTime = CommonStargateConfig.max_wormhole_open_time.get() * 20;
	protected static boolean energyBypassEnabled = CommonStargateConfig.enable_energy_bypass.get();
	protected static int energyBypassMultiplier = CommonStargateConfig.energy_bypass_multiplier.get();
	protected static boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
	protected final String uuid;
	protected final Stargate.ConnectionType connectionType;
	protected final AbstractStargateEntity dialingStargate;
	protected AbstractStargateEntity dialedStargate; // Dialed Stargates can be changed mid connection
	
	protected boolean used = false;
	protected int openTime = 0;
	protected int connectionTime = 0;
	protected int timeSinceLastTraveler = 0;
	
	private Connection(String uuid, Stargate.ConnectionType connectionType, AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate, boolean used, int openTime, int connectionTime, int timeSinceLastTraveler)
	{
		this.uuid = uuid;
		this.connectionType = connectionType;
		this.dialingStargate = dialingStargate;
		this.dialedStargate = dialedStargate;
		this.used = used;
		this.openTime = openTime;
		this.connectionTime = connectionTime;
		this.timeSinceLastTraveler = timeSinceLastTraveler;
	}
	
	private Connection(String uuid, Stargate.ConnectionType connectionType, AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate)
	{
		this(uuid, connectionType, dialingStargate, dialedStargate, false, 0, 0, 0);
	}
	
	public static Connection create(Stargate.ConnectionType connectionType, AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate)
	{
		String uuid = UUID.randomUUID().toString();
		
		if(dialingStargate != null && dialedStargate != null)
		{
			int addressLenght = dialingStargate.getAddress().length;
			int[] dialingAddress = Address.addressStringToIntArray(dialingStargate.getConnectionAddress(addressLenght));

			dialedStargate.resetStargate(Stargate.Feedback.INTERRUPTED_BY_INCOMING_CONNECTION);
			
			dialingStargate.setKawooshTickCount(0);
			dialingStargate.updateClient();
			dialedStargate.setKawooshTickCount(0);
			dialedStargate.updateClient();
			
			dialingStargate.connectStargate(uuid, true, dialingStargate.getAddress());
			dialedStargate.connectStargate(uuid, false, dialingAddress);
			
			return new Connection(uuid, connectionType, dialingStargate, dialedStargate);
		}
		return null;
	}
	
	public void terminate(MinecraftServer server, Stargate.Feedback feedback)
	{
		if(this.dialingStargate != null)
			this.dialingStargate.resetStargate(feedback);
		if(this.dialedStargate != null)
			this.dialedStargate.resetStargate(feedback);
		
		StargateNetwork.get(server).removeConnection(server, uuid, feedback);
	}
	
	public void reroute(AbstractStargateEntity newDialedStargate)
	{
		if(this.dialedStargate != null)
			this.dialedStargate.resetStargate(Stargate.Feedback.CONNECTION_REROUTED);
		newDialedStargate.connectStargate(this.uuid, false, this.dialingStargate.getAddress());
	}
	
	public boolean isStargateValid(AbstractStargateEntity stargate)
	{
		if(stargate == null)
		{
			StargateJourney.LOGGER.info("Stargate does not exist");
			return false;
		}
		
		BlockPos stargatePos = stargate.getBlockPos();
		Level stargateLevel = stargate.getLevel();
		
		if(stargateLevel.getBlockEntity(stargatePos) instanceof AbstractStargateEntity)
			return true;
		
		return false;
	}
	
	public void tick(MinecraftServer server)
	{
		Stargate.ChevronLockSpeed chevronLockSpeed = this.dialedStargate.getGateType().getChevronLockSpeed();
		int waitMultiplier = chevronLockSpeed.getMultiplier();
		int chevronWaitTicks = chevronLockSpeed.getChevronWaitTicks();
		int kawooshStartTicks = chevronLockSpeed.getKawooshStartTicks();
		int maxKawooshTicks = kawooshStartTicks + KAWOOSH_TICKS;
		int maxOpenTicks = maxKawooshTicks + VORTEX_TICKS;
		
		if(!isStargateValid(this.dialingStargate) || !isStargateValid(this.dialedStargate))
		{
			terminate(server, Stargate.Feedback.TARGET_STARGATE_DOES_NOT_EXIST);
			return;
		}
		
		this.increaseTicks(maxKawooshTicks, maxOpenTicks);
		int openTime = this.openTime - kawooshStartTicks;
		
		if(this.openTime < kawooshStartTicks)
		{
			playStargateOpenSound(this.dialingStargate, kawooshStartTicks, this.openTime);
			playStargateOpenSound(this.dialedStargate, kawooshStartTicks, this.openTime);
			
			int addressLenght = this.dialingStargate.getAddress().length;
			int[] dialingAddress = Address.addressStringToIntArray(this.dialingStargate.getConnectionAddress(addressLenght));
			
			this.dialedStargate.setEngagedChevrons(AbstractStargateEntity.getChevronConfiguration(addressLenght));
			
			if(this.dialedStargate instanceof PegasusStargateEntity pegasusStargate && pegasusStargate.currentSymbol < 36)
			{
				pegasusStargate.currentSymbol = this.openTime / waitMultiplier;
				pegasusStargate.updatePegasusStargate();
			}
			
			if(this.openTime % chevronWaitTicks == 0)
			{
				int dialedAddressLength = this.dialedStargate.getAddress().length;
				
				if(dialedAddressLength < dialingAddress.length)
				{
					if(this.openTime / chevronWaitTicks == 4 && addressLenght < 7)
						return;
					else if(this.openTime / chevronWaitTicks == 5 && addressLenght < 8)
						return;
					else
						this.dialedStargate.encodeChevron(dialingAddress[dialedAddressLength]);
				}
				else
					this.dialedStargate.engageChevron();
			}
			
			return;
		}
		
		if(this.openTime < maxOpenTicks)
		{
			this.dialingStargate.doKawoosh(openTime);
			this.dialedStargate.doKawoosh(openTime);
		}
		else
		{
			this.dialingStargate.setKawooshTickCount(openTime);
			this.dialingStargate.updateClient();
			this.dialedStargate.setKawooshTickCount(openTime);
			this.dialedStargate.updateClient();
		}

		if(this.openTime < maxKawooshTicks)
			return;
		
		this.dialingStargate.doWormholeSound();
		this.dialedStargate.doWormholeSound();
		
		if(this.connectionTime >= maxOpenTime && !energyBypassEnabled)
		{
			terminate(server, Stargate.Feedback.EXCEEDED_CONNECTION_TIME);
			return;
		}
		
		if(requireEnergy)
		{
			long energyDraw = this.connectionType.getPowerDraw();
			energyDraw = this.connectionTime >= maxOpenTime ? energyDraw * energyBypassMultiplier : energyDraw;
			
			if(this.dialingStargate.getEnergyStored() < energyDraw && this.dialedStargate.getEnergyStored() < energyDraw)
			{
				terminate(server, Stargate.Feedback.RAN_OUT_OF_POWER);
				return;
			}
			
			if(this.dialedStargate.getEnergyStored() > this.dialingStargate.getEnergyStored())
				this.dialedStargate.depleteEnergy(energyDraw, false);
			else
				this.dialingStargate.depleteEnergy(energyDraw, false);
		}
		
		if(this.used)
			this.timeSinceLastTraveler++;
		
		doWormhole(this.dialingStargate.getWormhole(), this.dialingStargate, this.dialedStargate, Stargate.WormholeTravel.ENABLED);
		doWormhole(this.dialedStargate.getWormhole(), this.dialedStargate, this.dialingStargate, CommonStargateConfig.two_way_wormholes.get());
		
		if((this.dialingStargate.advancedProtocolsEnabled() || this.dialedStargate.advancedProtocolsEnabled()) && this.timeSinceLastTraveler >= 200)
			terminate(server, Stargate.Feedback.CONNECTION_ENDED_BY_AUTOCLOSE);
	}
	
	protected void playStargateOpenSound(AbstractStargateEntity stargate, int kawooshStartTicks, int ticks)
	{
		if(ticks == kawooshStartTicks - stargate.getOpenSoundLead())
			stargate.openWormhole();
	}
	
	protected void increaseTicks(int maxKawooshTicks, int maxOpenTicks)
	{
		if(this.openTime < maxOpenTicks)
			this.openTime++;
		
		if(this.openTime > maxKawooshTicks)
			this.connectionTime++;
	}
	
	protected void doWormhole(Wormhole wormhole, AbstractStargateEntity initialStargate, AbstractStargateEntity targetStargate, Stargate.WormholeTravel wormholeTravel)
	{
		BlockPos pos = initialStargate.getCenterPos();
		double yCenter = initialStargate instanceof TollanStargateEntity ? 0.0 : 0.5;
		Vec3 stargatePos = new Vec3(pos.getX() + 0.5, pos.getY() + yCenter, pos.getZ() + 0.5);
		
		if(wormhole.findCandidates(initialStargate.getLevel(), stargatePos, initialStargate.getDirection()) && this.used)
			this.timeSinceLastTraveler = 0;
		if(wormhole.wormholeEntities(initialStargate, targetStargate, wormholeTravel))
			this.used = true;
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public String getID()
	{
		return this.uuid;
	}
	
	public int getKawooshTime()
	{
		return this.openTime;
	}
	
	public int getOpenTime()
	{
		return this.connectionTime;
	}
	
	public int getTimeSinceLastTraveler()
	{
		return this.timeSinceLastTraveler;
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	public CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.put(DIALING_STARGATE, serializeStargate(this.dialingStargate));
		tag.put(DIALED_STARGATE, serializeStargate(this.dialedStargate));
		tag.putInt(OPEN_TIME, this.openTime);
		tag.putInt(CONNECTION_TIME, this.connectionTime);
		tag.putInt(TIME_SINCE_LAST_TRAVELER, this.timeSinceLastTraveler);
		tag.putString(CONNECTION_TYPE, this.connectionType.toString().toUpperCase());
		
		return tag;
	}
	
	protected CompoundTag serializeStargate(AbstractStargateEntity stargate)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putString(DIMENSION, stargate.getLevel().dimension().location().toString());
		tag.putIntArray(COORDINATES, new int[] {stargate.getBlockPos().getX(), stargate.getBlockPos().getY(), stargate.getBlockPos().getZ()});
		
		return tag;
	}
	
	public static Connection deserialize(MinecraftServer server, String uuid, CompoundTag tag)
	{
		Stargate.ConnectionType connectionType = Stargate.ConnectionType.valueOf(tag.getString(CONNECTION_TYPE));
		AbstractStargateEntity dialingStargate = deserializeStargate(server, tag.getCompound(DIALING_STARGATE));
		AbstractStargateEntity dialedStargate = deserializeStargate(server, tag.getCompound(DIALED_STARGATE));
		boolean used = tag.getBoolean(USED);
		int openTime = tag.getInt(OPEN_TIME);
		int connectionTime = tag.getInt(CONNECTION_TIME);
		int timeSinceLastTraveler = tag.getInt(TIME_SINCE_LAST_TRAVELER);
		
		return new Connection(uuid, connectionType, dialingStargate, dialedStargate, used, openTime, connectionTime, timeSinceLastTraveler);
	}
	
	protected static AbstractStargateEntity deserializeStargate(MinecraftServer server, CompoundTag stargateInfo)
	{
		ResourceKey<Level> dimension = Conversion.stringToDimension(stargateInfo.getString(DIMENSION));
		BlockPos pos = Conversion.intArrayToBlockPos(stargateInfo.getIntArray(COORDINATES));
		
		BlockEntity blockEntity = server.getLevel(dimension).getBlockEntity(pos);
		
		if(blockEntity instanceof AbstractStargateEntity stargate)
			return stargate;
		
		return null;
	}
}
