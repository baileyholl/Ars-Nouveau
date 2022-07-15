package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.api.recipe.PotionIngredient;
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

    public static final RegistryObject<Potion> MANA_REGEN_POTION = POTIONS.register(potion(MANA_REGEN), () -> new Potion(new MobEffectInstance(MANA_REGEN_EFFECT.get(), 3600)));
    public static final RegistryObject<Potion> LONG_MANA_REGEN_POTION = POTIONS.register(longPotion(MANA_REGEN), () -> new Potion(new MobEffectInstance(MANA_REGEN_EFFECT.get(), 9600)));
    public static final RegistryObject<Potion> STRONG_MANA_REGEN_POTION = POTIONS.register(strongPotion(MANA_REGEN), () -> new Potion(new MobEffectInstance(MANA_REGEN_EFFECT.get(), 3600, 1)));

    public static final RegistryObject<Potion> SPELL_DAMAGE_POTION = POTIONS.register(potion(SPELL_DAMAGE), () -> new Potion(new MobEffectInstance(SPELL_DAMAGE_EFFECT.get(), 3600)));
    public static final RegistryObject<Potion> SPELL_DAMAGE_POTION_LONG = POTIONS.register(longPotion(SPELL_DAMAGE), () -> new Potion(new MobEffectInstance(SPELL_DAMAGE_EFFECT.get(), 9600)));
    public static final RegistryObject<Potion> SPELL_DAMAGE_POTION_STRONG = POTIONS.register(strongPotion(SPELL_DAMAGE), () -> new Potion(new MobEffectInstance(SPELL_DAMAGE_EFFECT.get(), 3600, 1)));

    public static void addRecipes() {
        ItemStack AWKWARD = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD);

        ItemStack manaPot = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.MANA_REGEN_POTION.get());
        ItemStack manaPotLong = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_MANA_REGEN_POTION.get());
        ItemStack manaPotStrong = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STRONG_MANA_REGEN_POTION.get());

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(AWKWARD), Ingredient.of(BlockRegistry.SOURCEBERRY_BUSH), manaPot));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(manaPot), Ingredient.of(Items.GLOWSTONE_DUST), manaPotStrong));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(manaPot), Ingredient.of(Items.REDSTONE), manaPotLong));

        ItemStack sDamagePot = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.SPELL_DAMAGE_POTION.get());
        ItemStack sDamagePotLong = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.SPELL_DAMAGE_POTION_LONG.get());
        ItemStack sDamagePotStrong = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.SPELL_DAMAGE_POTION_STRONG.get());

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(AWKWARD), Ingredient.of(ItemsRegistry.MAGE_BLOOM), sDamagePot));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(sDamagePot), Ingredient.of(Items.GLOWSTONE_DUST), sDamagePotStrong));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(sDamagePot), Ingredient.of(Items.REDSTONE), sDamagePotLong));

        ItemStack water = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(water), Ingredient.of(ItemsRegistry.WILDEN_WING), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LEAPING)));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(water), Ingredient.of(ItemsRegistry.WILDEN_HORN), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRENGTH)));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(water), Ingredient.of(ItemsRegistry.WILDEN_SPIKE), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LONG_WATER_BREATHING)));

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(water), Ingredient.of(ItemsRegistry.ABJURATION_ESSENCE), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)));
    }

}