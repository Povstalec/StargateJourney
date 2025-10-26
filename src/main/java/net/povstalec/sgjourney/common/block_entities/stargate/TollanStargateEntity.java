package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.StargatePeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo.ChevronLockSpeed;
import net.povstalec.sgjourney.common.sgjourney.stargate.TollanStargate;

public class TollanStargateEntity extends AbstractStargateEntity
{
	public static final int TOTAL_SYMBOLS = 48;
	
	public static final float TOLLAN_THICKNESS = 5.0F;
	public static final float VERTICAL_CENTER_TOLLAN_HEIGHT = 0F;
	public static final float HORIZONTAL_CENTER_TOLLAN_HEIGHT = (TOLLAN_THICKNESS / 2) / 16;
	
	public TollanStargateEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.TOLLAN_STARGATE.get(), StargateJourney.sgjourneyLocation("tollan/tollan"), pos, state,
				TOTAL_SYMBOLS, StargateInfo.Gen.GEN_2, 2, VERTICAL_CENTER_TOLLAN_HEIGHT, HORIZONTAL_CENTER_TOLLAN_HEIGHT);
	}

	@Override
	protected int getMaxObstructiveBlocks()
	{
		return CommonStargateConfig.max_obstructive_blocks_tollan.get();
	}

	@Override
	public void playRotationSound() {}

	@Override
	public void stopRotationSound() {}

	@Override
	public ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? TollanStargate.CHEVRON_LOCK_SPEED : ChevronLockSpeed.FAST;
	}

	@Override
	public void registerInterfaceMethods(StargatePeripheralWrapper wrapper)
	{
		CCTweakedCompatibility.registerTollanStargateMethods(wrapper);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, TollanStargateEntity stargate)
	{
		AbstractStargateEntity.tick(level, pos, state, stargate);
	}
}
