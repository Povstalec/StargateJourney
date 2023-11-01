package net.povstalec.sgjourney.common.block_entities.stargate;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Stargate.ChevronLockSpeed;

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
		super(BlockEntityInit.CLASSIC_STARGATE.get(), pos, state, Stargate.Gen.NONE, 0,
				VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_CLASSIC_HEIGHT);
		pointOfOrigin = "sgjourney:tauri";
		symbols = "sgjourney:milky_way";
		
		displayID = true;
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
        super.load(nbt);
        if(nbt.contains("Rotation"))
        	rotation = nbt.getShort("Rotation");
    }

	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.putShort("Rotation", rotation);
		
		super.saveAdditional(nbt);
	}

	@Override
	public SoundEvent getChevronEngageSound()
	{
		return SoundInit.CLASSIC_CHEVRON_ENGAGE.get();
	}

	@Override
	public SoundEvent getWormholeOpenSound()
	{
		return SoundInit.CLASSIC_WORMHOLE_OPEN.get();
	}

	@Override
	public SoundEvent getWormholeCloseSound()
	{
		return SoundInit.CLASSIC_WORMHOLE_CLOSE.get();
	}

	@Override
	public SoundEvent getFailSound()
	{
		return SoundInit.CLASSIC_DIAL_FAIL.get();
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
	
}
