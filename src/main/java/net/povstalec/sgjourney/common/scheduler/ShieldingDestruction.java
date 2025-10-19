package net.povstalec.sgjourney.common.scheduler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.ShieldingPart;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ShieldingDestruction(ResourceKey<Level> dimension, BlockPos basePos, List<ShieldingPart> parts,
                                   Direction direction, Orientation orientation, boolean updateStargate) implements TimerCallback<MinecraftServer> {

	public static final Codec<ShieldingDestruction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(ShieldingDestruction::dimension),
			BlockPos.CODEC.fieldOf("base_pos").forGetter(ShieldingDestruction::basePos),
			ShieldingPart.CODEC.listOf().fieldOf("parts").forGetter(ShieldingDestruction::parts),
			Direction.CODEC.fieldOf("direction").forGetter(ShieldingDestruction::direction),
			Orientation.CODEC.fieldOf("orientation").forGetter(ShieldingDestruction::orientation),
			Codec.BOOL.fieldOf("update_stargate").forGetter(ShieldingDestruction::updateStargate)
			).apply(instance, ShieldingDestruction::new));

	@Override
	public void handle(MinecraftServer server, @NotNull TimerQueue<MinecraftServer> manager, long gameTime)
	{
		var level = server.getLevel(dimension);
		if (level != null)
		{
			boolean destroyedIris = false;
			for(ShieldingPart part : parts)
			{
				BlockPos ringPos = part.getShieldingPos(basePos, direction, orientation);
				BlockState state = level.getBlockState(ringPos);

				if(state.getBlock() instanceof AbstractShieldingBlock && state.getValue(AbstractShieldingBlock.PART) == part &&
						state.getValue(AbstractShieldingBlock.FACING) == direction && state.getValue(AbstractShieldingBlock.ORIENTATION) == orientation)
				{
					boolean waterlogged = state.getValue(AbstractShieldingBlock.WATERLOGGED);

					level.setBlock(ringPos, waterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);

					destroyedIris = true;
				}
			}

			if (destroyedIris && updateStargate)
			{
				var stargateState = level.getBlockState(basePos);
				if(stargateState.getBlock() instanceof AbstractStargateBaseBlock stargateBlock)
				{
					stargateBlock.unsetIris(stargateState, level, basePos);
				}
			}
		}
	}
}
