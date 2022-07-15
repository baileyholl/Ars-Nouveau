package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.recipe.VanillaPotionRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionBrewing.class)
public class PotionRecipeMixin {
    //
    @Inject(method = "addMix", at = @At("HEAD"))
    private static void addMix(Potion potionEntry, Item potionIngredient, Potion potionResult, CallbackInfo ci) {
        ArsNouveauAPI.getInstance().vanillaPotionRecipes.add(new VanillaPotionRecipe(potionEntry, potionIngredient, potionResult));
    }
}
