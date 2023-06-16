package com.hollingsworth.arsnouveau.common.datagen;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.common.datagen.advancement.ANAdvancements;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.function.Consumer;

public class Advancements extends AdvancementProvider {
    private final List<Consumer<Consumer<Advancement>>> anTabs = ImmutableList.of(new ANAdvancements());
    public Advancements(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }


    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        for(Consumer<Consumer<Advancement>> consumer1 : this.anTabs) {
            consumer1.accept(consumer);
        }
    }
}
