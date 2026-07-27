package net.povstalec.sgjourney.common.block_entities.transporter;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonTransporterConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.TransporterInit;
import net.povstalec.sgjourney.common.sgjourney.transporter.AncientBlockEntityTransportRings;

public class AncientTransportRingsEntity extends AbstractTransportRingsEntity<AncientBlockEntityTransportRings>
{
	public AncientTransportRingsEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ANCIENT_TRANSPORT_RINGS.get(), TransporterInit.ANCIENT_TRANSPORT_RINGS.get(), pos, state, 0);
	}
	
	@Override
	public long getCapacity()
	{
		return CommonTransporterConfig.ancient_transport_rings_energy_capacity.get();
	}
	
	@Override
	public long getMaxReceive()
	{
		return CommonTransporterConfig.ancient_transport_rings_max_energy_receive.get();
	}
	
	@Override
	protected Component getDefaultName()
	{
		return Component.translatable("block.sgjourney.ancient_transport_rings");
	}
}
