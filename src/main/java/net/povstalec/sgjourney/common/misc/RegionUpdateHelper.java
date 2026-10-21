package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.neoforged.neoforge.event.ModMismatchEvent;
import net.povstalec.sgjourney.StargateJourney;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RegionUpdateHelper
{
	public static final String SECTIONS = "sections";
	public static final String BLOCK_STATES = "block_states";
	public static final String PALETTE = "palette";
	
	public static final String NAME = "Name";
	public static final String PROPERTIES = "Properties";
	
	public static boolean needsUpdate = false;
	
	public static boolean isVersionBelow(ModMismatchEvent.MismatchedVersionInfo info, int majorVersion, int minorVersion, int incrementalVersion)
	{
		if(info.oldVersion().getMajorVersion() != majorVersion)
			return info.oldVersion().getMajorVersion() < majorVersion;
		
		if(info.oldVersion().getMinorVersion() != minorVersion)
			return info.oldVersion().getMinorVersion() < minorVersion;
		
		if(info.oldVersion().getIncrementalVersion() != incrementalVersion)
			return info.oldVersion().getIncrementalVersion() < incrementalVersion;
		
		return false;
	}
	
	public static void replaceOrientation(CompoundTag blockState)
	{
		/*if(blockState.getString(NAME).equals("minecraft:yellow_wool")) //TODO Name
		{
			blockState.putString(NAME, "minecraft:red_wool"); //TODO Remove
			
			CompoundTag properties = blockState.getCompound(PROPERTIES);
			//TODO Replace properties
		}*/
	}
	
	private static void readRegionTag(CompoundTag regionTag)
	{
		if(regionTag.contains(SECTIONS, Tag.TAG_LIST))
		{
			ListTag sections = regionTag.getList(SECTIONS, Tag.TAG_COMPOUND);
			for(int i = 0; i < sections.size(); ++i)
			{
				CompoundTag section = sections.getCompound(i);
				
				if(section.contains(BLOCK_STATES))
				{
					CompoundTag blockStates = section.getCompound(BLOCK_STATES);
					
					if(blockStates.contains(PALETTE, Tag.TAG_LIST))
					{
						ListTag palette = blockStates.getList(PALETTE, Tag.TAG_COMPOUND);
						for(int j = 0; j < palette.size(); ++j)
						{
							CompoundTag blockState = palette.getCompound(j);
							replaceOrientation(blockState);
						}
					}
				}
			}
		}
	}
	
	public static void updateRegionFile(RegionStorageInfo regionstorageinfo, Path regionFolderPath, Path regionFilePath) throws IOException
	{
		try(RegionFile regionFile = new RegionFile(regionstorageinfo, regionFilePath, regionFolderPath, true))
		{
			String[] fileNameSplit = regionFilePath.getFileName().toString().split("\\.");
			
			CompoundTag regionTag;
			
			if(Integer.parseInt(fileNameSplit[1]) != -1 || Integer.parseInt(fileNameSplit[2]) != -1) //TODO Remove this specific pos
				return;
			
			//ChunkPos chunkPos = new ChunkPos(Integer.parseInt(fileNameSplit[1]) >> 5, Integer.parseInt(fileNameSplit[2]) >> 5);
			
			ChunkPos chunkPos = new ChunkPos(new BlockPos(-9, -60, -2)); //TODO Remove this specific pos
			try(DataInputStream datainputstream = regionFile.getChunkDataInputStream(chunkPos))
			{
				if(datainputstream == null)
					return;
				
				regionTag = NbtIo.read(datainputstream);
				readRegionTag(NbtIo.read(datainputstream));
			}
			
			// Replace region file
			try(DataOutputStream dataoutputstream = regionFile.getChunkDataOutputStream(chunkPos))
			{
				NbtIo.write(regionTag, dataoutputstream);
			}
		}
		catch(IOException e)
		{
			StargateJourney.LOGGER.error("Could not update regions file {}", regionFilePath, e);
		}
	}
	
	public static void updateRegionFilesForDimension(LevelStorageSource.LevelStorageAccess levelStorage, Path regionFolderPath, ResourceKey<Level> dimension)
	{
		if(!Files.exists(regionFolderPath))
			return;
		
		RegionStorageInfo regionstorageinfo = new RegionStorageInfo(levelStorage.getLevelId(), dimension, "sgjourney");
		
		//System.out.println("Updating region files for " + regionFolderPath);
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(regionFolderPath))
		{
			for(Path regionFilePath : stream)
			{
				//System.out.println(regionFilePath.getFileName());
				updateRegionFile(regionstorageinfo, regionFolderPath, regionFilePath);
			}
		}
		catch(IOException e)
		{
			StargateJourney.LOGGER.error("Could not update regions at path {}", regionFolderPath, e);
		}
	}
	
	public static void updateDimensionsInNamespace(LevelStorageSource.LevelStorageAccess levelStorage, Path dimensionsNamespaceFolderPath)
	{
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(dimensionsNamespaceFolderPath))
		{
			for(Path dimension : stream)
			{
				updateRegionFilesForDimension(levelStorage, dimension.resolve("region"),
					Conversion.locationToDimension(ResourceLocation.fromNamespaceAndPath(dimensionsNamespaceFolderPath.getFileName().toString(), dimension.getFileName().toString())));
			}
		}
		catch(IOException e)
		{
			StargateJourney.LOGGER.error("Could not update regions at path {}", dimensionsNamespaceFolderPath, e);
		}
	}
	
	public static void updateDimensions(LevelStorageSource.LevelStorageAccess levelStorage, Path dimensionsFolderPath)
	{
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(dimensionsFolderPath))
		{
			for(Path namespace : stream)
			{
				updateDimensionsInNamespace(levelStorage, namespace);
			}
		}
		catch(IOException e)
		{
			StargateJourney.LOGGER.error("Could not update regions at path {}", dimensionsFolderPath, e);
		}
	}
	
	public static void updateWorldRegionFiles(LevelStorageSource.LevelStorageAccess levelStorage)
	{
		RegionUpdateHelper.updateRegionFilesForDimension(levelStorage, levelStorage.getLevelDirectory().path().resolve("region"), Level.OVERWORLD);
		RegionUpdateHelper.updateRegionFilesForDimension(levelStorage, levelStorage.getLevelDirectory().path().resolve(Paths.get("DIM-1", "region")), Level.NETHER);
		RegionUpdateHelper.updateRegionFilesForDimension(levelStorage, levelStorage.getLevelDirectory().path().resolve(Paths.get("DIM1", "region")), Level.END);
		RegionUpdateHelper.updateDimensions(levelStorage, levelStorage.getLevelDirectory().path().resolve("dimensions"));
		
		needsUpdate = false;
	}
}
