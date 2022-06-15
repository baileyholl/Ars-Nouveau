package com.hollingsworth.arsnouveau.api.loot;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.datagen.DungeonLootGenerator;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.*;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class DungeonLootTables {

    public static List<Supplier<ItemStack>> BASIC_LOOT = new ArrayList<>();
    public static List<Supplier<ItemStack>> UNCOMMON_LOOT = new ArrayList<>();
    public static List<Supplier<ItemStack>> RARE_LOOT = new ArrayList<>();
 // /setblock ~ ~ ~ minecraft:chest{LootTable:"minecraft:chests/simple_dungeon"}
    public static Random r = new Random();
    static {
        BASIC_LOOT.add(() -> new ItemStack(ItemsRegistry.SOURCE_GEM,1 + r.nextInt(5)));
        BASIC_LOOT.add(() -> new ItemStack(ItemsRegistry.WILDEN_HORN,1 + r.nextInt(3)));
        BASIC_LOOT.add(() -> new ItemStack(ItemsRegistry.WILDEN_SPIKE, 1 + r.nextInt(3)));
        BASIC_LOOT.add(() -> new ItemStack(ItemsRegistry.WILDEN_WING, 1 + r.nextInt(3)));
        BASIC_LOOT.add(() -> new ItemStack(BlockRegistry.SOURCEBERRY_BUSH, 1 + r.nextInt(3)));
        BASIC_LOOT.add(() ->{
            ItemStack stack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(stack, ModPotions.LONG_MANA_REGEN_POTION.get());
            return stack;
        });

        BASIC_LOOT.add(() ->{
            ItemStack stack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(stack, ModPotions.STRONG_MANA_REGEN_POTION.get());
            return stack;
        });

        BASIC_LOOT.add(() ->{
            ItemStack stack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(stack, ModPotions.MANA_REGEN_POTION.get());
            return stack;
        });


        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.WARP_SCROLL, 1 + r.nextInt(2)));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.STARBUNCLE_SHARD));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.WHIRLISPRIG_SHARDS));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.DRYGMY_SHARD));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.WIXIE_SHARD));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.AMPLIFY_ARROW, 16 + r.nextInt(16)));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.SPLIT_ARROW, 16 + r.nextInt(16)));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.PIERCE_ARROW, 16 + r.nextInt(16)));

        UNCOMMON_LOOT.add(() ->{
            List<RitualTablet> tablets = new ArrayList<>(ArsNouveauAPI.getInstance().getRitualItemMap().values());
            return new ItemStack(tablets.get(r.nextInt(tablets.size())));
        });

        RARE_LOOT.add(() -> makeTome("Xacris' Tiny Hut", new Spell()
                .add(MethodUnderfoot.INSTANCE)
                .add(EffectPhantomBlock.INSTANCE)
                .add(AugmentAOE.INSTANCE, 3)
                .add(AugmentPierce.INSTANCE, 3)
        ,"Builds a small hut around the user."));
        RARE_LOOT.add(() -> makeTome("Glow Trap", new Spell()
                .add(MethodTouch.INSTANCE)
                .add(EffectRune.INSTANCE)
                .add(EffectSnare.INSTANCE)
                .add(AugmentExtendTime.INSTANCE)
                .add(EffectLight.INSTANCE)
        , "Snares the target and grants other targets Glowing."));

        RARE_LOOT.add(() -> makeTome("Bailey's Bovine Rocket", new Spell()
                .add(MethodProjectile.INSTANCE)
                .add(EffectLaunch.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2)
                .add(EffectDelay.INSTANCE)
                .add(EffectExplosion.INSTANCE)
                .add(AugmentAmplify.INSTANCE)
        ));
        RARE_LOOT.add(() -> makeTome("Arachne's Weaving", new Spell()
                .add(MethodProjectile.INSTANCE)
                .add(AugmentSplit.INSTANCE, 2)
                .add(EffectSnare.INSTANCE )
                .add(AugmentExtendTime.INSTANCE)
                .add(AugmentExtendTime.INSTANCE)
        , "Creates three snaring projectiles."));
        RARE_LOOT.add(() -> makeTome("Warp Impact", new Spell()
                .add(MethodProjectile.INSTANCE)
                .add(EffectBlink.INSTANCE)
                .add(EffectExplosion.INSTANCE )
                .add(AugmentAOE.INSTANCE)
        , "Teleportation, with style!"));

        RARE_LOOT.add(() -> makeTome("Farfalla's Frosty Flames", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectIgnite.INSTANCE)
                        .add(EffectDelay.INSTANCE )
                        .add(EffectConjureWater.INSTANCE)
                        .add(EffectFreeze.INSTANCE)
                , "Creates a fire that quickly freezes to ice."));

        RARE_LOOT.add(() -> makeTome("Gootastic's Telekinetic Fishing Rod", new Spell()
                .add(MethodProjectile.INSTANCE)
                .add(EffectLaunch.INSTANCE)
                .add(AugmentAmplify.INSTANCE,2)
                .add(EffectDelay.INSTANCE)
                .add(EffectPull.INSTANCE)
                .add(AugmentAmplify.INSTANCE,2), "The squid's Lovecraftian roots appear to make it immune."
        ));

        RARE_LOOT.add(() -> makeTome("Potent Toxin", new Spell()
                .add(MethodProjectile.INSTANCE)
                .add(EffectHex.INSTANCE)
                .add(EffectHarm.INSTANCE)
                .add(AugmentExtendTime.INSTANCE),
                "Poisons that target and causes them to take additional damage from all sources."
        ));
        RARE_LOOT.add(() -> makeTome("The Shadow's Temporary Tunnel", new Spell()
                        .add(MethodTouch.INSTANCE)
                        .add(EffectIntangible.INSTANCE)
                        .add(AugmentAOE.INSTANCE, 2)
                        .add(AugmentPierce.INSTANCE, 5)
                        .add(AugmentExtendTime.INSTANCE),
                "Creates a temporary tunnel of blocks."
        ));

        RARE_LOOT.add(() -> makeTome("Vault", new Spell()
                        .add(MethodSelf.INSTANCE)
                        .add(EffectLaunch.INSTANCE)
                        .add(EffectDelay.INSTANCE)
                        .add(EffectLeap.INSTANCE)
                        .add(EffectSlowfall.INSTANCE),
                "Sometimes you just need to get over that wall."
        ));

        RARE_LOOT.add(() -> makeTome("Fireball!", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectIgnite.INSTANCE)
                        .add(EffectExplosion.INSTANCE)
                        .add(AugmentAmplify.INSTANCE, 2)
                        .add(AugmentAOE.INSTANCE, 2),
                "A classic."
        ));
        RARE_LOOT.add(() -> makeTome("Rune of Renewing", new Spell()
                        .add(MethodTouch.INSTANCE)
                        .add(EffectRune.INSTANCE)
                        .add(EffectDispel.INSTANCE)
                        .add(EffectHeal.INSTANCE)
                        .add(AugmentAmplify.INSTANCE),
                "Cures status effects and heals the user."
        ));

        RARE_LOOT.add(() -> makeTome("Knocked out of Orbit", new Spell()
                        .add(MethodOrbit.INSTANCE)
                        .add(EffectLaunch.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2)
                        .add(EffectDelay.INSTANCE)
                .add(EffectKnockback.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2), "Summons orbiting projectiles that will launch nearby enemies.")
        );

        RARE_LOOT.add(() -> makeTome("Takeoff!", new Spell().add(MethodSelf.INSTANCE)
                .add(EffectLaunch.INSTANCE, 2)
                .add(EffectGlide.INSTANCE)
                .add(AugmentDurationDown.INSTANCE), "Launches the caster into the air and grants temporary elytra flight!"));
        RARE_LOOT.add(() -> makeTome("KirinDave's Sinister Switch", new Spell()
                .add(MethodSelf.INSTANCE)
                .add(EffectSummonDecoy.INSTANCE)
                .add(EffectBlink.INSTANCE)
                .add(AugmentAmplify.INSTANCE), "Heroes are so straightforward, so easily befuddled..." , new ParticleColor(25, 255, 255)));

        RARE_LOOT.add(() -> makeTome("Xacris's Firework Display", new Spell()
                .add(MethodProjectile.INSTANCE)
                .add(EffectLinger.INSTANCE)
                .add(AugmentSensitive.INSTANCE)
                .add(AugmentAOE.INSTANCE)
                .add(EffectFirework.INSTANCE)
                .add(AugmentExtendTime.INSTANCE, 4)
                .add(AugmentAmplify.INSTANCE), "Light up the sky" , new ParticleColor(255, 255, 255)));
    }

    public static ItemStack getRandomItem(List<Supplier<ItemStack>> pool){
        return pool.isEmpty() ? ItemStack.EMPTY : pool.get(r.nextInt(pool.size())).get();
    }

    public static List<ItemStack> getRandomRoll(DungeonLootGenerator.DungeonLootEnhancerModifier modifier){
        List<ItemStack> stacks = new ArrayList<>();

        for(int i = 0; i < modifier.commonRolls; i++){
            if(r.nextDouble() <= modifier.commonChance)
                stacks.add(getRandomItem(BASIC_LOOT));
        }

        for(int i = 0; i < modifier.uncommonRolls; i++){
            if(r.nextDouble() <= modifier.uncommonChance)
                stacks.add(getRandomItem(UNCOMMON_LOOT));
        }
        if(Config.SPAWN_TOMES.get()) {
            for (int i = 0; i < modifier.rareRolls; i++) {
                if (r.nextDouble() <= modifier.rareChance)
                    stacks.add(getRandomItem(RARE_LOOT));
            }
        }
        return stacks;
    }

    public static ItemStack makeTome(String name, Spell spell){
        ItemStack stack = new ItemStack(ItemsRegistry.CASTER_TOME);
        ISpellCaster spellCaster = CasterUtil.getCaster(stack);
        spellCaster.setSpell(spell);
        stack.setHoverName(Component.literal(name).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true)));
        return stack;
    }

    public static ItemStack makeTome(String name, Spell spell, String flavorText){
        ItemStack stack = makeTome(name, spell);
        ISpellCaster spellCaster =  CasterUtil.getCaster(stack);
        spellCaster.setFlavorText(flavorText);
        return stack;
    }
    public static ItemStack makeTome(String name, Spell spell, String flavorText, ParticleColor particleColor){
        ItemStack stack = makeTome(name, spell);
        ISpellCaster spellCaster =  CasterUtil.getCaster(stack);
        spellCaster.setFlavorText(flavorText);
        spellCaster.setColor(particleColor.toWrapper());
        return stack;
    }
}
