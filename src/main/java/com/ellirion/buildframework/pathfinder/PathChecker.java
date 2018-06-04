package com.ellirion.buildframework.pathfinder;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.Tuple;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathfinder.model.Direction;
import com.ellirion.buildframework.pathfinder.model.DirectionChange;
import com.ellirion.buildframework.pathfinder.model.PathingVertex;
import com.ellirion.buildframework.util.WorldHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PathChecker {

    private World world;
    private Map<Point, PointInfo> points;

    private int pathWidth;
    private int pathHeight;
    private int pathLength;

    private int[] turnThresholds;
    private int[] turnLengths;

    /**
     * Construct a new PathChecker that uses World {@code world}.
     * @param world The world to use for checking
     */
    public PathChecker(final World world) {
        this.world = world;
        this.points = new HashMap<>();
    }

    /**
     * Reconfigures this PathChecker to use the given settings.
     * @param width The pathWidth of the path
     * @param height The pathHeight of the path
     * @param length The pathLength of the path before permitting Y-changes
     * @param turnThresholds The thresholds for the turn checker
     * @param turnLengths The lenghts for the turn checker
     */
    public void configure(final int width, final int height, final int length,
                          final int[] turnThresholds, final int[] turnLengths) {
        this.pathWidth = width;
        this.pathHeight = height;
        this.pathLength = length;

        this.turnThresholds = turnThresholds;
        this.turnLengths = turnLengths;

        if (turnThresholds.length != turnLengths.length) {
            throw new IllegalArgumentException("Array lengths don't match");
        }
    }

    /**
     * Checks whether PathingVertex {@code v} is walkable by a player.
     * @param v The PathingVertex to check
     * @return Whether it is walkable
     */
    public boolean isWalkable(PathingVertex v) {
        return getPointInfo(v.getData()).solid;
    }

    /**
     * Checks whether PathingVertex {@code v} has sufficient air above it.
     * @param v The PathingVertex to check
     * @return Whether it is clear
     */
    public boolean isClear(PathingVertex v) {
        return getPointInfo(v.getData()).clear;
    }

    /**
     * Checks whether PathingVertex {@code v} has any solid adjacents.
     * @param v The PathingVertex to check
     * @return Whether there are any solid adjacents
     */
    public boolean isGrounded(PathingVertex v) {
        return getPointInfo(v.getData()).grounded;
    }

    /**
     * Whether the given Vertex may be visited from here.
     * @param va The Vertex to check
     * @param vb The Vertex to check
     * @return Whether it is visitable
     */
    public boolean isVisitable(PathingVertex va, PathingVertex vb) {
        if (va != null &&
            va.getData().getY() != vb.getData().getY() &&
            !mayChangeHeight(va)) {
            return false;
        }
        return true;
    }

    /**
     * Whether the area around this Vertex is clear enough for a path.
     * @param vCur The current Vertex
     * @param vNext The next Vertex
     * @return Whether the area is clear
     */
    public boolean isAreaClear(PathingVertex vCur, PathingVertex vNext) {
        Point pCur = vCur.getData();
        Point pNext = vNext.getData();
        Direction dNext = Direction.getDirectionTo(pCur, pNext);

        // We should check if the considered point is clear itself.
        PointInfo pi = getPointInfo(pNext);
        if (!pi.clear) {
            return false;
        }

        // Seek left and right until a point is not clear or we have enough clear points.
        List<Point> clear = new ArrayList<>(pathWidth);
        clear.add(pNext); // We already checked this.

        LinkedList<Tuple<Point, Direction>> remaining = new LinkedList<>();
        remaining.add(new Tuple<>(dNext.getLeft().apply(pNext), dNext.getLeft()));
        remaining.add(new Tuple<>(dNext.getRight().apply(pNext), dNext.getRight()));

        Point pTuple;
        Direction dTuple;
        while (!remaining.isEmpty()) {
            Tuple<Point, Direction> cur = remaining.removeFirst();
            pTuple = cur.a();
            dTuple = cur.b();
            pi = getPointInfo(pTuple);

            // If this tile is not clear, it can not be part of our 'tunnel'.
            if (pi.clear) {
                clear.add(pTuple);
                remaining.add(new Tuple<>(dTuple.apply(pTuple), dTuple));
            } else {
                // If we find a wall, the vNext is also grounded.
                getPointInfo(pNext).grounded = true;
            }

            // Stop checking if we have sufficient space
            if (clear.size() > pathWidth) {
                break;
            }
        }

        // If there are not enough clear blocks, we failed.
        if (clear.size() < pathWidth) {
            return false;
        }

        // If this is not a corner, no further testing is necessary. The area is clear.
        PathingVertex vPrev = vCur.getCameFrom();
        if (vPrev == null) {
            return true;
        }

        // Check the corner area if applicable
        return isCornerAreaClear(vPrev.getData(), pCur, pNext);
    }

    private boolean isCornerAreaClear(Point pFrom, Point pCur, Point pNext) {

        // If we didn't come from anywhere (start of path), then this isn't a corner.
        if (pFrom == null) {
            return true;
        }

        Direction dCur = Direction.getDirectionTo(pFrom, pCur);
        Direction dNext = Direction.getDirectionTo(pCur, pNext);

        // If there is no change in direction, this isn't even a corner.
        DirectionChange dChange = dCur.getChangeTo(dNext);
        if (dChange == DirectionChange.NONE) {
            return true;
        }

        /* If the direction changed, we're certain there was a turn. Consider the following area:
         *       OOO??
         *       OOO??
         *       OXXOO
         *       OOXOO
         *       OOOOO
         *  Where O = visited (and thus confirmed clear), X = our path and ? is unchecked
         *  Given dNext (left), we go the *opposite* direction (right) in combination with dCur (forward).
         *  We use a doubly nested for-loop with (pathWidth / 2) iterations to check the remaining area.
         * */

        // We reverse dNext to go towards the corner (right) instead of the path (left).
        dNext = dNext.getReverse();
        int width = this.pathWidth / 2;

        // Now we check the corner area. We actually start at (0,0), but we apply the directions
        // first so we effectively start at (1,1) as intended.
        Point pRow = pCur, pCol;
        PointInfo pi;
        for (int i = 0; i < width; i++) {

            // Move to next row
            pRow = dCur.apply(pRow);

            // Start the column at the begin of this row
            pCol = pRow;

            for (int j = 0; j < width; j++) {

                // Move to the next column
                pCol = dNext.apply(pCol);

                // Check if this tile is clear
                pi = getPointInfo(pCol);
                if (!pi.clear) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if moving in the given direction is allowed based on our previous direction.
     * @param vCur The Vertex we are currently at
     * @param vNext The Vertex we want to go to
     * @return Whether the turn radius is permitted or not.
     */
    public boolean isTurnRadiusPermitted(PathingVertex vCur, PathingVertex vNext) {

        // Get the direction we intend to go
        Direction dCur = Direction.getDirectionTo(vCur.getData(), vNext.getData());
        Direction dNext;
        DirectionChange dChange;

        // Get the maximum pathLength to check for
        int maxLength = 0;
        for (int len : turnLengths) {
            maxLength = Math.max(maxLength, len);
        }

        // Keep looping until we run out of pathLength
        int balance = 0;
        for (int curLength = 0; curLength < maxLength; curLength++) {

            // Move back
            vNext = vCur;
            dNext = dCur;
            vCur = vCur.getCameFrom();

            // If the path is shorter than pathLength, the turn is legal.
            if (vCur == null) {
                return true;
            }

            // Update the direction and change
            dCur = Direction.getDirectionTo(vCur.getData(), vNext.getData());
            dChange = dCur.getChangeTo(dNext);

            // Update the balance
            balance += dChange.getBalance();

            // Check all arrays
            for (int j = 0; j < turnLengths.length; j++) {
                int length = turnLengths[j];
                int threshold = turnThresholds[j];

                // If the current pathLength is still relevant for this particular
                // combination, and the balance exceeded the threshold, then
                // we have failed.
                if (curLength <= length && Math.abs(balance) > threshold) {
                    return false;
                }
            }
        }

        // The path is within the threshold.
        return true;
    }

    private PointInfo getPointInfo(Point p) {
        if (points.containsKey(p)) {
            return points.get(p);
        }

        // Create a PointInfo object by checking surroundings
        Block b = WorldHelper.getBlock(p.toLocation(world));
        Material m = b.getType();

        boolean isSolid = checkSolid(m);
        boolean isEmpty = !checkSolid(m);
        boolean isClear = checkClear(p);
        boolean isGrounded = checkGrounded(p);

        PointInfo pi = new PointInfo(isSolid, isEmpty, isClear, isGrounded);
        points.put(p, pi);

        return pi;
    }

    private boolean checkSolid(Material m) {
        return m.isSolid();
    }

    private boolean checkClear(Point p) {
        Block b;
        Material m;

        // Check for air above the ground
        for (int i = 0; i < pathHeight - 1; i++) {
            p = p.up();
            b = WorldHelper.getBlock(p.toLocation(world));
            m = b.getType();
            if (checkSolid(m) || b.isLiquid()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkGrounded(Point p) {
        for (Direction d : Direction.values()) {
            if (WorldHelper.getBlock(d.apply(p).toLocation(world)).getType().isSolid()) {
                return true;
            }
        }
        return false;
    }

    private boolean mayChangeHeight(PathingVertex to) {
        int count = 0;
        PathingVertex from;

        // Loop backwards along the known path to assert that a
        // vertical move is valid.
        while (count < pathLength) {
            from = to.getCameFrom();
            if (from == null) {
                break;
            }

            if (from.getData().getY() != to.getData().getY()) {
                break;
            }
            count++;
        }

        return count >= pathLength;
    }

    private static class PointInfo {

        @Getter private boolean solid;
        @Getter private boolean empty;
        @Getter private boolean clear;
        @Getter private boolean grounded;

        PointInfo(final boolean solid, final boolean empty, final boolean clear, final boolean grounded) {
            this.solid = solid;
            this.empty = empty;
            this.clear = clear;
            this.grounded = grounded;
        }
    }
}
