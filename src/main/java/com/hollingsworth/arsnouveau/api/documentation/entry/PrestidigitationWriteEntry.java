package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.api.particle.timelines.PrestidigitationTimeline;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.PrestidigitationRecipe;
import com.hollingsworth.arsnouveau.common.items.data.PrestidigitationData;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectPrestidigitation;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class PrestidigitationWriteEntry extends PedestalRecipeEntry {
    RecipeHolder<PrestidigitationRecipe> prestidigitationRecipe;

    public PrestidigitationWriteEntry(RecipeHolder<PrestidigitationRecipe> spellWriteRecipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.title = Component.translatable("block.ars_nouveau.enchanting_apparatus");
        this.prestidigitationRecipe = spellWriteRecipe;
        List<Ingredient> ingredients1 = new ArrayList<>();
        if (spellWriteRecipe != null) {
            for (Ingredient ingredient : spellWriteRecipe.value().pedestalItems()) {
                if (ingredient.test(new ItemStack(ItemsRegistry.SPELL_PARCHMENT))) {
                    ItemStack replacementParchment = new ItemStack(ItemsRegistry.SPELL_PARCHMENT);
                    replacementParchment.set(DataComponentRegistry.SPELL_CASTER, new SpellCaster(0, "", false, "", 1).setSpell(new Spell().add(EffectPrestidigitation.INSTANCE)));
                    ingredients1.add(Ingredient.of(replacementParchment));
                } else {
                    ingredients1.add(ingredient);
                }
            }
            this.ingredients = ingredients1;
        }
        this.reagentStack = Ingredient.of(new ItemStack(Items.STICK));

        ItemStack outputStick = new ItemStack(Items.STICK);
        outputStick.set(DataComponentRegistry.PRESTIDIGITATION, new PrestidigitationData(new PrestidigitationTimeline()));
        this.outputStack = outputStick;
    }

    public static SinglePageCtor create(ResourceLocation id) {
        return (parent, x, y, width, height) -> {
            RecipeHolder<PrestidigitationRecipe> recipe = parent.recipeManager().byKeyTyped(RecipeRegistry.PRESTIDIGITATION_TYPE.get(), id);
            return new PrestidigitationWriteEntry(recipe, parent, x, y, width, height);
        };
    }

    @Override
    public void addExportProperties(JsonObject object) {
        super.addExportProperties(object);
        if (prestidigitationRecipe != null) {
            object.addProperty(DocExporter.RECIPE_PROPERTY, prestidigitationRecipe.id().toString());
        }
    }
}
