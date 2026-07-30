package net.povstalec.sgjourney.common.items;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.world.UniqueStructurePlacement;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Schrödinger's Map Item works sort of like {@link net.minecraft.world.item.EmptyMapItem},
 * except it may fail to find its target (due to mod incompatibilities or something),
 * at which point it will announce its failure to the user
 */
public class SchrodingersMapItem extends Item
{
	public static final String TARGET_STRUCTURE = "target_structure";
	public static final String DECORATION_TYPE = "decoration_type";
	public static final String DIMENSION = "dimension";
	public static final String SKIP_LOADED_CHUNKS = "skip_loaded_chunks";
	
	public SchrodingersMapItem(Properties properties)
	{
		super(properties);
	}
	
	@Nullable
	public static TagKey<Structure> getTargetStructure(ItemStack stack)
	{
		if(stack.hasTag() && stack.getTag().contains(TARGET_STRUCTURE, Tag.TAG_STRING))
			return TagInit.Structures.createTag(stack.getTag().getString(TARGET_STRUCTURE));
		
		return null;
	}
	
	public static MapDecoration.Type getDecorationType(ItemStack stack)
	{
		if(stack.hasTag() && stack.getTag().contains(DECORATION_TYPE, Tag.TAG_BYTE))
			return MapDecoration.Type.byIcon(stack.getTag().getByte(DECORATION_TYPE));
		
		return MapDecoration.Type.RED_X;
	}
	
	@Nullable
	public static ResourceKey<Level> getDimension(ItemStack stack)
	{
		if(stack.hasTag() && stack.getTag().contains(DIMENSION, Tag.TAG_STRING))
			return Conversion.stringToDimension(stack.getTag().getString(DIMENSION));
		
		return null;
	}
	
	public static boolean shouldSkipLoadedChunks(ItemStack stack)
	{
		return stack.hasTag() && stack.getTag().getBoolean(SKIP_LOADED_CHUNKS);
	}
	
	public static BlockPos getSearchStartPos(TagKey<Structure> target, BlockPos defaultSearchStartPos)
	{
		if(TagInit.Structures.STARGATE_MAP.equals(target))
		{
			int xOffset = 16 * CommonGenerationConfig.stargate_generation_center_x_chunk_offset.get();
			int zOffset = 16 * CommonGenerationConfig.stargate_generation_center_z_chunk_offset.get();
			
			return new BlockPos(xOffset, 0, zOffset);
		}
		
		return defaultSearchStartPos;
	}
	
	@Nullable
	public static ItemStack tryCreateMapItem(ServerLevel level, @Nullable Player player, ItemStack schrodingersMapStack, BlockPos defaultSearchStartPos, MapDecoration.Type mapDecorationType, boolean skipLoadedChunks)
	{
		TagKey<Structure> target = getTargetStructure(schrodingersMapStack);
		if(target == null) // No target structure found, create a normal map
			return MapItem.create(level, defaultSearchStartPos.getX(), defaultSearchStartPos.getZ(), (byte) 0, true, false);
		
		BlockPos searchStartPos = getSearchStartPos(target, defaultSearchStartPos);
		
		BlockPos blockpos = level.findNearestMapStructure(target, searchStartPos, 150, skipLoadedChunks);
		if(blockpos == null)
		{
			StargateJourney.LOGGER.error("Couldn't locate {}", target);
			BlockPos expectedPos = findOriginalSpawnPosition(level, target);
			
			if(player != null)
			{
				if(expectedPos == null) // Could not find Structure, presumably has nothing to do with UniqueStructurePlacement issues, so it gets a generic message
					player.displayClientMessage(Component.translatable("message.sgjourney.schrodingers_map.error.structure").withStyle(ChatFormatting.DARK_RED), true);
				else
				{
					player.displayClientMessage(Component.translatable("message.sgjourney.schrodingers_map.error.unique_structure").withStyle(ChatFormatting.DARK_RED), true);
					
					Component component = ComponentUtils.wrapInSquareBrackets(Component.translatable("message.sgjourney.schrodingers_map.error.troubleshoot")).withStyle((style) ->
						style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://povstalec.github.io/StargateJourney/troubleshooting/"))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("message.sgjourney.open_wiki_link")))
							.applyFormat(ChatFormatting.WHITE));
					
					Component coordComponent = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", expectedPos.getX(), "~", expectedPos.getZ())).withStyle((style) ->
						style.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + expectedPos.getX() + "  ~  " + expectedPos.getZ()))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip"))));
					
					player.sendSystemMessage(Component.translatable("message.sgjourney.schrodingers_map.error.unique_structure_not_found", coordComponent)
						.withStyle(ChatFormatting.DARK_RED)
						.append(" ").append(component));
				}
			}
			
			return null;
		}
		StargateJourney.LOGGER.error("Successfully located {}", target);
		
		ItemStack newMapStack = MapItem.create(level, blockpos.getX(), blockpos.getZ(), (byte) 2, true, true);
		MapItem.renderBiomePreviewMap(level, newMapStack);
		MapItemSavedData.addTargetDecoration(newMapStack, blockpos, "+", mapDecorationType);
		
		if(schrodingersMapStack.hasCustomHoverName())
			newMapStack.setHoverName(schrodingersMapStack.getHoverName());
		
		return newMapStack;
	}
	
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand)
	{
		ItemStack itemStack = player.getItemInHand(interactionHand);
		if(level.isClientSide())
			return InteractionResultHolder.success(itemStack);
		else
		{
			ResourceKey<Level> expectedDimension = getDimension(itemStack);
			// Check if it's being created in the correct dimension
			if(expectedDimension != null && !level.dimension().equals(expectedDimension))
			{
				player.displayClientMessage(Component.translatable("message.sgjourney.schrodingers_map.error.dimension", expectedDimension.location()).withStyle(ChatFormatting.DARK_RED), true);
				return InteractionResultHolder.pass(itemStack);
			}
			
			ItemStack newMapStack = tryCreateMapItem((ServerLevel) level, player, itemStack, player.blockPosition(), getDecorationType(itemStack), shouldSkipLoadedChunks(itemStack));
			
			// Everything worked out
			if(newMapStack != null)
			{
				if(!player.getAbilities().instabuild)
					itemStack.shrink(1);
				
				player.awardStat(Stats.ITEM_USED.get(this));
				player.level.playSound(null, player, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, player.getSoundSource(), 1.0F, 1.0F);
				
				if(itemStack.isEmpty())
					return InteractionResultHolder.consume(newMapStack);
				else
				{
					if(!player.getInventory().add(newMapStack.copy()))
						player.drop(newMapStack, false);
					
					return InteractionResultHolder.consume(itemStack);
				}
			}
			// Something went wrong
			else
			{
				// Cooldown so players don't abuse this to lag servers
				player.getCooldowns().addCooldown(this, 100);
				return InteractionResultHolder.pass(itemStack);
			}
		}
	}
	
	public static BlockPos findOriginalSpawnPosition(ServerLevel level, TagKey<Structure> target)
	{
		if(!level.getServer().getWorldData().worldGenOptions().generateStructures())
			StargateJourney.LOGGER.error("Structure generation is disabled for this world");
		else
		{
			Optional<HolderSet.Named<Structure>> structuresHolder = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(target);
			if(structuresHolder.isEmpty())
				StargateJourney.LOGGER.error("Structure tag {} did not provide any valid Structures", target);
			else
			{
				ChunkGeneratorStructureState chunkgeneratorstructurestate = level.getChunkSource().getGeneratorState();
				Map<StructurePlacement, Set<Holder<Structure>>> placementStructureMap = new Object2ObjectArrayMap<>();
				
				for(Holder<Structure> holder : structuresHolder.get())
				{
					for(StructurePlacement structureplacement : chunkgeneratorstructurestate.getPlacementsForStructure(holder))
					{
						placementStructureMap.computeIfAbsent(structureplacement, (placement) -> new ObjectArraySet<>()).add(holder);
					}
				}
				
				if(placementStructureMap.isEmpty())
					StargateJourney.LOGGER.error("No placements found for Structures in {}", target);
				else
				{
					long levelSeed = chunkgeneratorstructurestate.getLevelSeed();
					
					for(Map.Entry<StructurePlacement, Set<Holder<Structure>>> placementEntry : placementStructureMap.entrySet())
					{
						if(placementEntry.getKey() instanceof UniqueStructurePlacement uniqueStructurePlacement)
						{
							int chunkX = uniqueStructurePlacement.getChunkX(chunkgeneratorstructurestate.getLevelSeed());
							int chunkZ = uniqueStructurePlacement.getChunkZ(chunkgeneratorstructurestate.getLevelSeed());
							
							StargateJourney.LOGGER.error("Structure was meant to generate at X={}, Z={} in Dimension {} on seed {}",
								chunkX * 16, chunkZ * 16, level.dimension().location(), levelSeed);
							
							return new BlockPos(chunkX * 16, 0, chunkZ * 16);
						}
					}
					
					StargateJourney.LOGGER.error("No Structure from {} uses a UniqueStructurePlacement", target);
				}
			}
		}
		
		return null;
	}
	
	public static ItemStack withDestination(Component name, @NotNull TagKey<Structure> target, @Nullable MapDecoration.Type mapDecorationType, @Nullable ResourceKey<Level> dimension, boolean skipLoadedChunks)
	{
		ItemStack stack = new ItemStack(ItemInit.SCHRODINGERS_MAP.get());
		stack.setHoverName(name);
		stack.getOrCreateTag().putString(TARGET_STRUCTURE, target.location().toString());
		if(mapDecorationType != null)
			stack.getOrCreateTag().putByte(DECORATION_TYPE, mapDecorationType.getIcon());
		if(dimension != null)
			stack.getOrCreateTag().putString(DIMENSION, dimension.location().toString());
		stack.getOrCreateTag().putBoolean(SKIP_LOADED_CHUNKS, skipLoadedChunks);
		return stack;
	}
}
