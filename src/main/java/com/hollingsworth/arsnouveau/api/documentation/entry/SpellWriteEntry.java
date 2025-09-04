package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.export.DocExporter;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.SpellWriteRecipe;
import com.hollingsworth.arsnouveau.common.items.data.ReactiveCasterData;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBreak;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLight;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class SpellWriteEntry extends PedestalRecipeEntry {
    RecipeHolder<SpellWriteRecipe> spellWriteRecipe;

    public SpellWriteEntry(RecipeHolder<SpellWriteRecipe> spellWriteRecipe, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.title = Component.translatable("block.ars_nouveau.enchanting_apparatus");
        this.spellWriteRecipe = spellWriteRecipe;
        List<Ingredient> ingredients1 = new ArrayList<>();
        if (spellWriteRecipe != null) {
            for (Ingredient ingredient : spellWriteRecipe.value().pedestalItems()) {
                if (ingredient.test(new ItemStack(ItemsRegistry.SPELL_PARCHMENT))) {
                    ItemStack replacementParchment = new ItemStack(ItemsRegistry.SPELL_PARCHMENT);
                    replacementParchment.set(DataComponentRegistry.SPELL_CASTER, new SpellCaster(0, "", false, "", 1).setSpell(new Spell().add(MethodTouch.INSTANCE).add(EffectLight.INSTANCE)));
                    ingredients1.add(Ingredient.of(replacementParchment));
                } else {
                    ingredients1.add(ingredient);
                }
            }
            this.ingredients = ingredients1;
        }

        ItemStack inputStick = new ItemStack(Items.STICK);
        Level level = parent.getMinecraft().level;
        Holder<Enchantment> enchantment = level.registryAccess().holderOrThrow(EnchantmentRegistry.REACTIVE_ENCHANTMENT);
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(inputStick.get(DataComponents.ENCHANTMENTS));
        enchantments.set(enchantment, 1);
        inputStick.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
        inputStick.set(DataComponentRegistry.REACTIVE_CASTER, new ReactiveCasterData(0, "", false, "", 1).setSpell(new Spell().add(MethodProjectile.INSTANCE).add(EffectBreak.INSTANCE)));
        this.reagentStack = Ingredient.of(inputStick);

        ItemStack outputStick = new ItemStack(Items.STICK);
        outputStick.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
        outputStick.set(DataComponentRegistry.REACTIVE_CASTER, new ReactiveCasterData(0, "", false, "", 1).setSpell(new Spell().add(MethodTouch.INSTANCE).add(EffectLight.INSTANCE)));
        this.outputStack = outputStick;
    }

    public static SinglePageCtor create(ResourceLocation id) {
        return (parent, x, y, width, height) -> {
            RecipeHolder<SpellWriteRecipe> recipe = parent.recipeManager().byKeyTyped(RecipeRegistry.SPELL_WRITE_TYPE.get(), id);
            return new SpellWriteEntry(recipe, parent, x, y, width, height);
        };
    }

    @Override
    public void addExportProperties(JsonObject object) {
        super.addExportProperties(object);
        if (spellWriteRecipe != null) {
            object.addProperty(DocExporter.RECIPE_PROPERTY, spellWriteRecipe.id().toString());
        }
    }
}
