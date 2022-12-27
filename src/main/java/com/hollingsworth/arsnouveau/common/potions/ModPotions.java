package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.PartialNBTIngredient;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;
import static com.hollingsworth.arsnouveau.common.lib.LibPotions.*;

public class ModPotions {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);

    public static final RegistryObject<MobEffect> SHOCKED_EFFECT = EFFECTS.register(SHOCKED, ShockedEffect::new);
    public static final RegistryObject<MobEffect> MANA_REGEN_EFFECT = EFFECTS.register(MANA_REGEN, ManaRegenEffect::new);
    public static final RegistryObject<MobEffect> SUMMONING_SICKNESS_EFFECT = EFFECTS.register(SUMMONING_SICKNESS, SummoningSicknessEffect::new);
    public static final RegistryObject<MobEffect> HEX_EFFECT = EFFECTS.register(HEX, HexEffect::new);
    public static final RegistryObject<MobEffect> SCRYING_EFFECT = EFFECTS.register(SCRYING, ScryingEffect::new);
    public static final RegistryObject<MobEffect> GLIDE_EFFECT = EFFECTS.register(GLIDE, GlideEffect::new);
    public static final RegistryObject<MobEffect> SNARE_EFFECT = EFFECTS.register(SNARE, SnareEffect::new);
    public static final RegistryObject<MobEffect> FLIGHT_EFFECT = EFFECTS.register(FLIGHT, FlightEffect::new);
    public static final RegistryObject<MobEffect> GRAVITY_EFFECT = EFFECTS.register(GRAVITY, GravityEffect::new);
    public static final RegistryObject<MobEffect> SPELL_DAMAGE_EFFECT = EFFECTS.register(SPELL_DAMAGE, () -> new PublicEffect(MobEffectCategory.BENEFICIAL, new ParticleColor(30, 200, 200).getColor()));
    public static final RegistryObject<MobEffect> FAMILIAR_SICKNESS_EFFECT = EFFECTS.register(FAMILIAR_SICKNESS, () -> new PublicEffect(MobEffectCategory.NEUTRAL, new ParticleColor(30, 200, 200).getColor(), new ArrayList<>()));
    public static final RegistryObject<MobEffect> BOUNCE_EFFECT = EFFECTS.register(BOUNCE, BounceEffect::new);
    public static final RegistryObject<MobEffect> MAGIC_FIND_EFFECT = EFFECTS.register(MAGIC_FIND, MagicFindEffect::new);

    public static final RegistryObject<MobEffect> RECOVERY_EFFECT = EFFECTS.register(RECOVERY, RecoveryEffect::new);
    public static final RegistryObject<MobEffect> BLAST_EFFECT = EFFECTS.register(BLAST, BlastEffect::new);
    public static final RegistryObject<MobEffect> FREEZING_EFFECT = EFFECTS.register(FREEZING, FreezingEffect::new);
    public static final RegistryObject<MobEffect> DEFENCE_EFFECT = EFFECTS.register(DEFENCE, DefenceEffect::new);

    public static final RegistryObject<Potion> MANA_REGEN_POTION = POTIONS.register(potion(MANA_REGEN), () -> new Potion(new MobEffectInstance(MANA_REGEN_EFFECT.get(), 3600)));
    public static final RegistryObject<Potion> LONG_MANA_REGEN_POTION = POTIONS.register(longPotion(MANA_REGEN), () -> new Potion(new MobEffectInstance(MANA_REGEN_EFFECT.get(), 9600)));
    public static final RegistryObject<Potion> STRONG_MANA_REGEN_POTION = POTIONS.register(strongPotion(MANA_REGEN), () -> new Potion(new MobEffectInstance(MANA_REGEN_EFFECT.get(), 3600, 1)));

    public static final RegistryObject<Potion> SPELL_DAMAGE_POTION = POTIONS.register(potion(SPELL_DAMAGE), () -> new Potion(new MobEffectInstance(SPELL_DAMAGE_EFFECT.get(), 3600)));
    public static final RegistryObject<Potion> SPELL_DAMAGE_POTION_LONG = POTIONS.register(longPotion(SPELL_DAMAGE), () -> new Potion(new MobEffectInstance(SPELL_DAMAGE_EFFECT.get(), 9600)));
    public static final RegistryObject<Potion> SPELL_DAMAGE_POTION_STRONG = POTIONS.register(strongPotion(SPELL_DAMAGE), () -> new Potion(new MobEffectInstance(SPELL_DAMAGE_EFFECT.get(), 3600, 1)));

    public static final RegistryObject<Potion> RECOVERY_POTION = POTIONS.register(potion(RECOVERY), () -> new Potion(new MobEffectInstance(RECOVERY_EFFECT.get(), 3600)));
    public static final RegistryObject<Potion> LONG_RECOVERY_POTION = POTIONS.register(longPotion(RECOVERY), () -> new Potion(new MobEffectInstance(RECOVERY_EFFECT.get(), 9600)));
    public static final RegistryObject<Potion> STRONG_RECOVERY_POTION = POTIONS.register(strongPotion(RECOVERY), () -> new Potion(new MobEffectInstance(RECOVERY_EFFECT.get(), 3600, 1)));

    public static final RegistryObject<Potion> BLAST_POTION = POTIONS.register(potion(BLAST), () -> new Potion(new MobEffectInstance(BLAST_EFFECT.get(), 200)));
    public static final RegistryObject<Potion> LONG_BLAST_POTION = POTIONS.register(longPotion(BLAST), () -> new Potion(new MobEffectInstance(BLAST_EFFECT.get(), 400)));
    public static final RegistryObject<Potion> STRONG_BLAST_POTION = POTIONS.register(strongPotion(BLAST), () -> new Potion(new MobEffectInstance(BLAST_EFFECT.get(), 140, 1)));

    public static final RegistryObject<Potion> FREEZING_POTION = POTIONS.register(potion(FREEZING), () -> new Potion(new MobEffectInstance(FREEZING_EFFECT.get(), 1800)));
    public static final RegistryObject<Potion> LONG_FREEZING_POTION = POTIONS.register(longPotion(FREEZING), () -> new Potion(new MobEffectInstance(FREEZING_EFFECT.get(), 3600)));
    public static final RegistryObject<Potion> STRONG_FREEZING_POTION = POTIONS.register(strongPotion(FREEZING), () -> new Potion(new MobEffectInstance(FREEZING_EFFECT.get(), 1800, 1)));

    public static final RegistryObject<Potion> DEFENCE_POTION = POTIONS.register(potion(DEFENCE), () -> new Potion(new MobEffectInstance(DEFENCE_EFFECT.get(), 3600)));
    public static final RegistryObject<Potion> LONG_DEFENCE_POTION = POTIONS.register(longPotion(DEFENCE), () -> new Potion(new MobEffectInstance(DEFENCE_EFFECT.get(), 9600)));
    public static final RegistryObject<Potion> STRONG_DEFENCE_POTION = POTIONS.register(strongPotion(DEFENCE), () -> new Potion(new MobEffectInstance(DEFENCE_EFFECT.get(), 3600, 1)));

    public static void addRecipes() {

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(Potions.AWKWARD), Ingredient.of(BlockRegistry.SOURCEBERRY_BUSH), stackFor(MANA_REGEN_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(MANA_REGEN_POTION.get()), Ingredient.of(Items.GLOWSTONE_DUST), stackFor(ModPotions.STRONG_MANA_REGEN_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(MANA_REGEN_POTION.get()), Ingredient.of(Items.REDSTONE), stackFor(ModPotions.LONG_MANA_REGEN_POTION.get())));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(Potions.AWKWARD), Ingredient.of(ItemsRegistry.MAGE_BLOOM), stackFor(ModPotions.SPELL_DAMAGE_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(ModPotions.SPELL_DAMAGE_POTION.get()), Ingredient.of(Items.GLOWSTONE_DUST), stackFor(ModPotions.SPELL_DAMAGE_POTION_STRONG.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(ModPotions.SPELL_DAMAGE_POTION.get()), Ingredient.of(Items.REDSTONE), stackFor(ModPotions.SPELL_DAMAGE_POTION_LONG.get())));

        // Recovery potions
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(Potions.AWKWARD), Ingredient.of(BlockRegistry.MENDOSTEEN_POD), stackFor(ModPotions.RECOVERY_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(ModPotions.RECOVERY_POTION.get()), Ingredient.of(Items.GLOWSTONE_DUST), stackFor(ModPotions.STRONG_RECOVERY_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(ModPotions.RECOVERY_POTION.get()), Ingredient.of(Items.REDSTONE), stackFor(ModPotions.LONG_RECOVERY_POTION.get())));

        // Blast potions using Bombegranate
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(Potions.AWKWARD), Ingredient.of(BlockRegistry.BOMBEGRANTE_POD), stackFor(ModPotions.BLAST_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(ModPotions.BLAST_POTION.get()), Ingredient.of(Items.GLOWSTONE_DUST), stackFor(ModPotions.STRONG_BLAST_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(ModPotions.BLAST_POTION.get()), Ingredient.of(Items.REDSTONE), stackFor(ModPotions.LONG_BLAST_POTION.get())));

        // Freezing potions
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(Potions.AWKWARD), Ingredient.of(BlockRegistry.FROSTAYA_POD), stackFor(ModPotions.FREEZING_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(ModPotions.FREEZING_POTION.get()), Ingredient.of(Items.GLOWSTONE_DUST), stackFor(ModPotions.STRONG_FREEZING_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(ModPotions.FREEZING_POTION.get()), Ingredient.of(Items.REDSTONE), stackFor(ModPotions.LONG_FREEZING_POTION.get())));

        // Defence potions
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(Potions.AWKWARD), Ingredient.of(BlockRegistry.BASTION_POD), stackFor(ModPotions.DEFENCE_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(ModPotions.DEFENCE_POTION.get()), Ingredient.of(Items.GLOWSTONE_DUST), stackFor(ModPotions.STRONG_DEFENCE_POTION.get())));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(ModPotions.DEFENCE_POTION.get()), Ingredient.of(Items.REDSTONE), stackFor(ModPotions.LONG_DEFENCE_POTION.get())));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(Potions.WATER), Ingredient.of(ItemsRegistry.WILDEN_WING), stackFor(Potions.LEAPING)));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(Potions.WATER), Ingredient.of(ItemsRegistry.WILDEN_HORN), stackFor(Potions.STRENGTH)));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(Potions.WATER), Ingredient.of(ItemsRegistry.WILDEN_SPIKE), stackFor(Potions.LONG_WATER_BREATHING)));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(fromPotion(Potions.WATER), Ingredient.of(ItemsRegistry.ABJURATION_ESSENCE), stackFor(Potions.AWKWARD)));
    }

    public static PartialNBTIngredient fromPotion(Potion potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        PotionUtils.setPotion(stack, potion);
        return PartialNBTIngredient.of(Items.POTION, stack.getOrCreateTag());
    }

    public static ItemStack stackFor(Potion potion){
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }

}