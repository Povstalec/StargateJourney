package net.povstalec.sgjourney.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.block_entities.energy_gen.NaquadahGeneratorEntity;
import net.povstalec.sgjourney.block_entities.energy_gen.NaquadahGeneratorMarkIEntity;
import net.povstalec.sgjourney.block_entities.energy_gen.NaquadahGeneratorMarkIIEntity;
import net.povstalec.sgjourney.config.ServerNaquadahGeneratorConfig;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.BlockInit;
import net.povstalec.sgjourney.menu.NaquadahGeneratorMenu;

public class NaquadahGeneratorBlock extends BaseEntityBlock
{
	private Tier tier;
	
	public NaquadahGeneratorBlock(Properties properties, Tier tier)
	{
		super(properties);
		this.tier = tier;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return tier == Tier.MARK_I ? new NaquadahGeneratorMarkIEntity(pos, state) : new NaquadahGeneratorMarkIIEntity(pos, state);
	}
	
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
        if(!level.isClientSide) 
        {
        	BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if (blockEntity instanceof NaquadahGeneratorEntity generator) 
        	{
        		MenuProvider containerProvider = new MenuProvider() 
        		{
        			@Override
        			public Component getDisplayName() 
        			{
        				return Component.translatable("screen.sgjourney.naquadah_generator");
        			}
        			
        			@Override
        			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) 
        			{
        				return new NaquadahGeneratorMenu(windowId, playerInventory, blockEntity);
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
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof NaquadahGeneratorEntity)
		{
			if (!level.isClientSide && !player.isCreative())
			{
				ItemStack itemstack = this.tier == Tier.MARK_I ? 
						new ItemStack(BlockInit.NAQUADAH_GENERATOR_MARK_I.get()) : 
						new ItemStack(BlockInit.NAQUADAH_GENERATOR_MARK_II.get());
				
				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return this.tier == Tier.MARK_I ? 
				createTickerHelper(type, BlockEntityInit.NAQUADAH_GENERATOR_MARK_I.get(), NaquadahGeneratorEntity::tick) : 
				createTickerHelper(type, BlockEntityInit.NAQUADAH_GENERATOR_MARK_II.get(), NaquadahGeneratorEntity::tick);
    }
	
	public enum Tier
	{
		MARK_I,
		MARK_II
	}
	
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	int capacity = tier == Tier.MARK_I ? 
    			ServerNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get() : 
    			ServerNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get();
    	
    	int energyPerTick = tier == Tier.MARK_I ? 
    			ServerNaquadahGeneratorConfig.naquadah_generator_mark_i_energy_per_tick.get() : 
    			ServerNaquadahGeneratorConfig.naquadah_generator_mark_ii_energy_per_tick.get();
    	
    	int energy = 0;
    	
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("Energy"))
			energy = stack.getTag().getCompound("BlockEntityTag").getInt("Energy");
		
        tooltipComponents.add(Component.literal("Energy: " + energy + "/" + capacity +" FE").withStyle(ChatFormatting.DARK_RED));
        tooltipComponents.add(Component.literal(energyPerTick + " FE/Tick").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
