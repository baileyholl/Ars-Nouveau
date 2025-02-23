package com.hollingsworth.arsnouveau.setup.reward;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
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

    record Data(List<UUID> supporters, List<ContributorStarby> adoptions) {
        public static final Codec<Data> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.list(UUIDUtil.STRING_CODEC).fieldOf("uuids").forGetter(Data::supporters),
                        Codec.list(ContributorStarby.CODEC).fieldOf("starbuncleAdoptions").forGetter(Data::adoptions)
                ).apply(instance, Data::new)
        );
    }

    public static void init() {
        try {
            JsonObject object = JsonParser.parseString(readUrl(new URL("https://raw.githubusercontent.com/baileyholl/Ars-Nouveau/main/supporters.json"))).getAsJsonObject();
            var dyn = new Dynamic<>(JsonOps.INSTANCE, object);
            Data data = Data.CODEC.decode(dyn.getOps(), dyn.getValue()).getOrThrow().getFirst();
            CONTRIBUTORS = data.supporters;
            starbuncles = data.adoptions;
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
        public static final Codec<ContributorStarby> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.STRING.fieldOf("name").forGetter(ContributorStarby::getName),
                        Codec.STRING.fieldOf("adopter").forGetter(ContributorStarby::getAdopter),
                        Codec.STRING.fieldOf("color").forGetter(ContributorStarby::getColor),
                        Codec.STRING.fieldOf("bio").forGetter(ContributorStarby::getBio)
                ).apply(instance, ContributorStarby::new)
        );

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

        public String getName() {
            return name;
        }

        public String getAdopter() {
            return adopter;
        }

        public String getColor() {
            return color;
        }

        public String getBio() {
            return bio;
        }
    }
}
