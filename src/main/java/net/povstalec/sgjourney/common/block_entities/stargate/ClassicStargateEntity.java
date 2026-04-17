package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.StargateInit;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo.ChevronLockSpeed;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import net.povstalec.sgjourney.common.sgjourney.stargate.ClassicBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.ClassicStargate;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ClassicStargateEntity extends RotatingStargateEntity<ClassicBlockEntityStargate>
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
		super(BlockEntityInit.CLASSIC_STARGATE.get(), StargateInit.CLASSIC.get(), new ResourceLocation(StargateJourney.MODID, "classic"), pos, state,
				TOTAL_SYMBOLS, 0, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_CLASSIC_HEIGHT, MAX_ROTATION);
	}

	@Override
	public CompoundTag serializeStargateInfo(CompoundTag tag)
	{
		super.serializeStargateInfo(tag);
		
		symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		return tag;
	}
	
	@Override
	public void deserializeStargateInfo(CompoundTag tag, boolean isUpgraded)
	{
		symbolInfo().loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
    	
    	super.deserializeStargateInfo(tag, isUpgraded);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = super.getUpdateTag();
		
		symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		super.onDataPacket(net, packet);
		CompoundTag tag = packet.getTag();
		if(tag != null)
			symbolInfo().loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, ClassicStargateEntity stargate)
	{
		RotatingStargateEntity.tick(level, pos, state, stargate);
	}

	@Override
	public ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? ClassicStargate.CHEVRON_LOCK_SPEED : ChevronLockSpeed.FAST;
	}

	@Override
	public void registerInterfaceMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper)
	{
		CCTweakedCompatibility.registerClassicStargateMethods(wrapper);
	}
	
	@Override
	public void generate()
	{
		super.generate();
		
		Random random = new Random();
		setRotation(2 * random.nextInt(0, MAX_ROTATION / 2 + 1));
	}
	
	@Override
	public void generateAdditional(StructureGenEntity.Step generationStep)
	{
		if(generationStep == StructureGenEntity.Step.SETUP)
		{
			if(!PointOfOrigin.isValid(level.getServer(), symbolInfo().pointOfOrigin()))
				symbolInfo().setPointOfOrigin(null);
			
			if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
				symbolInfo().setSymbols(null);
		}
		else
		{
			if(!PointOfOrigin.isValid(level.getServer(), symbolInfo().pointOfOrigin()))
			{
				if(localPointOfOrigin)
					symbolInfo().setPointOfOrigin(PointOfOrigin.fromDimension(level.getServer(), level.dimension()));
				else
					symbolInfo().setPointOfOrigin(PointOfOrigin.randomPointOfOrigin(level.getServer(), level.dimension()));
			}
			
			if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
				symbolInfo().setSymbols(Symbols.fromDimension(level.getServer(), level.dimension()));
		}
	}
}
