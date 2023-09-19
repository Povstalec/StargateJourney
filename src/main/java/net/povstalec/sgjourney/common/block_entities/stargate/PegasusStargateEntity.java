package net.povstalec.sgjourney.common.block_entities.stargate;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.misc.ArrayHelper;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundPegasusStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class PegasusStargateEntity extends AbstractStargateEntity
{
	public int currentSymbol = 0;
	public int[] addressBuffer = new int[0];
	public int symbolBuffer = 0;
	private boolean passedOver = false;
	
	public PegasusStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.PEGASUS_STARGATE.get(), pos, state, Stargate.Gen.GEN_3, 3);
		this.setOpenSoundLead(13);
	}
	
	@Override
    public void onLoad()
	{
        if(level.isClientSide())
        	return;
        setPointOfOrigin(this.getLevel());
        setSymbols(this.getLevel());
        
        super.onLoad();
    }
	
	@Override
    public void load(CompoundTag nbt)
	{
        super.load(nbt);
        
        addressBuffer = nbt.getIntArray("AddressBuffer");
        symbolBuffer = nbt.getInt("SymbolBuffer");
        currentSymbol = nbt.getInt("CurrentSymbol");
    }
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		
		nbt.putIntArray("AddressBuffer", addressBuffer);
		nbt.putInt("SymbolBuffer", symbolBuffer);
		nbt.putInt("CurrentSymbol", currentSymbol);
	}
	
	public SoundEvent getChevronEngageSound()
	{
		return SoundInit.PEGASUS_CHEVRON_ENGAGE.get();
	}
	
	public SoundEvent chevronIncomingSound()
	{
		return SoundInit.PEGASUS_CHEVRON_INCOMING.get();
	}
	
	public SoundEvent getWormholeOpenSound()
	{
		return SoundInit.PEGASUS_WORMHOLE_OPEN.get();
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
		
		if(Address.addressContainsSymbol(addressBuffer, symbol))
			return Stargate.Feedback.SYMBOL_ENCODED;
		
		if(addressBuffer.length == getAddress().length)
		{
			if(!this.level.isClientSide())
				PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, false));
		}
		
		addressBuffer = ArrayHelper.growIntArray(addressBuffer, symbol);
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
	public Stargate.Feedback encodeChevron(int symbol, boolean incoming)
	{
		symbolBuffer++;
		passedOver = false;
		
		if(!this.level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, true));
		Stargate.Feedback feedback = super.encodeChevron(symbol, incoming);
		
		if(addressBuffer.length > getAddress().length)
		{
			if(!this.level.isClientSide())
				PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, false));
		}
		
		return feedback;
	}
	
	@Override
	public void chevronSound(boolean incoming)
	{
		if(!level.isClientSide())
		{
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Chevron(this.worldPosition, incoming));
		}
	}
	
	public int getChevronPosition(int chevron)
	{
		if(chevron < 1 || chevron > 8)
			return 0;
		return new int[] {4, 8, 12, 24, 28, 32, 16, 20}[chevron - 1];
	}
	
	private void animateSpin()
	{
		if(!isConnected() && addressBuffer.length > symbolBuffer)
		{
			int symbol = addressBuffer[symbolBuffer];
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
					encodeChevron(symbol, false);
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
		stargate.updatePegasusStargate();
	}
	
	public void updatePegasusStargate()
	{
		if(level.isClientSide())
			return;
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundPegasusStargateUpdatePacket(this.worldPosition, this.symbolBuffer, this.addressBuffer, this.currentSymbol));
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
		addressBuffer = new int[0];
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
}
