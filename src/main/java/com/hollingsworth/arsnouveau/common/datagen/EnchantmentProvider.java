package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EnchantmentProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.ENCHANTMENT, EnchantmentProvider::bootstrap);


    public static void bootstrap(BootstrapContext<Enchantment> ctx) {
        HolderGetter<Item> holdergetter2 = ctx.lookup(Registries.ITEM);
        register(ctx, EnchantmentRegistry.MANA_BOOST_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
                holdergetter2.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                5,
                3,
                Enchantment.dynamicCost(1, 11),
                Enchantment.dynamicCost(12, 11),
                1,
                EquipmentSlotGroup.ARMOR
        )).withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                new EnchantmentAttributeEffect(
                        ArsNouveau.prefix("enchantment.max_mana"),
                        PerkAttributes.MAX_MANA,
                        LevelBasedValue.perLevel(25F),
                        AttributeModifier.Operation.ADD_VALUE
                )));

        register(ctx, EnchantmentRegistry.MANA_REGEN_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
                holdergetter2.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                5,
                3,
                Enchantment.dynamicCost(1, 11),
                Enchantment.dynamicCost(12, 11),
                1,
                EquipmentSlotGroup.ARMOR
        )).withEffect(EnchantmentEffectComponents.ATTRIBUTES,
                new EnchantmentAttributeEffect(
                        ArsNouveau.prefix("enchantment.mana_regen"),
                        PerkAttributes.MANA_REGEN_BONUS,
                        LevelBasedValue.perLevel(2.0F),
                        AttributeModifier.Operation.ADD_VALUE
                )));

        register(ctx, EnchantmentRegistry.REACTIVE_ENCHANTMENT, Enchantment.enchantment(Enchantment.definition(
                new AnyHolderSet<>(BuiltInRegistries.ITEM.asLookup()),
                1,
                4,
                Enchantment.dynamicCost(1, 11),
                Enchantment.dynamicCost(12, 11),
                1,
                EquipmentSlotGroup.ANY
        )));
    }

    protected static void register(BootstrapContext<Enchantment> ctx, ResourceKey<Enchantment> enchantment, Enchantment.Builder builder) {
        ctx.register(enchantment, builder.build(enchantment.location()));
    }

    public EnchantmentProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ArsNouveau.MODID));
    }

    @Override
    @NotNull
    public String getName() {
        return "Ars Nouveau's Enchantment Data";
    }


    public static class EnchantmentTagsProvider extends net.minecraft.data.tags.EnchantmentTagsProvider {

        public EnchantmentTagsProvider(PackOutput pPackOutput, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pPackOutput, provider, ArsNouveau.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider pProvider) {

            this.tag(EnchantmentTags.NON_TREASURE).addOptional(
                    EnchantmentRegistry.MANA_BOOST_ENCHANTMENT.location())
                    .addOptional(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT.location());
            this.tag(EnchantmentTags.TRADEABLE).addOptional(
                    EnchantmentRegistry.MANA_BOOST_ENCHANTMENT.location()).addOptional(
                    EnchantmentRegistry.MANA_REGEN_ENCHANTMENT.location());
        }
    }

}
