package net.povstalec.sgjourney.common.block_entities.stargate;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.init.StargateInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo.ChevronLockSpeed;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import net.povstalec.sgjourney.common.sgjourney.stargate.MilkyWayBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.MilkyWayStargate;
import org.jetbrains.annotations.NotNull;

public class MilkyWayStargateEntity extends RotatingStargateEntity<MilkyWayBlockEntityStargate>
{
	public static final String IS_CHEVRON_OPEN = "is_chevron_open";
	
	public static final int MAX_ROTATION = 156;
	
	public static final int TOTAL_SYMBOLS = 39;
	public static final int RING_SEGMENTS = 3;
	public static final int SYMBOLS_PER_SEGMENT = TOTAL_SYMBOLS / RING_SEGMENTS;

	private final ResourceLocation backVariant = StargateJourney.sgjourneyLocation("milky_way_back_chevron");
	
	public boolean isChevronOpen = false;

	public MilkyWayStargateEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.MILKY_WAY_STARGATE.get(), StargateInit.MILKY_WAY.get(), StargateJourney.sgjourneyLocation("milky_way"), pos, state, TOTAL_SYMBOLS, 2, MAX_ROTATION);
	}

	@Override
	public CompoundTag serializeStargateInfo(CompoundTag tag)
	{
		super.serializeStargateInfo(tag);
		
		symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		return tag;
	}
	
	@Override
	public void deserializeStargateInfo(CompoundTag tag, boolean isUpgraded)
	{
		symbolInfo().loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
    	
    	super.deserializeStargateInfo(tag, isUpgraded);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = super.getUpdateTag();
		
		symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		tag.putBoolean(IS_CHEVRON_OPEN, isChevronOpen);
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		super.onDataPacket(net, packet);
		CompoundTag tag = packet.getTag();
		if(tag != null)
		{
			symbolInfo().loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
			
			isChevronOpen = tag.getBoolean(IS_CHEVRON_OPEN);
		}
	}
	
	@Override
	public ResourceLocation defaultVariant()
	{
		return ClientStargateConfig.milky_way_stargate_back_lights_up.get() ? backVariant : super.defaultVariant();
	}
	
	@Override
	public StargateInfo.Feedback resetStargate(StargateInfo.Feedback feedback)
	{
		if(this.isChevronOpen)
		{
			this.isChevronOpen = false;
			chevronSound(getCurrentChevron(), false, false, false);
		}
		
		return super.resetStargate(feedback);
	}
	
	private short getCurrentChevron()
	{
		if(getCurrentSymbol() == 0)
			return 0;
		
		// If the current symbol under the primary chevron is the same one as the last encoded in the address, we want the current chevron
		if(getAddress().getLength() > 0 && getCurrentSymbol() == getAddress().symbolAt(getAddress().getLength() - 1))
			return (short) getAddress().getLength();
		
		// Otherwise we want the next chevron
		return (short) (getAddress().getLength() + 1);
	}
	
	public boolean isChevronOpen()
	{
		return this.isChevronOpen;
	}
	
	@Override
	public StargateInfo.Feedback encodeChevron()
	{
		if(!isChevronOpen())
			return setRecentFeedback(StargateInfo.Feedback.CHEVRON_NOT_OPEN);
		
		if(!level.isClientSide())
			updateClient();
		
		return setRecentFeedback(encodeChevron(getCurrentSymbol(), false, true));
	}
	
	public StargateInfo.Feedback openChevron()
	{
		if(!this.isChevronOpen)
		{
			if(!getAddress().containsSymbol(getCurrentSymbol()))
			{
				if(!level.isClientSide())
					PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Chevron(this.worldPosition, getCurrentChevron(), false, true, false));
				this.isChevronOpen = true;
				
				if(!level.isClientSide())
					updateClient();
				
				return setRecentFeedback(StargateInfo.Feedback.CHEVRON_OPENED);
			}
			else
				return setRecentFeedback(StargateInfo.Feedback.SYMBOL_IN_ADDRESS);
		}
		return setRecentFeedback(StargateInfo.Feedback.CHEVRON_ALREADY_OPENED);
	}
	
	public StargateInfo.Feedback closeChevron()
	{
		if(this.isChevronOpen)
		{
			this.isChevronOpen = false;
			
			StargateInfo.Feedback feedback = directEngageSymbol(getCurrentSymbol(), true);
			
			// This is a dumb way to make sure the sound plays even after the chevron is engaged 
			if(feedback == StargateInfo.Feedback.SYMBOL_IN_ADDRESS)
				chevronSound(getCurrentChevron(), false, false, false);
			
			return setRecentFeedback(feedback);
		}
		
		if(!level.isClientSide())
			updateClient();
		
		return setRecentFeedback(StargateInfo.Feedback.CHEVRON_ALREADY_CLOSED);
	}
	
	//============================================================================================
	//******************************************Rotation******************************************
	//============================================================================================
	
	@Override
	protected void rotate()
	{
		if(!isConnected() && !this.isChevronOpen)
		{
			if(this.rotationDirection.isRotating)
				rotateToTarget();
			else if(this.signalStrength > 0 && this.signalStrength < 15)
			{
				if(this.signalStrength > 7)
					rotate(RotationDirection.ANTICLOCKWISE);
				else
					rotate(RotationDirection.CLOCKWISE);
			}
			else
				syncRotation();
		}
		else
			syncRotation();
		setChanged();
	}
	
	@Override
	public StargateInfo.Feedback startRotation(int desiredSymbol, RotationDirection rotateClockwise)
	{
		if(this.isChevronOpen)
			return StargateInfo.Feedback.ROTATION_BLOCKED;
		
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
					disconnectStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_POINT_OF_ORIGIN);
			}
		}
		else if(this.signalStrength == 0 && this.previousSignalStrength == 15)
			closeChevron();
		
		if(!this.level.isClientSide())
			updateClient();
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, MilkyWayStargateEntity stargate)
	{
		RotatingStargateEntity.tick(level, pos, state, stargate);
	}

	@Override
	public ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? MilkyWayStargate.CHEVRON_LOCK_SPEED : ChevronLockSpeed.FAST;
	}

	@Override
	public void registerInterfaceMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper)
	{
		CCTweakedCompatibility.Stargate.registerMilkyWayStargateMethods(wrapper);
	}
	
	@Override
	public void generate()
	{
		super.generate();
		
		Random random = new Random();
		setRotation(2 * random.nextInt(0, MAX_ROTATION / 2 + 1));
	}
	
	@Override
	public void generateAdditional(StructureGenEntity.Step generationStep)
	{
		if(generationStep == StructureGenEntity.Step.SETUP)
		{
			if(!PointOfOrigin.isValid(level.getServer(), symbolInfo().pointOfOrigin()))
				symbolInfo().setPointOfOrigin(null);
			
			if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
				symbolInfo().setSymbols(null);
		}
		else
		{
			if(!PointOfOrigin.isValid(level.getServer(), symbolInfo().pointOfOrigin()))
			{
				if(localPointOfOrigin)
					symbolInfo().setPointOfOrigin(PointOfOrigin.fromDimension(level.getServer(), level.dimension()));
				else
					symbolInfo().setPointOfOrigin(PointOfOrigin.randomPointOfOrigin(level.getServer(), level.dimension()));
			}
			
			if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
				symbolInfo().setSymbols(Symbols.fromDimension(level.getServer(), level.dimension()));
		}
	}
}
