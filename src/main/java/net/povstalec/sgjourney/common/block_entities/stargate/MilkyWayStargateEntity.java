package net.povstalec.sgjourney.common.block_entities.stargate;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.StargatePeripheralWrapper;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundMilkyWayStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Stargate.ChevronLockSpeed;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class MilkyWayStargateEntity extends RotatingStargateEntity
{
	public static final int MAX_ROTATION = 156;
	
	public static final int TOTAL_SYMBOLS = 39;
	public static final int RING_SEGMENTS = 3;
	public static final int SYMBOLS_PER_SEGMENT = TOTAL_SYMBOLS / RING_SEGMENTS;

	private final ResourceLocation backVariant = new ResourceLocation(StargateJourney.MODID, "milky_way/milky_way_back_chevron");
	
	public boolean isChevronOpen = false;

	public MilkyWayStargateEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.MILKY_WAY_STARGATE.get(), new ResourceLocation(StargateJourney.MODID, "milky_way/milky_way"), pos, state,
				TOTAL_SYMBOLS, Stargate.Gen.GEN_2, 2, MAX_ROTATION);
	}

	@Override
    public void onLoad()
	{
        //Rotate the ring randomly
        if(!this.level.isClientSide() && !addToNetwork)
        {
        	Random random = new Random();
        	setRotation(2 * random.nextInt(0, MAX_ROTATION / 2 + 1));
        }

        super.onLoad();

        if(this.level.isClientSide())
        	return;

        if(!PointOfOrigin.validLocation(level.getServer(), symbolInfo().pointOfOrigin()))
			symbolInfo().setPointOfOrigin(PointOfOrigin.fromDimension(level.getServer(), level.dimension()));

        if(!Symbols.validLocation(level.getServer(), symbolInfo().symbols()))
			symbolInfo().setSymbols(Symbols.fromDimension(level.getServer(), level.dimension()));
    }

	@Override
	public CompoundTag serializeStargateInfo(CompoundTag tag)
	{
		super.serializeStargateInfo(tag);
		
		tag.putString(POINT_OF_ORIGIN, symbolInfo().pointOfOrigin().toString());
		tag.putString(SYMBOLS, symbolInfo().symbols().toString());
		
		return tag;
	}
	
	@Override
	public void deserializeStargateInfo(CompoundTag tag, boolean isUpgraded)
	{
		if(tag.contains(POINT_OF_ORIGIN))
			symbolInfo().setPointOfOrigin(new ResourceLocation(tag.getString(POINT_OF_ORIGIN)));
		
		if(tag.contains(SYMBOLS))
			symbolInfo().setSymbols(new ResourceLocation(tag.getString(SYMBOLS)));
    	
    	super.deserializeStargateInfo(tag, isUpgraded);
	}
	
	@Override
	public ResourceLocation defaultVariant()
	{
		return ClientStargateConfig.milky_way_stargate_back_lights_up.get() ? backVariant : super.defaultVariant();
	}
	
	@Override
	public Stargate.Feedback resetStargate(Stargate.Feedback feedback, boolean updateInterfaces)
	{
		if(this.isChevronOpen)
		{
			this.isChevronOpen = false;
			chevronSound(getCurrentChevron(), false, false, false);
		}
		
		return super.resetStargate(feedback, updateInterfaces);
	}
	
	private short getCurrentChevron()
	{
		if(getCurrentSymbol() == 0)
			return 0;
		
		// If the current symbol under the primary chevron is the same one as the last encoded in the address, we want the current chevron
		if(getAddress().getLength() > 0 && getCurrentSymbol() == getAddress().getSymbol(getAddress().getLength() - 1))
			return (short) getAddress().getLength();
		
		// Otherwise we want the next chevron
		return (short) (getAddress().getLength() + 1);
	}
	
	public boolean isChevronOpen()
	{
		return this.isChevronOpen;
	}
	
	@Override
	public Stargate.Feedback encodeChevron()
	{
		if(!isChevronOpen())
			return setRecentFeedback(Stargate.Feedback.CHEVRON_NOT_OPEN);
		
		if(!level.isClientSide())
			synchronizeWithClient();
		
		int symbol = getCurrentSymbol();
		
		if(symbol == 0)
			return setRecentFeedback(Stargate.Feedback.CANNOT_ENCODE_POINT_OF_ORIGIN);
		
		return setRecentFeedback(encodeChevron(symbol, false, true));
	}
	
	public Stargate.Feedback openChevron()
	{
		if(!this.isChevronOpen)
		{
			if(!getAddress().containsSymbol(getCurrentSymbol()))
			{
				if(!level.isClientSide())
					PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Chevron(this.worldPosition, getCurrentChevron(), false, true, false));
				this.isChevronOpen = true;
				
				if(!level.isClientSide())
					synchronizeWithClient();
				
				return setRecentFeedback(Stargate.Feedback.CHEVRON_RAISED);
			}
			else
				return setRecentFeedback(Stargate.Feedback.SYMBOL_IN_ADDRESS);
		}
		return setRecentFeedback(Stargate.Feedback.CHEVRON_ALREADY_OPENED);
	}
	
	public Stargate.Feedback closeChevron()
	{
		if(this.isChevronOpen)
		{
			this.isChevronOpen = false;
			
			Stargate.Feedback feedback = engageSymbol(getCurrentSymbol());
			
			// This is a dumb way to make sure the sound plays even after the chevron is engaged 
			if(feedback == Stargate.Feedback.SYMBOL_IN_ADDRESS)
				chevronSound(getCurrentChevron(), false, false, false);
			
			return setRecentFeedback(feedback);
		}
		
		if(!level.isClientSide())
			synchronizeWithClient();
		
		return setRecentFeedback(Stargate.Feedback.CHEVRON_ALREADY_CLOSED);
	}
	
	//============================================================================================
	//******************************************Rotation******************************************
	//============================================================================================
	
	@Override
	protected void rotate()
	{
		if(!isConnected() && !this.isChevronOpen)
		{
			if(this.rotating)
			{
				if(this.rotation == this.desiredRotation)
					endRotation(false);
				else
					rotate(this.rotateClockwise);
			}
			else if(this.signalStrength > 0 && this.signalStrength < 15)
			{
				if(this.signalStrength > 7)
					rotate(false);
				else
					rotate(true);
			}
			else
				syncRotation();
		}
		else
			syncRotation();
		setChanged();
	}
	
	@Override
	public Stargate.Feedback startRotation(int desiredSymbol, boolean rotateClockwise)
	{
		if(this.isChevronOpen)
			return Stargate.Feedback.ROTATION_BLOCKED;
		
		return super.startRotation(desiredSymbol, rotateClockwise);
	}
	
	//============================================================================================
	//***************************************Manual Dialing***************************************
	//============================================================================================
	
	@Override
	protected void manualDialing()
	{
		if(this.signalStrength > 0)
		{
			if(this.signalStrength == 15 && (getCurrentSymbol() != 0 || getAddress().getLength() > 0))
			{
				if(!isConnected())
					openChevron();
				else
					disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_POINT_OF_ORIGIN, true);
			}
		}
		else if(this.signalStrength == 0 && this.previousSignalStrength == 15)
			closeChevron();
		
		if(!this.level.isClientSide())
			synchronizeWithClient();
	}
	
	@Override
	public int getRedstoneSymbolOutput()
	{
		return (getCurrentSymbol() % SYMBOLS_PER_SEGMENT) + 1;
	}

	@Override
	public int getRedstoneSegmentOutput()
	{
		return (getCurrentSymbol() / SYMBOLS_PER_SEGMENT + 1) * 5;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, MilkyWayStargateEntity stargate)
	{
		RotatingStargateEntity.tick(level, pos, state, stargate);
	}
	
	@Override
	public boolean synchronizeWithClient()
	{
		if(!super.synchronizeWithClient())
			return false;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundMilkyWayStargateUpdatePacket(this.worldPosition, this.isChevronOpen));
		return true;
	}

	@Override
	public ChevronLockSpeed getChevronLockSpeed()
	{
		return CommonStargateConfig.milky_way_chevron_lock_speed.get();
	}

	@Override
	public void registerInterfaceMethods(StargatePeripheralWrapper wrapper)
	{
		CCTweakedCompatibility.registerMilkyWayStargateMethods(wrapper);
	}
}
