package net.povstalec.sgjourney.common.misc;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelDimensionProvider {

    private static final double MIN = 0.0D;
    private static final double MAX = 16.0D;
    private static final double MID = MAX / 2;
    private static final double HORIZONTAL_OFFSET = 1.0D;

    public final VoxelShape HORIZONTAL;

    public final VoxelShape X;

    public final VoxelShape Z;

    public final VoxelShape[][] DEFAULT;

    public final VoxelShape[][] TOP_LEFT;
    public final VoxelShape[][] BOTTOM_LEFT;
    public final VoxelShape[][] BOTTOM_RIGHT;
    public final VoxelShape[][] TOP_RIGHT;

    public final VoxelShape[][] STAIR_TOP_LEFT;
    public final VoxelShape[][] STAIR_BOTTOM_LEFT;
    public final VoxelShape[][] STAIR_BOTTOM_RIGHT;
    public final VoxelShape[][] STAIR_TOP_RIGHT;

    public VoxelDimensionProvider() {
        double width = 7.0D;

        double horizontalTop = HORIZONTAL_OFFSET + width;
        double verticalStart = MID - (width / 2);
        double verticalEnd = MID + (width / 2);

//        VoxelShape FULL_BLOCK = Block.box(MIN, MIN, MIN, MAX, MAX, MAX);
        HORIZONTAL = Block.box(MIN, HORIZONTAL_OFFSET, MIN, MAX, horizontalTop, MAX);
        VoxelShape horizontalBottomLeft = Block.box(MIN, HORIZONTAL_OFFSET, MIN, horizontalTop, horizontalTop, horizontalTop);
        VoxelShape horizontalBottomRight = Block.box(horizontalTop, HORIZONTAL_OFFSET, MIN, MAX, horizontalTop, horizontalTop);
        VoxelShape horizontalTopLeft = Block.box(MIN, HORIZONTAL_OFFSET, horizontalTop, horizontalTop, horizontalTop, MAX);
        VoxelShape horizontalTopRight = Block.box(horizontalTop, HORIZONTAL_OFFSET, horizontalTop, MAX, horizontalTop, MAX);

        VoxelShape horizontalStairBottomLeft = Shapes.or(horizontalBottomRight, horizontalTopLeft, horizontalTopRight);
        VoxelShape horizontalStairBottomRight = Shapes.or(horizontalBottomLeft, horizontalTopLeft, horizontalTopRight);
        VoxelShape horizontalStairTopLeft = Shapes.or(horizontalBottomLeft, horizontalBottomRight, horizontalTopRight);
        VoxelShape horizontalStairTopRight = Shapes.or(horizontalBottomLeft, horizontalBottomRight, horizontalTopLeft);

//        VoxelShape[] HORIZONTAL_SHAPES = new VoxelShape[]{horizontalBottomRight, horizontalBottomLeft, horizontalTopRight, horizontalTopLeft};
//        VoxelShape[] HORIZONTAL_STAIR_SHAPES = new VoxelShape[]{horizontalStairTopLeft, horizontalStairTopRight, horizontalStairBottomLeft, horizontalStairBottomRight};

        X = Block.box(MIN, MIN, verticalStart, MAX, MAX, verticalEnd);
        VoxelShape xTopLeft = Block.box(MIN, MID, verticalStart, MID, MAX, verticalEnd);
        VoxelShape xTopRight = Block.box(MID, MID, verticalStart, MAX, MAX, verticalEnd);
        VoxelShape xBottomLeft = Block.box(MIN, MIN, verticalStart, MID, MID, verticalEnd);
        VoxelShape xBottomRight = Block.box(MID, MIN, verticalStart, MAX, MID, verticalEnd);

        VoxelShape xStairTopLeft = Shapes.or(xBottomLeft, xBottomRight, xTopRight);
        VoxelShape xStairTopRight = Shapes.or(xBottomLeft, xBottomRight, xTopLeft);
        VoxelShape xStairBottomLeft = Shapes.or(xTopLeft, xTopRight, xBottomRight);
        VoxelShape xStairBottomRight = Shapes.or(xTopLeft, xTopRight, xBottomLeft);

        Z = Block.box(verticalStart, MIN, MIN, verticalEnd, MAX, MAX);
        VoxelShape zTopLeft = Block.box(verticalStart, MID, MIN, verticalEnd, MAX, MID);
        VoxelShape zTopRight = Block.box(verticalStart, MID, MID, verticalEnd, MAX, MAX);
        VoxelShape zBottomLeft = Block.box(verticalStart, MIN, MIN, verticalEnd, MID, MID);
        VoxelShape zBottomRight = Block.box(verticalStart, MIN, MID, verticalEnd, MID, MAX);

        VoxelShape zStairTopLeft = Shapes.or(zBottomLeft, zBottomRight, zTopRight);
        VoxelShape zStairTopRight = Shapes.or(zBottomLeft, zBottomRight, zTopLeft);
        VoxelShape zStairBottomLeft = Shapes.or(zTopLeft, zTopRight, zBottomRight);
        VoxelShape zStairBottomRight = Shapes.or(zTopLeft, zTopRight, zBottomLeft);

        DEFAULT = new VoxelShape[][] {{HORIZONTAL}, {X, Z}, {HORIZONTAL}};

        TOP_LEFT = new VoxelShape[][] {{horizontalBottomLeft, horizontalBottomRight, horizontalTopRight, horizontalTopLeft}, {xTopLeft, zTopLeft, xTopRight, zTopRight}, {horizontalTopLeft, horizontalBottomLeft, horizontalBottomRight, horizontalTopRight}};
        BOTTOM_LEFT = new VoxelShape[][] {{horizontalTopLeft, horizontalBottomLeft, horizontalBottomRight, horizontalTopRight}, {xBottomLeft, zBottomLeft, xBottomRight, zBottomRight}, {horizontalBottomLeft, horizontalBottomRight, horizontalTopRight, horizontalTopLeft}};
        BOTTOM_RIGHT = new VoxelShape[][] {{horizontalTopRight, horizontalTopLeft, horizontalBottomLeft, horizontalBottomRight}, {xBottomRight, zBottomRight, xBottomLeft, zBottomLeft}, {horizontalBottomRight, horizontalTopRight, horizontalTopLeft, horizontalBottomLeft}};
        TOP_RIGHT = new VoxelShape[][] {{horizontalBottomRight, horizontalTopRight, horizontalTopLeft, horizontalBottomLeft}, {xTopRight, zTopRight, xTopLeft, zTopLeft}, {horizontalTopRight, horizontalTopLeft, horizontalBottomLeft, horizontalBottomRight}};

        STAIR_TOP_LEFT = new VoxelShape[][] {{horizontalStairBottomLeft, horizontalStairBottomRight, horizontalStairTopRight, horizontalStairTopLeft}, {xStairTopLeft, zStairTopLeft, xStairTopRight, zStairTopRight}, {horizontalStairTopLeft, horizontalStairBottomLeft, horizontalStairBottomRight, horizontalStairTopRight}};
        STAIR_BOTTOM_LEFT = new VoxelShape[][] {{horizontalStairTopLeft, horizontalStairBottomLeft, horizontalStairBottomRight, horizontalStairTopRight}, {xStairBottomLeft, zStairBottomLeft, xStairBottomRight, zStairBottomRight}, {horizontalStairBottomLeft, horizontalStairBottomRight, horizontalStairTopRight, horizontalStairTopLeft}};
        STAIR_BOTTOM_RIGHT = new VoxelShape[][] {{horizontalStairTopRight, horizontalStairTopLeft, horizontalStairBottomLeft, horizontalStairBottomRight}, {xStairBottomRight, zStairBottomRight, xStairBottomLeft, zStairBottomLeft}, {horizontalStairBottomRight, horizontalStairTopRight, horizontalStairTopLeft, horizontalStairBottomLeft}};
        STAIR_TOP_RIGHT = new VoxelShape[][] {{horizontalStairBottomRight, horizontalStairTopRight, horizontalStairTopLeft, horizontalStairBottomLeft}, {xStairTopRight, zStairTopRight, xStairTopLeft, zStairTopLeft}, {horizontalStairTopRight, horizontalStairTopLeft, horizontalStairBottomLeft, horizontalStairBottomRight}};
    }

}
