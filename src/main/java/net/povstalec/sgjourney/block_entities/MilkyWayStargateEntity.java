package net.povstalec.sgjourney.block_entities;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.PacketHandlerInit;
import net.povstalec.sgjourney.init.SoundInit;
import net.povstalec.sgjourney.network.ClientboundStargateUpdatePacket;
import net.povstalec.sgjourney.stargate.StargatePart;

public class MilkyWayStargateEntity extends AbstractStargateEntity
{
    public short degrees = 0;
    public boolean isChevronRaised;
	
	public MilkyWayStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.MILKY_WAY_STARGATE.get(), pos, state);
	}
	
	@Override
    public void onLoad()
	{
        if(level.isClientSide)
        	return;
        
        if(!addToNetwork && pointOfOrigin.equals("sgjourney:error"))
        	setPointOfOrigin(this.getLevel());
        if(!addToNetwork && symbols.equals("sgjourney:error"))
        	setSymbols(this.getLevel());
        
        super.onLoad();
    }
	
	public void load(CompoundTag nbt)
	{
        super.load(nbt);
        if(nbt.contains("PointOfOrigin"))
        	pointOfOrigin = nbt.getString("PointOfOrigin");
        if(nbt.contains("Symbols"))
        	symbols = nbt.getString("Symbols");
    }
	
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
			nbt.putString("PointOfOrigin", pointOfOrigin);
			nbt.putString("Symbols", symbols);
		
		super.saveAdditional(nbt);
	}
	
	public SoundEvent chevronEngageSound()
	{
		return SoundInit.MILKY_WAY_CHEVRON_ENGAGE.get();
	}
	
	public SoundEvent failSound()
	{
		return SoundInit.MILKY_WAY_DIAL_FAIL.get();
	}
	
	//TODO
	public void redstoneBuffer(int signalStrength, StargatePart part)
	{
		
	}
	
	public double angle()
	{
		return (double) 360/symbolCount;
	}
	
	public void raiseChevron()
	{
		if(!isChevronRaised && !symbolInAddress(currentSymbol))
		{
			level.playSound((Player)null, worldPosition, SoundInit.MILKY_WAY_CHEVRON_ENCODE.get(), SoundSource.BLOCKS, 0.25F, 1F);
			isChevronRaised = true;
		}
	}
	
	public void lowerChevron()
	{
		currentSymbol = (int) Math.round(degrees * 2 / angle());
		
		if(isChevronRaised)
		{
			encodeChevron(currentSymbol);
			isChevronRaised = false;
		}
	}
	
	@Override
	public void tick(Level level, BlockPos pos, BlockState state)
	{
		if(!isBusy() && !isChevronRaised && isPowered && signalStrength <= 7)
		{
			degrees--;
		}
		else if(!isBusy() && !isChevronRaised && isPowered && signalStrength >= 8 && signalStrength <= 14)
		{
			degrees++;
		}
		
		if(degrees >= 180)
		{
			degrees = (short) (degrees - 180);
		}
		else if(degrees < 0)
		{
			degrees = (short) (degrees + 180);
		}
		super.tick(level, pos, state);
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundStargateUpdatePacket(pos, degrees, chevronsActive, isChevronRaised, isBusy(), tick, pointOfOrigin, symbols, currentSymbol));
	}
	
}
