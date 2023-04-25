package net.povstalec.sgjourney.common.blocks;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.capabilities.AncientGene;
import net.povstalec.sgjourney.common.misc.AncientTech;

public class ATAGeneDetectorBlock extends Block implements AncientTech
{
	public static final IntegerProperty MEASURED_GENE = IntegerProperty.create("measured_gene", 0, 3);
	public static final double DETECTION_DISTANCE = 5.0;
	private static final int TICKS_ACTIVE = 20;
	
	private static final int NO_GENE = 0;
	private static final int ARTIFICIAL_GENE = 9;
	private static final int INHERITED_GENE = 12;
	private static final int ANCIENT_GENE = 15;
	
	private int signalStrength = 0;
	
	public ATAGeneDetectorBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(MEASURED_GENE, 0));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(MEASURED_GENE);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		if(!level.isClientSide)
		{
			int measured;
			
			switch(getGeneType(player))
			{
			case ARTIFICIAL:
				measured = 1;
				break;
			case INHERITED:
				measured = 2;
				break;
			case ANCIENT:
				measured = 3;
				break;
			default:
				measured = 0;
			}
			
			if(state.getValue(MEASURED_GENE) != measured)
				level.setBlock(pos, state.setValue(MEASURED_GENE, measured), 3);
		}
		
		level.scheduleTick(pos, this, TICKS_ACTIVE);
		
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state)
	{
		return true;
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource source)
	{
		level.setBlock(pos, state.setValue(MEASURED_GENE, 0), 3);
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		int measuredGene = state.getValue(MEASURED_GENE);
		
		switch(measuredGene)
		{
		case 1:
			return ARTIFICIAL_GENE;
		case 2:
			return INHERITED_GENE;
		case 3:
			return ANCIENT_GENE;
		default:
			return NO_GENE;
		}
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bool)
	{
		if(level.isClientSide)
			return;
		
		int newSignalStrength = level.getBestNeighborSignal(pos);
		
		if(newSignalStrength > signalStrength || newSignalStrength == 0)
			signalStrength = newSignalStrength;
		
		if(level.hasNeighborSignal(pos) && signalStrength == newSignalStrength)
		{
			int measured = detectATAGene(state, level, pos, (double) signalStrength * 2);
			
			if(state.getValue(MEASURED_GENE) != measured)
				level.setBlock(pos, state.setValue(MEASURED_GENE, measured), 3);
		}
	}
	
	private int detectATAGene(BlockState state, Level level, BlockPos pos, double detectionDistance)
	{
		AABB localBox = new AABB((pos.getX() - detectionDistance), (pos.getY() - detectionDistance), (pos.getZ() - detectionDistance), 
							(pos.getX() + 1 + detectionDistance), (pos.getY() + 1 + detectionDistance), (pos.getZ() + 1 + detectionDistance));
		
		List<Entity> localEntities = level.getEntitiesOfClass(Entity.class, localBox);
		
		return measureHighestLevel(localEntities);
	}
	
	private int measureHighestLevel(List<Entity> entities)
	{
		int measured = 0;
		
		if(entities.stream().anyMatch(entity -> getGeneType(entity) == AncientGene.ATAGene.ARTIFICIAL))
			measured = 1;
		if(entities.stream().anyMatch(entity -> getGeneType(entity) == AncientGene.ATAGene.INHERITED))
			measured = 2;
		if(entities.stream().anyMatch(entity -> getGeneType(entity) == AncientGene.ATAGene.ANCIENT))
			measured = 3;
		
		return measured;
	}
}
