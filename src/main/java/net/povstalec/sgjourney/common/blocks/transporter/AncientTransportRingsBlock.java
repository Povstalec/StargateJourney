package net.povstalec.sgjourney.common.blocks.transporter;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransportRingsEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AncientTransportRingsEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.menu.TransportRingsMenu;
import net.povstalec.sgjourney.common.misc.NetworkUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class AncientTransportRingsBlock extends AbstractTransportRingsBlock
{
	public static final MapCodec<AncientTransportRingsBlock> CODEC = simpleCodec(AncientTransportRingsBlock::new);
	
	public AncientTransportRingsBlock(Properties properties)
	{
		super(properties);
	}
	
	protected MapCodec<AncientTransportRingsBlock> codec() {
		return CODEC;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new AncientTransportRingsEntity(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.ANCIENT_TRANSPORT_RINGS.get(), AbstractTransportRingsEntity::tick);
	}
	
	@Override
	public void openMenu(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof AncientTransportRingsEntity transportRings)
		{
			if(transportRings.hasPermissions(player, true))
			{
				MenuProvider containerProvider = new MenuProvider()
				{
					@Override
					public @NotNull Component getDisplayName()
					{
						return transportRings.hasCustomName() ? transportRings.getCustomName() : Component.translatable("screen.sgjourney.ancient_transport_rings");
					}
					
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity)
					{
						return new TransportRingsMenu.Ancient(windowId, playerInventory, transportRings);
					}
				};
				NetworkUtils.openMenu((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
			}
		}
		else
			throw new IllegalStateException("Our named container provider is missing!");
	}
}
