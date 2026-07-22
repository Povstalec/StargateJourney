package net.povstalec.sgjourney.common.blocks.transporter;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransportRingsEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.GoauldTransportRingsEntity;
import net.povstalec.sgjourney.common.config.CommonCrystalConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.PowerCellItem;
import net.povstalec.sgjourney.common.items.crystals.EnergyCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.TransferCrystalItem;
import net.povstalec.sgjourney.common.menu.TransportRingsMenu;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.misc.NetworkUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class GoauldTransportRingsBlock extends AbstractTransportRingsBlock
{
	public static final MapCodec<GoauldTransportRingsBlock> CODEC = simpleCodec(GoauldTransportRingsBlock::new);
	
	public GoauldTransportRingsBlock(Properties properties)
	{
		super(properties);
	}
	
	protected MapCodec<GoauldTransportRingsBlock> codec() {
		return CODEC;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new GoauldTransportRingsEntity(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.GOAULD_TRANSPORT_RINGS.get(), AbstractTransportRingsEntity::tick);
	}
	
	@Override
	public void openMenu(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof GoauldTransportRingsEntity transportRings)
		{
			if(transportRings.hasPermissions(player, true))
			{
				MenuProvider containerProvider = new MenuProvider()
				{
					@Override
					public @NotNull Component getDisplayName()
					{
						return transportRings.hasCustomName() ? transportRings.getCustomName() : Component.translatable("screen.sgjourney.goauld_transport_rings");
					}
					
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity)
					{
						return new TransportRingsMenu.Goauld(windowId, playerInventory, transportRings);
					}
				};
				NetworkUtils.openMenu((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
			}
		}
		else
			throw new IllegalStateException("Our named container provider is missing!");
	}
	
	public static ItemStack transportRingsItemSetup(HolderLookup.Provider registries)
	{
		ItemStack stack = new ItemStack(BlockInit.GOAULD_TRANSPORT_RINGS.get());
		CompoundTag blockEntityTag = new CompoundTag();
		
		blockEntityTag.putString("id", "sgjourney:goauld_transport_rings");
		blockEntityTag.putLong(EnergyBlockEntity.ENERGY, 0);
		
		CompoundTag crystalInventory = new CompoundTag();
		crystalInventory.putInt("Size", 9);
		crystalInventory.put("Items", setupCrystalInventory(registries));
		blockEntityTag.put(AbstractTransportRingsEntity.CRYSTAL_INVENTORY, crystalInventory);
		
		CompoundTag energyInventory = new CompoundTag();
		energyInventory.putInt("Size", 1);
		energyInventory.put("Items", setupEnergyInventory(registries));
		blockEntityTag.put(AbstractTransportRingsEntity.ENERGY_INVENTORY, energyInventory);
		
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));
		
		return stack;
	}
	
	private static ListTag setupEnergyInventory(HolderLookup.Provider registries)
	{
		ListTag nbtTagList = new ListTag();
		
		ItemStack stack = PowerCellItem.liquidNaquadahSetup();
		nbtTagList.add(InventoryUtil.addItem(registries, 0, stack));
		
		return nbtTagList;
	}
	
	private static ListTag setupCrystalInventory(HolderLookup.Provider registries)
	{
		ListTag nbtTagList = new ListTag();
		
		nbtTagList.add(InventoryUtil.addItem(registries, 0, new ItemStack(ItemInit.MATERIALIZATION_CRYSTAL.get())));
		nbtTagList.add(InventoryUtil.addItem(registries, 1, new ItemStack(ItemInit.ENERGY_CRYSTAL.get())));
		nbtTagList.add(InventoryUtil.addItem(registries, 2, new ItemStack(ItemInit.TRANSFER_CRYSTAL.get())));
		
		return nbtTagList;
	}
}
