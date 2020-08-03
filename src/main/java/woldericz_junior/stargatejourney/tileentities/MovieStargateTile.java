package woldericz_junior.stargatejourney.tileentities;

import static init.StargateBlocks.MOVIESTARGATE_TILE;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class MovieStargateTile extends TileEntity implements ITickableTileEntity
{
	public MovieStargateTile() 
	{
		super(MOVIESTARGATE_TILE);
	}

	@Override
	public void tick() 
	{
		
	}
}
