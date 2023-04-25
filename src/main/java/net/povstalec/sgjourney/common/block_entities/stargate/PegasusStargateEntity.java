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
import net.povstalec.sgjourney.common.packets.ClientboundPegasusStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Addressing;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class PegasusStargateEntity extends AbstractStargateEntity
{
	public int currentSymbol = 0;
	public int[] addressBuffer = new int[0];
	public int symbolBuffer = 0;
	private boolean passedOver = false;
	public int animationTick = 0;
	
	public PegasusStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.PEGASUS_STARGATE.get(), pos, state, Stargate.Gen.GEN_3);
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
	
	public SoundEvent chevronEngageSound()
	{
		return SoundInit.PEGASUS_CHEVRON_ENGAGE.get();
	}
	
	public SoundEvent failSound()
	{
		return SoundInit.PEGASUS_DIAL_FAIL.get();
	}
	
	@Override
	public Stargate.Feedback engageSymbol(int symbol)
	{
		if(isConnected() && symbol == 0)
			return disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
		
		if(Addressing.addressContainsSymbol(addressBuffer, symbol))
			return Stargate.Feedback.SYMBOL_ENCODED;
		
		addressBuffer = ArrayHelper.growIntArray(addressBuffer, symbol);
		return Stargate.Feedback.SYMBOL_ENCODED;
	}
	
	@Override
	protected Stargate.Feedback encodeChevron(int symbol)
	{
		symbolBuffer++;
		passedOver = false;
		animationTick = 0;
		return super.encodeChevron(symbol);
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
					encodeChevron(symbol);
			}
			else
				symbolWork();
		}
		
		/*if(animationTick == 1)
			Minecraft.getInstance().getSoundManager().play(new PegasusStargateRingSound(this, symbolBuffer));*/
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, PegasusStargateEntity stargate)
	{
		if(level.isClientSide())
			return;
		
		stargate.animateSpin();
		
		AbstractStargateEntity.tick(level, pos, state, (AbstractStargateEntity) stargate);
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(stargate.worldPosition)), new ClientboundPegasusStargateUpdatePacket(stargate.worldPosition, stargate.symbolBuffer, stargate.addressBuffer, stargate.currentSymbol));
	}
	
	private void symbolWork()
	{
		/*if(!canSpin())
			return;*/
		
		if(symbolBuffer % 2 == 0)
			currentSymbol--;
		else
			currentSymbol++;

		if(currentSymbol > 35)
			currentSymbol = 0;
		else if(currentSymbol < 0)
			currentSymbol = 35;

		animationTick++;
	}
	
	@Override
	public Stargate.Feedback resetStargate(Stargate.Feedback feedback)
	{
		currentSymbol = 0;
		symbolBuffer = 0;
		addressBuffer = new int[0];
		return super.resetStargate(feedback);
	}
}
