package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundBatteryBlockUpdatePacket;

public abstract class BatteryBlockEntity extends EnergyBlockEntity
{
	public BatteryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, BatteryBlockEntity battery)
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(battery.getBlockPos())),
				new ClientboundBatteryBlockUpdatePacket(battery.getBlockPos(), battery.getEnergyStored()));
	}
	
	
	
	public static class Naquadah extends BatteryBlockEntity
	{
		public Naquadah(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.LARGE_NAQUADAH_BATTERY.get(), pos, state);
		}
		
		@Override
		protected long capacity()
		{
			return 1000000000L;
		}
		
		@Override
		protected long maxReceive()
		{
			return 1000000000L;
		}
		
		@Override
		protected long maxExtract()
		{
			return 1000000000L;
		}
	}
}
