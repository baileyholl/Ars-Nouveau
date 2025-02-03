package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ArmorUpgradeRecipe;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmiArmorUpgradeRecipe extends EmiEnchantingApparatusRecipe<ArmorUpgradeRecipe> {
    public EmiArmorUpgradeRecipe(ResourceLocation id, ArmorUpgradeRecipe recipe) {
        super(id, recipe);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.ARMOR_UPGRADE_CATEGORY;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        this.reset();
        double angleBetweenEach = 360.0 / this.recipe.pedestalItems().size();

        MultiProvider provider = multiProvider;
        List<ItemStack> stacks = PerkRegistry.getPerkProviderItems().stream().filter(item -> item instanceof AnimatedMagicArmor ama && ama.getMinTier() < recipe.tier).map(Item::getDefaultInstance).toList();
        List<EmiStack> emiInputs = new ArrayList<>();
        List<EmiStack> emiOutputs = new ArrayList<>();

        for (ItemStack stack : stacks){
            ItemStack copy = stack.copy();
            List<List<PerkSlot>> perkProvider = PerkRegistry.getPerkProvider(stack);
            if (perkProvider != null) {
                ArmorPerkHolder perkHolder = stack.getOrDefault(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder(null, List.of(), 0, new HashMap<>()));
                stack.set(DataComponentRegistry.ARMOR_PERKS, perkHolder.setTier(recipe.tier-1));
                ArmorPerkHolder copyHolder = copy.getOrDefault(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder(null, List.of(), 0, new HashMap<>()));
                copy.set(DataComponentRegistry.ARMOR_PERKS, copyHolder.setTier(recipe.tier));
            }
            emiInputs.add(EmiStack.of(stack));
            emiOutputs.add(EmiStack.of(copy));
        }

        widgets.addSlot(EmiIngredient.of(emiInputs), (int) this.center.x, (int) this.center.y);
        widgets.addSlot(EmiIngredient.of(emiOutputs), 93, 3).recipeContext(this);

        for (EmiIngredient input : provider.getEmiInputs()) {
            widgets.addSlot(input, (int) point.x, (int) point.y);
            point = rotatePointAbout(point, center, angleBetweenEach);
        }

        widgets.addText(Component.translatable("ars_nouveau.tier", 1 + recipe.tier), 0, 0, 10,false);

        this.addSourceWidget(widgets);
    }
}
