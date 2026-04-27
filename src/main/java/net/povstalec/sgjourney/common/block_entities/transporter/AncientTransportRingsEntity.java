package net.povstalec.sgjourney.common.block_entities.transporter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.TransporterInit;
import net.povstalec.sgjourney.common.sgjourney.transporter.AncientBlockEntityTransportRings;

public class AncientTransportRingsEntity extends AbstractTransportRingsEntity<AncientBlockEntityTransportRings>
{
	public AncientTransportRingsEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ANCIENT_TRANSPORT_RINGS.get(), TransporterInit.ANCIENT_TRANSPORT_RINGS.get(), pos, state, 0);
	}
}
