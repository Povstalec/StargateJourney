package net.povstalec.sgjourney.common.misc;

import java.util.ArrayList;

import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.blockstates.Orientation;

public final class VoxelShapeProvider
{
    private static final double MIN = 0.0D;
    private static final double MAX = 16.0D;
    private static final double MID = MAX / 2;
    //private static final double HORIZONTAL_OFFSET = 1.0D;
    
    private static final double MIN_IRIS_THICKNESS = MID - 0.5D;
    private static final double MAX_IRIS_THICKNESS = MID + 0.5D;

    public static final VoxelShape X_IRIS_FULL = Block.box(MIN, MIN, MIN_IRIS_THICKNESS, MAX, MAX, MAX_IRIS_THICKNESS);

    public static final VoxelShape X_IRIS_TOP_LEFT = Block.box(MIN, MID, MIN_IRIS_THICKNESS, MID, MAX, MAX_IRIS_THICKNESS);
    public static final VoxelShape X_IRIS_TOP_RIGHT = Block.box(MID, MID, MIN_IRIS_THICKNESS, MAX, MAX, MAX_IRIS_THICKNESS);
    public static final VoxelShape X_IRIS_BOTTOM_LEFT = Block.box(MIN, MIN, MIN_IRIS_THICKNESS, MID, MID, MAX_IRIS_THICKNESS);
    public static final VoxelShape X_IRIS_BOTTOM_RIGHT = Block.box(MID, MIN,MIN_IRIS_THICKNESS, MAX, MID, MAX_IRIS_THICKNESS);

    public static final VoxelShape X_IRIS_TOP = Shapes.or(X_IRIS_TOP_LEFT, X_IRIS_TOP_RIGHT);
    public static final VoxelShape X_IRIS_LEFT = Shapes.or(X_IRIS_TOP_LEFT, X_IRIS_BOTTOM_LEFT);
    public static final VoxelShape X_IRIS_RIGHT = Shapes.or(X_IRIS_TOP_RIGHT, X_IRIS_BOTTOM_RIGHT);
    public static final VoxelShape X_IRIS_BOTTOM = Shapes.or(X_IRIS_BOTTOM_LEFT, X_IRIS_BOTTOM_RIGHT);

    public static final VoxelShape Z_IRIS_FULL = Block.box(MIN_IRIS_THICKNESS, MIN, MIN, MAX_IRIS_THICKNESS, MAX, MAX);
    
    public static final VoxelShape Z_IRIS_TOP_LEFT = Block.box(MIN_IRIS_THICKNESS, MID, MIN, MAX_IRIS_THICKNESS, MAX, MID);
    public static final VoxelShape Z_IRIS_TOP_RIGHT = Block.box(MIN_IRIS_THICKNESS, MID, MID, MAX_IRIS_THICKNESS, MAX, MAX);
    public static final VoxelShape Z_IRIS_BOTTOM_LEFT = Block.box(MIN_IRIS_THICKNESS, MIN, MIN, MAX_IRIS_THICKNESS, MID, MID);
    public static final VoxelShape Z_IRIS_BOTTOM_RIGHT = Block.box(MIN_IRIS_THICKNESS, MIN, MID, MAX_IRIS_THICKNESS, MID, MAX);

    public static final VoxelShape Z_IRIS_TOP = Shapes.or(Z_IRIS_TOP_LEFT, Z_IRIS_TOP_RIGHT);
    public static final VoxelShape Z_IRIS_LEFT = Shapes.or(Z_IRIS_TOP_LEFT, Z_IRIS_BOTTOM_LEFT);
    public static final VoxelShape Z_IRIS_RIGHT = Shapes.or(Z_IRIS_TOP_RIGHT, Z_IRIS_BOTTOM_RIGHT);
    public static final VoxelShape Z_IRIS_BOTTOM = Shapes.or(Z_IRIS_BOTTOM_LEFT, Z_IRIS_BOTTOM_RIGHT);

    public final VoxelShape HORIZONTAL_IRIS_FULL;
    
    public final VoxelShape HORIZONTAL_IRIS_TOP_LEFT;
    public final VoxelShape HORIZONTAL_IRIS_TOP_RIGHT;
    public final VoxelShape HORIZONTAL_IRIS_BOTTOM_LEFT;
    public final VoxelShape HORIZONTAL_IRIS_BOTTOM_RIGHT;
    
    public final VoxelShape HORIZONTAL_IRIS_TOP;
    public final VoxelShape HORIZONTAL_IRIS_LEFT;
    public final VoxelShape HORIZONTAL_IRIS_RIGHT;
    public final VoxelShape HORIZONTAL_IRIS_BOTTOM;
    
    public final VoxelShape[][] IRIS_FULL;

    public final VoxelShape[][] IRIS_BOTTOM;
    public final VoxelShape[][] IRIS_TOP;
    public final VoxelShape[][] IRIS_LEFT;
    public final VoxelShape[][] IRIS_RIGHT;

    public final VoxelShape[][] IRIS_CORNER_TOP_LEFT;
    public final VoxelShape[][] IRIS_CORNER_BOTTOM_LEFT;
    public final VoxelShape[][] IRIS_CORNER_BOTTOM_RIGHT;
    public final VoxelShape[][] IRIS_CORNER_TOP_RIGHT;

    public final VoxelShape[][] IRIS_STAIR_TOP_LEFT;
    public final VoxelShape[][] IRIS_STAIR_BOTTOM_LEFT;
    public final VoxelShape[][] IRIS_STAIR_BOTTOM_RIGHT;
    public final VoxelShape[][] IRIS_STAIR_TOP_RIGHT;

    public static final VoxelShape FULL_BLOCK = Block.box(MIN, MIN, MIN, MAX, MAX, MAX);
    
    public final VoxelShape HORIZONTAL_FULL;

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

    public final VoxelShape[][] STAIR_TOP_LEFT_BLOCKED;
    public final VoxelShape[][] STAIR_BOTTOM_LEFT_BLOCKED;
    public final VoxelShape[][] STAIR_BOTTOM_RIGHT_BLOCKED;
    public final VoxelShape[][] STAIR_TOP_RIGHT_BLOCKED;

    public VoxelShapeProvider(double width, double horizontalOffset)
    {
        double horizontalMax = horizontalOffset + width;
        double verticalStart = MID - (width / 2);
        double verticalEnd = MID + (width / 2);
        
        double horizontalIrisMin = horizontalOffset + (width / 2) - 0.5D;
        double horizontalIrisMax = horizontalOffset + (width / 2) + 0.5D;
        
        // Iris setup
        
        HORIZONTAL_IRIS_FULL = Block.box(MIN, horizontalIrisMin, MIN, MAX, horizontalIrisMax, MAX);

        IRIS_FULL = new VoxelShape[][] {{HORIZONTAL_IRIS_FULL}, {X_IRIS_FULL, Z_IRIS_FULL}, {HORIZONTAL_IRIS_FULL}};
        
        HORIZONTAL_IRIS_TOP_LEFT = Block.box(MIN, horizontalIrisMin, MID, MID, horizontalIrisMax, MAX);
        HORIZONTAL_IRIS_TOP_RIGHT = Block.box(MID, horizontalIrisMin, MID, MAX, horizontalIrisMax, MAX);
        HORIZONTAL_IRIS_BOTTOM_LEFT = Block.box(MIN, horizontalIrisMin, MIN, MID, horizontalIrisMax, MID);
        HORIZONTAL_IRIS_BOTTOM_RIGHT = Block.box(MID, horizontalIrisMin, MIN, MAX, horizontalIrisMax, MID);
        
        HORIZONTAL_IRIS_TOP = Shapes.or(HORIZONTAL_IRIS_TOP_LEFT, HORIZONTAL_IRIS_TOP_RIGHT);
        HORIZONTAL_IRIS_LEFT = Shapes.or(HORIZONTAL_IRIS_TOP_LEFT, HORIZONTAL_IRIS_BOTTOM_LEFT);
        HORIZONTAL_IRIS_RIGHT = Shapes.or(HORIZONTAL_IRIS_TOP_RIGHT, HORIZONTAL_IRIS_BOTTOM_RIGHT);
        HORIZONTAL_IRIS_BOTTOM = Shapes.or(HORIZONTAL_IRIS_BOTTOM_LEFT, HORIZONTAL_IRIS_BOTTOM_RIGHT);

        VoxelShape[] defaultHorizontalIrisShapes = {HORIZONTAL_IRIS_TOP, HORIZONTAL_IRIS_LEFT, HORIZONTAL_IRIS_BOTTOM, HORIZONTAL_IRIS_RIGHT};
        VoxelShape[] reverseHorizontalIrisShapes = {HORIZONTAL_IRIS_BOTTOM, HORIZONTAL_IRIS_RIGHT, HORIZONTAL_IRIS_TOP, HORIZONTAL_IRIS_LEFT};
        VoxelShape[] leftHorizontalIrisShapes = {HORIZONTAL_IRIS_LEFT, HORIZONTAL_IRIS_BOTTOM, HORIZONTAL_IRIS_RIGHT, HORIZONTAL_IRIS_TOP};
        VoxelShape[] rightHorizontalIrisShapes = {HORIZONTAL_IRIS_RIGHT, HORIZONTAL_IRIS_TOP, HORIZONTAL_IRIS_LEFT, HORIZONTAL_IRIS_BOTTOM};

        IRIS_BOTTOM = new VoxelShape[][] {defaultHorizontalIrisShapes, {X_IRIS_BOTTOM, Z_IRIS_BOTTOM}, reverseHorizontalIrisShapes};
        IRIS_TOP = new VoxelShape[][] {reverseHorizontalIrisShapes, {X_IRIS_TOP, Z_IRIS_TOP}, defaultHorizontalIrisShapes};
        IRIS_LEFT = new VoxelShape[][] {leftHorizontalIrisShapes, {X_IRIS_LEFT, Z_IRIS_LEFT, X_IRIS_RIGHT, Z_IRIS_RIGHT}, leftHorizontalIrisShapes};
        IRIS_RIGHT = new VoxelShape[][] {rightHorizontalIrisShapes, {X_IRIS_RIGHT, Z_IRIS_RIGHT, X_IRIS_LEFT, Z_IRIS_LEFT}, rightHorizontalIrisShapes};

        IRIS_CORNER_TOP_LEFT = new VoxelShape[][] {
        	{HORIZONTAL_IRIS_BOTTOM_LEFT, HORIZONTAL_IRIS_BOTTOM_RIGHT, HORIZONTAL_IRIS_TOP_RIGHT, HORIZONTAL_IRIS_TOP_LEFT},
        	{X_IRIS_TOP_LEFT, Z_IRIS_TOP_LEFT, X_IRIS_TOP_RIGHT, Z_IRIS_TOP_RIGHT},
        	{HORIZONTAL_IRIS_TOP_LEFT, HORIZONTAL_IRIS_BOTTOM_LEFT, HORIZONTAL_IRIS_BOTTOM_RIGHT, HORIZONTAL_IRIS_TOP_RIGHT}
        };
        IRIS_CORNER_BOTTOM_LEFT = new VoxelShape[][] {
        	{HORIZONTAL_IRIS_TOP_LEFT, HORIZONTAL_IRIS_BOTTOM_LEFT, HORIZONTAL_IRIS_BOTTOM_RIGHT, HORIZONTAL_IRIS_TOP_RIGHT},
        	{X_IRIS_BOTTOM_LEFT, Z_IRIS_BOTTOM_LEFT, X_IRIS_BOTTOM_RIGHT, Z_IRIS_BOTTOM_RIGHT},
        	{HORIZONTAL_IRIS_BOTTOM_LEFT, HORIZONTAL_IRIS_BOTTOM_RIGHT, HORIZONTAL_IRIS_TOP_RIGHT, HORIZONTAL_IRIS_TOP_LEFT}
        };
        IRIS_CORNER_BOTTOM_RIGHT = new VoxelShape[][] {
        	{HORIZONTAL_IRIS_TOP_RIGHT, HORIZONTAL_IRIS_TOP_LEFT, HORIZONTAL_IRIS_BOTTOM_LEFT, HORIZONTAL_IRIS_BOTTOM_RIGHT},
        	{X_IRIS_BOTTOM_RIGHT, Z_IRIS_BOTTOM_RIGHT, X_IRIS_BOTTOM_LEFT, Z_IRIS_BOTTOM_LEFT},
        	{HORIZONTAL_IRIS_BOTTOM_RIGHT, HORIZONTAL_IRIS_TOP_RIGHT, HORIZONTAL_IRIS_TOP_LEFT, HORIZONTAL_IRIS_BOTTOM_LEFT}
        };
        IRIS_CORNER_TOP_RIGHT = new VoxelShape[][] {
        	{HORIZONTAL_IRIS_BOTTOM_RIGHT, HORIZONTAL_IRIS_TOP_RIGHT, HORIZONTAL_IRIS_TOP_LEFT, HORIZONTAL_IRIS_BOTTOM_LEFT},
        	{X_IRIS_TOP_RIGHT, Z_IRIS_TOP_RIGHT, X_IRIS_TOP_LEFT, Z_IRIS_TOP_LEFT},
        	{HORIZONTAL_IRIS_TOP_RIGHT, HORIZONTAL_IRIS_TOP_LEFT, HORIZONTAL_IRIS_BOTTOM_LEFT, HORIZONTAL_IRIS_BOTTOM_RIGHT}
        };

        VoxelShape xIrisStairTopLeft = Shapes.or(X_IRIS_BOTTOM_LEFT, X_IRIS_BOTTOM_RIGHT, X_IRIS_TOP_RIGHT);
        VoxelShape xIrisStairTopRight = Shapes.or(X_IRIS_BOTTOM_LEFT, X_IRIS_BOTTOM_RIGHT, X_IRIS_TOP_LEFT);
        VoxelShape xIrisStairBottomLeft = Shapes.or(X_IRIS_TOP_LEFT, X_IRIS_TOP_RIGHT, X_IRIS_BOTTOM_RIGHT);
        VoxelShape xIrisStairBottomRight = Shapes.or(X_IRIS_TOP_LEFT, X_IRIS_TOP_RIGHT, X_IRIS_BOTTOM_LEFT);

        VoxelShape zIrisStairTopLeft = Shapes.or(Z_IRIS_BOTTOM_LEFT, Z_IRIS_BOTTOM_RIGHT, Z_IRIS_TOP_RIGHT);
        VoxelShape zIrisStairTopRight = Shapes.or(Z_IRIS_BOTTOM_LEFT, Z_IRIS_BOTTOM_RIGHT, Z_IRIS_TOP_LEFT);
        VoxelShape zIrisStairBottomLeft = Shapes.or(Z_IRIS_TOP_LEFT, Z_IRIS_TOP_RIGHT, Z_IRIS_BOTTOM_RIGHT);
        VoxelShape zIrisStairBottomRight = Shapes.or(Z_IRIS_TOP_LEFT, Z_IRIS_TOP_RIGHT, Z_IRIS_BOTTOM_LEFT);

        VoxelShape horizontalIrisStairBottomLeft = Shapes.or(HORIZONTAL_IRIS_BOTTOM_RIGHT, HORIZONTAL_IRIS_TOP_LEFT, HORIZONTAL_IRIS_TOP_RIGHT);
        VoxelShape horizontalIrisStairBottomRight = Shapes.or(HORIZONTAL_IRIS_BOTTOM_LEFT, HORIZONTAL_IRIS_TOP_LEFT, HORIZONTAL_IRIS_TOP_RIGHT);
        VoxelShape horizontalIrisStairTopLeft = Shapes.or(HORIZONTAL_IRIS_BOTTOM_LEFT, HORIZONTAL_IRIS_BOTTOM_RIGHT, HORIZONTAL_IRIS_TOP_RIGHT);
        VoxelShape horizontalIrisStairTopRight = Shapes.or(HORIZONTAL_IRIS_BOTTOM_LEFT, HORIZONTAL_IRIS_BOTTOM_RIGHT, HORIZONTAL_IRIS_TOP_LEFT);

        IRIS_STAIR_TOP_LEFT = new VoxelShape[][] {
        	{horizontalIrisStairBottomLeft, horizontalIrisStairBottomRight, horizontalIrisStairTopRight, horizontalIrisStairTopLeft},
        	{xIrisStairTopLeft, zIrisStairTopLeft, xIrisStairTopRight, zIrisStairTopRight},
        	{horizontalIrisStairTopLeft, horizontalIrisStairBottomLeft, horizontalIrisStairBottomRight, horizontalIrisStairTopRight}
        };
        IRIS_STAIR_BOTTOM_LEFT = new VoxelShape[][] {
        	{horizontalIrisStairTopLeft, horizontalIrisStairBottomLeft, horizontalIrisStairBottomRight, horizontalIrisStairTopRight},
        	{xIrisStairBottomLeft, zIrisStairBottomLeft, xIrisStairBottomRight, zIrisStairBottomRight},
        	{horizontalIrisStairBottomLeft, horizontalIrisStairBottomRight, horizontalIrisStairTopRight, horizontalIrisStairTopLeft}
        };
        IRIS_STAIR_BOTTOM_RIGHT = new VoxelShape[][] {
        	{horizontalIrisStairTopRight, horizontalIrisStairTopLeft, horizontalIrisStairBottomLeft, horizontalIrisStairBottomRight},
        	{xIrisStairBottomRight, zIrisStairBottomRight, xIrisStairBottomLeft, zIrisStairBottomLeft},
        	{horizontalIrisStairBottomRight, horizontalIrisStairTopRight, horizontalIrisStairTopLeft, horizontalIrisStairBottomLeft}
        };
        IRIS_STAIR_TOP_RIGHT = new VoxelShape[][] {
        	{horizontalIrisStairBottomRight, horizontalIrisStairTopRight, horizontalIrisStairTopLeft, horizontalIrisStairBottomLeft},
        	{xIrisStairTopRight, zIrisStairTopRight, xIrisStairTopLeft, zIrisStairTopLeft},
        	{horizontalIrisStairTopRight, horizontalIrisStairTopLeft, horizontalIrisStairBottomLeft, horizontalIrisStairBottomRight}
        };
        
        
        // Stargate setup

        HORIZONTAL_FULL = Block.box(MIN, horizontalOffset, MIN, MAX, horizontalMax, MAX);

        VoxelShape horizontalBottom = Block.box(MIN, horizontalOffset, MIN, MAX, horizontalMax, MID);
        VoxelShape horizontalTop = Block.box(MIN, horizontalOffset, MID, MAX, horizontalMax, MAX);
        VoxelShape horizontalLeft = Block.box(MIN, horizontalOffset, MIN, MID, horizontalMax, MAX);
        VoxelShape horizontalRight = Block.box(MID, horizontalOffset, MIN, MAX, horizontalMax, MAX);

        VoxelShape horizontalBottomLeft = Block.box(MIN, horizontalOffset, MIN, MID, horizontalMax, MID);
        VoxelShape horizontalBottomRight = Block.box(MID, horizontalOffset, MIN, MAX, horizontalMax, MID);
        VoxelShape horizontalTopLeft = Block.box(MIN, horizontalOffset, MID, MID, horizontalMax, MAX);
        VoxelShape horizontalTopRight = Block.box(MID, horizontalOffset, MID, MAX, horizontalMax, MAX);

        VoxelShape horizontalStairBottomLeft = Shapes.or(horizontalBottomRight, horizontalTopLeft, horizontalTopRight);
        VoxelShape horizontalStairBottomRight = Shapes.or(horizontalBottomLeft, horizontalTopLeft, horizontalTopRight);
        VoxelShape horizontalStairTopLeft = Shapes.or(horizontalBottomLeft, horizontalBottomRight, horizontalTopRight);
        VoxelShape horizontalStairTopRight = Shapes.or(horizontalBottomLeft, horizontalBottomRight, horizontalTopLeft);

        VoxelShape horizontalStairBottomLeftBlocked = Shapes.or(horizontalBottomRight, horizontalTopLeft, horizontalTopRight, HORIZONTAL_IRIS_BOTTOM_LEFT);
        VoxelShape horizontalStairBottomRightBlocked = Shapes.or(horizontalBottomLeft, horizontalTopLeft, horizontalTopRight, HORIZONTAL_IRIS_BOTTOM_RIGHT);
        VoxelShape horizontalStairTopLeftBlocked = Shapes.or(horizontalBottomLeft, horizontalBottomRight, horizontalTopRight, HORIZONTAL_IRIS_TOP_LEFT);
        VoxelShape horizontalStairTopRightBlocked = Shapes.or(horizontalBottomLeft, horizontalBottomRight, horizontalTopLeft, HORIZONTAL_IRIS_TOP_RIGHT);

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

        VoxelShape xStairTopLeftBlocked = Shapes.or(xBottomLeft, xBottomRight, xTopRight, X_IRIS_TOP_LEFT);
        VoxelShape xStairTopRightBlocked = Shapes.or(xBottomLeft, xBottomRight, xTopLeft, X_IRIS_TOP_RIGHT);
        VoxelShape xStairBottomLeftBlocked = Shapes.or(xTopLeft, xTopRight, xBottomRight, X_IRIS_BOTTOM_LEFT);
        VoxelShape xStairBottomRightBlocked = Shapes.or(xTopLeft, xTopRight, xBottomLeft, X_IRIS_BOTTOM_RIGHT);

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

        VoxelShape zStairTopLeftBlocked = Shapes.or(zBottomLeft, zBottomRight, zTopRight, Z_IRIS_TOP_LEFT);
        VoxelShape zStairTopRightBlocked = Shapes.or(zBottomLeft, zBottomRight, zTopLeft, Z_IRIS_TOP_RIGHT);
        VoxelShape zStairBottomLeftBlocked = Shapes.or(zTopLeft, zTopRight, zBottomRight, Z_IRIS_BOTTOM_LEFT);
        VoxelShape zStairBottomRightBlocked = Shapes.or(zTopLeft, zTopRight, zBottomLeft, Z_IRIS_BOTTOM_RIGHT);

        FULL = new VoxelShape[][] {{HORIZONTAL_FULL}, {X_FULL, Z_FULL}, {HORIZONTAL_FULL}};

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

        STAIR_TOP_LEFT_BLOCKED = new VoxelShape[][] {{horizontalStairBottomLeftBlocked, horizontalStairBottomRightBlocked, horizontalStairTopRightBlocked, horizontalStairTopLeftBlocked}, {xStairTopLeftBlocked, zStairTopLeftBlocked, xStairTopRightBlocked, zStairTopRightBlocked}, {horizontalStairTopLeftBlocked, horizontalStairBottomLeftBlocked, horizontalStairBottomRightBlocked, horizontalStairTopRightBlocked}};
        STAIR_BOTTOM_LEFT_BLOCKED = new VoxelShape[][] {{horizontalStairTopLeftBlocked, horizontalStairBottomLeftBlocked, horizontalStairBottomRightBlocked, horizontalStairTopRightBlocked}, {xStairBottomLeftBlocked, zStairBottomLeftBlocked, xStairBottomRightBlocked, zStairBottomRightBlocked}, {horizontalStairBottomLeftBlocked, horizontalStairBottomRightBlocked, horizontalStairTopRightBlocked, horizontalStairTopLeftBlocked}};
        STAIR_BOTTOM_RIGHT_BLOCKED = new VoxelShape[][] {{horizontalStairTopRightBlocked, horizontalStairTopLeftBlocked, horizontalStairBottomLeftBlocked, horizontalStairBottomRightBlocked}, {xStairBottomRightBlocked, zStairBottomRightBlocked, xStairBottomLeftBlocked, zStairBottomLeftBlocked}, {horizontalStairBottomRightBlocked, horizontalStairTopRightBlocked, horizontalStairTopLeftBlocked, horizontalStairBottomLeftBlocked}};
        STAIR_TOP_RIGHT_BLOCKED = new VoxelShape[][] {{horizontalStairBottomRightBlocked, horizontalStairTopRightBlocked, horizontalStairTopLeftBlocked, horizontalStairBottomLeftBlocked}, {xStairTopRightBlocked, zStairTopRightBlocked, xStairTopLeftBlocked, zStairTopLeftBlocked}, {horizontalStairTopRightBlocked, horizontalStairTopLeftBlocked, horizontalStairBottomLeftBlocked, horizontalStairBottomRightBlocked}};
    }

	public static VoxelShape getShapeFromArray(VoxelShape[][] shapes, Direction direction, Orientation orientation)
	{
		int horizontal = direction.get2DDataValue();
		int vertical = orientation.get2DDataValue();
		
		return shapes[vertical][horizontal % shapes[vertical].length];
	}
	
	public static VoxelShape getOrientedShape(Vector3d minVec, Vector3d maxVec, Matrix3d xMatrix, Matrix3d yMatrix)
	{
		minVec.x -= 8;
		minVec.y -= 8;
		minVec.z -= 8;

		maxVec.x -= 8;
		maxVec.y -= 8;
		maxVec.z -= 8;
		
		xMatrix.transform(minVec);
		xMatrix.transform(maxVec);

		yMatrix.transform(minVec);
		yMatrix.transform(maxVec);

		minVec.x += 8;
		minVec.y += 8;
		minVec.z += 8;

		maxVec.x += 8;
		maxVec.y += 8;
		maxVec.z += 8;
		
		double minX, minY, minZ, maxX, maxY, maxZ;
		
		if(minVec.x <= maxVec.x)
		{
			minX = minVec.x;
			maxX = maxVec.x;
		}
		else
		{
			minX = maxVec.x;
			maxX = minVec.x;
		}
		
		if(minVec.y <= maxVec.y)
		{
			minY = minVec.y;
			maxY = maxVec.y;
		}
		else
		{
			minY = maxVec.y;
			maxY = minVec.y;
		}
		
		if(minVec.z <= maxVec.z)
		{
			minZ = minVec.z;
			maxZ = maxVec.z;
		}
		else
		{
			minZ = maxVec.z;
			maxZ = minVec.z;
		}
		
		return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public static Matrix3d yRotationFromDirection(Direction direction)
	{
		return switch(direction)
		{
		case NORTH -> new Matrix3d();
		case EAST -> new Matrix3d().rotate(Vector3f.YP.rotationDegrees(270));
		case SOUTH -> new Matrix3d().rotate(Vector3f.YP.rotationDegrees(180));
		case WEST -> new Matrix3d().rotate(Vector3f.YP.rotationDegrees(90));
		
		default -> new Matrix3d();
		};
	}
	
	public static Tuple<Matrix3d, Matrix3d> xyRotationFromOrientation(FrontAndTop orientation)
	{
		return switch(orientation)
		{
		case NORTH_UP -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d(), new Matrix3d());
		case EAST_UP -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d(), new Matrix3d().rotate(Vector3f.YP.rotationDegrees(270)));
		case SOUTH_UP -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d(), new Matrix3d().rotate(Vector3f.YP.rotationDegrees(180)));
		case WEST_UP -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d(), new Matrix3d().rotate(Vector3f.YP.rotationDegrees(90)));

		case UP_NORTH -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d().rotate(Vector3f.XP.rotationDegrees(90)), new Matrix3d());
		case UP_EAST -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d().rotate(Vector3f.XP.rotationDegrees(90)), new Matrix3d().rotate(Vector3f.YP.rotationDegrees(270)));
		case UP_SOUTH -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d().rotate(Vector3f.XP.rotationDegrees(90)), new Matrix3d().rotate(Vector3f.YP.rotationDegrees(180)));
		case UP_WEST -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d().rotate(Vector3f.XP.rotationDegrees(90)), new Matrix3d().rotate(Vector3f.YP.rotationDegrees(90)));

		case DOWN_NORTH -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d().rotate(Vector3f.XP.rotationDegrees(-90)), new Matrix3d());
		case DOWN_EAST -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d().rotate(Vector3f.XP.rotationDegrees(-90)), new Matrix3d().rotate(Vector3f.YP.rotationDegrees(270)));
		case DOWN_SOUTH -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d().rotate(Vector3f.XP.rotationDegrees(-90)), new Matrix3d().rotate(Vector3f.YP.rotationDegrees(180)));
		case DOWN_WEST -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d().rotate(Vector3f.XP.rotationDegrees(-90)), new Matrix3d().rotate(Vector3f.YP.rotationDegrees(90)));
		
		default -> new Tuple<Matrix3d, Matrix3d>(new Matrix3d(), new Matrix3d());
		};
	}
	
	public static VoxelShape getOrientedShapes(ArrayList<Tuple<Vector3d, Vector3d>> minMax, FrontAndTop orientation)
	{
		if(minMax.size() == 0)
			return Shapes.empty();
		
		Tuple<Matrix3d, Matrix3d> xyRotation = xyRotationFromOrientation(orientation);
		
		Vector3d vecA = minMax.get(0).getA();
		Vector3d vecB = minMax.get(0).getB();
		
		VoxelShape firstShape = getOrientedShape(new Vector3d(vecA.x, vecA.y, vecA.z), new Vector3d(vecB.x, vecB.y, vecB.z), xyRotation.getA(), xyRotation.getB());
		
		if(minMax.size() > 1)
		{
			VoxelShape[] shapes = new VoxelShape[minMax.size() - 1];
			
			for(int i = 1; i < minMax.size(); i++)
			{
				vecA = minMax.get(i).getA();
				vecB = minMax.get(i).getB();
				
				shapes[i - 1] = getOrientedShape(new Vector3d(vecA.x, vecA.y, vecA.z), new Vector3d(vecB.x, vecB.y, vecB.z), xyRotation.getA(), xyRotation.getB());
			}
			
			return Shapes.or(firstShape, shapes);
		}
		
		return firstShape;
	}
	
	public static VoxelShape getDirectionalShapes(ArrayList<Tuple<Vector3d, Vector3d>> minMax, Direction direction)
	{
		if(minMax.size() == 0)
			return Shapes.empty();
		
		Matrix3d yRotation = yRotationFromDirection(direction);
		Matrix3d noRotation = new Matrix3d();
		
		Vector3d vecA = minMax.get(0).getA();
		Vector3d vecB = minMax.get(0).getB();
		
		VoxelShape firstShape = getOrientedShape(new Vector3d(vecA.x, vecA.y, vecA.z), new Vector3d(vecB.x, vecB.y, vecB.z), noRotation, yRotation);
		
		if(minMax.size() > 1)
		{
			VoxelShape[] shapes = new VoxelShape[minMax.size() - 1];
			
			for(int i = 1; i < minMax.size(); i++)
			{
				vecA = minMax.get(i).getA();
				vecB = minMax.get(i).getB();
				
				shapes[i - 1] = getOrientedShape(new Vector3d(vecA.x, vecA.y, vecA.z), new Vector3d(vecB.x, vecB.y, vecB.z), noRotation, yRotation);
			}
			
			return Shapes.or(firstShape, shapes);
		}
		
		return firstShape;
	}
}
