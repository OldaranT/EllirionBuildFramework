package com.ellirion.buildframework.pathfinder;

import com.ellirion.buildframework.model.Direction;
import com.ellirion.buildframework.model.DirectionChange;
import lombok.Getter;
import org.bukkit.World;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathfinder.model.PathingVertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathChecker {

    private World world;
    private Map<Point, PointInfo> points;

    /**
     * Construct a new PathChecker that uses World {@code world}.
     * @param world The world to use for checking
     */
    public PathChecker(final World world) {
        this.world = world;
        this.points = new HashMap<>();
    }

    /**
     * Checks whether PathingVertex {@code v} is walkable by a player.
     * @param v The PathingVertex to check
     * @return Whether it is walkable
     */
    public boolean isWalkable(PathingVertex v) {
        Point p = v.getData();
        PointInfo pi = getPointInfo(p);

        // Check for ground presence
        if (!pi.solid) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether the given vertex is clear.
     * @param v The vertex
     * @return Whether it is clear or not
     */
    public boolean isClear(PathingVertex v) {
        Point p = v.getData();
        PointInfo pi;

        // Check for air above the ground for 3 tiles
        for (int i = 0; i < 3; i++) {
            p = p.up();
            pi = getPointInfo(p);
            if (pi.solid) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the given vertex is grounded.
     * @param v The vertex
     * @return Whether it is grounded
     */
    public boolean isGrounded(PathingVertex v) {
        Point p1 = v.getData();
        Point p2;
        PointInfo pi;
        for (Direction d : Direction.values()) {
            p2 = d.apply(p1);
            pi = getPointInfo(p2);
            if (pi.solid) {
                return true;
            }
        }
        return false;
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
     * @param va The Vertex to check
     * @param vb The Vertex to check
     * @param width The width of the path
     * @return Whether the area is clear
     */
    public boolean isAreaClear(PathingVertex va, PathingVertex vb, int width) {
        //List<PointInfo> area = getArea(va, vb);
        return true;
    }

    private List<PointInfo> getArea(PathingVertex va, PathingVertex vb) {
        List<PointInfo> points = new ArrayList<>();

        return points;
    }

    /**
     * Checks if moving in the given direction is allowed based on our previous direction.
     * @param vCur The Vertex we are currently at
     * @param vNext The Vertex we want to go to
     * @param threshold The amount of turns in one direction that may be made
     * @param length The length during which we should check the threshold
     * @return Whether the turn radius is permitted or not.
     */
    public boolean isTurnRadiusPermitted(PathingVertex vCur, PathingVertex vNext,
                                         final int threshold, final int length) {

        // Get the direction we intend to go
        Direction dCur = Direction.getDirectionTo(vCur.getData(), vNext.getData());
        Direction dNext;
        DirectionChange dChange;

        int balance = 0;
        for (int i = 0; i < length; i++) {

            // Move back
            vNext = vCur;
            dNext = dCur;
            vCur = vCur.getCameFrom();

            // If the path is shorter than length, the turn is legal.
            if (vCur == null) {
                return true;
            }

            // Update the direction and change
            dCur = Direction.getDirectionTo(vCur.getData(), vNext.getData());
            dChange = dCur.getChangeTo(dNext);

            // Update the balance
            balance += dChange.getBalance();

            // Check if the balance exceeded the threshold
            if (Math.abs(balance) > threshold) {
                return false;
            }
        }

        // The path is within the threshold.
        return true;
    }

    private PointInfo getPointInfo(Point p) {
        if (points.containsKey(p)) {
            return points.get(p);
        }

        return new PointInfo(world.getBlockAt(p.toLocation(world)).getType().isSolid());
    }

    private boolean mayChangeHeight(PathingVertex to) {
        int count = 0;
        PathingVertex from;

        // Loop backwards along the known path to assert that a
        // vertical move is valid.
        while (count < 2) {
            from = to.getCameFrom();
            if (from == null) {
                break;
            }

            if (from.getData().getY() != to.getData().getY()) {
                break;
            }
            count++;
        }

        return count >= 2;
    }

    private boolean mayChangeDirection(PathingVertex cur) {
        PathingVertex prev = cur.getCameFrom();
        Direction last = null, d;

        for (int i = 0; i < 2; i++) {
            if (prev == null) {
                return true;
            }

            d = Direction.getDirectionTo(prev.getData(), cur.getData());
            if (last != null) {
                if (d != last) {
                    return false;
                }
            } else {
                last = d;
            }

            cur = prev;
            prev = cur.getCameFrom();
        }

        return true;
    }

    private static class PointInfo {

        @Getter private boolean solid;

        PointInfo(final boolean solid) {
            this.solid = solid;
        }
    }
}
