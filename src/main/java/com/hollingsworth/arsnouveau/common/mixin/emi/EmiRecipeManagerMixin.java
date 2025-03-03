package com.hollingsworth.arsnouveau.common.mixin.emi;

import com.hollingsworth.arsnouveau.client.emi.EmiApparatusEnchantingRecipe;
import com.hollingsworth.arsnouveau.client.emi.EmiArmorUpgradeRecipe;
import com.hollingsworth.arsnouveau.client.emi.EmiArsNouveauPlugin;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ApparatusRecipeInput;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ArmorUpgradeRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ReactiveEnchantmentRecipe;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(targets = "dev.emi.emi.registry.EmiRecipes$Manager", remap = false)
public abstract class EmiRecipeManagerMixin {
    @Shadow public abstract List<EmiRecipe> getRecipes(EmiRecipeCategory category);

    @WrapMethod(method = "getRecipesByInput")
    public List<EmiRecipe> getRecipesByInput(EmiStack stack, Operation<List<EmiRecipe>> original) {
        List<EmiRecipe> out = new ArrayList<>();

        ApparatusRecipeInput apparatus = new ApparatusRecipeInput(stack.getItemStack(), List.of(), null);
        ClientLevel level = Minecraft.getInstance().level;

        for (var ench : this.getRecipes(EmiArsNouveauPlugin.APPARATUS_ENCHANTING_CATEGORY)) {
            if (ench instanceof EmiApparatusEnchantingRecipe emiRecipe && ench.getBackingRecipe().value() instanceof EnchantmentRecipe recipe && !(recipe instanceof ReactiveEnchantmentRecipe) && recipe.doesReagentMatch(apparatus, level, null)) {
                try {
                    out.add(emiRecipe.withReagentAndOutput(stack, EmiStack.of(recipe.assemble(apparatus, level.registryAccess()))));
                } catch (Exception ignored) {}
            }
        }

        for (var upgrade : this.getRecipes(EmiArsNouveauPlugin.ARMOR_UPGRADE_CATEGORY)) {
            if (upgrade instanceof EmiArmorUpgradeRecipe emiRecipe && upgrade.getBackingRecipe().value() instanceof ArmorUpgradeRecipe recipe && recipe.doesReagentMatch(apparatus, level, null)) {
                try {
                    out.add(emiRecipe.withReagentAndOutput(stack, EmiStack.of(recipe.assemble(apparatus, level.registryAccess()))));
                } catch (Exception ignored) {}
            }
        }

        if (out.isEmpty()) {
            return original.call(stack);
        } else {
            out.addAll(original.call(stack));
            return out;
        }
    }
}
