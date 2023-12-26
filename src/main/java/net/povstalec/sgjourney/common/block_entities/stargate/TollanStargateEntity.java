package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.StargatePeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Stargate.ChevronLockSpeed;

public class TollanStargateEntity extends AbstractStargateEntity
{
	public static final float TOLLAN_THICKNESS = 5.0F;
	public static final float VERTICAL_CENTER_TOLLAN_HEIGHT = 0F;
	public static final float HORIZONTAL_CENTER_TOLLAN_HEIGHT = (TOLLAN_THICKNESS / 2) / 16;
	
	public TollanStargateEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.TOLLAN_STARGATE.get(), pos, state, Stargate.Gen.GEN_2, 2,
				VERTICAL_CENTER_TOLLAN_HEIGHT, HORIZONTAL_CENTER_TOLLAN_HEIGHT);
	}
	
	@Override
    public void onLoad()
	{
        if(level.isClientSide())
        	return;
		pointOfOrigin = "sgjourney:tauri";
		symbols = "sgjourney:milky_way";
        
        super.onLoad();
    }

	@Override
	public SoundEvent getChevronEngageSound()
	{
		return SoundInit.TOLLAN_CHEVRON_ENGAGE.get();
	}

	@Override
	public SoundEvent getWormholeOpenSound()
	{
		return SoundInit.TOLLAN_WORMHOLE_OPEN.get();
	}

	@Override
	public SoundEvent getWormholeCloseSound()
	{
		return SoundInit.TOLLAN_WORMHOLE_CLOSE.get();
	}

	@Override
	public SoundEvent getFailSound()
	{
		return SoundInit.TOLLAN_DIAL_FAIL.get();
	}

	@Override
	public void playRotationSound(){}

	@Override
	public void stopRotationSound(){}

	@Override
	public ChevronLockSpeed getChevronLockSpeed()
	{
		return CommonStargateConfig.tollan_chevron_lock_speed.get();
	}

	@Override
	public void registerInterfaceMethods(StargatePeripheralWrapper wrapper)
	{
		CCTweakedCompatibility.registerTollanStargateMethods(wrapper);
	}
}
