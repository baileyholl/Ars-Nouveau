package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.api.registry.AlakarkinosConversionRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.AlakarkinosRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectUtil;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class EmiAlakarkinosRecipe implements EmiRecipe {
    public static float ITEMS_PER_ROW = 7f;

    private final ResourceLocation id;
    private final AlakarkinosRecipe recipe;

    public EmiAlakarkinosRecipe(ResourceLocation id, AlakarkinosRecipe recipe) {
        this.id = id;
        this.recipe = recipe;
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.ALAKARKINOS_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiStack.of(this.recipe.input()));
    }

    List<EmiStack> outputs = null;

    @Override
    public List<EmiStack> getOutputs() {
        if (this.recipe.drops().isPresent()) {
            if (this.outputs == null) {
                this.outputs = new ArrayList<>();
                for (var drop : this.recipe.drops().get().list()) {
                    this.outputs.add(EmiStack.of(drop.item()));
                }
            }
            return this.outputs;
        }

        return List.of();
    }

    @Override
    public int getDisplayWidth() {
        return 126;
    }

    @Override
    public int getDisplayHeight() {
        return ((int) Math.floor(this.getOutputs().size() / ITEMS_PER_ROW) + 1) * 18 + 9 + 22;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        String prepared = recipe.table().location().getPath().replace("archaeology/", "").replaceAll("_[0-9]", "").replaceAll("_", " ").toLowerCase(Locale.ROOT);
        String name = WordUtils.capitalizeFully(prepared);
        widgets.addText(Component.literal(name), 22, 4, 0xFF000000, false);

        DecimalFormat df = new DecimalFormat("##.##%");
        Optional<AlakarkinosConversionRegistry.LootDrops> lootDrops = recipe.drops();
        if (lootDrops.isEmpty()) {
            return;
        }

        AlakarkinosConversionRegistry.LootDrops drops = lootDrops.get();

        String recipeChance = df.format((float) recipe.weight() / drops.weight());
        widgets.addSlot(EmiStack.of(recipe.input()), 0, 0).appendTooltip(Component.translatable("ars_nouveau.alakarkinos_recipe.chance", recipeChance));

        int yOffset = 9;
        int i = (int) ITEMS_PER_ROW;

        for (var drop : drops.list()) {
            int row = (int) Math.floor(i / ITEMS_PER_ROW);
            int x = (int) ((i - (row * ITEMS_PER_ROW)) * 18);
            int y = row * 18 + yOffset;

            var slot = widgets.addSlot(EmiStack.of(drop.item()).setChance(drop.chance()), x, y).recipeContext(this);
            if (drop.item().has(DataComponents.SUSPICIOUS_STEW_EFFECTS)) {
                for (var effectHolder : drop.item().get(DataComponents.SUSPICIOUS_STEW_EFFECTS).effects()) {
                    var effect = effectHolder.createEffectInstance();
                    MutableComponent tooltip = Component.translatable(effect.getDescriptionId());
                    if (effect.getAmplifier() > 0) {
                        tooltip = Component.translatable("potion.withAmplifier", tooltip, Component.translatable("potion.potency." + effect.getAmplifier()));
                    }

                    if (!effect.endsWithin(20)) {
                        tooltip = Component.translatable("potion.withDuration", tooltip, MobEffectUtil.formatDuration(effect, 1.0F, 20));
                    }

                    slot.appendTooltip(tooltip);
                }
            }

            i += 1;
        }
    }
}
