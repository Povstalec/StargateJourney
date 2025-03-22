package net.povstalec.sgjourney.common.blocks.dhd;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
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
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.CrystalDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.MilkyWayDHDEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.menu.MilkyWayDHDMenu;
import net.povstalec.sgjourney.common.misc.InventoryHelper;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.misc.NetworkUtils;

import java.util.List;

public class MilkyWayDHDBlock extends CrystalDHDBlock implements SimpleWaterloggedBlock
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static final MapCodec<MilkyWayDHDBlock> CODEC = simpleCodec(MilkyWayDHDBlock::new);
	
	public MilkyWayDHDBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
	}

	protected MapCodec<MilkyWayDHDBlock> codec() {
		return CODEC;
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
	public void use(Level level, BlockPos pos, Player player)
	{
		if(level.isClientSide())
			return;

		BlockEntity blockEntity = level.getBlockEntity(pos);

		if(blockEntity instanceof AbstractDHDEntity dhd)
		{
			dhd.setStargate();

			if(player.isShiftKeyDown())
				this.openCrystalMenu(player, blockEntity);
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
				NetworkUtils.openMenu((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
			}
		}
		else
		{
			throw new IllegalStateException("Our named container provider is missing!");
		}
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
		
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));
		
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
		
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));
		
		return stack;
	}
	
	private static ListTag setupEnergyInventory()
	{
		ListTag nbtTagList = new ListTag();
		
		nbtTagList.add(InventoryHelper.addItem(0, InventoryUtil.itemName(ItemInit.FUSION_CORE.get()), 1, null));
		
		return nbtTagList;
	}
	
	private static ListTag setupCrystalInventory()
	{
		ListTag nbtTagList = new ListTag();
		
		nbtTagList.add(InventoryHelper.addItem(0, InventoryUtil.itemName(ItemInit.LARGE_CONTROL_CRYSTAL.get()), 1, null));
		nbtTagList.add(InventoryHelper.addItem(1, InventoryUtil.itemName(ItemInit.ENERGY_CRYSTAL.get()), 1, null));
		nbtTagList.add(InventoryHelper.addItem(2, InventoryUtil.itemName(ItemInit.COMMUNICATION_CRYSTAL.get()), 1, null));
		nbtTagList.add(InventoryHelper.addItem(3, InventoryUtil.itemName(ItemInit.ENERGY_CRYSTAL.get()), 1, null));
		nbtTagList.add(InventoryHelper.addItem(5, InventoryUtil.itemName(ItemInit.ENERGY_CRYSTAL.get()), 1, null));
		nbtTagList.add(InventoryHelper.addItem(7, InventoryUtil.itemName(ItemInit.TRANSFER_CRYSTAL.get()), 1, null));
		
		return nbtTagList;
	}
}
