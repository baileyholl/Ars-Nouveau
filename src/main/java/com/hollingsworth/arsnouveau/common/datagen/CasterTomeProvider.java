package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CasterTomeData;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.spell.method.MethodUnderfoot;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.SoundRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CasterTomeProvider extends SimpleDataProvider {


    public List<CasterRecipeWrapper> tomes = new ArrayList<>();

    public CasterTomeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {

        tomes.add(buildTome("glow", "Glow Trap", new Spell()
                        .add(MethodTouch.INSTANCE)
                        .add(EffectRune.INSTANCE)
                        .add(EffectSnare.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                        .add(EffectLight.INSTANCE)
                , "Snares the target and grants other targets Glowing."));

        tomes.add(buildTome("bailey", "Bailey's Bovine Rocket", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectLaunch.INSTANCE)
                        .add(AugmentAmplify.INSTANCE, 2)
                        .add(EffectDelay.INSTANCE)
                        .add(EffectExplosion.INSTANCE)
                        .add(AugmentAmplify.INSTANCE)
                , "To the MOOn"));

        tomes.add(buildTome("arachne", "Arachne's Weaving", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(AugmentSplit.INSTANCE, 2)
                        .add(EffectSnare.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                , "Creates three snaring projectiles."));

        tomes.add(buildTome("warp_impact", "Warp Impact", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectBlink.INSTANCE)
                        .add(EffectExplosion.INSTANCE)
                        .add(AugmentAOE.INSTANCE)
                , "Teleportation, with style!"));

        tomes.add(buildTome("farfalla", "Farfalla's Frosty Flames", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectIgnite.INSTANCE)
                        .add(EffectDelay.INSTANCE)
                        .add(EffectConjureWater.INSTANCE)
                        .add(EffectFreeze.INSTANCE)
                , "Creates a fire that quickly freezes to ice."));

        tomes.add(buildTome("gootastic", "Gootastic's Telekinetic Fishing Rod", new Spell()
                .add(MethodProjectile.INSTANCE)
                .add(EffectLaunch.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2)
                .add(EffectDelay.INSTANCE)
                .add(EffectPull.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2), "The squid's Lovecraftian roots appear to make it immune."
        ));

        tomes.add(buildTome("toxin", "Potent Toxin", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectHex.INSTANCE)
                        .add(EffectHarm.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE),
                "Poisons that target and causes them to take additional damage from all sources."
        ));
        tomes.add(buildTome("shadow", "The Shadow's Temporary Tunnel", new Spell()
                        .add(MethodTouch.INSTANCE)
                        .add(EffectIntangible.INSTANCE)
                        .add(AugmentAOE.INSTANCE, 2)
                        .add(AugmentPierce.INSTANCE, 5)
                        .add(AugmentExtendTime.INSTANCE),
                "Creates a temporary tunnel of blocks."
        ));

        tomes.add(buildTome("vault", "Vault", new Spell()
                        .add(MethodSelf.INSTANCE)
                        .add(EffectLaunch.INSTANCE)
                        .add(EffectDelay.INSTANCE)
                        .add(EffectLeap.INSTANCE)
                        .add(EffectSlowfall.INSTANCE),
                "Sometimes you just need to get over that wall."
        ));

        tomes.add(buildTome("fireball", "Fireball!", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectIgnite.INSTANCE)
                        .add(EffectExplosion.INSTANCE)
                        .add(AugmentAmplify.INSTANCE, 2)
                        .add(AugmentAOE.INSTANCE, 2),
                "A classic."
        ));
        tomes.add(buildTome("renew_rune", "Rune of Renewing", new Spell()
                        .add(MethodTouch.INSTANCE)
                        .add(EffectRune.INSTANCE)
                        .add(EffectDispel.INSTANCE)
                        .add(EffectHeal.INSTANCE)
                        .add(AugmentAmplify.INSTANCE),
                "Cures status effects and heals the user."
        ));

        tomes.add(buildTome("yeet", "Knocked out of Orbit", new Spell()
                .add(MethodSelf.INSTANCE)
                .add(EffectOrbit.INSTANCE)
                .add(EffectLaunch.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2)
                .add(EffectDelay.INSTANCE)
                .add(EffectKnockback.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2), "Summons orbiting projectiles that will launch nearby enemies.")
        );

        tomes.add(buildTome("takeoff", "Takeoff!", new Spell().add(MethodSelf.INSTANCE)
                .add(EffectLaunch.INSTANCE, 2)
                .add(EffectGlide.INSTANCE)
                .add(AugmentDurationDown.INSTANCE), "Launches the caster into the air and grants temporary elytra flight!"));

        tomes.add(buildTome("kirin", "KirinDave's Sinister Switch", new Spell()
                .add(MethodSelf.INSTANCE)
                .add(EffectSummonDecoy.INSTANCE)
                .add(EffectBlink.INSTANCE)
                .add(AugmentAmplify.INSTANCE), "Heroes are so straightforward, so easily befuddled...", new ParticleColor(25, 255, 255)));

        tomes.add(buildTome("xacris", "Xacris' Tiny Hut", new Spell()
                        .add(MethodUnderfoot.INSTANCE)
                        .add(EffectPhantomBlock.INSTANCE)
                        .add(AugmentAOE.INSTANCE, 3)
                        .add(AugmentPierce.INSTANCE, 3)
                , "Builds a small hut around the user."));

        tomes.add(buildTome("xacris_2", "Xacris's Firework Display", new Spell()
                .add(MethodProjectile.INSTANCE)
                .add(EffectWall.INSTANCE)
                .add(AugmentSensitive.INSTANCE)
                .add(AugmentAOE.INSTANCE)
                .add(EffectFirework.INSTANCE)
                .add(AugmentExtendTime.INSTANCE, 4)
                .add(AugmentAmplify.INSTANCE), "Light up the sky", new ParticleColor(25, 255, 255)));

        tomes.add(buildTome("othy", "Othy's Misdirection", new Spell()
                .add(MethodSelf.INSTANCE)
                .add(AugmentExtendTime.INSTANCE)
                .add(EffectSummonDecoy.INSTANCE)
                .add(EffectBlink.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2), "Swarm your enemies with bladed spirits.", new ParticleColor(255, 255, 255)));

        tomes.add(buildTome("aurellia", "Aurellia's Bite Storm", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectLinger.INSTANCE)
                        .add(AugmentSensitive.INSTANCE)
                        .add(EffectFangs.INSTANCE)
                        .add(EffectLightning.INSTANCE)
                        .add(AugmentAmplify.INSTANCE, 2)
                        .add(AugmentExtendTime.INSTANCE, 3)
                        .withSound(new ConfiguredSpellSound(SoundRegistry.TEMPESTRY_SPELL_SOUND)),
                "The bite from this storm is worse than its bark.",
                new ParticleColor(255, 119, 203)));

        tomes.add(buildTome("alex", "Alex's Magnificent Mansion", new Spell(MethodSelf.INSTANCE)
                        .add(EffectBurst.INSTANCE)
                        .add(AugmentSensitive.INSTANCE)
                        .add(AugmentDampen.INSTANCE)
                        .add(AugmentAOE.INSTANCE, 5)
                        .add(EffectPhantomBlock.INSTANCE)
                        .add(AugmentAmplify.INSTANCE)
                        .add(AugmentPierce.INSTANCE)
                        .withSound(new ConfiguredSpellSound(SoundRegistry.TEMPESTRY_SPELL_SOUND)),
                "For those who can't settle with just a tiny hut.",
                new RainbowParticleColor(255, 255, 255))
        );


        tomes.add(buildTome("poseidon", "Poseidon's Refuge", new Spell(MethodProjectile.INSTANCE)
                        .add(AugmentSensitive.INSTANCE)
                        .add(EffectLight.INSTANCE)
                        .add(EffectBurst.INSTANCE)
                        .add(AugmentAOE.INSTANCE, 2)
                        .add(AugmentSensitive.INSTANCE)
                        .add(EffectFreeze.INSTANCE)
                        .add(EffectBreak.INSTANCE)
                        .add(EffectFreeze.INSTANCE)
                        .withSound(new ConfiguredSpellSound(SoundRegistry.TEMPESTRY_SPELL_SOUND))
                , "Fire at a body of water to create a Ice bubble in the depths.", new ParticleColor(0,0,255)));

        tomes.add(buildTome("chems", "Chem's Scientific Repulsion Runes", new Spell(MethodProjectile.INSTANCE)
                .add(EffectLinger.INSTANCE)
                        .add(AugmentSensitive.INSTANCE)
                        .add(EffectRune.INSTANCE)
                        .add(EffectLaunch.INSTANCE, 4),
                "Smothers an area with runes that launch entities upward. Do NOT get covered in the Repulsion Runes.", new ParticleColor(61, 207, 248)));


        tomes.add(buildTome("ivy", "Ivy", new Spell(MethodTouch.INSTANCE)
                .add(EffectAnimate.INSTANCE)
                .add(AugmentExtendTime.INSTANCE, 7)
                .add(EffectName.INSTANCE), "Now you never have to be lonely again! You will always have a friend with you! Feel free to change their name to whatever you want! :D",
                new ParticleColor(255, 105, 180)));

        tomes.add(buildTome("darkfira","Darkfira's Flash Freeze", new Spell(MethodProjectile.INSTANCE)
                .add(EffectBurst.INSTANCE)
                .add(AugmentSensitive.INSTANCE)
                .add(AugmentAOE.INSTANCE, 3)
                .add(EffectConjureWater.INSTANCE)
                .add(EffectFreeze.INSTANCE)
                .add(EffectLightning.INSTANCE)
                        .add(AugmentAmplify.INSTANCE)
                        .withSound(new ConfiguredSpellSound(SoundRegistry.TEMPESTRY_SPELL_SOUND, 1.69f, 1.9f)), "Encases your enemies or friends in a tomb of ice. Guaranteed to leave them shocked and confused.",
                new ParticleColor(25, 255, 255)));
        
        tomes.add(buildTome("spinoftw", "You were hurt, but you're fine", new Spell(MethodSelf.INSTANCE)
                .add(EffectConjureWater.INSTANCE)
                .add(EffectHeal.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2)
                .add(EffectPhantomBlock.INSTANCE)
                .add(AugmentAOE.INSTANCE, 3), " A utility spell to aid you in your adventures", new ParticleColor(225, 90 ,1)));

        tomes.add(buildTome("lyrellion", "Lyrellion’s Wall of Force", new Spell(MethodProjectile.INSTANCE)
                .add(AugmentAccelerate.INSTANCE)
                .add(EffectWall.INSTANCE)
                .add(AugmentSensitive.INSTANCE)
                .add(EffectPhantomBlock.INSTANCE)
                .add(AugmentPierce.INSTANCE)
                .add(AugmentAmplify.INSTANCE), "You. Shall. Not. Pass."));

        tomes.add(buildTome("silvanus","Emergency Flair", new Spell(MethodTouch.INSTANCE)
                .add(EffectLight.INSTANCE)
                .add(AugmentExtendTime.INSTANCE, 3), "When you want to go deeper but just don't have the torches", new ParticleColor(0, 255, 0)));

        tomes.add(buildTome("uni","Uni's Windshield", new Spell(MethodSelf.INSTANCE, EffectOrbit.INSTANCE, EffectKnockback.INSTANCE).add(AugmentAmplify.INSTANCE, 8), "Stay away!"));

        tomes.add(buildTome("crowdrone", "Crow's Gluttonous Gaze", new Spell(MethodSelf.INSTANCE, EffectSenseMagic.INSTANCE, AugmentExtendTime.INSTANCE, EffectLight.INSTANCE).add(AugmentExtendTime.INSTANCE, 6), "Crows are known to collect shiny things, even in the darkest of places.", new ParticleColor(0,0,0)));

        tomes.add(buildTome("chosenarchitect","Chosen's Chaotic Shift", new Spell(MethodProjectile.INSTANCE)
                .add(AugmentAccelerate.INSTANCE, 5)
                .add(EffectExchange.INSTANCE)
                .add(AugmentAmplify.INSTANCE)
                .add(AugmentRandomize.INSTANCE)
                .add(EffectPickup.INSTANCE)
                .withSound(new ConfiguredSpellSound(SoundRegistry.GAIA_SPELL_SOUND, 0.55f, 2.0f)), "In a flash of magic, the world twists, and what was once here is now there.", new ParticleColor(255, 114, 32)));

        tomes.add(buildTome("lufia", "Lufia's Bwomp", new Spell(MethodProjectile.INSTANCE)
                .add(AugmentSensitive.INSTANCE)
                .add(AugmentPierce.INSTANCE, 3)
                .add(EffectIntangible.INSTANCE)
                .add(AugmentAOE.INSTANCE, 4)
                .withSound(new ConfiguredSpellSound(SoundRegistry.TEMPESTRY_SPELL_SOUND, 1.0f, 0.5f)),
                "Your enemies will try to kill you. Use this, to kill them back. Cast under a targets feet to bury them. Results may vary.",
                new ParticleColor(255, 128, 1)));

        tomes.add(buildTome("mystifi","Mysti's Gravely Dig", new Spell(MethodUnderfoot.INSTANCE)
                .add(EffectLinger.INSTANCE, AugmentSensitive.INSTANCE)
                .add(AugmentExtendTime.INSTANCE, 2)
                .add(EffectIntangible.INSTANCE)
                .add(AugmentPierce.INSTANCE, 2)
                .add(AugmentExtendTime.INSTANCE, 2)
                .withSound(new ConfiguredSpellSound(SoundRegistry.FIRE_SPELL_SOUND, 0.27f, 1)),
                "Be careful About digging straight down, People say its like digging your own grave",
                new ParticleColor(255, 128, 1)));

        tomes.add(buildTome("plauged757", "Self Atomic", new Spell(MethodSelf.INSTANCE, EffectExplosion.INSTANCE, AugmentAmplify.INSTANCE, AugmentAmplify.INSTANCE)
                .add(AugmentAOE.INSTANCE, 6)
                .withSound(new ConfiguredSpellSound(SoundRegistry.GAIA_SPELL_SOUND)), "WARNING: ONLY TO BE USED AS A LAST RESORT!!", new ParticleColor(255, 1, 1)));

        tomes.add(buildTome("bugcolez", "Pixie Pummel", new Spell(MethodSelf.INSTANCE).add(EffectSummonVex.INSTANCE).add(AugmentExtendTime.INSTANCE, 8)
                .withSound(new ConfiguredSpellSound(SoundRegistry.DEFAULT_SPELL_SOUND, 1.0f, 1.9f)), "Their Friends are their power!", new ParticleColor(255, 25, 180)));

        tomes.add(buildTome("sloppybox", "Arcane Smelt", new Spell(MethodTouch.INSTANCE).add(EffectRune.INSTANCE, EffectSmelt.INSTANCE).add(AugmentPierce.INSTANCE, 4), "Enough heat to melt a mountain or evaporate oceans.", new ParticleColor(139, 1, 1)));

        tomes.add(buildTome("lootz", "Fireworks and Mobs", new Spell(MethodProjectile.INSTANCE, EffectHarm.INSTANCE, AugmentAmplify.INSTANCE, EffectLaunch.INSTANCE, AugmentAmplify.INSTANCE, AugmentAmplify.INSTANCE, EffectFirework.INSTANCE, AugmentExtendTime.INSTANCE, EffectExplosion.INSTANCE), "Mobs meet fireworks", new ParticleColor(255, 255, 1)));
        tomes.add(buildTome("yuukiukami", "Yuuki's Mineral Assimilator", new Spell(MethodProjectile.INSTANCE, EffectBreak.INSTANCE, AugmentAmplify.INSTANCE, AugmentExtract.INSTANCE, EffectPickup.INSTANCE), "One of YuukiUkami’s earliest creations, this spell reflects their desire for precision and mastery. Infused with the arcane finesse of its creator, it draws minerals from the earth at a distance, as if the ores themselves were eager to join the mage’s growing legacy", new ParticleColor(48, 1, 105)));

        tomes.add(buildTome("riftderp","Rift's Blink Strike", new Spell(MethodProjectile.INSTANCE, AugmentAccelerate.INSTANCE, AugmentAccelerate.INSTANCE, EffectHarm.INSTANCE, EffectBlink.INSTANCE), "Just like that one guy, from that one thing! This spell will put you right in the enemy's face, so be ready for some swordplay!", new ParticleColor(120, 25, 255)));

        tomes.add(buildTome("nikk", "Sutokahs LOVE", new Spell(MethodProjectile.INSTANCE, EffectHex.INSTANCE, EffectHarm.INSTANCE, EffectBlink.INSTANCE, AugmentAmplify.INSTANCE, EffectPickup.INSTANCE, AugmentAOE.INSTANCE), "Weaken it, kill it and take it home.", new ParticleColor(255, 204, 204)));
        for (CasterRecipeWrapper g : tomes) {
            Path path = getRecipePath(output, g.id().getPath());
            saveStable(pOutput, CasterTomeData.CODEC.encodeStart(JsonOps.INSTANCE, g.toData()).getOrThrow(), path);
        }
    }

    protected Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_nouveau/recipe/tomes/" + str + ".json");
    }

    public CasterRecipeWrapper buildTome(String id, String name, Spell spell, String flavorText) {
        return new CasterRecipeWrapper(ArsNouveau.prefix( id + "_tome"),
                name,
                spell.serializeRecipe(),
                ItemsRegistry.CASTER_TOME.registryObject.getId(),
                flavorText,
                spell.color().serialize(), spell.sound());
    }

    public CasterRecipeWrapper buildTome(String id, String name, Spell spell, String flavorText, ParticleColor color) {
        return new CasterRecipeWrapper(ArsNouveau.prefix( id + "_tome"),
                name,
                spell.serializeRecipe(),
                ItemsRegistry.CASTER_TOME.registryObject.getId(),
                flavorText,
                color.serialize(), spell.sound());
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Ars Nouveau Caster Tomes Datagen";
    }

    public record CasterRecipeWrapper(ResourceLocation id, String name, List<ResourceLocation> spell, ResourceLocation tomeType, String flavorText, CompoundTag particleColor, ConfiguredSpellSound sound) {
        public CasterTomeData toData() {
            return new CasterTomeData(name, spell, tomeType, flavorText, particleColor, sound);
        }
    }
}
