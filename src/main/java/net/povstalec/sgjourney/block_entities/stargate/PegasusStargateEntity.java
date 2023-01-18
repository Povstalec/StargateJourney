package net.povstalec.sgjourney.block_entities.stargate;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.PacketHandlerInit;
import net.povstalec.sgjourney.init.SoundInit;
import net.povstalec.sgjourney.network.ClientboundPegasusStargateUpdatePacket;

public class PegasusStargateEntity extends AbstractStargateEntity
{
	public int[] addressBuffer = new int[0];
	public int symbolBuffer = 0;
	private boolean passedOver = false;
	
	public PegasusStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.PEGASUS_STARGATE.get(), pos, state);
	}
	
	@Override
    public void onLoad()
	{
        if(level.isClientSide)
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
    }
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.putIntArray("AddressBuffer", addressBuffer);
		nbt.putInt("SymbolBuffer", symbolBuffer);
		super.saveAdditional(nbt);
	}
	
	public SoundEvent chevronEngageSound()
	{
		return SoundInit.PEGASUS_CHEVRON_ENGAGE.get();
	}
	
	public SoundEvent failSound()
	{
		return SoundInit.PEGASUS_DIAL_FAIL.get();
	}
	
	@Override
	public void encodeChevron(int symbol)
	{
		if(symbolInAddress(symbol))
			return;
		
		if(isBusy() && symbol == 0)
			disconnectGate();
		
		addressBuffer = growIntArray(addressBuffer, symbol);
	}
	
	@Override
	protected void engageChevron(int symbol)
	{
		symbolBuffer++;
		passedOver = false;
		super.engageChevron(symbol);
	}
	
	public int getChevronPosition(int chevron)
	{
		switch(chevron)
		{
		case 1:
			return 4;
		case 2:
			return 8;
		case 3:
			return 12;
		case 4:
			return 24;
		case 5:
			return 28;
		case 6:
			return 32;
		case 7:
			return 16;
		case 8:
			return 20;
		default:
			return 0;
		}
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, PegasusStargateEntity stargate)
	{
		if(!stargate.isBusy() && stargate.addressBuffer.length > stargate.symbolBuffer)
		{
			int symbol = stargate.addressBuffer[stargate.symbolBuffer];
			if(symbol == 0)
			{
				if(stargate.currentSymbol == stargate.getChevronPosition(9))
				{
					stargate.lockChevron();
				}
				else
					stargate.symbolWork();
			}
			else if(stargate.currentSymbol == stargate.getChevronPosition(stargate.symbolBuffer + 1))
			{
				if(stargate.symbolBuffer % 2 != 0 && !stargate.passedOver)
				{
					stargate.passedOver = true;
					stargate.symbolWork();
				}
				else
					stargate.engageChevron(symbol);
			}
			else
				stargate.symbolWork();
		}
		
		AbstractStargateEntity.tick(level, pos, state, (AbstractStargateEntity) stargate);
		
		if(level.isClientSide())
			return;
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(stargate.worldPosition)), new ClientboundPegasusStargateUpdatePacket(stargate.worldPosition, stargate.inputAddress, stargate.symbolBuffer, stargate.addressBuffer));
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
	public void resetGate()
	{
		currentSymbol = 0;
		symbolBuffer = 0;
		addressBuffer = new int[0];
		super.resetGate();
	}
}
