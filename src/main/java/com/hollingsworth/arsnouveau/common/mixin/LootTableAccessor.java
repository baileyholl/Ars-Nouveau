package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Optional;

@Mixin(LootTable.class)
public interface LootTableAccessor {
    @Accessor
    List<LootItemFunction> getFunctions();

    @Accessor
    Optional<ResourceLocation> getRandomSequence();
}
