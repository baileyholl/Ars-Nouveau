package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PotionBrewing.class)
public interface PotionRecipeMixin {
    @Accessor("potionMixes")
    static List<PotionBrewing.Mix<Potion>> mixList() {
        throw new AssertionError();
    }
}