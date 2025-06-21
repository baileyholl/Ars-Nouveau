package com.hollingsworth.arsnouveau.client.documentation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.arsnouveau.api.documentation.DocPlayerData;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellSoundRegistry;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DocDataLoader {
    public static final String DATA_FOLDER = "./config/ars_nouveau/";
    public static final Path DOC_DATA_PATH = Path.of(DATA_FOLDER + "doc_data.json");

    public static void writeBookmarks() {
        List<ResourceLocation> bookmarks = DocPlayerData.bookmarks;
        List<SpellSound> spellSounds = DocPlayerData.favoriteSounds;
        try {
            Files.createDirectories(Path.of(DATA_FOLDER));
            JsonObject element = new JsonObject();
            element.addProperty("version", 1);
            JsonArray bookmarksArray = new JsonArray();
            bookmarks.forEach(e -> bookmarksArray.add(e.toString()));
            element.add("bookmarks", bookmarksArray);

            JsonArray soundsArray = new JsonArray();
            spellSounds.forEach(e -> soundsArray.add(e.getId().toString()));
            element.add("sounds", soundsArray);

            JsonArray particlesArray = new JsonArray();
            DocPlayerData.favoriteParticles.forEach(e -> particlesArray.add(BuiltInRegistries.PARTICLE_TYPE.getKeyOrNull(e).toString()));
            element.add("particles", particlesArray);
            if (!Files.exists(DOC_DATA_PATH)) {
                Files.createFile(DOC_DATA_PATH);
            }
            Files.writeString(DOC_DATA_PATH, element.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadBookmarks() {
        List<ResourceLocation> bookmarks = new ArrayList<>();
        List<SpellSound> sounds = new ArrayList<>();
        List<ParticleType<?>> particles = new ArrayList<>();
        try {
            Files.createDirectories(Path.of(DATA_FOLDER));
            String content = Files.readString(Path.of(DATA_FOLDER + "doc_data.json"), StandardCharsets.UTF_8);
            JsonObject element = JsonParser.parseString(content).getAsJsonObject();
            if (element.has("bookmarks")) {
                element.getAsJsonArray("bookmarks").forEach(e -> bookmarks.add(ResourceLocation.tryParse(e.getAsString())));

                List<ResourceLocation> toRemove = new ArrayList<>();
                for (ResourceLocation loc : bookmarks) {
                    if (loc == null || DocumentationRegistry.getEntry(loc) == null) {
                        toRemove.add(loc);
                    }
                }
                bookmarks.removeAll(toRemove);
            }

            if (element.has("sounds")) {
                element.getAsJsonArray("sounds").forEach(e -> {
                    SpellSound spellSound = SpellSoundRegistry.get(ResourceLocation.tryParse(e.getAsString()));
                    if (spellSound != null) {
                        sounds.add(spellSound);
                    }
                });
            }
            if (element.has("particles")) {
                element.getAsJsonArray("particles").forEach(e -> {
                    ParticleType<?> particle = BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.tryParse(e.getAsString()));
                    if (particle != null) {
                        particles.add(particle);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DocPlayerData.bookmarks = bookmarks;
        DocPlayerData.favoriteSounds = sounds;
        DocPlayerData.favoriteParticles = particles;
    }
}
