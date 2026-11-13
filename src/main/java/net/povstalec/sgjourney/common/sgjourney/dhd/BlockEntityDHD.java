package net.povstalec.sgjourney.common.sgjourney.dhd;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface BlockEntityDHD<DHDEntity extends BlockEntity> extends DHD
{
	DHDEntity getDHDEntity();
}
