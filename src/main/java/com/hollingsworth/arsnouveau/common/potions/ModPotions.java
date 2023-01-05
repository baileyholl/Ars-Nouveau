package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.recipe.PotionIngredient;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.lib.LibPotions;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.*;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(ArsNouveau.MODID)
public class ModPotions {

    public static final ShieldEffect SHIELD_POTION = new ShieldEffect();
    public static final ShockedEffect SHOCKED_EFFECT = new ShockedEffect();
    public static final ManaRegenEffect MANA_REGEN_EFFECT = new ManaRegenEffect();
    public static final SummoningSicknessEffect SUMMONING_SICKNESS = new SummoningSicknessEffect();
    public static final HexEffect HEX_EFFECT = new HexEffect();
    public static final ScryingEffect SCRYING_EFFECT = new ScryingEffect();
    public static final GlideEffect GLIDE_EFFECT = new GlideEffect();
    public static final SnareEffect SNARE_EFFECT = new SnareEffect();
    public static final FlightEffect FLIGHT_EFFECT = new FlightEffect();
    public static final GravityEffect GRAVITY_EFFECT = new GravityEffect();
    public static final Effect SPELL_DAMAGE_EFFECT = new PublicEffect(EffectType.BENEFICIAL, new ParticleColor(30, 200, 200).getColor()).setRegistryName(ArsNouveau.MODID, LibPotions.SPELL_DAMAGE);
    public static final Effect FAMILIAR_SICKNESS_EFFECT = new PublicEffect(EffectType.NEUTRAL, new ParticleColor(30, 200, 200).getColor(), new ArrayList<>()).setRegistryName(ArsNouveau.MODID, LibPotions.FAMILIAR_SICKNESS);
    public static final BounceEffect BOUNCE_EFFECT = new BounceEffect();
    @ObjectHolder(LibPotions.MANA_REGEN) public static Potion MANA_REGEN_POTION;
    @ObjectHolder(LibPotions.MANA_REGEN_LONG) public static Potion LONG_MANA_REGEN_POTION;
    @ObjectHolder(LibPotions.MANA_REGEN_STRONG) public static Potion STRONG_MANA_REGEN_POTION;

    @ObjectHolder(LibPotions.SPELL_DAMAGE) public static Potion SPELL_DAMAGE_POTION;
    @ObjectHolder(LibPotions.SPELL_DAMAGE_LONG) public static Potion SPELL_DAMAGE_POTION_LONG;
    @ObjectHolder(LibPotions.SPELL_DAMAGE_STRONG) public static Potion SPELL_DAMAGE_POTION_STRONG;

    public static void addRecipes() {
        ItemStack AWKWARD = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD);

        ItemStack manaPot = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.MANA_REGEN_POTION);
        ItemStack manaPotLong = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_MANA_REGEN_POTION);
        ItemStack manaPotStrong = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STRONG_MANA_REGEN_POTION);

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(AWKWARD), Ingredient.of(BlockRegistry.MANA_BERRY_BUSH),  manaPot));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(manaPot), Ingredient.of(Items.GLOWSTONE_DUST),  manaPotStrong));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(manaPot), Ingredient.of(Items.REDSTONE),  manaPotLong));


        ItemStack sDamagePot = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.SPELL_DAMAGE_POTION);
        ItemStack sDamagePotLong = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.SPELL_DAMAGE_POTION_LONG);
        ItemStack sDamagePotStrong = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.SPELL_DAMAGE_POTION_STRONG);

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(AWKWARD), Ingredient.of(ItemsRegistry.MAGE_BLOOM),  sDamagePot));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(sDamagePot), Ingredient.of(Items.GLOWSTONE_DUST),  sDamagePotStrong));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(sDamagePot), Ingredient.of(Items.REDSTONE),  sDamagePotLong));


        ItemStack water = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(water), Ingredient.of(ItemsRegistry.WILDEN_WING), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LEAPING)));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(water), Ingredient.of(ItemsRegistry.WILDEN_HORN), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRENGTH)));
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(water), Ingredient.of(ItemsRegistry.WILDEN_SPIKE), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LONG_WATER_BREATHING)));
    }

    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerEffects(final RegistryEvent.Register<Effect> event) {
            final IForgeRegistry<Effect> registry = event.getRegistry();
            registry.registerAll(SCRYING_EFFECT,
                    SHIELD_POTION,
                    MANA_REGEN_EFFECT,
                    SUMMONING_SICKNESS,
                    SHOCKED_EFFECT,
                    HEX_EFFECT,
                    GLIDE_EFFECT,
                    SNARE_EFFECT,
                    FLIGHT_EFFECT,
                    GRAVITY_EFFECT,
                    SPELL_DAMAGE_EFFECT,
                    FAMILIAR_SICKNESS_EFFECT,
                    BOUNCE_EFFECT
            );
        }

        @SubscribeEvent
        public static void registerPotions(final RegistryEvent.Register<Potion> event) {
            final IForgeRegistry<Potion> registry = event.getRegistry();

            registry.register(new Potion(new EffectInstance(MANA_REGEN_EFFECT, 3600)).setRegistryName(LibPotions.MANA_REGEN));
            registry.register(new Potion(new EffectInstance(MANA_REGEN_EFFECT, 9600)).setRegistryName(LibPotions.MANA_REGEN_LONG));
            registry.register(new Potion(new EffectInstance(MANA_REGEN_EFFECT, 3600, 1)).setRegistryName(LibPotions.MANA_REGEN_STRONG));
            registry.register(new Potion(new EffectInstance(SPELL_DAMAGE_EFFECT, 3600)).setRegistryName(LibPotions.SPELL_DAMAGE));
            registry.register(new Potion(new EffectInstance(SPELL_DAMAGE_EFFECT, 9600)).setRegistryName(LibPotions.SPELL_DAMAGE_LONG));
            registry.register(new Potion(new EffectInstance(SPELL_DAMAGE_EFFECT, 3600, 1)).setRegistryName(LibPotions.SPELL_DAMAGE_STRONG));
        }
    }
}