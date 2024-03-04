package net.povstalec.sgjourney.common.block_entities.stargate;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.StargatePeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundPegasusStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Stargate.ChevronLockSpeed;

public class PegasusStargateEntity extends AbstractStargateEntity
{
	public int currentSymbol = 0;
	public Address addressBuffer = new Address(true);
	public int symbolBuffer = 0;
	private boolean passedOver = false;
	
	protected boolean dynamicSymbols = true;
	
	public PegasusStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.PEGASUS_STARGATE.get(), pos, state, Stargate.Gen.GEN_3, 3);
		this.setOpenSoundLead(13);
	}
	
	@Override
    public void onLoad()
	{
        super.onLoad();

        if(this.level.isClientSide())
        	return;

        if(!isPointOfOriginValid(this.level))
        {
        	StargateJourney.LOGGER.info("PoO is not valid " + this.pointOfOrigin);
        	setPointOfOrigin(this.getLevel());
        }

        if(!areSymbolsValid(this.level))
        {
        	StargateJourney.LOGGER.info("Symbols are not valid " + this.symbols);
        	setSymbols(this.getLevel());
        }
    }
	
	@Override
    public void load(CompoundTag tag)
	{
        super.load(tag);
        
        addressBuffer.fromArray(tag.getIntArray("AddressBuffer"));
        symbolBuffer = tag.getInt("SymbolBuffer");
        currentSymbol = tag.getInt("CurrentSymbol");
        
        dynamicSymbols = tag.getBoolean("DynamicSymbols");

        if(!dynamicSymbols)
        {
    		pointOfOrigin = tag.getString("PointOfOrigin");
    		symbols = tag.getString("Symbols");
        }
    }
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		tag.putIntArray("AddressBuffer", addressBuffer.toArray());
		tag.putInt("SymbolBuffer", symbolBuffer);
		tag.putInt("CurrentSymbol", currentSymbol);
		
		tag.putBoolean("DynamicSymbols", dynamicSymbols);

        if(!dynamicSymbols)
        {
    		tag.putString("PointOfOrigin", pointOfOrigin);
    		tag.putString("Symbols", symbols);
        }
	}
	
	public void dynamicSymbols(boolean dynamicSymbols)
	{
		this.dynamicSymbols = dynamicSymbols;
		this.setChanged();
	}
	
	public boolean useDynamicSymbols()
	{
		return this.dynamicSymbols;
	}
	
	@Override
	public SoundEvent getRotationSound()
	{
		return SoundInit.PEGASUS_RING_SPIN.get();
	}

	@Override
	public SoundEvent getChevronEngageSound()
	{
		return SoundInit.PEGASUS_CHEVRON_ENGAGE.get();
	}

	@Override
	public SoundEvent getPrimaryChevronEngageSound()
	{
		return SoundInit.PEGASUS_PRIMARY_CHEVRON_ENGAGE.get();
	}

	@Override
	public SoundEvent getChevronIncomingSound()
	{
		return SoundInit.PEGASUS_CHEVRON_INCOMING.get();
	}

	@Override
	public SoundEvent getPrimaryChevronIncomingSound()
	{
		return SoundInit.PEGASUS_PRIMARY_CHEVRON_INCOMING.get();
	}

	@Override
	public SoundEvent getWormholeOpenSound()
	{
		return SoundInit.PEGASUS_WORMHOLE_OPEN.get();
	}

	@Override
	public SoundEvent getWormholeIdleSound()
	{
		return SoundInit.PEGASUS_WORMHOLE_IDLE.get();
	}

	@Override
	public SoundEvent getWormholeCloseSound()
	{
		return SoundInit.PEGASUS_WORMHOLE_CLOSE.get();
	}
	
	public SoundEvent getFailSound()
	{
		return SoundInit.PEGASUS_DIAL_FAIL.get();
	}
	
	@Override
	public Stargate.Feedback engageSymbol(int symbol)
	{
		if(isConnected() && symbol == 0)
			return disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
		
		if(addressBuffer.containsSymbol(symbol))
			return Stargate.Feedback.SYMBOL_ENCODED;
		
		if(addressBuffer.getLength() == getAddress().getLength())
		{
			if(!this.level.isClientSide())
				PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, false));
		}
		addressBuffer.addSymbol(symbol);
		return Stargate.Feedback.SYMBOL_ENCODED;
	}
	
	@Override
	protected Stargate.Feedback lockPrimaryChevron()
	{
		if(!this.level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, true));
		return super.lockPrimaryChevron();
	}
	
	@Override
	public Stargate.Feedback encodeChevron(int symbol, boolean incoming, boolean encode)
	{
		symbolBuffer++;
		passedOver = false;
		
		if(!this.level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, true));
		Stargate.Feedback feedback = super.encodeChevron(symbol, incoming, encode);
		
		if(addressBuffer.getLength() > getAddress().getLength())
		{
			if(!this.level.isClientSide())
				PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, false));
		}
		
		return feedback;
	}
	
	public int getChevronPosition(int chevron)
	{
		if(chevron < 1 || chevron > 8)
			return 0;
		
		return 4 * getEngagedChevrons()[chevron];
	}
	
	private void animateSpin()
	{
		if(!isConnected() && addressBuffer.getLength() > symbolBuffer)
		{
			int symbol = addressBuffer.getSymbol(symbolBuffer);
			if(symbol == 0)
			{
				if(currentSymbol == getChevronPosition(9))
					lockPrimaryChevron();
				else
					symbolWork();
			}
			else if(currentSymbol == getChevronPosition(symbolBuffer + 1))
			{
				if(symbolBuffer % 2 != 0 && !passedOver)
				{
					passedOver = true;
					symbolWork();
				}
				else
					encodeChevron(symbol, false, false);
			}
			else
				symbolWork();
		}
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, PegasusStargateEntity stargate)
	{
		if(level.isClientSide())
			return;
		
		stargate.animateSpin();
		
		AbstractStargateEntity.tick(level, pos, state, (AbstractStargateEntity) stargate);
		stargate.updateClient();
	}
	
	@Override
	public void updateClient()
	{
		super.updateClient();
		
		if(this.level.isClientSide())
			return;
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundPegasusStargateUpdatePacket(this.worldPosition, this.symbolBuffer, this.addressBuffer.toArray(), this.currentSymbol));
	}
	
	private void symbolWork()
	{
		if(symbolBuffer % 2 == 0)
			currentSymbol--;
		else
			currentSymbol++;

		if(currentSymbol > 35)
			currentSymbol = 0;
		else if(currentSymbol < 0)
			currentSymbol = 35;
	}
	
	@Override
	public Stargate.Feedback resetStargate(Stargate.Feedback feedback)
	{
		currentSymbol = 0;
		symbolBuffer = 0;
		addressBuffer.reset();
		return super.resetStargate(feedback);
	}

	@Override
	public void playRotationSound()
	{
		this.stopRotationSound();
		this.spinSound.playSound();
	}

	@Override
	public void stopRotationSound()
	{
		this.spinSound.stopSound();
	}

	@Override
	public ChevronLockSpeed getChevronLockSpeed()
	{
		return CommonStargateConfig.pegasus_chevron_lock_speed.get();
	}

	@Override
	public void registerInterfaceMethods(StargatePeripheralWrapper wrapper)
	{
		CCTweakedCompatibility.registerPegasusStargateMethods(wrapper);
	}
	
	@Override
	public void doWhileDialed(int openTime, Stargate.ChevronLockSpeed chevronLockSpeed)
	{
		if(this.level.isClientSide())
			return;
		
		if(this.currentSymbol >= 36)
			return;
		
		this.currentSymbol = openTime / chevronLockSpeed.getMultiplier();
		this.updateClient();
	}
}