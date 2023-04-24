package net.povstalec.sgjourney.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.block_entities.CrystalInterfaceEntity;
import net.povstalec.sgjourney.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.BlockInit;
import net.povstalec.sgjourney.menu.CrystalInterfaceMenu;

public class CrystalInterfaceBlock extends BasicInterfaceBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public CrystalInterfaceBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new CrystalInterfaceEntity(pos, state);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
        if (!level.isClientSide) 
        {
    		BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if (blockEntity instanceof CrystalInterfaceEntity) 
        	{
        		MenuProvider containerProvider = new MenuProvider() 
        		{
        			@Override
        			public Component getDisplayName() 
        			{
        				return Component.translatable("screen.sgjourney.crystal_interface");
        			}
        			
        			@Override
        			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) 
        			{
        				return new CrystalInterfaceMenu(windowId, playerInventory, blockEntity);
        			}
        		};
        		NetworkHooks.openScreen((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
        	}
        	else
        	{
        		throw new IllegalStateException("Our named container provider is missing!");
        	}
        }
        return InteractionResult.SUCCESS;
    }
	
	public Block getDroppedBlock()
	{
		return BlockInit.CRYSTAL_INTERFACE.get();
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bool)
	{
		if(level.isClientSide())
			return;

		Direction direction = state.getValue(FACING);
		BlockPos targetPos = pos.relative(direction);

		if (level.getBlockEntity(pos) instanceof CrystalInterfaceEntity crystalInterface) {
			crystalInterface.setInputSignal(level.getBestNeighborSignal(pos));
			if(targetPos.equals(pos2) && crystalInterface.updateInterface())
				level.updateNeighborsAtExceptFromFacing(pos, state.getBlock(), state.getValue(FACING));
		}
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.CRYSTAL_INTERFACE.get(), CrystalInterfaceEntity::tick);
    }
	
	public long getCapacity()
	{
		return 50000000;
	}

	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		BlockEntity entity = level.getBlockEntity(pos);

		if (entity instanceof CrystalInterfaceEntity crystalInterface) {
			BlockEntity energyBlockEntity = crystalInterface.findEnergyBlockEntity();
			if (energyBlockEntity instanceof MilkyWayStargateEntity stargate) {
				int offset = 0;
				if (crystalInterface.getInputSignal() >= 7)
					offset = 26;
				else if (crystalInterface.getInputSignal() > 0) {
					offset = 13;
				}
				int output = stargate.getCurrentSymbol() - offset;

//				System.out.println("Offset: " + offset + " Current symbol: " + stargate.getCurrentSymbol() + " Output: " + output);

				if (output <= 0)
					return 0;
				else if (output >= 15)
					return 15;
				else
					return output;
			}
		}

		return 0;
	}

}
