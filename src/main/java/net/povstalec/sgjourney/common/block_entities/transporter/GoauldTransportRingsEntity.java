package net.povstalec.sgjourney.common.block_entities.transporter;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonTransporterConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.TransporterInit;
import net.povstalec.sgjourney.common.sgjourney.transporter.GoauldBlockEntityTransportRings;

public class GoauldTransportRingsEntity extends AbstractTransportRingsEntity<GoauldBlockEntityTransportRings>
{
	public GoauldTransportRingsEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.GOAULD_TRANSPORT_RINGS.get(), TransporterInit.GOAULD_TRANSPORT_RINGS.get(), pos, state, 1);
	}
	
	@Override
	public long getCapacity()
	{
		return CommonTransporterConfig.goauld_transport_rings_energy_capacity.get();
	}
	
	@Override
	public long getMaxReceive()
	{
		return CommonTransporterConfig.goauld_transport_rings_max_energy_receive.get();
	}
	
	@Override
	protected Component getDefaultName()
	{
		return Component.translatable("block.sgjourney.goauld_transport_rings");
	}
}
