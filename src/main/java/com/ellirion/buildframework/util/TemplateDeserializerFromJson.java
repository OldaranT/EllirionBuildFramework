package com.ellirion.buildframework.util;

import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateBlock;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TemplateDeserializerFromJson implements JsonDeserializer<Template>, JsonSerializer<Template> {
    private Logger logger = BuildFramework.getInstance().getLogger();

    @Override
    public Template deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            Template t = new Template();

            JsonObject jObject = jsonElement.getAsJsonObject();
            t.setTemplateID(jObject.get("templateID").getAsInt());
            t.setTemplateName(jObject.get("templateName").getAsString());

            int x = 0;
            int y = 0;
            int z = 0;

            //loop through all templateBlocks and create and load them individually
            JsonArray jArrayX = jObject.getAsJsonArray("templateBlocks");
            TemplateBlock[][][] tBlocks = new TemplateBlock[jArrayX.size()][jArrayX.get(0).getAsJsonArray().size()][jArrayX.get(0).getAsJsonArray().get(0).getAsJsonArray().size()];
            for (JsonElement elementX : jArrayX) {
                for (JsonElement elementY : elementX.getAsJsonArray()) {
                    for (JsonElement elementZ : elementY.getAsJsonArray()) {
                        Logger.getGlobal().info(elementZ.toString());
                        JsonObject obj = elementZ.getAsJsonObject();
                        TemplateBlock tb = new TemplateBlock(Material.getMaterial(obj.get("material").getAsString()));
                        MaterialData metadata = new MaterialData(obj.get("metadata").getAsJsonObject().get("type").getAsInt(), obj.get("metadata").getAsJsonObject().get("data").getAsByte());
                        tb.setMetadata(metadata);

                        String nbtString = obj.get("nbt").getAsString();
                        byte[] nbtBytes = Base64.getDecoder().decode(nbtString);
                        ByteArrayInputStream istream = new ByteArrayInputStream(nbtBytes);
                        GZIPInputStream gzIn = new GZIPInputStream(istream);
                        NBTTagCompound ntc = NBTCompressedStreamTools.a(gzIn);
                        tb.setData(ntc);

                        tBlocks[x][y][z] = tb;
                        z++;
                    }
                    z = 0;
                    y++;
                }
                y = 0;
                x++;
            }
            t.setTemplateBlocks(tBlocks);

            return t;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            return null;
        }
    }

    @Override
    public JsonElement serialize(Template template, Type type, JsonSerializationContext jsonSerializationContext) {
        try {
            JsonObject jObject = new JsonObject();

            jObject.add("templateID", new JsonPrimitive(template.getTemplateID()));
            jObject.add("templateName", new JsonPrimitive(template.getTemplateName()));

            TemplateBlock[][][] tBlocks = template.getTemplateBlocks();

            int xDepth = tBlocks.length;
            int yDepth = tBlocks[0].length;
            int zDepth = tBlocks[0][0].length;

            JsonArray jArrayX = new JsonArray();
            for (int x = 0; x < xDepth; x++) {
                JsonArray jArrayY = new JsonArray();
                for (int y = 0; y < yDepth; y++) {
                    JsonArray jArrayZ = new JsonArray();
                    for (int z = 0; z < zDepth; z++) {
                        JsonObject j = new JsonObject();
                        //save material
                        j.add("material", new JsonPrimitive(tBlocks[x][y][z].getMaterial().name()));

                        //save metadata
                        JsonObject meta = new JsonObject();
                        meta.add("type", new JsonPrimitive(tBlocks[x][y][z].getMetadata().getItemTypeId()));
                        meta.add("data", new JsonPrimitive(tBlocks[x][y][z].getMetadata().getData()));
                        j.add("metadata", meta);

                        //save nbt data
                        OutputStream stream = new ByteArrayOutputStream();
                        OutputStream gzipStream = new GZIPOutputStream(stream);
                        NBTCompressedStreamTools.a(tBlocks[x][y][z].getData(), gzipStream);
                        byte[] nbtData = ((ByteArrayOutputStream) stream).toByteArray();
                        j.add("nbt", new JsonPrimitive(new String(Base64.getEncoder().encode(nbtData))));

                        jArrayZ.add(j);
                    }
                    jArrayY.add(jArrayZ);
                }
                jArrayX.add(jArrayY);
            }
            jObject.add("templateBlocks", jArrayX);

            return jObject;
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            return null;
        }
    }
}
