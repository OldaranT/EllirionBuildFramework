package com.ellirion.buildframework.pathfinder;

import com.ellirion.buildframework.model.Direction;
import lombok.Getter;
import org.bukkit.World;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathfinder.model.PathingVertex;

import java.util.HashMap;
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
        PointInfo pi = getPointInfo(p);

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
     * Checks if moving in the given direction is allowed based on our previous direction.
     * @param vcur The Vertex we are currently at
     * @param vnext The Vertex we want to go to
     * @return Whether the turn radius is permitted or not.
     */
    public boolean isTurnRadiusPermitted(PathingVertex vcur, PathingVertex vnext) {
        // Get previous vertex
        PathingVertex vprev = vcur.getCameFrom();
        if (vprev == null) {
            // If we don't have a previous direction, any turn is permitted.
            return true;
        }

        // Get the direction we moved previously and the direction we will move next
        Direction dprev = Direction.getDirectionTo(vprev.getData(), vcur.getData());
        Direction dnext = Direction.getDirectionTo(vcur.getData(), vnext.getData());

        // No change in direction, this is allowed.
        if (dprev == dnext) {
            return true;
        }

        // Check if turning is allowed at all
        if (!mayChangeDirection(vcur)) {
            return false;
        }

        // If the turn is not perpendicular, the turn is not permitted.
        if (!dprev.isPerpendicularTo(dnext)) {
            return false;
        }

        // We're sure the turn is legal at this point.
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
