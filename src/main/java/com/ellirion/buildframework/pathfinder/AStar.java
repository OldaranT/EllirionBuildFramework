package com.ellirion.buildframework.pathfinder;

import com.ellirion.buildframework.pathfinder.model.PathingSession;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathfinder.model.PathingGraph;
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
     * @param useHeap Using a heap or not
     * @return A Promise that is resolved or rejected depending on whether a path is found.
     */
    public static Promise<List<Point>> searchAsync(Player player, Point start, Point goal, boolean useHeap) {

        // - lijst met blokken die sinds altijd gevisit zijn (oranje)
        // - lijst met blokken die gevisit zijn deze tick (groen)

        return new Promise<>((finisher) -> {

            long before = System.currentTimeMillis();

            PathChecker checker = new PathChecker(player.getWorld());
            PathingGraph graph = new PathingGraph(useHeap);
            PathingVertex startVert = graph.find(start);
            startVert.setGScore(0d);
            startVert.setFScore(start.distanceEuclidian(goal));

            PathingVertex cur;

            // Async client-side updates
            List<PathingVertex> everSeen = new ArrayList<>();
            List<PathingVertex> nowSeen = new ArrayList<>();
            List<PathingVertex> nowVisited = new ArrayList<>();

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
                    finisher.resolve(path);
                    long after = System.currentTimeMillis() - before;
                    player.sendMessage((useHeap ? "heap" : "scoringlist") + " finished in " + after + "ms");
                    return;
                }

                // Move from openSet to closedSet
                cur.setVisited(true);
                nowSeen.remove(cur);
                nowVisited.add(cur);

                // Ignore if it cannot realistically be walked over
                if (!checker.isClear(cur)) {
                    continue;
                }

                // Check all adjacent vertices
                for (PathingVertex adjacent : cur.getAdjacents()) {

                    // Ignore this adjacent point if we've visited it before`
                    if (adjacent.isVisited()) {
                        continue;
                    }

                    // If it is not visitable, ignore
                    if (!checker.isVisitable(cur, adjacent)) {
                        continue;
                    }

                    // If we can't make this turn, ignore
                    if (!checker.isTurnRadiusPermitted(cur, adjacent)) {
                        continue;
                    }

                    // TODO Check if the current node doesn't go back under the previous node.
                    // TODO This can be done by checking the horizontal (2d) direction the node moves in.
                    // TODO Subtract? score if flying but adjacent to another block

                    // Determine the gScore
                    double gScore = cur.getGScore() +
                                    ((cur.getData().getBlockY() == adjacent.getData().getBlockY()) ? 0.0 : 0.6) +
                                    (!checker.isWalkable(adjacent) ? 3.5 : 0);
                    if (gScore >= adjacent.getGScore()) {
                        continue; // This is not a better path.
                    }

                    // This path is the current best. Record it!
                    adjacent.setCameFrom(cur);
                    adjacent.setGScore(gScore);
                    adjacent.setFScore(gScore + adjacent.getData().distanceEuclidian(goal) * 2);
                    nowSeen.add(adjacent);
                }

                // Send updates to client
                haveVisited++;
                if (haveVisited % 50 == 0) {
                    final List<PathingVertex> sendNowSeen = nowSeen, sendNowVisited = nowVisited;
                    nowSeen = new ArrayList<>();
                    nowVisited = new ArrayList<>();

                    sendUpdates(player, sendNowSeen, sendNowVisited);
                }

                // Update timing
                wantVisited = (System.currentTimeMillis() - startTime);

                // Keep our pace correct (throttle)
                while (wantVisited < haveVisited) {
                    try {
                        Thread.sleep(1);
                    } catch (Exception ex) {
                    }
                    wantVisited = System.currentTimeMillis() - startTime;
                }
            }

            finisher.reject(new Exception("No path found"));
        }, true);
    }

    private static void sendUpdates(Player player, List<PathingVertex> nowSeen, List<PathingVertex> nowVisited) {
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
                    player.sendBlockChange(location, 95, (byte) 4); // yellow glass
                }
                visited.add(v.getData());
            }

            // Display blocks that have now been visited
            for (PathingVertex v : nowVisited) {
                Location location = v.getData().toLocation(world);
                Block block = world.getBlockAt(location);
                if (block.getType().isSolid()) {
                    player.sendBlockChange(location, 251, (byte) 11); // blue concrete
                } else {
                    player.sendBlockChange(location, 95, (byte) 11); // blue glass
                }
                visited.add(v.getData());
            }

            subfinisher.resolve(null);
        }, false);
    }

}
