package com.ellirion.buildframework.pathbuilder.util;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import com.ellirion.buildframework.model.BlockChange;
import com.ellirion.buildframework.model.Point;

import java.util.LinkedList;
import java.util.List;

public class BresenhamLine3D {

    /**
     * Draw a line in 3D.
     * @param start start point of the line
     * @param end end point of the line
     * @param world the world to draw the line in
     * @param material material to draw the line out of
     * @return list of block changes
     */
    public static List<BlockChange> drawLine(Point start, Point end, World world, Material material) {
        // Bresenham3D
        //
        // A slightly modified version of the source found at
        // http://www.ict.griffith.edu.au/anthony/info/graphics/bresenham.procs
        // Provided by Anthony Thyssen, though he does not take credit for the original implementation
        //
        // It is highly likely that the original Author was Bob Pendelton, as referenced here
        //
        // ftp://ftp.isc.org/pub/usenet/comp.sources.unix/volume26/line3d
        //
        // line3d was dervied from DigitalLine.c published as "Digital Line Drawing"
        // by Paul Heckbert from "Graphics Gems", Academic Press, 1990
        //
        // 3D modifications by Bob Pendleton. The original source code was in the public
        // domain, the author of the 3D version places his modifications in the
        // public domain as well.
        //
        // line3d uses Bresenham's algorithm to generate the 3 dimensional points on a
        // line from (x1, y1, z1) to (x2, y2, z2)

        List<BlockChange> blockChanges = new LinkedList<>();

        int i, dx, dy, dz, l, m, n, xInc, yInc, zInc, err1, err2, dx2, dy2, dz2;
        int[] point = new int[3];

        point[0] = start.getBlockX();
        point[1] = start.getBlockY();
        point[2] = start.getBlockZ();
        dx = end.getBlockX() - start.getBlockX();
        dy = end.getBlockY() - start.getBlockY();
        dz = end.getBlockZ() - start.getBlockZ();
        xInc = (dx < 0) ? -1 : 1;
        l = Math.abs(dx);
        yInc = (dy < 0) ? -1 : 1;
        m = Math.abs(dy);
        zInc = (dz < 0) ? -1 : 1;
        n = Math.abs(dz);
        dx2 = l << 1;
        dy2 = m << 1;
        dz2 = n << 1;

        if ((l >= m) && (l >= n)) {
            err1 = dy2 - l;
            err2 = dz2 - l;
            for (i = 0; i < l; i++) {
                Block b = world.getBlockAt(point[0], point[1], point[2]);
                blockChanges.add(new BlockChange(b.getType(), b.getData(), material, (byte) 0, b.getLocation()));
                if (err1 > 0) {
                    point[1] += yInc;
                    err1 -= dx2;
                }
                if (err2 > 0) {
                    point[2] += zInc;
                    err2 -= dx2;
                }
                err1 += dy2;
                err2 += dz2;
                b = world.getBlockAt(point[0], point[1], point[2]);
                blockChanges.add(new BlockChange(b.getType(), b.getData(), material, (byte) 0, b.getLocation()));
                point[0] += xInc;
            }
        } else if ((m >= l) && (m >= n)) {
            err1 = dx2 - m;
            err2 = dz2 - m;
            for (i = 0; i < m; i++) {
                Block b = world.getBlockAt(point[0], point[1], point[2]);
                blockChanges.add(new BlockChange(b.getType(), b.getData(), material, (byte) 0, b.getLocation()));
                if (err1 > 0) {
                    point[0] += xInc;
                    err1 -= dy2;
                }
                if (err2 > 0) {
                    point[2] += zInc;
                    err2 -= dy2;
                }
                err1 += dx2;
                err2 += dz2;
                b = world.getBlockAt(point[0], point[1], point[2]);
                blockChanges.add(new BlockChange(b.getType(), b.getData(), material, (byte) 0, b.getLocation()));
                point[1] += yInc;
            }
        } else {
            err1 = dy2 - n;
            err2 = dx2 - n;
            for (i = 0; i < n; i++) {
                Block b = world.getBlockAt(point[0], point[1], point[2]);
                blockChanges.add(new BlockChange(b.getType(), b.getData(), material, (byte) 0, b.getLocation()));
                if (err1 > 0) {
                    point[1] += yInc;
                    err1 -= dz2;
                }
                if (err2 > 0) {
                    point[0] += xInc;
                    err2 -= dz2;
                }
                err1 += dy2;
                err2 += dx2;
                b = world.getBlockAt(point[0], point[1], point[2]);
                blockChanges.add(new BlockChange(b.getType(), b.getData(), material, (byte) 0, b.getLocation()));
                point[2] += zInc;
            }
        }
        Block b = world.getBlockAt(point[0], point[1], point[2]);
        blockChanges.add(new BlockChange(b.getType(), b.getData(), material, (byte) 0, b.getLocation()));

        return blockChanges;
    }
}
