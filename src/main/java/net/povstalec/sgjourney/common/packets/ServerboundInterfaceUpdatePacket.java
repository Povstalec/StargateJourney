package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.blocks.tech_interface.AbstractInterfaceBlock;
import net.povstalec.sgjourney.common.blockstates.InterfaceMode;

import java.util.function.Supplier;

public class ServerboundInterfaceUpdatePacket
{
	public final BlockPos pos;
	public final long energyTarget;
	public final InterfaceMode mode;

    public ServerboundInterfaceUpdatePacket(BlockPos pos, long energyTarget, InterfaceMode mode)
    {
    	this.pos = pos;
    	this.energyTarget = energyTarget;
		this.mode = mode;
    }

    public ServerboundInterfaceUpdatePacket(FriendlyByteBuf buffer)
    {
    	this(buffer.readBlockPos(), buffer.readLong(), buffer.readEnum(InterfaceMode.class));
    }

    public void encode(FriendlyByteBuf buffer)
    {
    	buffer.writeBlockPos(pos);
    	buffer.writeLong(energyTarget);
		buffer.writeEnum(mode);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
    	ctx.get().enqueueWork(() -> {
			Level level = ctx.get().getSender().level;
    		final BlockEntity blockEntity = level.getBlockEntity(pos);
    		
    		if(blockEntity instanceof AbstractInterfaceEntity interfaceEntity)
				interfaceEntity.setEnergyTarget(energyTarget);
			
			BlockState state = level.getBlockState(pos);
			if(level.getBlockState(pos).getBlock() instanceof AbstractInterfaceBlock interfaceBlock)
				interfaceBlock.setMode(state, level, pos, mode);
				
    	});
        return true;
    }
}


