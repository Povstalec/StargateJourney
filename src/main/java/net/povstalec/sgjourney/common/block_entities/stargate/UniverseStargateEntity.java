package net.povstalec.sgjourney.common.block_entities.stargate;

import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import net.povstalec.sgjourney.common.sgjourney.info.DHDInfo;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.StargatePeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundUniverseStargateUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo.ChevronLockSpeed;

public class UniverseStargateEntity extends RotatingStargateEntity
{
	public static final String ADDRESS_BUFFER = "AddressBuffer"; //TODO Rename to "address_buffer"
	public static final String SYMBOL_BUFFER = "SymbolBuffer"; //TODO Rename to "symbol_buffer"
	
	public static final boolean FAST_ROTATION = CommonStargateConfig.universe_fast_rotation.get();
	
	public static final int TOTAL_SYMBOLS = 36;
	public static final int MAX_ROTATION = 324; // 54 * (FAST_ROTATION ? 2 : 3); // 108 : 162
	
	public static final int ROTATION_THIRD = MAX_ROTATION / 3;
	public static final int RESET_DEGREES = ROTATION_THIRD * 2;
	
	public static final int MAX_WAIT_TICKS = 20;
	
	public static final ChevronLockSpeed CHEVRON_LOCK_SPEED = CommonStargateConfig.universe_chevron_lock_speed.get();
	
	public int waitTicks = 1;
	
	public Address addressBuffer = new Address(true);
	public int symbolBuffer = 0;
	
	protected int angle;
	
	public UniverseStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.UNIVERSE_STARGATE.get(), new ResourceLocation(StargateJourney.MODID, "universe/universe"), pos, state,
				TOTAL_SYMBOLS, StargateInfo.Gen.GEN_1, 1, MAX_ROTATION);
		this.setOpenSoundLead(8);
		
		this.angle = this.maxRotation / 54;
		
		symbolInfo.setPointOfOrigin(PointOfOrigin.UNIVERSAL_LOCATION);
		symbolInfo.setSymbols(Symbols.UNIVERSAL_LOCATION);
		
		this.dhdInfo = new DHDInfo(this)
		{
			@Override
			public void updateDHD()
			{
				if(hasDHD())
					this.dhd.updateDHD(!stargate.isConnected() || (stargate.isConnected() && stargate.isDialingOut()) ?
							addressBuffer : new Address(), addressBuffer.hasPointOfOrigin() || stargate.isConnected());
			}
		};
		
		this.oldRotation = RESET_DEGREES;
		this.rotation = RESET_DEGREES;
	}
	
	@Override
	public void load(CompoundTag tag)
	{
        super.load(tag);
        
        addressBuffer.fromArray(tag.getIntArray(ADDRESS_BUFFER));
        symbolBuffer = tag.getInt(SYMBOL_BUFFER);
    }
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		tag.putIntArray(ADDRESS_BUFFER, addressBuffer.toArray());
		tag.putInt(SYMBOL_BUFFER, symbolBuffer);
	}
	
	@Override
	public StargateInfo.Feedback dhdEngageSymbol(int symbol)
	{
		if(isSymbolOutOfBounds(symbol))
			return setRecentFeedback(StargateInfo.Feedback.SYMBOL_OUT_OF_BOUNDS);
		
		if(addressBuffer.getLength() == 0 && address.getLength() > 0)
			resetStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT, true);
		
		if(isConnected())
		{
			if(symbol == 0)
				return disconnectStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT, true);
			else
				return setRecentFeedback(StargateInfo.Feedback.ENCODE_WHEN_CONNECTED);
		}
		else if(symbol == 0 && !isConnected() && addressBuffer.getLength() == 0)
			return setRecentFeedback(StargateInfo.Feedback.INCOMPLETE_ADDRESS);
		
		if(addressBuffer.containsSymbol(symbol))
		{
			if(symbol == 0)
				return resetStargate(StargateInfo.Feedback.INCOMPLETE_ADDRESS);
			else
				return setRecentFeedback(StargateInfo.Feedback.SYMBOL_IN_ADDRESS);
		}
		
		if(addressBuffer.getLength() == 0 && address.getLength() == 0)
			startSound();
		
		addressBuffer.addSymbol(symbol);
		return setRecentFeedback(StargateInfo.Feedback.SYMBOL_ENCODED);
	}
	
	@Override
	public StargateInfo.Feedback engageSymbol(int symbol)
	{
		if(!addressBuffer.containsSymbol(symbol))
			addressBuffer.addSymbol(symbol);
		
		return super.engageSymbol(symbol);
	}
	
	@Override
	public StargateInfo.Feedback encodeChevron(int symbol, boolean incoming, boolean encode)
	{
		symbolBuffer++;
		waitTicks++;
		
		return super.encodeChevron(symbol, incoming, encode);
	}
	
	public void startSound()
	{
		if(!level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.UniverseStart(this.worldPosition));
	}
	
	public boolean dialFromBuffer()
	{
		return !addressBuffer.isEmpty();
	}
	
	public void bufferDialing()
	{
		if(waitTicks != 0)
		{
			if(waitTicks >= MAX_WAIT_TICKS)
				waitTicks = 0;
			else
				waitTicks++;
			return;
		}
		
		if(!isConnected() && addressBuffer.getLength() > symbolBuffer)
		{
			if(!isRotating())
				startRotation(addressBuffer.getSymbol(symbolBuffer), CommonStargateConfig.universe_best_direction.get() ?
						bestSymbolDirection(addressBuffer.getSymbol(symbolBuffer)) : alternatingDirection(address.getLength()));
			
			if(rotation == desiredRotation)
				engageSymbol(getCurrentSymbol());
		}
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, UniverseStargateEntity stargate)
	{
		if(stargate.dialFromBuffer() && !level.isClientSide())
			stargate.bufferDialing();
		RotatingStargateEntity.tick(level, pos, state, stargate);
	}
	
	@Override
	public int getRedstoneSymbolOutput()
	{
		int currentSymbol = getCurrentSymbol();
		if(currentSymbol == -1)
			return 0;
		
		return currentSymbol % 12 + 1;
	}
	
	@Override
	public int getRedstoneSegmentOutput()
	{
		return ((rotation / ROTATION_THIRD) + 1) * 5;
	}
	
	@Override
	public int getDesiredRotation(int desiredSymbol)
	{
		int whole = desiredSymbol / 4;
		int leftover = desiredSymbol % 4;
		
		return 3 * (this.angle / 2) + whole * this.maxRotation / 9 + (this.angle * leftover);
	}
	
	@Override
	public int getCurrentSymbol()
	{
		int segment6 = rotation / 6;
		if(segment6 % 6 == 0 || segment6 % 6 == 5)
			return -1;
		
		int emptySegments = 2 * (segment6 / 6) + 1;
		
		return segment6 - emptySegments;
	}
	
	@Override
	protected int rotationStep()
	{
		return FAST_ROTATION ? (this.rotating ? 3 : 2) : 2; // Only rotates fast during computer dialing or DHD dialing, not during redstone dialing
	}
	
	@Override
	public StargateInfo.Feedback resetStargate(StargateInfo.Feedback feedback, boolean updateInterfaces)
	{
		super.resetStargate(feedback, updateInterfaces);
		
		if(this.rotation != RESET_DEGREES)
			rotateTo(RESET_DEGREES, bestRotationDirection(RESET_DEGREES));
		
		return feedback;
	}

	@Override
	protected void resetAddress(boolean updateInterfaces)
	{
		waitTicks = 1;
		symbolBuffer = 0;
		addressBuffer.reset();
		super.resetAddress(updateInterfaces);
	}
	
	@Override
	public boolean updateClient()
	{
		if(!super.updateClient())
			return false;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundUniverseStargateUpdatePacket(this.worldPosition, this.symbolBuffer, this.addressBuffer.toArray()));
		return true;
	}

	@Override
	public ChevronLockSpeed getChevronLockSpeed()
	{
		return CHEVRON_LOCK_SPEED;
	}

	@Override
	public void registerInterfaceMethods(StargatePeripheralWrapper wrapper)
	{
		CCTweakedCompatibility.registerUniverseStargateMethods(wrapper);
	}
	
	@Override
	public void doWhileDialed(Address connectedAddress, int kawooshStartTicks, StargateInfo.ChevronLockSpeed chevronLockSpeed, int openTime)
	{
		super.doWhileDialed(connectedAddress, kawooshStartTicks, chevronLockSpeed, openTime);
		
		if(this.level.isClientSide())
			return;
		
		if(openTime == 1)
		{
			startSound();
			startRotation(-1, true);
		}
		
		if(openTime == chevronLockSpeed.getChevronWaitTicks() * 8)
			endRotation(false);
	}
}
