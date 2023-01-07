package net.povstalec.sgjourney.block_entities.address;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.data.StargateNetwork;
import net.povstalec.sgjourney.init.PacketHandlerInit;
import net.povstalec.sgjourney.network.ClientboundSymbolUpdatePacket;

public abstract class SymbolBlockEntity extends BlockEntity
{
	public String symbol = "sgjourney:error";
	
	public SymbolBlockEntity(BlockEntityType<?> entity, BlockPos pos, BlockState state) 
	{
		super(entity, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		
		if(level.isClientSide)
			return;
		
		if(symbol.equals("sgjourney:error"))
			setSymbol(level);
	}
	
	@Override
    public void load(CompoundTag tag)
    {
    	super.load(tag);
    	
    	if(tag.contains("Symbol"))
    		symbol = tag.getString("Symbol");
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag)
	{
		if(symbol != null)
			tag.putString("Symbol", symbol);
		
		super.saveAdditional(tag);
	}
	
	public void setSymbol(Level level)
	{
		if(level.isClientSide())
			return;
		
		symbol = StargateNetwork.get(level).getPointOfOrigin(level.dimension().location().toString());
	}
	
	public String getAddress()
	{
		return StargateNetwork.get(level).getLocalAddress(symbol);
	}
	
	public void tick(Level level, BlockPos pos, BlockState state)
	{
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundSymbolUpdatePacket(worldPosition, symbol));
	}

}
