package net.povstalec.sgjourney.block_entities;

import net.minecraft.core.BlockPos;
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
        
        super.onLoad();
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
		
		addressBuffer = growIntArray(addressBuffer, symbol);
	}
	
	public int getLitSymbol(int symbolsActive)
	{
		switch(symbolsActive)
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

	//TODO Yeah, I completely broke this... Well, I was gonna leave it for later anyway
	/*@Override
	public void tick(Level level, BlockPos pos, BlockState state)
	{
		//System.out.println(currentSymbol);
		if(addressBuffer.length > symbolBuffer)
		{
			int symbol = addressBuffer[symbolBuffer];
			
			if(currentSymbol == getLitSymbol(addressBuffer.length))
			{
				System.out.println("Lit");
				if(symbolBuffer % 2 != 0 && !passedOver)
				{
					System.out.println("A");
					passedOver = true;
				}
				else
				{
					System.out.println("B");
					passedOver = false;
					symbolBuffer++;
					if(symbol == 0)
						lockChevron();
					else
						engageChevron(symbol);
				}
			}
			else
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
		}
		
		super.tick(level, pos, state);
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundPegasusStargateUpdatePacket(pos, chevronsActive, isBusy(), tick, pointOfOrigin, currentSymbol, inputAddress, symbolBuffer, addressBuffer));
	}*/
	
	@Override
	public void resetGate()
	{
		currentSymbol = 0;
		symbolBuffer = 0;
		addressBuffer = new int[0];
		super.resetGate();
	}
}
