package net.povstalec.sgjourney.common.blocks.dhd;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.CrystalDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.MilkyWayDHDEntity;
import net.povstalec.sgjourney.common.config.CommonCrystalConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.EnergyCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.TransferCrystalItem;
import net.povstalec.sgjourney.common.menu.MilkyWayDHDMenu;
import net.povstalec.sgjourney.common.misc.InventoryUtil;

public class MilkyWayDHDBlock extends CrystalDHDBlock implements SimpleWaterloggedBlock
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public MilkyWayDHDBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(WATERLOGGED);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		return new MilkyWayDHDEntity(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return super.getStateForPlacement(context).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
		if(!level.isClientSide()) 
        {
    		BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if(blockEntity instanceof MilkyWayDHDEntity dhd)
        	{
        		dhd.setStargate();
        		
        		if(player.isShiftKeyDown())
        			this.openCrystalMenu(player, dhd);
        		else
        		{
        			MenuProvider containerProvider = new MenuProvider() 
            		{
            			@Override
            			public Component getDisplayName() 
            			{
            				return Component.translatable("screen.sgjourney.dhd");
            			}
            			
            			@Override
            			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) 
            			{
            				return new MilkyWayDHDMenu(windowId, playerInventory, blockEntity);
            			}
            		};
            		NetworkHooks.openScreen((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
        		}
        	}
        	else
        	{
        		throw new IllegalStateException("Our named container provider is missing!");
        	}
        }
        return InteractionResult.SUCCESS;
    }

	@Override
	public Block getDHD()
	{
		return BlockInit.MILKY_WAY_DHD.get();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.MILKY_WAY_DHD.get(), AbstractDHDEntity::tick);
    }
	
	public static ItemStack generatedDHD()
	{
		ItemStack stack = new ItemStack(BlockInit.MILKY_WAY_DHD.get());
		CompoundTag blockEntityTag = new CompoundTag();
		
		blockEntityTag.putString("id", "sgjourney:milky_way_dhd");
		
		blockEntityTag.putByte(AbstractDHDEntity.GENERATION_STEP, StructureGenEntity.Step.SETUP.byteValue());
		
		stack.addTagElement("BlockEntityTag", blockEntityTag);
		
		return stack;
	}
	
	public static ItemStack milkyWayCrystalSetup()
	{
		ItemStack stack = new ItemStack(BlockInit.MILKY_WAY_DHD.get());
        CompoundTag blockEntityTag = new CompoundTag();
        
        blockEntityTag.putString("id", "sgjourney:milky_way_dhd");
        blockEntityTag.putLong(EnergyBlockEntity.ENERGY, 0);
		
		CompoundTag crystalInventory = new CompoundTag();
        crystalInventory.putInt("Size", 9);
        crystalInventory.put("Items", setupCrystalInventory());
        blockEntityTag.put(CrystalDHDEntity.CRYSTAL_INVENTORY, crystalInventory);
		
		CompoundTag energyInventory = new CompoundTag();
		energyInventory.putInt("Size", 2);
			energyInventory.put("Items", setupEnergyInventory());
			blockEntityTag.put(AbstractDHDEntity.ENERGY_INVENTORY, energyInventory);
		
		stack.addTagElement("BlockEntityTag", blockEntityTag);
		
		return stack;
	}
	
	private static ListTag setupEnergyInventory()
	{
		ListTag nbtTagList = new ListTag();
		
		nbtTagList.add(InventoryUtil.addItem(0, InventoryUtil.itemName(ItemInit.FUSION_CORE.get()), 1, null));
		
		return nbtTagList;
	}
	
	private static ListTag setupCrystalInventory()
	{
		ListTag nbtTagList = new ListTag();
		
		nbtTagList.add(InventoryUtil.addItem(0, InventoryUtil.itemName(ItemInit.LARGE_CONTROL_CRYSTAL.get()), 1, null));
		nbtTagList.add(InventoryUtil.addItem(1, InventoryUtil.itemName(ItemInit.ENERGY_CRYSTAL.get()), 1, EnergyCrystalItem.tagSetup(0)));
		nbtTagList.add(InventoryUtil.addItem(2, InventoryUtil.itemName(ItemInit.COMMUNICATION_CRYSTAL.get()), 1, CommunicationCrystalItem.tagSetup(0)));
		nbtTagList.add(InventoryUtil.addItem(3, InventoryUtil.itemName(ItemInit.ENERGY_CRYSTAL.get()), 1, EnergyCrystalItem.tagSetup(0)));
		nbtTagList.add(InventoryUtil.addItem(5, InventoryUtil.itemName(ItemInit.ENERGY_CRYSTAL.get()), 1, EnergyCrystalItem.tagSetup(0)));
		nbtTagList.add(InventoryUtil.addItem(7, InventoryUtil.itemName(ItemInit.TRANSFER_CRYSTAL.get()), 1, TransferCrystalItem.tagSetup(CommonCrystalConfig.transfer_crystal_max_transfer.get())));
		
		return nbtTagList;
	}
}
