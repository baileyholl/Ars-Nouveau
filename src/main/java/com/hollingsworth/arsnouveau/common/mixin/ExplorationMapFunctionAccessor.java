package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExplorationMapFunction.class)
public interface ExplorationMapFunctionAccessor {
    @Accessor
    TagKey<Structure> getDestination();

    @Accessor
    Holder<MapDecorationType> getMapDecoration();

    @Accessor
    byte getZoom();

    @Accessor
    int getSearchRadius();

    @Accessor
    boolean getSkipKnownStructures();
}
