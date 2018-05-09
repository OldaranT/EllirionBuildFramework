package com.ellirion.buildframework.pathbuilder.model;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.ellirion.buildframework.model.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PathBuilder {

    @Getter @Setter private String name;
    @Getter private HashMap<PathMaterial, Double> weightedBlocks;
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
     * Add a block to the weighted blocks map.
     * @param mat material
     * @param weight weight
     * @param data metadata
     */
    public void addBlock(final Material mat, double weight, byte data) {
        denormalizeWeights();
        weightedBlocks.put(new PathMaterial(mat, data), weight);
        //        weightedBlocks.put(mat, weight);
        totalWeight += weight;
        normalizeWeights();
    }

    /**
     * Overload of other method, does the same but with data set to 0.
     * @param mat material
     * @param weight weight
     */
    public void addBlock(final Material mat, double weight) {
        addBlock(mat, weight, (byte) 0);
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
                //                if (b.getType() != Material.AIR) {
                double random = r.nextDouble();
                for (Map.Entry pair2 : weightedBlocks.entrySet()) {
                    if (random < ((double) pair2.getValue())) {
                        PathMaterial pm = (PathMaterial) pair2.getKey();
                        b.setType(pm.getMat());
                        b.setData(pm.getData());
                        break;
                    }
                }
                //                }
            }
        }
    }

    private List<Point> getPoints(Point p) {
        List<Point> points = new LinkedList<>();
        double localRadius = radius;
        Point localPoint = p;
        double dist = localRadius * 0.66;

        int x1 = localPoint.getBlockX() - (int) (localRadius + 1);
        int x2 = localPoint.getBlockX() + (int) (localRadius + 1);

        for (int x = x1; x <= x2; x++) {
            //keep going forwards until we're out of range
            int z = localPoint.getBlockZ();
            Point point = new Point(x, localPoint.getBlockY(), z);
            while (point.distanceEuclidian(localPoint) < dist) {
                points.add(new Point(x, localPoint.getBlockY(), z));
                z++;
                point = new Point(point.getBlockX(), point.getBlockY(), z);
            }

            //keep going backwards until we're out of range
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
            weightedBlocks.put((PathMaterial) pair.getKey(),
                               (double) pair.getValue() - previous);
            previous = (double) pair.getValue();
        }

        for (Map.Entry pair : weightedBlocks.entrySet()) {
            weightedBlocks.put((PathMaterial) pair.getKey(),
                               (double) pair.getValue() * totalWeight);
        }
    }

    private void normalizeWeights() {
        for (Map.Entry pair : weightedBlocks.entrySet()) {
            weightedBlocks.put((PathMaterial) pair.getKey(),
                               (double) pair.getValue() / totalWeight);
        }

        double previous = 0;
        for (Map.Entry pair : weightedBlocks.entrySet()) {
            weightedBlocks.put((PathMaterial) pair.getKey(),
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

    /**
     * Serialize the pathbuilder to NBT format.
     * @param pb the PathBuilder to serialize
     * @return the serialized PathBuilder
     */
    public static NBTTagCompound serialize(PathBuilder pb) {
        NBTTagCompound ntc = new NBTTagCompound();

        //name, radius, weightedblocks
        ntc.setString("name", pb.name);
        ntc.setInt("radius", pb.radius);

        NBTTagList blocks = new NBTTagList();
        for (Map.Entry pair : pb.weightedBlocks.entrySet()) {
            PathMaterial pm = (PathMaterial) pair.getKey();
            double weight = (double) pair.getValue();

            NBTTagCompound mat = new NBTTagCompound();
            mat.set("PathMaterial", PathMaterial.serialize(pm));
            mat.setDouble("weight", weight);
            blocks.add(mat);
        }
        ntc.set("weightedBlocks", blocks);

        return ntc;
    }

    /**
     * Deserialize a PathBuilder from NBT.
     * @param ntc the NBTTagCompound
     * @return the PathBuilder
     */
    public static PathBuilder deserialize(NBTTagCompound ntc) {
        String name = ntc.getString("name");
        int radius = ntc.getInt("radius");
        PathBuilder builder = new PathBuilder(name);
        builder.setRadius(radius);

        NBTTagList blocks = ntc.getList("weightedBlocks", 10);
        for (int i = 0; i < blocks.size(); i++) {
            NBTTagCompound block = blocks.get(i);
            PathMaterial mat = PathMaterial.deserialize(block.getCompound("PathMaterial"));
            double weight = block.getDouble("weight");
            builder.addBlock(mat.getMat(), weight, mat.getData());
        }

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
            //create directory if not exists
            //create file if not exists
            //write to file
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
        //check if the file exists
        //load the NBTTagCompound
        //create path builder
        try {
            InputStream in = new FileInputStream(new File(path));
            NBTTagCompound ntc = NBTCompressedStreamTools.a(in);
            return PathBuilder.deserialize(ntc);
        } catch (Exception e) {
            return null;
        }
    }
}
