package net.povstalec.sgjourney.common.misc;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeProvider {

    private static final double MIN = 0.0D;
    private static final double MAX = 16.0D;
    private static final double MID = MAX / 2;
    private static final double HORIZONTAL_OFFSET = 1.0D;

    public final VoxelShape HORIZONTAL;

    public final VoxelShape X_FULL;
    public final VoxelShape X_BOTTOM;
    public final VoxelShape X_TOP;
    public final VoxelShape X_LEFT;
    public final VoxelShape X_RIGHT;

    public final VoxelShape Z_FULL;

    public final VoxelShape Z_BOTTOM;
    public final VoxelShape Z_TOP;
    public final VoxelShape Z_LEFT;
    public final VoxelShape Z_RIGHT;

    public final VoxelShape[][] FULL;

    public final VoxelShape[][] BOTTOM;
    public final VoxelShape[][] TOP;
    public final VoxelShape[][] LEFT;
    public final VoxelShape[][] RIGHT;

    public final VoxelShape[][] CORNER_TOP_LEFT;
    public final VoxelShape[][] CORNER_BOTTOM_LEFT;
    public final VoxelShape[][] CORNER_BOTTOM_RIGHT;
    public final VoxelShape[][] CORNER_TOP_RIGHT;

    public final VoxelShape[][] STAIR_TOP_LEFT;
    public final VoxelShape[][] STAIR_BOTTOM_LEFT;
    public final VoxelShape[][] STAIR_BOTTOM_RIGHT;
    public final VoxelShape[][] STAIR_TOP_RIGHT;

    public VoxelShapeProvider() {
        this(7.0D);
    }

    public VoxelShapeProvider(double width) {
        double horizontalMax = HORIZONTAL_OFFSET + width;
        double verticalStart = MID - (width / 2);
        double verticalEnd = MID + (width / 2);

//        VoxelShape FULL_BLOCK = Block.box(MIN, MIN, MIN, MAX, MAX, MAX);
        HORIZONTAL = Block.box(MIN, HORIZONTAL_OFFSET, MIN, MAX, horizontalMax, MAX);

        VoxelShape horizontalBottom = Block.box(MIN, HORIZONTAL_OFFSET, MIN, MAX, horizontalMax, MID);
        VoxelShape horizontalTop = Block.box(MIN, HORIZONTAL_OFFSET, MID, MAX, horizontalMax, MAX);
        VoxelShape horizontalLeft = Block.box(MIN, HORIZONTAL_OFFSET, MIN, MID, horizontalMax, MAX);
        VoxelShape horizontalRight = Block.box(MID, HORIZONTAL_OFFSET, MIN, MAX, horizontalMax, MAX);

        VoxelShape horizontalBottomLeft = Block.box(MIN, HORIZONTAL_OFFSET, MIN, MID, horizontalMax, MID);
        VoxelShape horizontalBottomRight = Block.box(MID, HORIZONTAL_OFFSET, MIN, MAX, horizontalMax, MID);
        VoxelShape horizontalTopLeft = Block.box(MIN, HORIZONTAL_OFFSET, MID, MID, horizontalMax, MAX);
        VoxelShape horizontalTopRight = Block.box(MID, HORIZONTAL_OFFSET, MID, MAX, horizontalMax, MAX);

        VoxelShape horizontalStairBottomLeft = Shapes.or(horizontalBottomRight, horizontalTopLeft, horizontalTopRight);
        VoxelShape horizontalStairBottomRight = Shapes.or(horizontalBottomLeft, horizontalTopLeft, horizontalTopRight);
        VoxelShape horizontalStairTopLeft = Shapes.or(horizontalBottomLeft, horizontalBottomRight, horizontalTopRight);
        VoxelShape horizontalStairTopRight = Shapes.or(horizontalBottomLeft, horizontalBottomRight, horizontalTopLeft);

//        VoxelShape[] HORIZONTAL_SHAPES = new VoxelShape[]{horizontalBottomRight, horizontalBottomLeft, horizontalTopRight, horizontalTopLeft};
//        VoxelShape[] HORIZONTAL_STAIR_SHAPES = new VoxelShape[]{horizontalStairTopLeft, horizontalStairTopRight, horizontalStairBottomLeft, horizontalStairBottomRight};

        X_FULL = Block.box(MIN, MIN, verticalStart, MAX, MAX, verticalEnd);
        X_BOTTOM = Block.box(MIN, MIN, verticalStart, MAX, MID, verticalEnd);
        X_TOP = Block.box(MIN, MID, verticalStart, MAX, MAX, verticalEnd);
        X_LEFT = Block.box(MID, MIN, verticalStart, MAX, MAX, verticalEnd);
        X_RIGHT = Block.box(MIN, MIN, verticalStart, MID, MAX, verticalEnd);
        VoxelShape xTopLeft = Block.box(MIN, MID, verticalStart, MID, MAX, verticalEnd);
        VoxelShape xTopRight = Block.box(MID, MID, verticalStart, MAX, MAX, verticalEnd);
        VoxelShape xBottomLeft = Block.box(MIN, MIN, verticalStart, MID, MID, verticalEnd);
        VoxelShape xBottomRight = Block.box(MID, MIN, verticalStart, MAX, MID, verticalEnd);

        VoxelShape xStairTopLeft = Shapes.or(xBottomLeft, xBottomRight, xTopRight);
        VoxelShape xStairTopRight = Shapes.or(xBottomLeft, xBottomRight, xTopLeft);
        VoxelShape xStairBottomLeft = Shapes.or(xTopLeft, xTopRight, xBottomRight);
        VoxelShape xStairBottomRight = Shapes.or(xTopLeft, xTopRight, xBottomLeft);

        Z_FULL = Block.box(verticalStart, MIN, MIN, verticalEnd, MAX, MAX);
        Z_BOTTOM = Block.box(verticalStart, MIN, MIN, verticalEnd, MID, MAX);
        Z_TOP = Block.box(verticalStart, MID, MIN, verticalEnd, MAX, MAX);
        Z_LEFT = Block.box(verticalStart, MIN, MID, verticalEnd, MAX, MAX);
        Z_RIGHT = Block.box(verticalStart, MIN, MIN, verticalEnd, MAX, MID);
        VoxelShape zTopLeft = Block.box(verticalStart, MID, MIN, verticalEnd, MAX, MID);
        VoxelShape zTopRight = Block.box(verticalStart, MID, MID, verticalEnd, MAX, MAX);
        VoxelShape zBottomLeft = Block.box(verticalStart, MIN, MIN, verticalEnd, MID, MID);
        VoxelShape zBottomRight = Block.box(verticalStart, MIN, MID, verticalEnd, MID, MAX);

        VoxelShape zStairTopLeft = Shapes.or(zBottomLeft, zBottomRight, zTopRight);
        VoxelShape zStairTopRight = Shapes.or(zBottomLeft, zBottomRight, zTopLeft);
        VoxelShape zStairBottomLeft = Shapes.or(zTopLeft, zTopRight, zBottomRight);
        VoxelShape zStairBottomRight = Shapes.or(zTopLeft, zTopRight, zBottomLeft);

        FULL = new VoxelShape[][] {{HORIZONTAL}, {X_FULL, Z_FULL}, {HORIZONTAL}};

        VoxelShape[] defaultHorizontalSlabShapes = {horizontalTop, horizontalLeft, horizontalBottom, horizontalRight};
        VoxelShape[] reverseHorizontalSlabShapes = {horizontalBottom, horizontalRight, horizontalTop, horizontalLeft};
        VoxelShape[] leftHorizontalSlabShapes = {horizontalRight, horizontalTop, horizontalLeft, horizontalBottom};
        VoxelShape[] rightHorizontalSlabShapes = {horizontalLeft, horizontalBottom, horizontalRight, horizontalTop};

        BOTTOM = new VoxelShape[][] {defaultHorizontalSlabShapes, {X_BOTTOM, Z_BOTTOM}, reverseHorizontalSlabShapes};
        TOP = new VoxelShape[][] {reverseHorizontalSlabShapes, {X_TOP, Z_TOP}, defaultHorizontalSlabShapes};
        LEFT = new VoxelShape[][] {leftHorizontalSlabShapes, {X_LEFT, Z_LEFT, X_RIGHT, Z_RIGHT}, leftHorizontalSlabShapes};
        RIGHT = new VoxelShape[][] {rightHorizontalSlabShapes, {X_RIGHT, Z_RIGHT, X_LEFT, Z_LEFT}, rightHorizontalSlabShapes};

        CORNER_TOP_LEFT = new VoxelShape[][] {{horizontalBottomLeft, horizontalBottomRight, horizontalTopRight, horizontalTopLeft}, {xTopLeft, zTopLeft, xTopRight, zTopRight}, {horizontalTopLeft, horizontalBottomLeft, horizontalBottomRight, horizontalTopRight}};
        CORNER_BOTTOM_LEFT = new VoxelShape[][] {{horizontalTopLeft, horizontalBottomLeft, horizontalBottomRight, horizontalTopRight}, {xBottomLeft, zBottomLeft, xBottomRight, zBottomRight}, {horizontalBottomLeft, horizontalBottomRight, horizontalTopRight, horizontalTopLeft}};
        CORNER_BOTTOM_RIGHT = new VoxelShape[][] {{horizontalTopRight, horizontalTopLeft, horizontalBottomLeft, horizontalBottomRight}, {xBottomRight, zBottomRight, xBottomLeft, zBottomLeft}, {horizontalBottomRight, horizontalTopRight, horizontalTopLeft, horizontalBottomLeft}};
        CORNER_TOP_RIGHT = new VoxelShape[][] {{horizontalBottomRight, horizontalTopRight, horizontalTopLeft, horizontalBottomLeft}, {xTopRight, zTopRight, xTopLeft, zTopLeft}, {horizontalTopRight, horizontalTopLeft, horizontalBottomLeft, horizontalBottomRight}};

        STAIR_TOP_LEFT = new VoxelShape[][] {{horizontalStairBottomLeft, horizontalStairBottomRight, horizontalStairTopRight, horizontalStairTopLeft}, {xStairTopLeft, zStairTopLeft, xStairTopRight, zStairTopRight}, {horizontalStairTopLeft, horizontalStairBottomLeft, horizontalStairBottomRight, horizontalStairTopRight}};
        STAIR_BOTTOM_LEFT = new VoxelShape[][] {{horizontalStairTopLeft, horizontalStairBottomLeft, horizontalStairBottomRight, horizontalStairTopRight}, {xStairBottomLeft, zStairBottomLeft, xStairBottomRight, zStairBottomRight}, {horizontalStairBottomLeft, horizontalStairBottomRight, horizontalStairTopRight, horizontalStairTopLeft}};
        STAIR_BOTTOM_RIGHT = new VoxelShape[][] {{horizontalStairTopRight, horizontalStairTopLeft, horizontalStairBottomLeft, horizontalStairBottomRight}, {xStairBottomRight, zStairBottomRight, xStairBottomLeft, zStairBottomLeft}, {horizontalStairBottomRight, horizontalStairTopRight, horizontalStairTopLeft, horizontalStairBottomLeft}};
        STAIR_TOP_RIGHT = new VoxelShape[][] {{horizontalStairBottomRight, horizontalStairTopRight, horizontalStairTopLeft, horizontalStairBottomLeft}, {xStairTopRight, zStairTopRight, xStairTopLeft, zStairTopLeft}, {horizontalStairTopRight, horizontalStairTopLeft, horizontalStairBottomLeft, horizontalStairBottomRight}};
    }

}
