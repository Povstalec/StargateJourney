package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.StargatePeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Stargate.ChevronLockSpeed;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class ClassicStargateEntity extends RotatingStargateEntity
{
	public static final float CLASSIC_THICKNESS = 8.0F;
	public static final float HORIZONTAL_CENTER_CLASSIC_HEIGHT = (CLASSIC_THICKNESS / 2) / 16;
	
	public static final int TOTAL_SYMBOLS = 39;
	public static final int MAX_ROTATION = 156;
	
	private static final short ROTATION_TICK_DURATION = 40;
	private static final short CHEVRON_LOCK_TICK_DURATION = 20;
	
	public int[] addressBuffer = new int[0];
	public int symbolBuffer = 0;
	
	public ClassicStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.CLASSIC_STARGATE.get(), new ResourceLocation(StargateJourney.MODID, "classic/classic"), pos, state,
				TOTAL_SYMBOLS, Stargate.Gen.NONE, 0, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_CLASSIC_HEIGHT, MAX_ROTATION);
		
		displayID = true;
	}
	
	@Override
    public void onLoad()
	{
       super.onLoad();

        if(this.level.isClientSide())
        	return;
		
		if(!PointOfOrigin.validLocation(level.getServer(), symbolInfo().pointOfOrigin()))
			symbolInfo().setPointOfOrigin(PointOfOrigin.fromDimension(level.getServer(), level.dimension()));
		
		if(!Symbols.validLocation(level.getServer(), symbolInfo().symbols()))
			symbolInfo().setSymbols(Symbols.fromDimension(level.getServer(), level.dimension()));
    }

	@Override
	public CompoundTag serializeStargateInfo(CompoundTag tag)
	{
		super.serializeStargateInfo(tag);
		
		tag.putString(POINT_OF_ORIGIN, symbolInfo().pointOfOrigin().toString());
		tag.putString(SYMBOLS, symbolInfo().symbols().toString());
		
		return tag;
	}
	
	@Override
	public void deserializeStargateInfo(CompoundTag tag, boolean isUpgraded)
	{
		if(tag.contains(POINT_OF_ORIGIN))
			symbolInfo().setPointOfOrigin(new ResourceLocation(tag.getString(POINT_OF_ORIGIN)));
		
		if(tag.contains(SYMBOLS))
			symbolInfo().setSymbols(new ResourceLocation(tag.getString(SYMBOLS)));
    	
    	super.deserializeStargateInfo(tag, isUpgraded);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, ClassicStargateEntity stargate)
	{
		RotatingStargateEntity.tick(level, pos, state, stargate);
	}

	@Override
	public ChevronLockSpeed getChevronLockSpeed()
	{
		return CommonStargateConfig.classic_chevron_lock_speed.get();
	}

	@Override
	public void registerInterfaceMethods(StargatePeripheralWrapper wrapper)
	{
		CCTweakedCompatibility.registerClassicStargateMethods(wrapper);
	}
	
}
