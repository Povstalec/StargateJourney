package net.povstalec.sgjourney.blocks.dhd;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.block_entities.dhd.MilkyWayDHDEntity;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.BlockInit;
import net.povstalec.sgjourney.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.items.crystals.EnergyCrystalItem;
import net.povstalec.sgjourney.menu.MilkyWayDHDMenu;
import net.povstalec.sgjourney.misc.InventoryHelper;

public class MilkyWayDHDBlock extends AbstractDHDBlock
{
	public MilkyWayDHDBlock(Properties properties)
	{
		super(properties);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		MilkyWayDHDEntity dhd = new MilkyWayDHDEntity(pos, state);
		
		return dhd;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
        if(!level.isClientSide) 
        {
    		BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if(blockEntity instanceof AbstractDHDEntity dhd) 
        	{
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
	
	public static ItemStack milkyWayCrystalSetup(ItemStack stack)
	{
        CompoundTag blockEntityTag = new CompoundTag();
        CompoundTag inventory = new CompoundTag();
        
        blockEntityTag.putString("id", "sgjourney:milky_way_dhd");
        blockEntityTag.putLong("energy", 0);
        
        inventory.putInt("Size", 9);
        inventory.put("Items", setupMilkyWayInventory());
        
        blockEntityTag.put("Inventory", inventory);
		stack.addTagElement("BlockEntityTag", blockEntityTag);
		
		return stack;
	}
	
	private static ListTag setupMilkyWayInventory()
	{
		ListTag nbtTagList = new ListTag();
		
		nbtTagList.add(InventoryHelper.addItem(0, "sgjourney:large_control_crystal", 1, null));
		nbtTagList.add(InventoryHelper.addItem(1, "sgjourney:energy_crystal", 1, EnergyCrystalItem.tagSetup(EnergyCrystalItem.CrystalMode.ENERGY_STORAGE, 0, EnergyCrystalItem.MAX_TRANSFER)));
		nbtTagList.add(InventoryHelper.addItem(2, "sgjourney:communication_crystal", 1, CommunicationCrystalItem.tagSetup(0)));
		nbtTagList.add(InventoryHelper.addItem(3, "sgjourney:energy_crystal", 1, EnergyCrystalItem.tagSetup(EnergyCrystalItem.CrystalMode.ENERGY_STORAGE, 0, EnergyCrystalItem.MAX_TRANSFER)));
		nbtTagList.add(InventoryHelper.addItem(4, "sgjourney:communication_crystal", 1, CommunicationCrystalItem.tagSetup(0)));
		nbtTagList.add(InventoryHelper.addItem(5, "sgjourney:energy_crystal", 1, EnergyCrystalItem.tagSetup(EnergyCrystalItem.CrystalMode.ENERGY_STORAGE, 0, EnergyCrystalItem.MAX_TRANSFER)));
		nbtTagList.add(InventoryHelper.addItem(6, "sgjourney:memory_crystal", 1, null));
		nbtTagList.add(InventoryHelper.addItem(7, "sgjourney:energy_crystal", 1, EnergyCrystalItem.tagSetup(EnergyCrystalItem.CrystalMode.ENERGY_TRANSFER, 0, EnergyCrystalItem.MAX_TRANSFER)));
		nbtTagList.add(InventoryHelper.addItem(8, "sgjourney:communication_crystal", 1, CommunicationCrystalItem.tagSetup(0)));
		
		return nbtTagList;
	}
	
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
		if(stack.hasTag())
		{
			CompoundTag blockEntityTag = BlockItem.getBlockEntityData(stack);
			ListTag tagList = blockEntityTag.getCompound("Inventory").getList("Items", Tag.TAG_COMPOUND);
			
			if(tagList.size() > 0)
			{
				CompoundTag list1 = tagList.getCompound(0);
				
				if(list1.contains("id", Tag.TAG_STRING) && list1.getString("id").equals("sgjourney:large_control_crystal") && list1.contains("Count", Tag.TAG_BYTE) && list1.getByte("Count") > 0)
			        tooltipComponents.add(Component.literal("Has Control Crystal").withStyle(ChatFormatting.DARK_RED));
			}
		}
		
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
