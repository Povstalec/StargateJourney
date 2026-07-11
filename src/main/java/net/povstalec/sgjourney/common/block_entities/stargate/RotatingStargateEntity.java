package net.povstalec.sgjourney.common.block_entities.stargate;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.client.sound.SoundWrapper;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class RotatingStargateEntity extends IrisStargateEntity
{
	public static final String ROTATION = "rotation";
	public static final String OLD_ROTATION = "old_rotation";
	public static final String SIGNAL_STRENGTH = "signal_strength";
	public static final String COMPUTER_ROTATION = "computer_rotation";
	public static final String ROTATE_CLOCKWISE = "rotate_clockwise";
	public static final String DESIRED_ROTATION = "desired_rotation";
	
	// Rotation stuff
	protected final int maxRotation;
	protected final int stepsPerSymbol;
	protected final int symbolAddition;
	
	protected int rotation;
	protected int oldRotation;
	
	@Nullable
	public SoundWrapper buildupSound = null;
	
	// Redstone signal stuff (Manual dialing)
	protected Map<StargatePart, Integer> signalMap; //TODO Maybe start saving the map?
	public int previousSignalStrength;
	public int signalStrength;
	
	public boolean rotating;
	public int desiredRotation;
	public boolean rotateClockwise;
	
	public RotatingStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
								  int totalSymbols, StargateInfo.Gen gen, int defaultNetwork, float verticalCenterHeight, float horizontalCenterHeight, int maxRotation)
	{
		super(blockEntity, defaultVariant, pos, state, totalSymbols, gen, defaultNetwork, verticalCenterHeight, horizontalCenterHeight);
		
		this.maxRotation = maxRotation;
		this.stepsPerSymbol = this.maxRotation / this.totalSymbols;
		this.symbolAddition = this.stepsPerSymbol / 2;
		
		this.rotation = 0;
		this.oldRotation = 0;
		
		this.signalMap = Maps.newHashMap();
		this.previousSignalStrength = 0;
		this.signalStrength = 0;
		
		this.rotating = false;
		this.desiredRotation = 0;
		this.rotateClockwise = true;
	}
	
	public RotatingStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
								  int totalSymbols, StargateInfo.Gen gen, int defaultNetwork, int maxRotation)
	{
		this(blockEntity, defaultVariant, pos, state, totalSymbols, gen, defaultNetwork, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT, maxRotation);
	}
	
	@Override
	public CompoundTag serializeStargateInfo(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.serializeStargateInfo(tag, registries);
		
		tag.putInt(ROTATION, this.rotation);
		
		return tag;
	}
	
	@Override
	public void deserializeStargateInfo(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgraded)
	{
		if(tag.contains(ROTATION))
			this.rotation = tag.getInt(ROTATION);
		this.oldRotation = this.rotation;
		
		super.deserializeStargateInfo(tag, registries, isUpgraded);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		CompoundTag tag = super.getUpdateTag(registries);
		
		tag.putInt(ROTATION, this.rotation);
		tag.putInt(OLD_ROTATION, this.oldRotation);
		tag.putInt(SIGNAL_STRENGTH, this.signalStrength);
		tag.putBoolean(COMPUTER_ROTATION, this.rotating);
		tag.putBoolean(ROTATE_CLOCKWISE, this.rotateClockwise);
		tag.putInt(DESIRED_ROTATION, this.desiredRotation);
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries)
	{
		super.onDataPacket(net, packet, registries);
		CompoundTag tag = packet.getTag();
		
		int rotation = tag.getInt(ROTATION);
		int oldRotation = tag.getInt(OLD_ROTATION);
		
		if(rotation == oldRotation && rotation != this.rotation)
		{
			this.rotation = rotation;
			this.oldRotation = oldRotation;
		}
		
		this.signalStrength = tag.getInt(SIGNAL_STRENGTH);
		this.rotating = tag.getBoolean(COMPUTER_ROTATION);
		this.rotateClockwise = tag.getBoolean(ROTATE_CLOCKWISE);
		this.desiredRotation = tag.getInt(DESIRED_ROTATION);
	}
	
	//============================================================================================
	//******************************************Rotation******************************************
	//============================================================================================
	
	public int getRotation()
	{
		return this.rotation;
	}
	
	public double getRotationDegrees()
	{
		return (double) getRotation() / maxRotation * 360F;
	}
	
	public float getRotation(float partialTick)
	{
		return StargateJourneyConfig.disable_smooth_animations.get() ?
				(float) getRotation() : Mth.lerp(partialTick, this.oldRotation, this.rotation);
	}
	
	public float getRotationDegrees(float partialTick)
	{
		return getRotation(partialTick) / maxRotation * 360F;
	}
	
	public void setRotation(int oldRotation, int rotation)
	{
		this.oldRotation = oldRotation;
		this.rotation = rotation;
	}
	
	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}
	
	public boolean isRotating()
	{
		return this.rotation != this.oldRotation;
	}
	
	protected int rotationStep()
	{
		return 1;
	}
	
	/**
	 * Calculates the absolute distance between two points on the Stargate's ring
	 * @param rotationA Point A
	 * @param rotationB Point B
	 * @return Distance between point A and point B
	 */
	public int ringDistance(int rotationA, int rotationB)
	{
		int distance = Math.abs(rotationA - rotationB);
		
		if(distance > this.maxRotation / 2F)
			return this.maxRotation - distance;
		
		return distance;
	}
	
	protected void rotateToTarget()
	{
		if(this.desiredRotation < 0)
			rotate(this.rotateClockwise);
		else
		{
			int ringDistance = ringDistance(this.rotation, this.desiredRotation);
			
			if(ringDistance == 0)
				endRotation(false);
			else if(ringDistance < rotationStep())
				rotate(this.rotateClockwise, ringDistance);
			else
				rotate(this.rotateClockwise);
		}
	}
	
	protected void rotate()
	{
		if(!isConnected())
		{
			if(this.rotating)
				rotateToTarget();
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
		else if(!isDialingOut() && getKawooshTickCount() <= 0 && this.rotating)
			rotateToTarget();
		else
			syncRotation();
		setChanged();
	}
	
	public void rotate(boolean clockwise, int rotationStep)
	{
		this.oldRotation = this.rotation;
		
		if(clockwise)
			this.rotation -= rotationStep;
		else
			this.rotation += rotationStep;
		
		if(this.rotation >= this.maxRotation)
		{
			this.rotation -= this.maxRotation;
			this.oldRotation -= this.maxRotation;
		}
		else if(this.rotation < 0)
		{
			this.rotation += this.maxRotation;
			this.oldRotation += this.maxRotation;
		}
		this.rotation -= this.rotation % rotationStep;
		
		setChanged();
	}
	
	public void rotate(boolean clockwise)
	{
		rotate(clockwise, rotationStep());
	}
	
	protected StargateInfo.Feedback rotateTo(int degrees, boolean rotateClockwise)
	{
		this.rotating = true;
		this.desiredRotation = degrees;
		this.rotateClockwise = rotateClockwise;
		
		if(!this.level.isClientSide())
			PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(), new ClientBoundSoundPackets.RotationStartup(worldPosition));
		
		updateClient();
		
		updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STARTED, rotateClockwise);
		
		return setRecentFeedback(StargateInfo.Feedback.ROTATING);
	}
	
	public StargateInfo.Feedback startRotation(int desiredSymbol, boolean rotateClockwise)
	{
		return rotateTo(desiredSymbol < 0 ? -1 : getDesiredRotation(desiredSymbol), rotateClockwise);
	}
	
	public StargateInfo.Feedback endRotation(boolean playSound)
	{
		if(!this.level.isClientSide() && playSound)
			PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(), new ClientBoundSoundPackets.RotationStop(worldPosition));
		
		if(!this.rotating)
			return setRecentFeedback(StargateInfo.Feedback.NOT_ROTATING);
		
		this.rotating = false;
		
		syncRotation();
		
		updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STOPPED);
		
		return setRecentFeedback(StargateInfo.Feedback.ROTATION_STOPPED);
	}
	
	protected void syncRotation()
	{
		this.oldRotation = this.rotation;
		
		if(!this.level.isClientSide())
			updateClient();
	}
	
	public void playBuildupSound()
	{
		if(this.buildupSound.isPlaying())
			this.buildupSound.stopSound();
		this.buildupSound.playSound();
	}
	
	@Override
	public void playRotationSound()
	{
		if(!this.spinSound.isPlaying())
		{
			this.spinSound.stopSound();
			this.spinSound.playSound();
		}
	}
	
	@Override
	public void stopRotationSound(){}
	
	public static boolean alternatingDirection(int addressLength)
	{
		return addressLength % 2 == 1;
	}
	
	public boolean bestSymbolDirection(int desiredSymbol)
	{
		return bestRotationDirection(getDesiredRotation(desiredSymbol));
	}
	
	public boolean bestRotationDirection(int desiredRotation)
	{
		int rotation = this.rotation;
		double difference = desiredRotation - rotation;
		
		if(difference >= this.maxRotation / 2)
			rotation = this.maxRotation;
		else if(difference <= -this.maxRotation / 2)
			rotation = -this.maxRotation;
		
		double lowerBound = desiredRotation - 1;
		
		if(rotation > lowerBound)
			return true;
		else
			return false;
	}
	
	//============================================================================================
	//***************************************Redstone Signal**************************************
	//============================================================================================
	
	protected boolean hadBestRedstoneSignalChanged()
	{
		this.previousSignalStrength = this.signalStrength;
		this.signalStrength = 0;
		this.signalMap.forEach((stargatePart, signal) ->
		{
			if(signal > this.signalStrength)
				this.signalStrength = signal;
		});
		
		return previousSignalStrength != signalStrength;
	}
	
	public void updateSignal(StargatePart part, int signal)
	{
		if(!CommonStargateConfig.enable_redstone_dialing.get())
			return;
		
		if(this.signalMap.containsKey(part))
			this.signalMap.remove(part);
		this.signalMap.put(part, signal);
		
		if(hadBestRedstoneSignalChanged())
			manualDialing();
	}
	
	//============================================================================================
	//***************************************Manual Dialing***************************************
	//============================================================================================
	
	public StargateInfo.Feedback encodeChevron()
	{
		if(!level.isClientSide())
			updateClient();
		
		return setRecentFeedback(engageSymbol(getCurrentSymbol()));
	}
	
	public int getDesiredRotation(int desiredSymbol)
	{
		return desiredSymbol * this.stepsPerSymbol;
	}
	
	public boolean isCurrentSymbol(int desiredSymbol)
	{
		return getDesiredRotation(desiredSymbol) == this.rotation;
	}
	
	public int getCurrentSymbol()
	{
		int symbolPosition = this.rotation + this.symbolAddition;
		
		int currentSymbol = (symbolPosition / stepsPerSymbol) % totalSymbols;
		
		return currentSymbol;
	}
	
	protected void manualDialing()
	{
		if(this.signalStrength == 15 && this.previousSignalStrength != this.signalStrength)
		{
			if(!isConnected())
				engageSymbol(getCurrentSymbol());
			else
				disconnectStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_POINT_OF_ORIGIN, true);
		}
		
		if(!this.level.isClientSide())
			updateClient();
	}
	
	@Override
	public int getRedstoneSymbolOutput()
	{
		return (getCurrentSymbol() % (totalSymbols / SEGMENTS)) + 1;
	}
	
	@Override
	public int getRedstoneSegmentOutput()
	{
		return (getCurrentSymbol() / (totalSymbols / SEGMENTS) + 1) * 5;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, RotatingStargateEntity stargate)
	{
		stargate.rotate();
		if(stargate.isRotating() && !level.isClientSide())
			PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(stargate.worldPosition).getPos(), new ClientBoundSoundPackets.StargateRotation(stargate.worldPosition, false));
		
		if(stargate.isRotating())
			stargate.updateInterfaceBlocks(null);
		
		AbstractStargateEntity.tick(level, pos, state, stargate);
	}
}
