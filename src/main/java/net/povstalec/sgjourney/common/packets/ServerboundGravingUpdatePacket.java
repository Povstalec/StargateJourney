package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.common.blocks.SpecialGravableBlock;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ServerboundGravingUpdatePacket
{
	private final BlockPos blockPos;
	private String pointOfOrigin = "";
	private String symbols = "";
	@Nullable
	private Address address = null;
	
	public ServerboundGravingUpdatePacket(BlockPos blockPos)
	{
		this.blockPos = blockPos;
	}

    public ServerboundGravingUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos());
		
		this.pointOfOrigin = buffer.readUtf();
		this.symbols = buffer.readUtf();
		if(buffer.readBoolean())
			this.address = Address.read(buffer);
    }
	
	public ServerboundGravingUpdatePacket withPointOfOrigin(ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		this.pointOfOrigin = pointOfOrigin.location().toString();
		return this;
	}
	
	public ServerboundGravingUpdatePacket withSymbols(ResourceKey<Symbols> symbols)
	{
		this.symbols = symbols.location().toString();
		return this;
	}
	
	public ServerboundGravingUpdatePacket withAddress(Address address)
	{
		this.address = address;
		return this;
	}

    public void encode(FriendlyByteBuf buffer)
    {
    	buffer.writeBlockPos(blockPos);
		buffer.writeUtf(pointOfOrigin);
    	buffer.writeUtf(symbols);
		buffer.writeBoolean(address != null);
		if(address != null)
			Address.write(buffer, address);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
    	ctx.get().enqueueWork(() ->
		{
			ServerPlayer player = ctx.get().getSender();
			Level level = player.level;
			BlockState state = level.getBlockState(blockPos);
			if(state.getBlock() instanceof SpecialGravableBlock gravableBlock)
			{
				if(!pointOfOrigin.isEmpty())
					gravableBlock.setPointOfOrigin(level, blockPos, state, Conversion.stringToPointOfOrigin(pointOfOrigin));
				if(!symbols.isEmpty())
					gravableBlock.setSymbols(level, blockPos, state, Conversion.stringToSymbols(symbols));
				if(address != null)
					gravableBlock.setAddress(level, blockPos, state, address);
				
				gravableBlock.useGraver(player, blockPos);
			}
    	});
        return true;
    }
}


