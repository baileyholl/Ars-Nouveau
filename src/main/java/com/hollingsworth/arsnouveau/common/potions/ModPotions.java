package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.recipe.PotionIngredient;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.lib.LibPotions;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;

public class ModPotions {

    //TODO change holders
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
    public static final MobEffect SPELL_DAMAGE_EFFECT = new PublicEffect(MobEffectCategory.BENEFICIAL, new ParticleColor(30, 200, 200).getColor()).setRegistryName(ArsNouveau.MODID, LibPotions.SPELL_DAMAGE);
    public static final MobEffect FAMILIAR_SICKNESS_EFFECT = new PublicEffect(MobEffectCategory.NEUTRAL, new ParticleColor(30, 200, 200).getColor(), new ArrayList<>()).setRegistryName(ArsNouveau.MODID, LibPotions.FAMILIAR_SICKNESS);
    public static final BounceEffect BOUNCE_EFFECT = new BounceEffect();
    public static final MagicFindEffect MAGIC_FIND_EFFECT = new MagicFindEffect();


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

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(new PotionIngredient(AWKWARD), Ingredient.of(BlockRegistry.SOURCEBERRY_BUSH),  manaPot));
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

        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(water), Ingredient.of(ItemsRegistry.ABJURATION_ESSENCE), PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)));
    }

    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerEffects(final RegisterEvent event) {
            final IForgeRegistry<MobEffect> registry = event.getForgeRegistry();
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
                    BOUNCE_EFFECT,
                    MAGIC_FIND_EFFECT
            );
        }

        @SubscribeEvent
        public static void registerPotions(final RegisterEvent event) {
            final IForgeRegistry<Potion> registry = event.getForgeRegistry();

            registry.register((LibPotions.MANA_REGEN), new Potion(new MobEffectInstance(MANA_REGEN_EFFECT, 3600)));
            registry.register((LibPotions.MANA_REGEN_LONG), new Potion(new MobEffectInstance(MANA_REGEN_EFFECT, 9600)));
            registry.register((LibPotions.MANA_REGEN_STRONG), new Potion(new MobEffectInstance(MANA_REGEN_EFFECT, 3600, 1)));
            registry.register((LibPotions.SPELL_DAMAGE), new Potion(new MobEffectInstance(SPELL_DAMAGE_EFFECT, 3600)));
            registry.register((LibPotions.SPELL_DAMAGE_LONG), new Potion(new MobEffectInstance(SPELL_DAMAGE_EFFECT, 9600)));
            registry.register((LibPotions.SPELL_DAMAGE_STRONG), new Potion(new MobEffectInstance(SPELL_DAMAGE_EFFECT, 3600, 1)));
        }
    }
}