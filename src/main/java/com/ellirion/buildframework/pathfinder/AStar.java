package com.ellirion.buildframework.pathfinder;

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

    /**
     * Searches for a path asynchronously.
     * @param player The player to keep updated on progress
     * @param start The start point
     * @param goal The goal point
     * @return A Promise that is resolved or rejected depending on whether a path is found.
     */
    // CHECKSTYLE.OFF: MethodLength
    public static Promise<List<Point>> searchAsync(Player player, Point start, Point goal) {
        // - lijst met blokken die sinds altijd gevisit zijn (oranje)
        // - lijst met blokken die gevisit zijn deze tick (groen)
        return new Promise<>((finisher) -> {

            long before = System.currentTimeMillis();

            // Load the config
            PathingSession session = PathingManager.getSession(player);
            NBTTagCompound config = session.getConfig();
            double vStep = config.getDouble("v-step");
            double vGrounded = config.getDouble("v-grounded");
            double vFlying = config.getDouble("v-flying");
            double vExp = config.getDouble("v-exp");
            double fGoalFactor = config.getDouble("f-goal-fac");
            double fGoalExp = config.getDouble("f-goal-exp");
            double fLine = config.getDouble("f-line");
            int turnShortThreshold = config.getInt("turn-short-threshold");
            int turnShortLength = config.getInt("turn-short-length");
            int turnLongThreshold = config.getInt("turn-long-threshold");
            int turnLongLength = config.getInt("turn-long-length");
            int pathWidth = config.getInt("path-width");
            int pathHeight = config.getInt("path-height");
            int pathLength = config.getInt("path-length");
            boolean visualEnable = config.getBoolean("visual-enable");
            int visualThrottle = config.getInt("visual-throttle");

            // Initialize our instances
            PathChecker checker = new PathChecker(player.getWorld(),
                                                  pathWidth, pathHeight, pathLength,
                                                  new int[] {turnShortThreshold, turnLongThreshold},
                                                  new int[] {turnShortLength, turnLongLength});
            PathingGraph graph = new PathingGraph();
            PathingVertex startVert = graph.find(start);
            startVert.setGScore(0d);
            startVert.setFScore(start.distanceEuclidian(goal));

            PathingVertex cur;

            // Async client-side updates
            List<PathingVertex> nowSeen = new ArrayList<>();

            long startTime = System.currentTimeMillis();
            long wantVisited;
            long haveVisited = 0;

            while ((cur = graph.next()) != null) {

                // Check if we reached the goal
                if (cur.getData().equals(goal)) {
                    List<Point> path = new ArrayList<>();
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

                    // Ignore this adjacent point if we've visited it before
                    if (adjacent.isVisited()) {
                        continue;
                    }

                    // If we are not allowed to change height from here, and the vertex
                    // changes height at all, ignore.
                    if (!checker.isVisitable(cur, adjacent)) {
                        continue;
                    }

                    // If we can't make this turn, ignore.
                    if (!checker.isTurnRadiusPermitted(cur, adjacent)) {
                        continue;
                    }

                    // Lastly, check if the area surrounding the adjacent
                    // is clear of any obstacles for a path to be generated.
                    if (!checker.isAreaClear(cur, adjacent)) {
                        continue;
                    }

                    // Determine the gScore
                    double gScore = cur.getGScore() + cur.getData().distanceEuclidian(adjacent.getData());

                    // Determine the vScore component
                    boolean solid = checker.isWalkable(adjacent);
                    boolean grounded = checker.isGrounded(adjacent);
                    double vScore = 0;
                    if (!solid) {
                        // Inherit previous vScore
                        vScore = cur.getVScore() + vStep;

                        // Increase vScore depending on groundedness
                        vScore += grounded ? vGrounded : vFlying;

                        // Apply the exponential vScore punishment
                        gScore += Math.max(0, Math.pow(vScore, vExp) - Math.pow(cur.getVScore(), vExp));
                    }

                    if (gScore >= adjacent.getGScore()) {
                        continue; // This is not a better path.
                    }

                    // Determine the fScore, starting from the gScore.
                    // The gScore is the actual determined score, and fScore is the heuristic.
                    double fScore = gScore;

                    // Add pow(distance from goal, fGoalExp) * fGoalFactor
                    fScore += Math.pow(adjacent.getData().distanceEuclidian(goal), fGoalExp) * fGoalFactor;

                    // Add the distance from the "optimal" line
                    fScore += adjacent.getData().distanceFromLine(start, goal) * fLine;

                    // This path is the current best. Record it!
                    adjacent.setVScore(vScore);
                    adjacent.setCameFrom(cur);
                    adjacent.setGScore(gScore);
                    adjacent.setFScore(fScore);
                }

                // Send updates to client
                haveVisited++;
                if (visualEnable && haveVisited % 50 == 0) {
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
    // CHECKSTYLE.ON: MethodLength

    private static void sendUpdates(Player player, List<PathingVertex> nowSeen) {
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
