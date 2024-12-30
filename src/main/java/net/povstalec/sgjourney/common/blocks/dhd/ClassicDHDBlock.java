package net.povstalec.sgjourney.common.blocks.dhd;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.ClassicDHDEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.menu.ClassicDHDMenu;
import net.povstalec.sgjourney.common.misc.NetworkUtils;

public class ClassicDHDBlock extends AbstractDHDBlock
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
	protected void use(Level level, BlockPos pos, Player player)
	{
        if(level.isClientSide())
			return;

		BlockEntity blockEntity = level.getBlockEntity(pos);

		if(blockEntity instanceof AbstractDHDEntity dhd)
		{
			dhd.setStargate();

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
					return new ClassicDHDMenu(windowId, playerInventory, blockEntity);
				}
			};
			NetworkUtils.openMenu((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
		}
		else
		{
			throw new IllegalStateException("Our named container provider is missing!");
		}
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
}
