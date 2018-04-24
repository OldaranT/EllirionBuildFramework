package com.ellirion.buildframework.pathbuilder.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.ellirion.buildframework.model.Point;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PathBuilder {

    @Getter @Setter private String name;
    @Getter private HashMap<Material, Integer> weightedBlocks;
    @Getter @Setter private Material fenceType;
    @Getter @Setter private int radius;

    /**
     * Create a path builder with a given name.
     * @param name the name of the path builder
     */
    public PathBuilder(final String name) {
        this.name = name;
        weightedBlocks = new HashMap<>();
    }

    /**
     * Add a block to the weighted blocks map.
     * @param mat The block to add
     * @param weight The weight of the block
     */
    public void addBlock(final Material mat, int weight) {
        weightedBlocks.put(mat, weight);
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
            //replace these blocks according to the weight map
            //place fences
        }
    }

    private List<Location> getLocations(Point p) {
        List<Location> locations = new LinkedList<>();

        int x1 = p.getBlockX() - radius;
        int x2 = p.getBlockX() + radius;

        for(int i = x1; i <= x2; i++) {
            //keep going forwards until we're out of range
            int z = p.getBlockZ();

            //keep going backwards until we're out of range
        }

        return locations;
    }

    {
        private static IEnumerable<Vector2> GetPointsInCircle(Vector2 circleCenter, float radius,
        Vector2 gridCenter, Vector2 gridStep)
        {
            if (radius <= 0)
            {
                throw new ArgumentOutOfRangeException("radius", "Argument must be positive.");
            }
            if (gridStep.x <= 0 || gridStep.y <= 0)
            {
                throw new ArgumentOutOfRangeException("gridStep", "Argument must contain positive components only.");
            }

            // Loop bounds for X dimension:
            int i1 = (int)Math.Ceiling((circleCenter.x - gridCenter.x - radius) / gridStep.x);
            int i2 = (int)Math.Floor((circleCenter.x - gridCenter.x + radius) / gridStep.x);

            // Constant square of the radius:
            float radius2 = radius * radius;

            for (int i = i1; i <= i2; i++)
            {
                // X-coordinate for the points of the i-th circle segment:
                float x = gridCenter.x + i * gridStep.x;

                // Local radius of the circle segment (half-length of chord) calulated in 3 steps.
                // Step 1. Offset of the (x, *) from the (circleCenter.x, *):
                float localRadius = circleCenter.x - x;
                // Step 2. Square of it:
                localRadius *= localRadius;
                // Step 3. Local radius of the circle segment:
                localRadius = (float)Math.Sqrt(radius2 - localRadius);

                // Loop bounds for Y dimension:
                int j1 = (int)Math.Ceiling((circleCenter.y - gridCenter.y - localRadius) / gridStep.y);
                int j2 = (int)Math.Floor((circleCenter.y - gridCenter.y + localRadius) / gridStep.y);

                for (int j = j1; j <= j2; j++)
                {
                    yield return new Vector2(x, gridCenter.y + j * gridStep.y);
                }
            }
        }
    }
}
