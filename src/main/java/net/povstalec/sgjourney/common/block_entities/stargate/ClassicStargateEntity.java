package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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

public class ClassicStargateEntity extends AbstractStargateEntity
{
	public static final float CLASSIC_THICKNESS = 8.0F;
	public static final float HORIZONTAL_CENTER_CLASSIC_HEIGHT = (CLASSIC_THICKNESS / 2) / 16;
	
	private static final short ROTATION_TICK_DURATION = 40;
	private static final short CHEVRON_LOCK_TICK_DURATION = 20;
	
	private short rotationOld = 0;
	private short rotation = 0;
	
	public int[] addressBuffer = new int[0];
	public int symbolBuffer = 0;
	
	public ClassicStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.CLASSIC_STARGATE.get(), new ResourceLocation(StargateJourney.MODID, "classic/classic"), pos, state, Stargate.Gen.NONE, 0,
				VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_CLASSIC_HEIGHT);
		
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
		tag.putShort("Rotation", rotation);
		
		return tag;
	}
	
	@Override
	public void deserializeStargateInfo(CompoundTag tag, boolean isUpgraded)
	{
		if(tag.contains(POINT_OF_ORIGIN))
			symbolInfo().setPointOfOrigin(new ResourceLocation(tag.getString(POINT_OF_ORIGIN)));
		
		if(tag.contains(SYMBOLS))
			symbolInfo().setSymbols(new ResourceLocation(tag.getString(SYMBOLS)));
		
        if(tag.contains("Rotation"))
        	rotation = tag.getShort("Rotation");
    	
    	super.deserializeStargateInfo(tag, isUpgraded);
	}
	
	public double angle()
	{
		return 10.0D;
	}
	
	public short getRotation()
	{
		return rotation;
	}
	
	public void setRotation(short rotation)
	{
		this.rotation = rotation;
	}
	
	public int getCurrentSymbol()
	{
		int currentSymbol;
		double position = rotation * 2 / angle();
		currentSymbol = (int) position;
		if(position >= currentSymbol + 0.5)
			currentSymbol++;
		
		if(currentSymbol > 38)
			currentSymbol = currentSymbol - 39;
		
		return currentSymbol;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, ClassicStargateEntity stargate)
	{
		stargate.handleRotation();
		//stargate.rotate();
		AbstractStargateEntity.tick(level, pos, state, (AbstractStargateEntity) stargate);
	}
	
	private void handleRotation()
	{
		this.rotationOld = this.rotation;
	}
	
	private void rotate()
	{
		this.rotation += 4;
	}
	
	public float getRotation(float partialTick)
	{
		return Mth.lerp(partialTick, this.rotationOld, this.rotation);
	}

	@Override
	public void playRotationSound()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopRotationSound()
	{
		// TODO Auto-generated method stub
		
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
