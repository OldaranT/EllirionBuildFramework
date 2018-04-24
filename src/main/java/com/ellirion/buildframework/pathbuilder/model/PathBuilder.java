package com.ellirion.buildframework.pathbuilder.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.ellirion.buildframework.model.Point;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PathBuilder {

    @Getter @Setter private String name;
    @Getter private HashMap<Material, Double> weightedBlocks;
    private double totalWeight;
    @Getter @Setter private Material fenceType;
    @Getter @Setter private int radius;
    private Random r;

    /**
     * Create a path builder with a given name.
     * @param name the name of the path builder
     */
    public PathBuilder(final String name) {
        this.name = name;
        weightedBlocks = new HashMap<>();
        r = new Random();
        totalWeight = 0;
    }

    /**
     * Overload of other method, does the same but with data set to 0.
     * @param mat material
     * @param weight weight
     */
    public void addBlock(final Material mat, double weight) {
        denormalizeWeights();
        weightedBlocks.put(mat, weight);
        totalWeight += weight;
        normalizeWeights();
    }

    /**
     * Removes a block from the weighted blocks map.
     * @param mat The block to remove
     */
    public void removeBlock(final Material mat) {
        weightedBlocks.remove(mat);
    }

    /**
     * Build a path along the given list of points.
     * @param points The list of points to generate a path along
     * @param w The world in which to place the path
     */
    public void build(List<Point> points, World w) {
        for (Point p : points) {
            //Get all 'locations' around the point within radius r
            List<Point> nearbyPoints = getPoints(p);

            //replace these blocks according to the weight map
            for (Point point : nearbyPoints) {
                Block b = w.getBlockAt(point.toLocation(w));
                if (b.getType() != Material.AIR) {
                    double random = r.nextDouble();
                    for (Map.Entry pair2 : weightedBlocks.entrySet()) {
                        if (random < ((double) pair2.getValue())) {
                            b.setType((Material) pair2.getKey());
                            break;
                        }
                    }
                    //place fences
                    if (distanceToPath(point, points) >= radius) {
                        w.getBlockAt(b.getX(), b.getY() + 1, b.getZ()).setType(fenceType);
                    }
                }
            }
        }
    }

    private List<Point> getPoints(Point p) {
        List<Point> points = new LinkedList<>();
        int localRadius = radius;
        Point localPoint = randomMutation(p);

        double random = r.nextDouble();
        if (random < 0.33) {
            localRadius -= 1;
        } else if (random > 0.66) {
            localRadius += 1;
        }

        int x1 = localPoint.getBlockX() - (localRadius + 1);
        int x2 = localPoint.getBlockX() + (localRadius + 1);

        for (int x = x1; x <= x2; x++) {
            //keep going forwards until we're out of range
            int z = localPoint.getBlockZ();
            Point point = new Point(x, localPoint.getBlockY(), z);
            while (point.distanceEuclidian(localPoint) < localRadius) {
                points.add(new Point(x, localPoint.getBlockY(), z));
                z++;
                point = new Point(point.getBlockX(), point.getBlockY(), z);
            }

            //keep going backwards until we're out of range
            z = localPoint.getBlockZ();
            point = new Point(x, localPoint.getBlockY(), z);
            while (point.distanceEuclidian(localPoint) < localRadius) {
                points.add(new Point(x, localPoint.getBlockY(), z));
                z--;
                point = new Point(point.getBlockX(), point.getBlockY(), z);
            }
        }

        return points;
    }

    private Point randomMutation(Point p) {
        Point localPoint = new Point(p.getBlockX(), p.getBlockY(), p.getBlockZ());

        double randomX = r.nextDouble();
        double randomZ = r.nextDouble();

        if (randomX < 0.33) {
            localPoint = new Point(localPoint.getBlockX() - 1, localPoint.getBlockY(), localPoint.getBlockZ());
        } else if (randomX > 0.66) {
            localPoint = new Point(localPoint.getBlockX() + 1, localPoint.getBlockY(), localPoint.getBlockZ());
        }

        if (randomZ < 0.33) {
            localPoint = new Point(localPoint.getBlockX(), localPoint.getBlockY(), localPoint.getBlockZ() - 1);
        } else if (randomZ > 0.66) {
            localPoint = new Point(localPoint.getBlockX(), localPoint.getBlockY(), localPoint.getBlockZ() + 1);
        }

        return localPoint;
    }

    private void denormalizeWeights() {
        double previous = 0;
        for (Map.Entry pair : weightedBlocks.entrySet()) {
            weightedBlocks.put((Material) pair.getKey(),
                               (double) pair.getValue() - previous);
            previous = (double) pair.getValue();
        }

        // weight * totalWeight
        for (Map.Entry pair : weightedBlocks.entrySet()) {
            weightedBlocks.put((Material) pair.getKey(),
                               (double) pair.getValue() * totalWeight);
        }
    }

    private void normalizeWeights() {
        // weight / total
        // Counting up all weights should total 1
        for (Map.Entry pair : weightedBlocks.entrySet()) {
            weightedBlocks.put((Material) pair.getKey(),
                               (double) pair.getValue() / totalWeight);
        }

        double previous = 0;
        for (Map.Entry pair : weightedBlocks.entrySet()) {
            weightedBlocks.put((Material) pair.getKey(),
                               (double) pair.getValue() + previous);
            previous = (double) pair.getValue();
        }
    }

    private double distanceToPath(Point point, List<Point> path) {
        double closest = Double.MAX_VALUE;
        for (Point p : path) {
            closest = Math.min(closest, p.distanceEuclidian(point));
        }
        return closest;
    }
}
