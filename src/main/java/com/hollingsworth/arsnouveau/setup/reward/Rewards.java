package com.hollingsworth.arsnouveau.setup.reward;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.world.item.DyeColor;
import net.neoforged.fml.loading.FMLEnvironment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Rewards {
    public static List<ContributorStarby> starbuncles = new ArrayList<>();
    public static List<UUID> CONTRIBUTORS = new ArrayList<>();

    public static void init() {
        try {
            JsonObject object = JsonParser.parseString(readUrl(new URL("https://raw.githubusercontent.com/baileyholl/Ars-Nouveau/main/supporters.json"))).getAsJsonObject();

            JsonArray supporters = object.getAsJsonArray("uuids");
            for (JsonElement element : supporters) {
                String uuid = element.getAsString();
                try {
                    CONTRIBUTORS.add(UUID.fromString(uuid.trim()));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            JsonArray adoptions = object.getAsJsonArray("starbuncleAdoptions");
            for (JsonElement element : adoptions) {
                JsonObject jsonObject = element.getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                String adopter = jsonObject.get("adopter").getAsString();
                String color = jsonObject.get("color").getAsString();
                String bio = jsonObject.get("bio").getAsString();
                starbuncles.add(new ContributorStarby(name, adopter, color, bio));
            }
        } catch (IOException var2) {
            var2.printStackTrace();
            if (!FMLEnvironment.production) {
                throw new RuntimeException("Failed to load supporters.json");
            }
        }
    }

    public static String readUrl(URL url) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            StringBuilder buffer = new StringBuilder();
            char[] chars = new char[1024];

            int read;
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        }
    }

    public static class ContributorStarby {
        public String name;
        public String adopter;
        public String color;
        public String bio;

        public ContributorStarby(String name, String adopter, String color, String bio) {
            this.name = name;
            this.adopter = adopter;
            this.color = color;
            this.bio = bio;

            if (!FMLEnvironment.production) {
                if (name == null) {
                    throw new RuntimeException("Name is null");
                }
                if (adopter == null) {
                    throw new RuntimeException("Adopter is null");
                }
                if (color == null) {
                    throw new RuntimeException("Color is null");
                }
                if (bio == null) {
                    throw new RuntimeException("Bio is null");
                }
                boolean foundColor = Arrays.stream(DyeColor.values()).anyMatch(dye -> dye.getName().equals(color));
                if (!foundColor) {
                    throw new RuntimeException("Color is not a valid dye color");
                }
            }
        }
    }
}
