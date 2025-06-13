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
        var upgradable = this.getUpgradable();
        List<EmiStack> emiInputs = upgradable.from;
        List<EmiStack> emiOutputs = upgradable.to;

        widgets.addSlot(EmiIngredient.of(emiInputs), (int) this.center.x, (int) this.center.y);
        widgets.addSlot(EmiIngredient.of(emiOutputs), 100, 3).recipeContext(this);

        for (EmiIngredient input : provider.getEmiInputs()) {
            widgets.addSlot(input, (int) point.x, (int) point.y);
            point = rotatePointAbout(point, center, angleBetweenEach);
        }

        widgets.addText(Component.translatable("ars_nouveau.tier", 1 + recipe.tier), 0, 0, 10, false);

        this.addSourceWidget(widgets);
    }

    @Override
    protected List<EmiIngredient> generateInputs() {
        var inputs = super.generateInputs();
        if (!inputs.isEmpty()) {
            inputs.set(0, EmiIngredient.of(this.getUpgradable().from));
        }
        return inputs;
    }

    protected record Upgrades(List<EmiStack> from, List<EmiStack> to) {}

    protected Upgrades upgradableCache = null;

    private Upgrades getUpgradable() {
        if (upgradableCache != null) {
            return upgradableCache;
        }

        List<EmiStack> fromList = new ArrayList<>();
        List<EmiStack> toList = new ArrayList<>();

        List<ItemStack> stacks = PerkRegistry.getPerkProviderItems().stream().filter(item -> item instanceof AnimatedMagicArmor ama && ama.getMinTier() < recipe.tier).map(Item::getDefaultInstance).toList();

        for (ItemStack from : stacks) {
            ItemStack to = from.copy();
            List<List<PerkSlot>> perkProvider = PerkRegistry.getPerkProvider(from);
            if (perkProvider != null) {
                ArmorPerkHolder perkHolder = from.getOrDefault(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder(null, List.of(), 0, new HashMap<>()));
                from.set(DataComponentRegistry.ARMOR_PERKS, perkHolder.setTier(recipe.tier - 1));
                ArmorPerkHolder copyHolder = to.getOrDefault(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder(null, List.of(), 0, new HashMap<>()));
                to.set(DataComponentRegistry.ARMOR_PERKS, copyHolder.setTier(recipe.tier));
            }

            fromList.add(EmiStack.of(from));
            toList.add(EmiStack.of(to));
        }

        upgradableCache = new Upgrades(fromList, toList);
        return upgradableCache;
    }
}
