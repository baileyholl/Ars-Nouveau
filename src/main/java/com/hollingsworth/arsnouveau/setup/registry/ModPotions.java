package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.potions.*;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.UUID;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;
import static com.hollingsworth.arsnouveau.common.lib.LibPotions.*;

public class ModPotions {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);

    public static final RegistryObject<MobEffect> SHOCKED_EFFECT = EFFECTS.register(SHOCKED, ShockedEffect::new);

    //TODO Consider switching to vanilla builder method for adding attribute modifiers
    public static final RegistryObject<MobEffect> MANA_REGEN_EFFECT = EFFECTS.register(MANA_REGEN, () -> new PublicEffect(MobEffectCategory.BENEFICIAL, 8080895) {
        @Override
        public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
            AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString("bb72a21d-3e49-4e8e-b81c-3bfa9cf746b0"), this::getDescriptionId, ServerConfig.MANA_REGEN_POTION.get() * (1 + pAmplifier), AttributeModifier.Operation.ADDITION);
            this.getAttributeModifiers().put(PerkAttributes.MANA_REGEN_BONUS.get(), attributemodifier);
            super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        }

    });
    public static final RegistryObject<MobEffect> SUMMONING_SICKNESS_EFFECT = EFFECTS.register(SUMMONING_SICKNESS, () -> new PublicEffect(MobEffectCategory.HARMFUL, 2039587, new ArrayList<>()));

    public static final RegistryObject<MobEffect> HEX_EFFECT = EFFECTS.register(HEX, () -> new PublicEffect(MobEffectCategory.HARMFUL, 8080895) {
        @Override
        public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
            getAttributeModifiers().put(PerkAttributes.MANA_REGEN_BONUS.get(), new AttributeModifier(UUID.fromString("5636ef7d-0136-4205-aabc-fd7cf0d29d05"), this::getDescriptionId, -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL));
            super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        }
    });
    public static final RegistryObject<MobEffect> SCRYING_EFFECT = EFFECTS.register(SCRYING, () -> new PublicEffect(MobEffectCategory.BENEFICIAL, 2039587));
    public static final RegistryObject<MobEffect> GLIDE_EFFECT = EFFECTS.register(GLIDE, () -> new PublicEffect(MobEffectCategory.BENEFICIAL, 8080895));
    public static final RegistryObject<MobEffect> SNARE_EFFECT = EFFECTS.register(SNARE, SnareEffect::new);
    public static final RegistryObject<MobEffect> FLIGHT_EFFECT = EFFECTS.register(FLIGHT, FlightEffect::new);
    public static final RegistryObject<MobEffect> GRAVITY_EFFECT = EFFECTS.register(GRAVITY, GravityEffect::new);
    public static final RegistryObject<MobEffect> SPELL_DAMAGE_EFFECT = EFFECTS.register(SPELL_DAMAGE, () -> new PublicEffect(MobEffectCategory.BENEFICIAL, new ParticleColor(30, 200, 200).getColor()) {
        @Override
        public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
            getAttributeModifiers().put(PerkAttributes.SPELL_DAMAGE_BONUS.get(), new AttributeModifier(UUID.fromString("7a8b8f12-077b-430c-8da7-fd21c95dceb3"), this::getDescriptionId, 1.5F * (1 + pAmplifier), AttributeModifier.Operation.ADDITION));
            super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        }
    });
    public static final RegistryObject<MobEffect> IMMOLATE_EFFECT = EFFECTS.register(IMMOLATE, ImmolateEffect::new);
    public static final RegistryObject<MobEffect> SOAKED_EFFECT = EFFECTS.register(SOAKED, SoggyEffect::new);
    public static final RegistryObject<MobEffect> BOUNCE_EFFECT = EFFECTS.register(BOUNCE, BounceEffect::new);
    public static final RegistryObject<MobEffect> MAGIC_FIND_EFFECT = EFFECTS.register(MAGIC_FIND, MagicFindEffect::new);

    public static final RegistryObject<MobEffect> RECOVERY_EFFECT = EFFECTS.register(RECOVERY, () -> new PublicEffect(MobEffectCategory.BENEFICIAL, new ParticleColor(0, 200, 40).getColor()));
    public static final RegistryObject<MobEffect> BLAST_EFFECT = EFFECTS.register(BLAST, BlastEffect::new);
    public static final RegistryObject<MobEffect> FREEZING_EFFECT = EFFECTS.register(FREEZING, FreezingEffect::new);
    public static final RegistryObject<MobEffect> DEFENCE_EFFECT = EFFECTS.register(DEFENCE, () -> new PublicEffect(MobEffectCategory.BENEFICIAL, new ParticleColor(150, 0, 150).getColor()));

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
        PotionBrewing.addMix(Potions.WATER, ItemsRegistry.ABJURATION_ESSENCE.get(), Potions.AWKWARD);
        PotionBrewing.addMix(Potions.AWKWARD, ItemsRegistry.MAGE_BLOOM.get(), ModPotions.SPELL_DAMAGE_POTION.get());

        PotionBrewing.addMix(ModPotions.SPELL_DAMAGE_POTION.get(), Items.GLOWSTONE_DUST, ModPotions.SPELL_DAMAGE_POTION_STRONG.get());
        PotionBrewing.addMix(ModPotions.SPELL_DAMAGE_POTION.get(), Items.REDSTONE, ModPotions.SPELL_DAMAGE_POTION_LONG.get());

        PotionBrewing.addMix(Potions.AWKWARD, BlockRegistry.SOURCEBERRY_BUSH.asItem(), ModPotions.MANA_REGEN_POTION.get());
        PotionBrewing.addMix(ModPotions.MANA_REGEN_POTION.get(), Items.GLOWSTONE_DUST, ModPotions.STRONG_MANA_REGEN_POTION.get());
        PotionBrewing.addMix(ModPotions.MANA_REGEN_POTION.get(), Items.REDSTONE, ModPotions.LONG_MANA_REGEN_POTION.get());

        PotionBrewing.addMix(Potions.AWKWARD, BlockRegistry.MENDOSTEEN_POD.asItem(), ModPotions.RECOVERY_POTION.get());
        PotionBrewing.addMix(ModPotions.RECOVERY_POTION.get(), Items.GLOWSTONE_DUST, ModPotions.STRONG_RECOVERY_POTION.get());
        PotionBrewing.addMix(ModPotions.RECOVERY_POTION.get(), Items.REDSTONE, ModPotions.LONG_RECOVERY_POTION.get());

        PotionBrewing.addMix(Potions.AWKWARD, BlockRegistry.BOMBEGRANTE_POD.asItem(), ModPotions.BLAST_POTION.get());
        PotionBrewing.addMix(ModPotions.BLAST_POTION.get(), Items.GLOWSTONE_DUST, ModPotions.STRONG_BLAST_POTION.get());
        PotionBrewing.addMix(ModPotions.BLAST_POTION.get(), Items.REDSTONE, ModPotions.LONG_BLAST_POTION.get());

        PotionBrewing.addMix(Potions.AWKWARD, BlockRegistry.FROSTAYA_POD.asItem(), ModPotions.FREEZING_POTION.get());
        PotionBrewing.addMix(ModPotions.FREEZING_POTION.get(), Items.GLOWSTONE_DUST, ModPotions.STRONG_FREEZING_POTION.get());
        PotionBrewing.addMix(ModPotions.FREEZING_POTION.get(), Items.REDSTONE, ModPotions.LONG_FREEZING_POTION.get());

        PotionBrewing.addMix(Potions.AWKWARD, BlockRegistry.BASTION_POD.asItem(), ModPotions.DEFENCE_POTION.get());
        PotionBrewing.addMix(ModPotions.DEFENCE_POTION.get(), Items.GLOWSTONE_DUST, ModPotions.STRONG_DEFENCE_POTION.get());
        PotionBrewing.addMix(ModPotions.DEFENCE_POTION.get(), Items.REDSTONE, ModPotions.LONG_DEFENCE_POTION.get());

        PotionBrewing.addMix(Potions.WATER, ItemsRegistry.WILDEN_WING.get(), Potions.LEAPING);
        PotionBrewing.addMix(Potions.WATER, ItemsRegistry.WILDEN_HORN.get(), Potions.STRENGTH);
        PotionBrewing.addMix(Potions.WATER, ItemsRegistry.WILDEN_SPIKE.get(), Potions.LONG_WATER_BREATHING);
    }
}