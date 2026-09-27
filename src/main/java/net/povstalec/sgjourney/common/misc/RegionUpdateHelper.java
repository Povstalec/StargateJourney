package net.povstalec.sgjourney.common.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.povstalec.sgjourney.StargateJourney;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class RegionUpdateHelper
{
	/*public static boolean updateRegionFiles(Path regionFolderPath)
	{
		boolean resolved = true;
		
		System.out.println("Updating region files for " + regionFolderPath);
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(regionFolderPath))
		{
			for(Path file : stream)
			{
				System.out.println(file.getFileName());
				CompoundTag tag = NbtIo.readCompressed(file, NbtAccounter.create(104857600L));
			}
		}
		catch(IOException e)
		{
			StargateJourney.LOGGER.error("Could not update regions at path {}", regionFolderPath, e);
			resolved = false;
		}
		
		return resolved;
	}
	
	public static boolean updateDimensions(Path dimensionsFolderPath)
	{
		boolean resolved = true;
		
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(dimensionsFolderPath))
		{
			for(Path dimension : stream)
			{
				resolved &= updateRegionFiles(dimension.resolve("region"));
			}
		}
		catch(IOException e)
		{
			StargateJourney.LOGGER.error("Could not update regions at path {}", dimensionsFolderPath, e);
			resolved = false;
		}
		
		return resolved;
	}*/
}
