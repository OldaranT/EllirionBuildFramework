package com.ellirion.buildframework.pathfinder;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathfinder.model.PathingGraph;
import com.ellirion.buildframework.pathfinder.model.PathingSession;
import com.ellirion.buildframework.pathfinder.model.PathingVertex;
import com.ellirion.buildframework.util.Promise;

import java.util.ArrayList;
import java.util.List;

public class AStar {

    private Player player;
    private Point start;
    private Point goal;

    private PathChecker checker;
    @Getter private PathingGraph graph;
    @Getter private List<Point> path;

    private double vStep;
    private double vGrounded;
    private double vFlying;
    private double vExp;

    private double gHoriz;
    private double gVert;

    private double fGoalFactor;
    private double fGoalExp;

    private int turnShortThreshold;
    private int turnShortLength;
    private int turnLongThreshold;
    private int turnLongLength;

    private int pathWidth;
    private int pathHeight;
    private int pathLength;

    private boolean visualEnable;
    private int visualThrottle;

    /**
     * Construct a new AStar pathfinder.
     * @param player The player for whom to find the path
     * @param start The start point
     * @param goal The end point
     */
    public AStar(final Player player, final Point start, final Point goal) {
        this.player = player;
        this.start = start;
        this.goal = goal;

        // Load the config
        PathingSession session = PathingManager.getSession(player);
        NBTTagCompound config = session.getConfig();

        vStep = config.getDouble("v-step");
        vGrounded = config.getDouble("v-grounded");
        vFlying = config.getDouble("v-flying");
        vExp = config.getDouble("v-exp");

        gHoriz = config.getDouble("g-horiz");
        gVert = config.getDouble("g-vert");

        fGoalFactor = config.getDouble("f-goal-fac");
        fGoalExp = config.getDouble("f-goal-exp");

        turnShortThreshold = config.getInt("turn-short-threshold");
        turnShortLength = config.getInt("turn-short-length");

        turnLongThreshold = config.getInt("turn-long-threshold");
        turnLongLength = config.getInt("turn-long-length");

        pathWidth = config.getInt("path-width");
        pathHeight = config.getInt("path-height");
        pathLength = config.getInt("path-length");

        visualEnable = config.getBoolean("visual-enable");
        visualThrottle = config.getInt("visual-throttle");

        // Initialize objects
        checker = new PathChecker(player.getWorld(),
                                  pathWidth, pathHeight, pathLength,
                                  new int[] {turnShortThreshold, turnLongThreshold},
                                  new int[] {turnShortLength, turnLongLength});
        graph = new PathingGraph();
        PathingVertex startVert = graph.findOrCreate(this.start);
        startVert.setGScore(0);
        startVert.setFScore(0);
    }

    /**
     * Searches for a path asynchronously.
     * @return A Promise that is resolved or rejected depending on whether a path is found.
     */
    public Promise<List<Point>> searchAsync() {
        // - lijst met blokken die sinds altijd gevisit zijn (oranje)
        // - lijst met blokken die gevisit zijn deze tick (groen)
        return new Promise<>((finisher) -> {

            long before = System.currentTimeMillis();

            PathingVertex cur;

            // Async client-side updates
            List<PathingVertex> nowSeen = new ArrayList<>();

            long startTime = System.currentTimeMillis();
            long wantVisited;
            long haveVisited = 0;

            int visitIndex = 0;
            int seenIndex = 0;

            while ((cur = graph.next()) != null) {
                cur.setVisitIndex(visitIndex++);

                // Check if we reached the goal
                if (cur.getData().equals(goal)) {
                    path = new ArrayList<>();
                    while (cur != null) {
                        path.add(cur.getData());
                        cur = cur.getCameFrom();
                    }
                    if (visualEnable) {
                        sendUpdates(player, nowSeen);
                    }
                    finisher.resolve(path);
                    long after = System.currentTimeMillis() - before;
                    player.sendMessage("Finished in " + after + "ms");
                    return;
                }

                // Move from openSet to closedSet
                cur.setVisited(true);
                if (visualEnable) {
                    nowSeen.add(cur);
                }

                // Ignore if it cannot realistically be walked over
                if (!checker.isClear(cur)) {
                    continue;
                }

                // Check all adjacent vertices
                for (PathingVertex adjacent : cur.getAdjacents()) {

                    // Ignore this adjacent point if we've visited it before or if it
                    // does not pass some of the possibility checks
                    if (!canVisitAdjacent(cur, adjacent)) {
                        continue;
                    }

                    // Set the seen index if it wasn't seen yet
                    if (adjacent.getSeenIndex() == Integer.MAX_VALUE) {
                        adjacent.setSeenIndex(seenIndex++);
                    }

                    // Determine the gScore
                    double gScore = cur.getGScore() + gHoriz +
                                    ((cur.getData().getY() != adjacent.getData().getY()) ? gVert : 0);

                    // Determine the vScore component
                    double vScore = calculateVScore(cur, adjacent);
                    gScore += vScore;

                    if (gScore >= adjacent.getGScore()) {
                        continue; // This is not a better path.
                    }

                    // Determine the fScore, starting from the gScore.
                    // The gScore is the actual determined score, and fScore is the heuristic.
                    double fScore = gScore + calculateFScore(adjacent);

                    // This path is the current best. Record it!
                    adjacent.setVScore(vScore);
                    adjacent.setCameFrom(cur);
                    adjacent.setGScore(gScore);
                    adjacent.setFScore(fScore);
                }

                // Send updates to client
                haveVisited++;
                if (visualEnable) {
                    final List<PathingVertex> sendNowSeen = nowSeen;
                    nowSeen = new ArrayList<>();

                    sendUpdates(player, sendNowSeen);
                }

                // Update timing
                wantVisited = visualThrottle * (System.currentTimeMillis() - startTime) / 1000;

                // Keep our pace correct (throttle)
                while (visualEnable && wantVisited < haveVisited) {
                    try {
                        Thread.sleep(1);
                    } catch (Exception ex) {
                    }
                    wantVisited = visualThrottle * (System.currentTimeMillis() - startTime) / 1000;
                }
            }

            finisher.reject(new Exception("No path found"));
        }, true);
    }

    private boolean canVisitAdjacent(PathingVertex cur, PathingVertex adjacent) {
        // Ignore this adjacent point if we've visited it before
        if (adjacent.isVisited()) {
            return false;
        }

        // If we are not allowed to change height from here, and the vertex
        // changes height at all, ignore.
        if (!checker.isVisitable(cur, adjacent)) {
            return false;
        }

        // If we can't make this turn, ignore.
        if (!checker.isTurnRadiusPermitted(cur, adjacent)) {
            return false;
        }

        // Lastly, check if the area surrounding the adjacent
        // is clear of any obstacles for a path to be generated.
        if (!checker.isAreaClear(cur, adjacent)) {
            return false;
        }

        return true;
    }

    private double calculateVScore(PathingVertex cur, PathingVertex adjacent) {
        if (checker.isWalkable(adjacent)) {
            return 0;
        }

        // Inherit previous vScore
        double vScore = cur.getVScore() + vStep;

        // Increase vScore depending on groundedness
        vScore += checker.isGrounded(adjacent) ? vGrounded : vFlying;

        // Apply the exponential vScore punishment
        return Math.max(0, Math.pow(vScore, vExp) - Math.pow(cur.getVScore(), vExp));
    }

    private double calculateFScore(PathingVertex adjacent) {
        // Add pow(distance from goal, fGoalExp) * fGoalFactor
        double fScore = Math.pow(adjacent.getData().distanceEuclidian(goal), fGoalExp) * fGoalFactor;

        // Add the distance from the "optimal" line
        //fScore += adjacent.getData().distanceFromLine(start, goal) * fLine;

        return fScore;
    }

    private void sendUpdates(Player player, List<PathingVertex> nowSeen) {
        // Send the update!
        new Promise<>((subfinisher) -> {
            World world = player.getWorld();

            // Update the 'visited' list
            PathingSession session = PathingManager.getSession(player);
            List<Point> visited = session.getVisited();
            if (visited == null) {
                visited = new ArrayList<>();
                session.setVisited(visited);
            }

            // Display blocks that have now been seen
            for (PathingVertex v : nowSeen) {
                Location location = v.getData().toLocation(world);
                Block block = world.getBlockAt(location);
                if (block.getType().isSolid()) {
                    player.sendBlockChange(location, 251, (byte) 4); // yellow concrete
                } else {
                    player.sendBlockChange(location, 20, (byte) 0); // glass
                }
                visited.add(v.getData());
            }

            subfinisher.resolve(null);
        }, false);
    }
}
