package com.hollingsworth.arsnouveau.common.datagen.advancement;

import com.hollingsworth.arsnouveau.common.datagen.ModDatagen;
import com.hollingsworth.arsnouveau.common.datagen.SimpleDataProvider;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SimpleAdvancements extends SimpleDataProvider {

    public SimpleAdvancements(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        List<AdvancementHolder> holders = new ArrayList<>();
        var advancements = new ANAdvancements();
        advancements.generate(null, holders::add, null);

        for(AdvancementHolder holder : holders){

            Path path = getRecipePath(ModDatagen.output.getOutputFolder(), holder.id().getPath());
            saveStable(pOutput, Advancement.CODEC.encodeStart(JsonOps.INSTANCE, holder.value()).getOrThrow(), path);
        }

    }

    private static Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_nouveau/advancements/" + str + ".json");
    }


    @Override
    public String getName() {
        return "";
    }
}
