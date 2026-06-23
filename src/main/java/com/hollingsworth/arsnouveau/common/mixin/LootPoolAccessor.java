package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(LootPool.class)
public interface LootPoolAccessor {
    @Accessor
    Predicate<LootContext> getCompositeCondition();

    @Invoker
    void callAddRandomItem(Consumer<ItemStack> stackConsumer, LootContext context);
}
