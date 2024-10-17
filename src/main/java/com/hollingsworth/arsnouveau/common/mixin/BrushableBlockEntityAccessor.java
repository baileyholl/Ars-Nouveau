package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrushableBlockEntity.class)
public interface BrushableBlockEntityAccessor {
    @Accessor
    long getLootTableSeed();

    @Accessor
    void setLootTableSeed(long lootTableSeed);

    @Accessor
    ResourceKey<LootTable> getLootTable();

    @Accessor
    void setLootTable(ResourceKey<LootTable> lootTable);
}
