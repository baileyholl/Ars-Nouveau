package com.hollingsworth.arsnouveau.client.documentation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.arsnouveau.api.documentation.DocPlayerData;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DocDataLoader {
    public static final String DATA_FOLDER = "./config/ars_nouveau/";
    public static final Path DOC_DATA_PATH = Path.of(DATA_FOLDER + "doc_data.json");
    public static void writeBookmarks(){
        List<ResourceLocation> bookmarks = DocPlayerData.bookmarks;
        try {
            Files.createDirectories(Path.of(DATA_FOLDER));
            JsonObject element = new JsonObject();
            element.addProperty("version", 1);
            JsonArray bookmarksArray = new JsonArray();
            bookmarks.forEach(e -> bookmarksArray.add(e.toString()));
            element.add("bookmarks", bookmarksArray);
            if(!Files.exists(DOC_DATA_PATH)) {
                Files.createFile(DOC_DATA_PATH);
            }
            Files.writeString(DOC_DATA_PATH, element.toString(), StandardCharsets.UTF_8);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static List<ResourceLocation> loadBookmarks(){
        List<ResourceLocation> bookmarks = new ArrayList<>();
        try {
            Files.createDirectories(Path.of(DATA_FOLDER));
            String content = Files.readString(Path.of(DATA_FOLDER + "doc_data.json"), StandardCharsets.UTF_8);
            JsonObject element = JsonParser.parseString(content).getAsJsonObject();
            if(element.has("bookmarks")){
                element.getAsJsonArray("bookmarks").forEach(e -> bookmarks.add(ResourceLocation.tryParse(e.getAsString())));

                List<ResourceLocation> toRemove = new ArrayList<>();
                for(ResourceLocation loc : bookmarks){
                    if(loc == null || DocumentationRegistry.getEntry(loc) == null){
                        toRemove.add(loc);
                    }
                }
                bookmarks.removeAll(toRemove);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return bookmarks;
    }
}
