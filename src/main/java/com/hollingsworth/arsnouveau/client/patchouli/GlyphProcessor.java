package com.hollingsworth.arsnouveau.client.patchouli;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class GlyphProcessor implements IComponentProcessor {

    RecipeHolder<? extends GlyphRecipe> holder;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        String recipeID = variables.get("recipe", level.registryAccess()).asString();
        try {
            holder = (RecipeHolder<? extends GlyphRecipe>) manager.byKey(ResourceLocation.tryParse(recipeID)).orElse(null);
        } catch (Exception ignored) {
        }
    }

    @Override
    public IVariable process(Level level,  String s) {
        if (holder == null)
            return null;
        var recipe = holder.value();
        if (s.equals("tier")) {
            return IVariable.wrap(Component.translatable("ars_nouveau.tier").getString() + ": " + Component.translatable("ars_nouveau.spell_tier." + recipe.getSpellPart().getConfigTier().value).getString());
        }
        if (s.equals("schools")) {
            AbstractSpellPart part = ((Glyph) recipe.output.getItem()).spellPart;
            StringBuilder str = new StringBuilder(Component.translatable("ars_nouveau.spell_schools").getString() + ": ");
            for (SpellSchool spellSchool : part.spellSchools) {
                str.append(spellSchool.getTextComponent().getString()).append(",");
            }
            if (!part.spellSchools.isEmpty())
                str = new StringBuilder(str.substring(0, str.length() - 1));
            return IVariable.wrap(str.toString());
        }
        if (s.equals("mana_cost")) {
            if (recipe.output.getItem() instanceof Glyph) {
                int cost = ((Glyph) recipe.output.getItem()).spellPart.getCastingCost();
                String costLang = "";
                if (cost == 0)
                    costLang = Component.translatable("ars_nouveau.mana_cost.none").getString();
                if (cost < 20)
                    costLang = Component.translatable("ars_nouveau.mana_cost.low").getString();
                if (cost < 50)
                    costLang = Component.translatable("ars_nouveau.mana_cost.medium").getString();
                if (cost >= 50)
                    costLang = Component.translatable("ars_nouveau.mana_cost.high").getString();
                return IVariable.wrap(Component.translatable("ars_nouveau.casting_cost").getString() + ": " + costLang);
            }
            return IVariable.wrap("");
        }
        return null;
    }
}

