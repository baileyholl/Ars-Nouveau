package com.hollingsworth.arsnouveau.common.armor;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

// https://github.com/TheKiritoPlayer20/SuperTools/blob/SuperTools_1.14/src/main/java/me/KG20/supertools/Armor/BasisArmorMaterial.java
public class Materials {

    public static final ModdedArmorMaterial LIGHT = new ModdedArmorMaterial("an_light", 25, new int[]{1, 3, 5, 2},
            30, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, () -> Ingredient.of(ItemsRegistry.MAGE_FIBER));

    public static final ModdedArmorMaterial MEDIUM = new ModdedArmorMaterial("an_medium", 25, new int[]{2, 5, 6, 2},
            30, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, () -> Ingredient.of(ItemsRegistry.MAGE_FIBER));

    public static final ModdedArmorMaterial HEAVY = new ModdedArmorMaterial("an_heavy", 33, new int[]{3, 6, 8, 3},
            30, SoundEvents.ARMOR_EQUIP_LEATHER, 2.0f, () -> Ingredient.of(ItemsRegistry.MAGE_FIBER));

    @Deprecated(forRemoval = true)
    public static class ModdedArmorMaterial implements ArmorMaterial {

        private static final int[] Max_Damage_Array = new int[]{13, 15, 16, 11};
        private final String name;
        private final int maxDamageFactor;
        private final int[] damageReductionAmountArray;
        private final int enchantability;

        public ModdedArmorMaterial(String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, SoundEvent soundEvent, float toughness, Supplier<Ingredient> supplier) {
            this.name = name;
            this.maxDamageFactor = maxDamageFactor;
            this.damageReductionAmountArray = damageReductionAmountArray;
            this.enchantability = enchantability;
            this.soundEvent = soundEvent;
            this.toughness = toughness;
            this.repairMaterial = new LazyLoadedValue<>(supplier);
        }

        private final SoundEvent soundEvent;
        private final float toughness;
        private final LazyLoadedValue<Ingredient> repairMaterial;
        
        @Override
        public int getDurabilityForType(ArmorItem.Type p_266807_) {
            return Max_Damage_Array[p_266807_.getSlot().getIndex()] * maxDamageFactor;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type p_267168_) {
            return damageReductionAmountArray[p_267168_.getSlot().getIndex()];
        }

        @Override
        public int getEnchantmentValue() {
            return enchantability;
        }

        @Override
        public SoundEvent getEquipSound() {
            return soundEvent;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return repairMaterial.get();
        }
        
        @Override
        public String getName() {
            return name;
        }

        @Override
        public float getToughness() {
            return toughness;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    }
}
