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
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateRingBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record StargateDestruction(ResourceKey<Level> dimension, BlockPos basePos, List<StargatePart> parts,
		Direction direction, Orientation orientation) implements TimerCallback<MinecraftServer> {

	public static final Codec<StargateDestruction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(StargateDestruction::dimension),
			BlockPos.CODEC.fieldOf("base_pos").forGetter(StargateDestruction::basePos),
			StargatePart.CODEC.listOf().fieldOf("parts").forGetter(StargateDestruction::parts),
			Direction.CODEC.fieldOf("direction").forGetter(StargateDestruction::direction),
			Orientation.CODEC.fieldOf("orientation").forGetter(StargateDestruction::orientation)
			).apply(instance, StargateDestruction::new));

	@Override
	public void handle(MinecraftServer server, @NotNull TimerQueue<MinecraftServer> manager, long gameTime)
	{
		var level = server.getLevel(dimension);
		if (level != null)
		{
			for(StargatePart part : parts)
			{
				BlockPos ringPos = part.getRingPos(basePos, direction, orientation);
				BlockState state = level.getBlockState(ringPos);

				if(state.getBlock() instanceof AbstractStargateBlock && part.equals(state.getValue(AbstractStargateBlock.PART)) &&
						state.getValue(AbstractStargateBlock.FACING) == direction && state.getValue(AbstractStargateBlock.ORIENTATION) == orientation)
				{
					boolean waterlogged = state.getBlock() instanceof AbstractStargateRingBlock ? state.getValue(AbstractStargateRingBlock.WATERLOGGED) : false;

					level.setBlock(ringPos, waterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
				}
			}
		}
	}
}
