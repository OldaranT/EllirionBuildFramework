package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;

import java.util.LinkedList;
import java.util.List;

public class TemplateHologram {

    @Getter private Location location;
    private Template template;
    @Getter private BoundingBox box;
    @Getter private List<TemplateHologramBlock> hologramBlocks;

    /**
     * Constructor taking in a Template.
     * @param t The Template this is a hologram of
     * @param loc the location of the template
     */
    public TemplateHologram(final Template t, final Location loc) {
        location = loc.clone();
        template = t;
        box = template.getBoundingBox();
        hologramBlocks = new LinkedList<>();

        fillHologramBlocks();
    }

    private void fillHologramBlocks() {
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();
        World w = location.getWorld();

        for (Point p : box.getCorners()) {
            hologramBlocks.add(
                    new TemplateHologramBlock(
                            new Location(w, x + p.getX(), y + p.getY(), z + p.getZ()),
                            Material.GLOWSTONE
                    )
            );
        }
    }
}
