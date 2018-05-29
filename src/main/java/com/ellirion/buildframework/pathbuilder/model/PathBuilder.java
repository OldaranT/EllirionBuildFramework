package com.ellirion.buildframework.pathbuilder.model;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BlockChange;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathbuilder.util.BresenhamLine3D;
import com.ellirion.buildframework.util.MinecraftHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PathBuilder {

    private static final Point[] FILL_PATTERN = new Point[] {
            new Point().down(),
            new Point().down().translate(new Point().north()),
            new Point().down().translate(new Point(2, 0, 0)),
            new Point().down().translate(new Point().east()),
            new Point().down().translate(new Point(0, 0, 2)),
            new Point().down().translate(new Point().south()),
            new Point().down().translate(new Point(0, 0, -2)),
            new Point().down().translate(new Point().west()),
            new Point().down().translate(new Point(-2, 0, 0)),
            new Point().down().translate(new Point().down()),
            };

    @Getter @Setter private String name;
    @Getter private HashMap<PathMaterial, Double> weightedBlocks;
    @Getter private HashMap<PathMaterial, Double> weightedSteps;
    private double blocksWeight;
    private double stepsWeight;
    @Getter @Setter private Material supportType;
    @Getter @Setter private int radius;
    private Random r;

    /**
     * Create a path builder with a given name.
     * @param name the name of the path builder
     */
    public PathBuilder(final String name) {
        this.name = name;
        weightedBlocks = new HashMap<>();
        weightedSteps = new HashMap<>();
        r = new Random();
        supportType = Material.FENCE;
    }

    /**
     * Add a block to the weighted blocks map.
     * @param mat material to add
     * @param weight weight to set the material to
     * @param data optional metadata value
     */
    public void addBlock(Material mat, double weight, byte data) {
        denormalizeWeights(weightedBlocks, blocksWeight);
        weightedBlocks.put(new PathMaterial(mat, data), weight);
        blocksWeight = getTotalBlockWeight();
        normalizeWeights(weightedBlocks, blocksWeight);
    }

    /**
     * Overload of other method, does the same but with data set to 0.
     * @param mat material to add
     * @param weight weight to set the material to
     */
    public void addBlock(Material mat, double weight) {
        addBlock(mat, weight, (byte) 0);
    }

    /**
     * Add a block to the weighted blocks map.
     * @param mat material to add
     * @param weight weight to set the material to
     * @param data optional metadata value
     */
    public void addStep(Material mat, double weight, byte data) {
        denormalizeWeights(weightedSteps, stepsWeight);
        weightedSteps.put(new PathMaterial(mat, data), weight);
        stepsWeight = getTotalStepWeight();
        normalizeWeights(weightedSteps, stepsWeight);
    }

    /**
     * Overload of other method, does the same but with data set to 0.
     * @param mat material to add
     * @param weight weight to set the material to
     */
    public void addStep(Material mat, double weight) {
        addBlock(mat, weight, (byte) 0);
    }

    /**
     * Removes a material from the weighted blocks list.
     * @param mat the material to remove
     * @param data the metadata on the material to remove
     */
    public void removeBlock(final Material mat, byte data) {
        PathMaterial toRemove = new PathMaterial(mat, data);
        weightedBlocks.remove(toRemove);
    }

    /**
     * Build a path along the given list of points.
     * @param points The list of points to generate a path along
     * @param w The world in which to place the path
     * @return the list of BlockChanges for the created path
     */
    public List<BlockChange> build(List<Point> points, World w) {
        int count = 0;

        List<BlockChange> blockChanges = new LinkedList<>();

        for (Point p : points) {
            // Get all 'locations' around the point within radius r
            List<Point> nearbyPoints = getNearbyPoints(p);

            // Replace these blocks according to the weight map
            for (Point point : nearbyPoints) {
                Block b = w.getBlockAt(point.toLocation(w));
                double random = r.nextDouble();
                PathMaterial pm = getPathMaterial(weightedBlocks, random);
                blockChanges.add(
                        new BlockChange(b.getType(), b.getData(), pm.getMat(), pm.getData(), b.getLocation()));
            }
            // If the path at this point isn't grounded, create supports
            if (count % (radius * 3) == 0 && !isGrounded(p, w)) {
                // Get all points within certain radius, and build towards anchor point
                // Create multiple anchor points to build towards
                List<Point> anchorPoints = getAnchorPoints(p.down(), w);
                if (anchorPoints == null) {
                    continue;
                }
                for (Point supportPoint : getNearbyPoints(p.down())) {
                    if (w.getBlockAt(supportPoint.toLocation(w)).getType() == Material.AIR) {
                        for (Point anchorPoint : anchorPoints) {
                            blockChanges.addAll(BresenhamLine3D.drawLine(supportPoint, anchorPoint, w, supportType));
                        }
                    }
                }
            }

            count++;
        }

        return postProcessing(blockChanges);
    }

    private List<BlockChange> postProcessing(List<BlockChange> blockChanges) {
        Collections.sort(blockChanges, new Comparator<BlockChange>() {
            // Sort the list to put supports first
            @Override
            public int compare(BlockChange lhs, BlockChange rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                if (lhs.getMatAfter() == supportType && rhs.getMatAfter() != supportType) {
                    return -1;
                }
                if (rhs.getMatAfter() == supportType && lhs.getMatAfter() != supportType) {
                    return 1;
                }
                return 0;
            }
        });

        // Add all to hashmap
        // This will override some of the supports with path blocks
        HashMap<Point, BlockChange> map = new HashMap<>();
        for (BlockChange change : blockChanges) {
            map.put(new Point(change.getLocation()), change);
        }

        smoothPath(blockChanges, map);

        addSteps(blockChanges, map);

        return blockChanges;
    }

    private void smoothPath(List<BlockChange> blockChanges, HashMap<Point, BlockChange> map) {
        World w = blockChanges.get(0).getLocation().getWorld();

        // We want to remove unnecessary blocks.
        // From the top of the path, remove blocks that stand on top of other path blocks
        List<Material> pathMats = Arrays.asList(getPathMaterials());
        List<BlockChange> toRemove = new LinkedList<>();
        for (BlockChange change : blockChanges) {
            // Check if it's the top of the path, then remove the block if it stands on another path block
            BlockChange up = map.get(new Point(change.getLocation()).up());
            BlockChange below = map.get(new Point(change.getLocation()).down());
            // If the block above is air, the block below is a path block, and the current block is air, we can remove this block
            if ((up == null || up.getMatAfter() == Material.AIR) && below != null &&
                (pathMats.contains(below.getMatAfter()) &&
                 w.getBlockAt(change.getLocation()).getType() == Material.AIR) &&
                countPathBlocksAbove(new Point(change.getLocation()), map) < 4) {
                toRemove.add(change);
                map.remove(new Point(change.getLocation()));
            }
        }

        for (BlockChange change : toRemove) {
            blockChanges.remove(change);
        }
    }

    private void addSteps(List<BlockChange> blockChanges, HashMap<Point, BlockChange> map) {
        if (weightedSteps.size() == 0) {
            return;
        }

        World w = blockChanges.get(0).getLocation().getWorld();

        // Now we want to add steps to the stairs
        // Check each block, and if it has air above and a path block diagonally above, place a step above
        List<BlockChange> toAdd = new LinkedList<>();
        Random r = new Random();
        for (BlockChange change : blockChanges) {
            if (change.getMatAfter() == supportType) {
                continue;
            }

            Point p = new Point(change.getLocation());

            if (!map.containsKey(p.up()) && countPathBlocksAbove(p, map) > 0 &&
                w.getBlockAt(p.up().toLocation(w)).getType() == Material.AIR) {
                PathMaterial mat = getPathMaterial(weightedSteps, r.nextDouble());
                BlockChange newChange = new BlockChange(Material.AIR, (byte) 0, mat.getMat(), mat.getData(),
                                                        p.up().toLocation(w));
                toAdd.add(newChange);
                map.put(p.up(), newChange);
            }
        }

        for (BlockChange change : toAdd) {
            blockChanges.add(change);
        }
    }

    private PathMaterial getPathMaterial(HashMap<PathMaterial, Double> map, double weight) {
        for (Map.Entry pair : map.entrySet()) {
            if (weight < (double) pair.getValue()) {
                return (PathMaterial) pair.getKey();
            }
        }

        return (PathMaterial) map.keySet().toArray()[0];
    }

    private int countPathBlocksAbove(Point p, HashMap<Point, BlockChange> blockChanges) {
        Point up = p.up();
        Point north = up.north();
        Point east = up.east();
        Point south = up.south();
        Point west = up.west();

        BlockChange[] changes = new BlockChange[] {
                blockChanges.get(up),
                blockChanges.get(north),
                blockChanges.get(east),
                blockChanges.get(south),
                blockChanges.get(west),
                };

        int i = 0;
        List<Material> pathMats = Arrays.asList(getPathMaterials());
        for (BlockChange change : changes) {
            if (change != null && pathMats.contains(change.getMatAfter())) {
                i++;
            }
        }

        return i;
    }

    private List<Point> getNearbyPoints(Point p) {
        List<Point> points = new LinkedList<>();
        Point localPoint = p;
        double dist = radius;

        int x1 = localPoint.getBlockX() - (radius + 1);
        int x2 = localPoint.getBlockX() + (radius + 1);

        for (int x = x1; x <= x2; x++) {
            int z = localPoint.getBlockZ();
            Point point = new Point(x, localPoint.getBlockY(), z);
            while (point.distanceEuclidian(localPoint) < dist) {
                points.add(new Point(x, localPoint.getBlockY(), z));
                z++;
                point = new Point(point.getBlockX(), point.getBlockY(), z);
            }

            z = localPoint.getBlockZ();
            point = new Point(x, localPoint.getBlockY(), z);
            while (point.distanceEuclidian(localPoint) < dist) {
                points.add(new Point(x, localPoint.getBlockY(), z));
                z--;
                point = new Point(point.getBlockX(), point.getBlockY(), z);
            }
        }

        return points;
    }

    private double getTotalBlockWeight() {
        double weight = 0;
        for (double d : weightedBlocks.values()) {
            weight += d;
        }
        return weight;
    }

    private double getTotalStepWeight() {
        double weight = 0;
        for (double d : weightedSteps.values()) {
            weight += d;
        }
        return weight;
    }

    private void denormalizeWeights(HashMap<PathMaterial, Double> weightMap, double totalWeight) {
        double previous = 0;
        for (Map.Entry pair : weightMap.entrySet()) {
            weightMap.put((PathMaterial) pair.getKey(),
                          (double) pair.getValue() - previous);
            previous += (double) pair.getValue();
        }

        for (Map.Entry pair : weightMap.entrySet()) {
            weightMap.put((PathMaterial) pair.getKey(),
                          (double) pair.getValue() * totalWeight);
        }
    }

    private void normalizeWeights(HashMap<PathMaterial, Double> weightMap, double totalWeight) {
        for (Map.Entry pair : weightMap.entrySet()) {
            weightMap.put((PathMaterial) pair.getKey(),
                          (double) pair.getValue() / totalWeight);
        }

        double previous = 0;
        for (Map.Entry pair : weightMap.entrySet()) {
            weightMap.put((PathMaterial) pair.getKey(),
                          (double) pair.getValue() + previous);
            previous = (double) pair.getValue();
        }
    }

    private Material[] getPathMaterials() {
        Material[] mats = new Material[weightedBlocks.size()];
        int i = 0;
        for (PathMaterial mat : weightedBlocks.keySet()) {
            mats[i] = mat.getMat();
            i++;
        }
        return mats;
    }

    private boolean isGrounded(Point p, World w) {
        // Returns true if two thirds of the path is anchored
        List<Point> points = getNearbyPoints(p);
        int countGrounded = 0;
        for (Point point : points) {
            if (MinecraftHelper.isAnchorPoint(w.getBlockAt(point.toLocation(w)).getRelative(0, -1, 0).getType())) {
                countGrounded++;
            }
        }
        return (double) countGrounded / (double) points.size() >= 0.66;
    }

    /**
     * GetAnchorPoints without showing the flood fill.
     * @param point The point of the path from which to find anchor points
     * @param w the world in which the path is built
     * @return a list of anchor points
     */
    public List<Point> getAnchorPoints(Point point, World w) {
        return getAnchorPoints(point, w, null);
    }

    /**
     * Get the anchor points for a given path point.
     * @param point The point of the path from which to find anchor points
     * @param w the world in which the path is built
     * @param player if a player is given, show the flood fill to the player
     * @return a list of anchor points
     */
    public List<Point> getAnchorPoints(Point point, World w, Player player) {
        List<Point> anchorPoints = new ArrayList<>();

        Point anchor = floodFill(point, w, FILL_PATTERN, player);

        Point[] points = new Point[] {
                anchor.up(),
                anchor.north(),
                anchor.south(),
                anchor.east(),
                anchor.west()
        };

        // Flood fill will find a point in the ground, so try to move it out of the ground
        if (!isBlockAnchored(anchor.toLocation(w))) {
            for (Point p : points) {
                if (isBlockAnchored(p.toLocation(w))) {
                    anchor = p;
                    break;
                }
            }
        }

        anchorPoints.add(anchor);

        // We want multiple anchor points
        // Get north east south west down and determine whether they are air and an anchorpoint
        Point[] anchors = new Point[] {
                anchor.north(),
                anchor.south(),
                anchor.east(),
                anchor.west(),
                anchor.down()
        };

        for (Point p : anchors) {
            // Also check up/down for if the anchor point is standing on a block that sits 1 above the blocks around it like this: ____|‾‾|____
            Point a = checkUpDown(p, w, 2);
            if (a != null && a.getBlockY() < point.getBlockY()) {
                anchorPoints.add(a);
            }
        }

        return anchorPoints;
    }

    // Flood fill according to the given pattern until we find a block that's an anchor point
    private Point floodFill(Point p, World w, Point[] fillPattern, Player player) {
        Point anchor = p;
        LinkedList<Point> pointsToCheck = new LinkedList<>();
        pointsToCheck.add(p);

        HashMap<Point, Boolean> visited = new HashMap<>();

        boolean anchorFound = false;
        int steps = 0;
        while (!anchorFound) {
            Point curr = pointsToCheck.removeFirst();

            if (player != null) {
                player.sendBlockChange(curr.toLocation(w), Material.GLASS, (byte) 0);
            }

            Block b = w.getBlockAt(curr.toLocation(w));
            if (MinecraftHelper.isAnchorPoint(b.getType()) && b.getType() != supportType) {
                anchor = curr;
                anchorFound = true;
            }

            // Translate the current point with each point in the fill pattern, these are where we 'fill' to
            Point[] points = new Point[fillPattern.length];
            for (int i = 0; i < fillPattern.length; i++) {
                points[i] = curr.translate(fillPattern[i]);
            }

            for (int i = 0; i < points.length; i++) {
                if (!visited.containsKey(points[i])) {
                    pointsToCheck.addLast(points[i]);
                    visited.put(points[i], false);
                }
            }

            visited.put(curr, true);

            steps++;
            if (steps % 1000000 == 0) {
                BuildFramework.getInstance().getLogger().info(steps + " steps");
            }
            if (steps >= 5000000) {
                BuildFramework.getInstance().getLogger().info("Could not find anchor point for " + p.toString());
                anchorFound = true;
            }
        }

        return anchor;
    }

    private Point checkUpDown(Point p, World w, int radius) {
        for (int i = 0; i <= radius; i++) {
            Point translated = p.translate(new Point(0, i, 0));
            if (w.getBlockAt(translated.toLocation(w)).getType() == Material.AIR &&
                isBlockAnchored(translated.toLocation(w))) {
                return translated;
            }
            translated = p.translate(new Point(0, -i, 0));
            if (w.getBlockAt(translated.toLocation(w)).getType() == Material.AIR &&
                isBlockAnchored(translated.toLocation(w))) {
                return translated;
            }
        }

        return null;
    }

    // Will return true if the block at the given location is air and it's touching at least 1 block that we can anchor on
    private boolean isBlockAnchored(Location l) {
        Block b = l.getWorld().getBlockAt(l);
        if (b.getType() != Material.AIR) {
            return false;
        }

        BlockFace[] faces = new BlockFace[] {
                BlockFace.NORTH,
                BlockFace.EAST,
                BlockFace.SOUTH,
                BlockFace.WEST,
                BlockFace.DOWN
        };

        for (BlockFace face : faces) {
            if (MinecraftHelper.isAnchorPoint(b.getRelative(face).getType()) &&
                b.getRelative(face).getType() != supportType) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(75);
        sb.append("PathBuilder with name '" + name + "'\nRadius: " + radius + "\nBlocks:");

        double previous = 0;
        for (Map.Entry pair : weightedBlocks.entrySet()) {
            PathMaterial mat = (PathMaterial) pair.getKey();
            String matName = mat.getMat().name() + ":" + mat.getData();
            String percentage = new DecimalFormat("00.0").format((((double) pair.getValue()) - previous) * 100);
            sb.append("\n-" + matName + " - " + (((double) pair.getValue()) - previous) * blocksWeight + " [" +
                      percentage + "%]");
            previous = (double) pair.getValue();
        }

        sb.append("\n\nSteps:\n");
        previous = 0;
        for (Map.Entry pair : weightedSteps.entrySet()) {
            PathMaterial mat = (PathMaterial) pair.getKey();
            String matName = mat.getMat().name() + ":" + mat.getData();
            String percentage = new DecimalFormat("00.0").format((((double) pair.getValue()) - previous) * 100);
            sb.append("\n-" + matName + " - " + (((double) pair.getValue()) - previous) * stepsWeight + " [" +
                      percentage + "%]");
            previous = (double) pair.getValue();
        }

        return sb.toString();
    }

    private static NBTTagCompound serialize(PathBuilder pb) {
        NBTTagCompound ntc = new NBTTagCompound();

        ntc.setString("name", pb.name);
        ntc.setInt("radius", pb.radius);

        pb.denormalizeWeights(pb.weightedBlocks, pb.blocksWeight);
        NBTTagList blocks = new NBTTagList();
        for (Map.Entry pair : pb.weightedBlocks.entrySet()) {
            PathMaterial pm = (PathMaterial) pair.getKey();
            double weight = (double) pair.getValue();

            NBTTagCompound mat = new NBTTagCompound();
            mat.set("PathMaterial", PathMaterial.serialize(pm)); //NOPMD
            mat.setDouble("weight", weight); //NOPMD
            blocks.add(mat);
        }
        ntc.set("weightedBlocks", blocks);
        pb.normalizeWeights(pb.weightedBlocks, pb.blocksWeight);

        pb.denormalizeWeights(pb.weightedSteps, pb.stepsWeight);
        NBTTagList steps = new NBTTagList();
        for (Map.Entry pair : pb.weightedSteps.entrySet()) {
            PathMaterial pm = (PathMaterial) pair.getKey();
            double weight = (double) pair.getValue();

            NBTTagCompound mat = new NBTTagCompound();
            mat.set("PathMaterial", PathMaterial.serialize(pm)); //NOPMD
            mat.setDouble("weight", weight); //NOPMD
            steps.add(mat);
        }
        ntc.set("weightedSteps", steps);
        pb.normalizeWeights(pb.weightedSteps, pb.stepsWeight);

        return ntc;
    }

    private static PathBuilder deserialize(NBTTagCompound ntc) {
        String name = ntc.getString("name");
        int radius = ntc.getInt("radius");
        PathBuilder builder = new PathBuilder(name);
        builder.setRadius(radius);

        NBTTagList blocks = ntc.getList("weightedBlocks", 10);
        for (int i = 0; i < blocks.size(); i++) {
            NBTTagCompound block = blocks.get(i);
            PathMaterial mat = PathMaterial.deserialize(block.getCompound("PathMaterial")); //NOPMD
            double weight = block.getDouble("weight"); //NOPMD
            builder.weightedBlocks.put(new PathMaterial(mat.getMat(), mat.getData()), weight);
        }

        NBTTagList steps = ntc.getList("weightedSteps", 10);
        for (int i = 0; i < steps.size(); i++) {
            NBTTagCompound block = steps.get(i);
            PathMaterial mat = PathMaterial.deserialize(block.getCompound("PathMaterial")); //NOPMD
            double weight = block.getDouble("weight"); //NOPMD
            builder.weightedSteps.put(new PathMaterial(mat.getMat(), mat.getData()), weight);
        }

        builder.blocksWeight = builder.getTotalBlockWeight();
        builder.stepsWeight = builder.getTotalStepWeight();

        builder.normalizeWeights(builder.weightedBlocks, builder.getTotalBlockWeight());
        builder.normalizeWeights(builder.weightedSteps, builder.getTotalStepWeight());

        return builder;
    }

    /**
     * Saves the pathbuilder to a file.
     * @param pb the PathBuilder to save
     * @param path the path to the file
     * @return whether the PathBuilder was successfully saved
     */
    public static boolean save(PathBuilder pb, String path) {
        try {
            NBTTagCompound ntc = PathBuilder.serialize(pb);
            OutputStream out = new FileOutputStream(new File(path));
            NBTCompressedStreamTools.a(ntc, out);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Loads a PathBuilder from a file.
     * @param path the path of the file
     * @return the PathBuilder
     */
    public static PathBuilder load(String path) {
        try {
            InputStream in = new FileInputStream(new File(path));
            NBTTagCompound ntc = NBTCompressedStreamTools.a(in);
            return PathBuilder.deserialize(ntc);
        } catch (Exception e) {
            return null;
        }
    }
}
