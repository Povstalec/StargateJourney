package net.povstalec.sgjourney.common.block_entities.stargate;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class ClassicStargateEntity extends AbstractStargateEntity
{
	public String pointOfOrigin = "sgjourney:tauri";
	public String symbols = "sgjourney:milky_way";

	private short rotationOld = 0;
	private short rotation = 0;
	
	public int[] addressBuffer = new int[0];
	public int symbolBuffer = 0;
	
	public ClassicStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.CLASSIC_STARGATE.get(), pos, state, Stargate.Gen.GEN_1);
	}
	
	public void load(CompoundTag nbt)
	{
        super.load(nbt);
        if(nbt.contains("Rotation"))
        	rotation = nbt.getShort("Rotation");
    }
	
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.putShort("Rotation", rotation);
		
		super.saveAdditional(nbt);
	}
	
	public SoundEvent chevronEngageSound()
	{
		return SoundInit.MILKY_WAY_CHEVRON_ENGAGE.get();
	}
	
	public SoundEvent failSound()
	{
		return SoundInit.MILKY_WAY_DIAL_FAIL.get();
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
		//stargate.rotate();
		AbstractStargateEntity.tick(level, pos, state, (AbstractStargateEntity) stargate);
	}
	
	private void rotate()
	{
		this.rotationOld = this.rotation;
		this.rotation++;
	}
	
	public float getRotation(float partialTick)
	{
		return Mth.lerp(partialTick, this.rotationOld, this.rotation);
	}
	
}
