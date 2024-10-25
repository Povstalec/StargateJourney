package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.StargatePeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Stargate.ChevronLockSpeed;

public class TollanStargateEntity extends AbstractStargateEntity
{
	public static final float TOLLAN_THICKNESS = 5.0F;
	public static final float VERTICAL_CENTER_TOLLAN_HEIGHT = 0F;
	public static final float HORIZONTAL_CENTER_TOLLAN_HEIGHT = (TOLLAN_THICKNESS / 2) / 16;
	
	public TollanStargateEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.TOLLAN_STARGATE.get(), StargateJourney.sgjourneyLocation("tollan/tollan"), pos, state, Stargate.Gen.GEN_2, 2,
				VERTICAL_CENTER_TOLLAN_HEIGHT, HORIZONTAL_CENTER_TOLLAN_HEIGHT);
		this.symbolBounds = 47;
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
	public boolean hasIris()
	{
		return false;
	}
	
	@Override
	public boolean isIrisClosed()
	{
		return false;
	}
	
	@Override
	public short getIrisProgress()
	{
		return 0;
	}
	
	@Override
	public float getIrisProgress(float partialTick)
	{
		return 0;
	}
	
	@Override
	protected void setIrisState() {}

	@Override
	public void playRotationSound() {}

	@Override
	public void stopRotationSound() {}

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
	
	public static void tick(Level level, BlockPos pos, BlockState state, MilkyWayStargateEntity stargate)
	{
		AbstractStargateEntity.tick(level, pos, state, (AbstractStargateEntity) stargate);
	}
}
