package com.hollingsworth.arsnouveau.api.documentation.export;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.common.util.Log;
import com.hollingsworth.arsnouveau.setup.registry.Documentation;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;

public class DocExporter {
    public static String ID_PROPERTY = "id";
    public static String TITLE_PROPERTY = "title";
    public static String ORDER_PROPERTY = "order";
    public static String DESCRIPTION_PROPERTY = "description";
    public static String ICON_PROPERTY = "icon";
    public static String CATEGORY_PROPERY = "category";
    public static String RECIPE_PROPERTY = "recipe_1";
    public static String RECIPE2_PROPERTY = "recipe_2";
    public static String RELATED_PROPERTY = "related";
    public static String TIER_PROPERTY = "tier";
    public static String ENTITY_PROPERTY = "entity_type";
    public static String ITEM_PROPERTY = "item";
    public static String PERKS_PROPERTY = "perks";


    static ToIntFunction<String> FIXED_ORDER_FIELDS = Util.make(new Object2IntOpenHashMap<>(), p_236070_ -> {
        // Neo: conditions go first
        p_236070_.put("id", -1);
        p_236070_.defaultReturnValue(2);
    });
    static Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS).thenComparing(p_236077_ -> (String)p_236077_);

    static Path basePath = Path.of("./data/ars_nouveau/doc/");

    public static void export(String modId){
        try {
            Path categoryPath = Path.of("../../wiki/" + modId + "/categories/");
            FileUtils.cleanDirectory(categoryPath.toFile());
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            Documentation.initOnWorldReload();
            for (DocCategory category : DocumentationRegistry.getMainCategoryMap().values()) {
                if (category.id().getNamespace().equals(modId)) {
                    futures.add(exportJson(category.toJson(), category.id(), categoryPath));
                }
                for (DocCategory category1 : category.subCategories()) {
                    if (category1.id().getNamespace().equals(modId)) {
                        futures.add(exportJson(category1.toJson(), category1.id(), categoryPath));
                    }
                }
            }
            Path entryPath = Path.of("../../wiki/" + modId + "/entries/");
            FileUtils.cleanDirectory(entryPath.toFile());
            for (DocEntry entry : DocumentationRegistry.getEntries()) {
                if (entry.id().getNamespace().equals(modId)) {
                    futures.add(exportJson(entry.toJson(), entry.id(), entryPath));
                }
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }catch (IOException e){
            Log.getLogger().error("Failed to write files", e);
        }
    }

    public static CompletableFuture<Void> exportJson(JsonElement element, ResourceLocation id, Path basePath){
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);

                try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
                    jsonwriter.setSerializeNulls(false);
                    jsonwriter.setIndent("  ");
                    GsonHelper.writeValue(jsonwriter, element, KEY_COMPARATOR);
                }

                Path filePath = basePath.resolve(id.getPath() + ".json");
                Files.createDirectories(filePath.getParent());
                if(!Files.exists(filePath)) {
                    Files.createFile(filePath);
                }
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Files.writeString(filePath, gson.toJson(element), StandardCharsets.UTF_8);


            } catch (IOException ioexception) {
                Log.getLogger().error("Failed to save file to {}", basePath.resolve(id.getPath()), ioexception);
            }
        }, Util.backgroundExecutor());
    }
}
