package net.povstalec.sgjourney.common.blocks.dhd;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.ClassicDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.CrystalDHDEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.blocks.SpecialEngravableBlock;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.menu.dhd.ClassicDHDMenu;
import net.povstalec.sgjourney.common.menu.dhd.DHDCrystalMenu;
import net.povstalec.sgjourney.common.menu.graver.DHDEngravingMenu;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.misc.NetworkUtils;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ClassicDHDBlock extends CrystalDHDBlock implements SpecialEngravableBlock
{
	public static final MapCodec<ClassicDHDBlock> CODEC = simpleCodec(ClassicDHDBlock::new);

	public ClassicDHDBlock(Properties properties)
	{
		super(properties);
	}

	protected MapCodec<ClassicDHDBlock> codec() {
		return CODEC;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		return new ClassicDHDEntity(pos, state);
	}

	@Override
	protected boolean use(Level level, BlockPos pos, Player player, BlockHitResult hitResult)
	{
		if(player.getItemInHand(InteractionHand.MAIN_HAND).is(TagInit.Items.STOPS_DHD_INTERACTION) ||
			player.getItemInHand(InteractionHand.OFF_HAND).is(TagInit.Items.STOPS_DHD_INTERACTION))
			return false;
		
		if(!level.isClientSide())
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof ClassicDHDEntity dhd)
			{
				if((hitResult.getDirection() != Direction.UP || player.isShiftKeyDown()) && dhd.hasPermissions(player, true))
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
							return new DHDCrystalMenu.Classic(windowId, playerInventory, dhd);
						}
					};
					NetworkUtils.openMenu((ServerPlayer) player, containerProvider, dhd.getBlockPos());
				}
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
							return new ClassicDHDMenu(windowId, playerInventory, dhd);
						}
					};
					NetworkUtils.openMenu((ServerPlayer) player, containerProvider, dhd.getBlockPos());
				}
			}
			else
				throw new IllegalStateException("Our named container provider is missing!");
		}
		
		return true;
	}

	@Override
	public Block getDHD()
	{
		return BlockInit.CLASSIC_DHD.get();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.CLASSIC_DHD.get(), AbstractDHDEntity::tick);
    }
	
	protected void openDHDGravingMenu(Level level, BlockPos pos, BlockState state, @Nullable Player player)
	{
		if(level.getBlockEntity(pos) instanceof ClassicDHDEntity classic)
		{
			MenuProvider containerProvider = new MenuProvider()
			{
				@Override
				public @NotNull Component getDisplayName()
				{
					return Component.empty();
				}
				
				@Override
				public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity)
				{
					return new DHDEngravingMenu.Classic(windowId, playerInventory, classic, ContainerLevelAccess.create(level, pos));
				}
			};
			NetworkUtils.openMenu((ServerPlayer) player, containerProvider, classic.getBlockPos());
		}
	}
	
	@Override
	public InteractionResult onGraverUsed(Level level, BlockPos pos, @Nullable Player player, InteractionHand hand, ItemStack graverStack)
	{
		if(!level.isClientSide())
			openDHDGravingMenu(level, pos, level.getBlockState(pos), player);
		
		return InteractionResult.PASS;
	}
	
	@Override
	public void setPointOfOrigin(Level level, BlockPos pos, BlockState state, ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		if(level.getBlockEntity(pos) instanceof ClassicDHDEntity dhd)
		{
			dhd.symbolInfo().setPointOfOrigin(pointOfOrigin);
			dhd.setChanged();
			dhd.updateClient();
		}
	}
	
	@Override
	public void setSymbols(Level level, BlockPos pos, BlockState state, ResourceKey<Symbols> symbols)
	{
		if(level.getBlockEntity(pos) instanceof ClassicDHDEntity dhd)
		{
			dhd.symbolInfo().setSymbols(symbols);
			dhd.setChanged();
			dhd.updateClient();
		}
	}
	
	public static ItemStack generatedDHD()
	{
		ItemStack stack = new ItemStack(BlockInit.CLASSIC_DHD.get());
		CompoundTag blockEntityTag = new CompoundTag();
		
		blockEntityTag.putString("id", "sgjourney:classic_dhd");
		
		blockEntityTag.putByte(AbstractDHDEntity.GENERATION_STEP, StructureGenEntity.Step.SETUP.byteValue());
		
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));
		
		return stack;
	}
	
	public static ItemStack classicCrystalSetup(HolderLookup.Provider registries)
	{
		ItemStack stack = new ItemStack(BlockInit.CLASSIC_DHD.get());
		CompoundTag blockEntityTag = new CompoundTag();
		
		blockEntityTag.putString("id", "sgjourney:classic_dhd");
		blockEntityTag.putLong(EnergyBlockEntity.ENERGY, CommonDHDConfig.classic_dhd_energy_buffer_capacity.get());
		
		CompoundTag crystalInventory = new CompoundTag();
		crystalInventory.putInt("Size", 9);
		crystalInventory.put("Items", setupCrystalInventory(registries));
		blockEntityTag.put(CrystalDHDEntity.CRYSTAL_INVENTORY, crystalInventory);
		
		CompoundTag energyInventory = new CompoundTag();
		energyInventory.putInt("Size", 2);
			energyInventory.put("Items", setupEnergyInventory(registries));
			blockEntityTag.put(AbstractDHDEntity.ENERGY_INVENTORY, energyInventory);
		
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));
		
		return stack;
	}
	
	private static ListTag setupEnergyInventory(HolderLookup.Provider registries)
	{
		ListTag nbtTagList = new ListTag();
		
		nbtTagList.add(InventoryUtil.addItem(registries, 0, new ItemStack(ItemInit.NAQUADAH_GENERATOR_CORE.get())));
		nbtTagList.add(InventoryUtil.addItem(registries, 1, new ItemStack(ItemInit.NAQUADAH_FUEL_ROD.get())));
		
		return nbtTagList;
	}
	
	private static ListTag setupCrystalInventory(HolderLookup.Provider registries)
	{
		ListTag nbtTagList = new ListTag();
		
		nbtTagList.add(InventoryUtil.addItem(registries, 0, new ItemStack(ItemInit.LARGE_CONTROL_CRYSTAL.get())));
		nbtTagList.add(InventoryUtil.addItem(registries, 1, new ItemStack(ItemInit.ENERGY_CRYSTAL.get())));
		nbtTagList.add(InventoryUtil.addItem(registries, 2, new ItemStack(ItemInit.COMMUNICATION_CRYSTAL.get())));
		nbtTagList.add(InventoryUtil.addItem(registries, 3, new ItemStack(ItemInit.ENERGY_CRYSTAL.get())));
		nbtTagList.add(InventoryUtil.addItem(registries, 5, new ItemStack(ItemInit.ENERGY_CRYSTAL.get())));
		nbtTagList.add(InventoryUtil.addItem(registries, 7, new ItemStack(ItemInit.TRANSFER_CRYSTAL.get())));
		
		return nbtTagList;
	}
}
