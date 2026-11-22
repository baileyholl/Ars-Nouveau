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
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.SoundRegistry;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CasterTomeProvider extends SimpleDataProvider {


    public List<CasterRecipeWrapper> tomes = new ArrayList<>();
    public List<UpdatedRecipeWrapper> updatedTomes = new ArrayList<>();

    public CasterTomeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput pOutput) {
        collectTomes(tomes, updatedTomes);

        for (CasterRecipeWrapper g : tomes) {
            Path path = getRecipePath(output, g.id().getPath());
            saveStable(pOutput, CasterTomeData.CODEC.encodeStart(JsonOps.INSTANCE, g.toData()).getOrThrow(), path);
        }

        for (UpdatedRecipeWrapper g : updatedTomes) {
            Path path = getRecipePath(output, g.id().getPath());
            saveStable(pOutput, CasterTomeData.CODEC.encodeStart(JsonOps.INSTANCE, g.toData()).getOrThrow(), path);
        }
    }

    public static void collectTomes(List<CasterRecipeWrapper> tomes, List<UpdatedRecipeWrapper> updatedTomes) {
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
                new RainbowParticleColor())
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
                , "Fire at a body of water to create a Ice bubble in the depths.", new ParticleColor(0, 0, 255)));

        updatedTomes.add(new UpdatedRecipeWrapper(ArsNouveau.prefix("pandoxyy_tome"),
                "Pandoxyy's Spin Cycle",
                "First you're airborne. Then you're dizzy. Then you're somewhere else. By the time you figure out what happened, it's already happening again.",
                Spell.fromBinaryBase64("H4sIAAAAAAAA/5WTTW+DMAyG/8qUM5qAjbXl2lXaZVrV7rZNKIChkVInIqEUofz3JdCPtaPSxiWR3/i1/SR0RIkacxJ3xiMVZEwCiT8IrVSCot4BreOSt3KTKOAF8UYEUaVMjyoNwzxJ60qNy7cTleT/Vm67VUJTDeN2gIppthtXYZ9tKJZAvjwiaaVZxuGdbYEztJC6i4wej40JXIESfMewXBQFZNoFt0IzgW4nKyFfqRxw69bBvrAZaJlzvTfpUtUxF2wU1HXx4+FkcOzO3f6ugNAkJReNnVjV6fKmZya4qH56zY8Bll9ZZrZBTdHRtweCydQjJYnD0PdIateHmZuVoX5miqYcbHpBuQJjzCX1vL+N1tU4bSPrSXNW2/78+8AfvuBx5gfBUxDaGSRt8DDmevmyWC2IGUd7Gtz0L91dEqwPb//8E4zn9rfrNKRbpy0p5mLftp+174cTdbeWDO/mraVkGWR/BBVG0QAq6jkFU9+YbyvT/qqPAwAA")
                        .withColor(new ParticleColor(178, 220, 239))
                        .withSound(new ConfiguredSpellSound(SoundRegistry.PUFFER_FISH_BLOW_UP, 1.0f, 1.2f))));

        updatedTomes.add(fromCode("chems", "Chem's Scientific Repulsion Runes", "Smothers an area with runes that launch entities upward. Do NOT get covered in the Repulsion Runes.", "H4sIAAAAAAAA/+1ZW2/aMBT+K1OeGUooZZTXrdJeqlXQt2mKTGKCV8e2bAdIUf77jhMgJIRCgF4Y8ILx5fhcvnPxYW4pHjHf6s2ThiWxRwS2er8tJJXLeDTBKOoFNBZjV0j+F3uaUGw1KpYpYQGWlUsKM0U0mVQflBHbQhFFzBu/tvSnYQkkNfEofiIhBhaA93lh/zCSSptJzvpYcToBNu9HIxDETIZcE87MCKQTD0hkatCxUUIFnSS/8JcwR9XyLIZZrMq3Lze7GcV5zu7mDQxP3YDyKQisouHjVpoep1yu0/q+nCB+iaQHDGrENJCEDa3b24YVmO+GNbR6Ttc2ohKmfxCFhhTDaS0jnCRJUeV+ar7Y3LAaAgmJfBIBd3bTsbOP076zHafjtEACgaZsIeTg8ed9/95KqhW7EjtJ8WdMhAcLRObQ3GGU9ckFEOG0lojQQ4wdiSmSvjsimPq1bT5FlJo5iYAPUHPD8saIeThVGotCd0kNTrbshuUTadzKcGbk1eOyQHVA5EsiBCjAnSKdeuPbQOnOyZBk2ymUAFkbUBohqnBJElW2az7YuFBwIId910ikNGc4HYHnTxBAZT+Q2jlKnaZdB5UlPZpdnz6AGC9yCXv+D62+tMJeZnfs9eB0W8fsKxW+FoxeYR+HAjhItlBfRKaye8M9mvgkIBotwXQk1igJxvWxpgTGqVjxA2GD7Idt3CZ+QLP8N4Bg9lLaARPrW44IYGeVBVsFpDmHpz3EfB6eDmgbkNqQhaI4w9kT8Z6vELsAiH3u3HVWNnnT4hdMtG/5m7nxhicwzUN4DV0fPWdl970fPbmBSyyHKMBDyr3nomnfWY+VPOesbRQ/q6YC8DCiiMhDgPpCghcUvClS4UUooDBVgkt86RV2MSnZtUrsgh7NHg8pXaf0mRLmu/CklgF28wBFtDc2r7z2gZW7hooKiiYZuyMUEhqvEbWb31JZdncTipjnEJ/TRgBwE0mvrIishFsh6ZTO2rlJYeR0OxmM2t0tMCob+0xD94cFBYjDMFRuCn9IspceGBy7EBlq5cQNXX5Y0wWHROushSh5/iifWb2vrXbT7tx0uuBgcRpuWt27zo3RjOn1HfMqGZLADSKlPx5C9Xss9TLAStLMwANz8EKr05Pa5JisXChIKzsKqzqtxEn69832LLhc1nimI2nImpllVGm8txorhUt5LPHC5ZDoo2pSxjOq54vOYxNwAVSVZd7O58+h/+CkBy9F+7Xz8unertcGwxk0GI7IsdcgtiszZonCLDJkmm5WH4uIKtDSlz7kFQVceIezmvwDAgwHyp8hAAA="));

        tomes.add(buildTome("ivy", "Ivy", new Spell(MethodTouch.INSTANCE)
                        .add(EffectAnimate.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE, 7)
                        .add(EffectName.INSTANCE), "Now you never have to be lonely again! You will always have a friend with you! Feel free to change their name to whatever you want! :D",
                new ParticleColor(255, 105, 180)));

        tomes.add(buildTome("darkfira", "Darkfira's Flash Freeze", new Spell(MethodProjectile.INSTANCE)
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
                .add(AugmentAOE.INSTANCE, 3), " A utility spell to aid you in your adventures", new ParticleColor(225, 90, 1)));

        tomes.add(buildTome("lyrellion", "Lyrellion’s Wall of Force", new Spell(MethodProjectile.INSTANCE)
                .add(AugmentAccelerate.INSTANCE)
                .add(EffectWall.INSTANCE)
                .add(AugmentSensitive.INSTANCE)
                .add(EffectPhantomBlock.INSTANCE)
                .add(AugmentPierce.INSTANCE)
                .add(AugmentAmplify.INSTANCE), "You. Shall. Not. Pass."));

        tomes.add(buildTome("silvanus", "Emergency Flair", new Spell(MethodTouch.INSTANCE)
                .add(EffectLight.INSTANCE)
                .add(AugmentExtendTime.INSTANCE, 3), "When you want to go deeper but just don't have the torches", new ParticleColor(0, 255, 0)));

        tomes.add(buildTome("uni", "Uni's Windshield", new Spell(MethodSelf.INSTANCE, EffectOrbit.INSTANCE, EffectKnockback.INSTANCE).add(AugmentAmplify.INSTANCE, 8), "Stay away!"));

        tomes.add(buildTome("crowdrone", "Crow's Gluttonous Gaze", new Spell(MethodSelf.INSTANCE, EffectSenseMagic.INSTANCE, AugmentExtendTime.INSTANCE, EffectLight.INSTANCE).add(AugmentExtendTime.INSTANCE, 6), "Crows are known to collect shiny things, even in the darkest of places.", new ParticleColor(0, 0, 0)));

        tomes.add(buildTome("chosenarchitect", "Chosen's Chaotic Shift", new Spell(MethodProjectile.INSTANCE)
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

        tomes.add(buildTome("mystifi", "Mysti's Gravely Dig", new Spell(MethodUnderfoot.INSTANCE)
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

        tomes.add(buildTome("riftderp", "Rift's Blink Strike", new Spell(MethodProjectile.INSTANCE, AugmentAccelerate.INSTANCE, AugmentAccelerate.INSTANCE, EffectHarm.INSTANCE, EffectBlink.INSTANCE), "Just like that one guy, from that one thing! This spell will put you right in the enemy's face, so be ready for some swordplay!", new ParticleColor(120, 25, 255)));

        tomes.add(buildTome("nikk", "Sutokahs LOVE", new Spell(MethodProjectile.INSTANCE, EffectHex.INSTANCE, EffectHarm.INSTANCE, EffectBlink.INSTANCE, AugmentAmplify.INSTANCE, EffectPickup.INSTANCE, AugmentAOE.INSTANCE), "Weaken it, kill it and take it home.", new ParticleColor(255, 204, 204)));

        tomes.add(buildTome("cocoaeyebrows", "Gobute's Humble Tunnel", new Spell(MethodTouch.INSTANCE, EffectBreak.INSTANCE).add(AugmentDampen.INSTANCE, 2).add(AugmentAOE.INSTANCE, 2).add(AugmentPierce.INSTANCE, 4), "Leave the riches untouched. Seek only to carve the earth.", new ParticleColor(255, 128, 1)));

        tomes.add(buildTome("beepsterr", "Verdant Aura", new Spell(MethodProjectile.INSTANCE, EffectOrbit.INSTANCE, AugmentExtendTime.INSTANCE, AugmentSensitive.INSTANCE, AugmentSplit.INSTANCE, EffectGrow.INSTANCE, AugmentAOE.INSTANCE, AugmentAOE.INSTANCE).withSound(new ConfiguredSpellSound(SoundRegistry.GAIA_SPELL_SOUND)), "It's covered in dirt and moss..", new ParticleColor(145, 145, 201)));

        tomes.add(buildTome("worsecookie", "The Yellow Sun", new Spell(MethodTouch.INSTANCE, EffectRune.INSTANCE, EffectHex.INSTANCE).add(AugmentAmplify.INSTANCE, 4).add(EffectWither.INSTANCE).add(AugmentAmplify.INSTANCE, 2), "Along the shore the cloud waves break, The twin suns sink beneath the lake, The shadows lengthen. In Carcosa", new ParticleColor(204, 153, 1)));

        tomes.add(buildTome("pranks", "Pranks", new Spell(MethodProjectile.INSTANCE, EffectLinger.INSTANCE, EffectLaunch.INSTANCE, AugmentAmplify.INSTANCE, EffectDelay.INSTANCE, AugmentDurationDown.INSTANCE, EffectFirework.INSTANCE, AugmentAmplify.INSTANCE, EffectSummonVex.INSTANCE, EffectWindshear.INSTANCE), "Tome-Fuzzer sending chaotic magic streams into existence. May be hacking the planet."));

        updatedTomes.add(fromCode("frantastic", "Simulmantic Storm", "It is unknown whether the original author of this spell survived its first casting, or if she was replaced by one of her simulacra in the ensuing chaos. Don't think too hard about it. ", "H4sIAAAAAAAA/+1W32/aMBD+X+45Qw40lOa1q7SXaVXbt2mKHOcSvDp2ZDswVOV/ny7AgBC6shapXcsLju07333f/XoAi0JWCPF34NYl2tQz5HVcqEU1TSprfqLwUiEEPceuLkujkwyFWfReMDaV/qVFldQF2pd+T8li6rXUBfwIoOLWS6HwTpaopEaIH3ZEtnCJHyBXXNqrPEfh6XMt/K3y0mjXbllTofUS3Z6m1eXEL4iEjfRd+71zV+M8KZSZQwCuTq8P6hRGGbut63K9IbOOSmG081wTZhbiYRQFUNB/ACnE4YQ1AXip/WfpeKowg9jbGpumof1HDKRzwZ2/NbXO6GG3WtCBRWfUDHvPvOVSbaAsDUG4RvArr7quliZDRZuktLaiaxGWlV9s8FpqOGR++zg0wbtgcDcHMtRO+gW98GcZMhaA5ZmsHcRsELLlLzy7YGE4DocBuIrP9crN2+svVzdX8JTIMPqmDQGpiz6qe8TT2jp/NDNYSu+xhc0az9f6f0H8aTgZjM/PR+NJAK2ng8komozOCJUCISbU3gXH0akYviXBj5rYwaUHt61W0uFr1ecoIw5VxR59heUz6RdJLlFlb7uYRWwV58tAH0bRXqDnXLn9SJ9ztWwKXFM2jwIQU66pOUQB6LpM1pY4iIcsgExaooAwpW7kp/CE1CHBTe68/mr4f1Db36hOU8UODSqbxZ6XqLOkMtZzleSWl5jkUrUjxcyoukSIQ4qUSnoxJVvHB8xYpX7H9+V0+zFwHjNwPsJVikolqTLifpsgNjjbIij6xzg4pJt69q7yY2r76QdUVCi8lSJxFbf3pxthhqM2/8OLizb/x8NnpP9uIWbRMfne8fd1TKdswNqxlA3Y2xhIX5LN8Blsfsygzd9x6TlfNhY61JzqFEDzG9F+q4gYEgAA"));

        updatedTomes.add(fromCode("shoob", "Shoob's Graceful Retreat", "Need a quick getaway? Using Shoob's patented \"Tactical retreat\" technique, you can escape AND do it gracefully! (Shibe Inc is not liable for misuse or failure in the escape)", "H4sIAAAAAAAA/5VRwW7CMAz9F5+jiaLBtFw3pN2GgNs0VWnjMmtpEsUpUFX99ylFDBidpuWU+D2/92J3ELAkjyDfQAXOrWt2qBq5Na3/yBlNBWIEwIM3jsnZUVQ5/Fedm7p2NtdYunaUUBiyn+OStTdUtf/GyO6IqSBDcZyAh4hW55FqhHcBXoVIpcEN1WjIIsjuqmmYlOzA2RWyMzuy20VVYRlTsXYxjUp2ENs06qvOogkcoT9bvPrE5kT3wXkMkXB4XXZptJyiyw6+rzMBQWlqGOTkLpscT3b/OMmyeTYVwF7t7eaYYL18WawWyfZS9RQhP+bszt++zb1XwaPO2buQtspNsfw1bemMC5d6T6cC6R+ypbMclY0gIICcPwjYgpzOZgIKkFkvIJKNz8SqMKhBVsow9n2fgD8yJk4YloNr11id7PnmchMouoh13nASGHcZdp8wq+qEQf8F3qLek1YDAAA="));

        updatedTomes.add(fromCode("sebuss", "Sebuss's Very Hot Smelt", "Even the book is burning hot! You might need to use this spell with caution if you don't want your item to burn!", "H4sIAAAAAAAA/+1VTY+bMBD9L3N2I6AlSrh2I+0l6mqzt6pCDgzsrIyNbJPARvz3ypDsQkIqpe1eqnLB45l543nPHwfQmFCJEH0Hrk0sVbVDXkW5aMrnuNTqBRNLAoFNuE2Bwk56uLqSgdKQpd20l3JJFuEHg5JrS4nAJypQkESIDqP4wcKiA2SCk15lGSbWmafkb6UlJU03pVWJ2hKaC6RjcGwbx8J79lNnj2Il7uNcqD0wMNX24SpmooTSQ6yvpwlKzyATJY3l0rGoIQrCkEHu/gy2EPkLr2VgSdo7MnwrMIXI6grbtnXzv1ig8yfc2I2qZOoKm4vBxVIy0hhnvCDRxAF0JTQaJXZ4CwoWpW36bKs5iXdZCuXkOKmx5uU5bYVKUbhJV7bSCU5Cv3HfI7hCv6tnJniBHyZm6B217MUMwvBCzIwLc13NZxRUQ8tu29CmRAd9gGZNctMb3sxj0Kx5PbTr17OA+nUU8S8QO24h7W6fxpV4Gwaex0DzlCrjup57o89nYEq+l8ceNw/3q8cVXNGr79o5lXzsTg7JfGr/T+RuK23szVpjQdZix5lWlp/wa4g+BcuZN18sFwxck7MwXCzmnwNHSI4Q/Ym4H34L/lV9w6G6/knWL0vP9+d+cIu8o8tVyY1L/P/oVGNeJngbvNUuQrpDEgG0PwGLtS8uewgAAA=="));

        updatedTomes.add(fromCode("treeleafs", "Treeleafs Gardener's Dream", "Just make sure your somewhere you want to be greener", "H4sIAAAAAAAA/+1UyU7DMBD9lzlbqGYp1FeoxA3U9oZQ5CbTdIRjGy8pUZV/R04o3blxay6ZzMx7nuU5a3CYk0UQbyCdz7SJNcooStXYZeZRLYCdCMyj8+FkxKP2FKjGk1Fp/tdfOrM6GVhKV+OZki3lH9HCOwMrXaBc4YwqVKQRxHovv5uHWIPRE/RG1aTL8WKBeUjOygQyOlmhSQPdQ/YDa7dHvNiU7VO6dcaiC4Td1y4KKwoBXXI7E+SG/wvE4GrAoOneLQNZIojO2AEX3SaaBPg17xg4WVD0CckH/cNvRwPOh/yagbdypWd9+dPX5/FkDAesm/qzvsn1dmbHTZfKrDL/GakABj7OX882mhtl3C7b48ZBxQFpbrQPUqdVOhCcQQmC8xGDOQj+cN8yCKTDE3k5V1iAWEjlsW3bFPizxJThurXi1ERdpNP9kXFUD1Y2NHCOv1PMwQh7LVxkdJGR24pCKiWbrEAZlkkvtVGxQhD8asjAUsiX6cbe3J2R2c/vpWWgZYIBtN+E19hV2gUAAA=="));

        updatedTomes.add(fromCode("kamiihoney", "Kamii's Curse", "Safely contain your enemies while they wither away. Some say Kamii is still stuck in a bubble somewhere after miscasting this curse.", "H4sIAAAAAAAA/+1VS4/aMBD+L3N2kZ0lPHJtkXpBXS17qyrkJAO4cuzInizJovz3yjwKZENbpB62UnOJ5/XZ832ZeAcOM1UiJF9BOr80tnpBWSVr3ZSbZensd8xIaQTWE95IV/QGZFFqtWrujm0VbdD1htIqTW8cA2tCky9JFb+Pf2NQSkcq0/isCtTKICS7q6KLppMdrLRUbrZaYUbBPBV/KUlZ4/cuZ0t0pNC/QTomL6kJDJ+rn/f2Va7B7XKt7RYY+Cp9vImZWW3dJdbHk0PlHcjMGk/SEDBwkERxzGAd3gxSSMSEtwxIGfqkvEw15pCQq7Bt2+D/xQFDPJOeFrYyedjYHxch4NBb/YK9MXJS6TOVhQ0Unhicy7LbamFz1MEZQCuXdU+ERUnNma8DQtjo3WoQ86MEBw2iOH6jwUpqf1uEDWpVQ8vu+w59iQF6B81cmcXB4APOoJnL+tKuXzsJ9etVRofbHI1X1ATkn0vBOQMnc1X5UPLA948Q04hPo8l0xMCXcmuO7C8eP8+eZvCeNTvNjZg+HETjwztFu5oca572I6LMum8UesrTynm6W3MsFBHuG3SW5Am/hmQ8HESTUTxm0EDyYTIexHwi4lHQYI2Q/InI8aXE4iAxF8MpF2Ikon9W4r/ya7RmEbr/f2VU17z08HZx04YMI4uQAe0PWrJZfZUIAAA="));

        updatedTomes.add(fromCode("urticantodin", "Urti's Bounce'a Bounce", " Traveling issues? Nonsense, all it takes is one leap and you get there in no time! Why the sticky feeling? Safety precautions, your legs aren't strong enough for the sudden stop with the ground, so we made them bouncy!", "H4sIAAAAAAAA/61SUW8TMQz+L36Oqg6oQHlk7JlpjCeEotzFd7WWS6LEaTlV99+RM8a6tpuExFO+2P5sf7YPkLGnhKB/gM3FhFh3aKse/Zy2pqAfQF1weLTposNOydMw/1dfF2vo8aLL1WyZYjAu7sObiX8qSDYz9R7vaUJPAUEfXhCaWn2AGO6wRL+jMN4MA/YsxilKHUE8y7heMLuaC8PyXOJrkugi4SnHhJkJ2++YhRMxYxZzjtx0CP4Fer1aK5jbuyiwI4Ju4IjsMBTiWQh/4bu1gmwd1SLUDxsFJdl9uH9s+Pr75xs4yfLUr3kUdXie0blIb/OIpkzxQXZRanf7qrISa3BiPAfkTtKmSIHRGZcpFY4BGzJ7K6NZlpOG++hjPm70+slwlriPobANDAoy6KuPnxSMf96uvYsCpsBfqNjOowM9WF9waTUvLPlYv4TkdiT47R+0lm31D5hNV71HNltqR7OLvk6y4tVmoyAR91vQV6v3r/TRzlR8wQoLYPkNgyVHnsUDAAA="));

        updatedTomes.add(fromCode("oaksa", "0AKSA's Open Concept", "The hill has been reassigned as a floor.", "H4sIAAAAAAAA/+VWTW/bMAz9Lzq7gWTHWe3rVmCXYkWz2zAYisKkXGVJkOQ2aeD/Pkie13w4WDM02Ir6JIkU/fgeSWhDLAg0QMpvhFtXKd08AG/KpVybu8pY/QOERwkkGTDPGuv8oMWBcujxYfge12/jfGaB3w9aYOUtF8O5GxT3jSHfE2K49SgkfMUaJCog5WbHf4veckMWkqO9WixA+LCttUetwspYba652b/tDMA8HK6vUU27DR3RhKyv+Wp7v3rac1g97Xi0u1n0qCu/DnWxeU4j7nd8QYk7rgIPrpndWG3AegS3D1Voqe12qI/9Ac73IgqtnO9CWlKmeZ6QJSlTNknIjJSsTYhH5T+h4zMZ8C+4dNC2bTAc4rsDiSvSPkvxxQRWXU/rMFyo0XuI+Kz2vNdh1fNLystiVIQvULcEUmZx9c7EYXkWxWGURXFSOj4qz24W8zge1uEfv5eM0oRYPsfGkZIFJpzhj+pXXtObz1e3V+SIzH2mwSy481PdqMi+O1gc5IRSNg61AluFq5UzICWJkC04LR/glGig5mBrrioPEoy2AVPAbDnKE3q71nOQsf7A6caKg4Rr49fPwnYR2v+xBiUIb1FUznB7f/Y5wcb0HcyJi7cpEutGeZAqTIs8f61pQUcZjR9jRUqL9LKYnDQ9dtMPXlrdxt5HtRzq2oEg3Vvo9SroIstHRfqBsiwW0gWjdDSe5Fk67muJvoCmfJskmg+z8veF47RCUc20rs9XNNllLJvxuKuadHJia29h7JSdBgr+karH5sJLtGT0vGLqGpVuXOiCR4Vqee45kBZ/nAPDih4APeK39bwOHorXwYO0PwH1hXNw9AwAAA=="));
    }

    public static Map<String, String> langEntries() {
        List<CasterRecipeWrapper> tomes = new ArrayList<>();
        List<UpdatedRecipeWrapper> updatedTomes = new ArrayList<>();
        collectTomes(tomes, updatedTomes);

        Map<String, String> entries = new LinkedHashMap<>();
        for (CasterRecipeWrapper g : tomes) {
            entries.put(CasterTomeData.nameKey(g.id()), g.name());
            entries.put(CasterTomeData.flavorKey(g.id()), g.flavorText());
        }

        for (UpdatedRecipeWrapper g : updatedTomes) {
            entries.put(CasterTomeData.nameKey(g.id()), g.name());
            entries.put(CasterTomeData.flavorKey(g.id()), g.flavorText());
        }
        return entries;
    }

    protected Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_nouveau/recipe/tomes/" + str + ".json");
    }

    public static CasterRecipeWrapper buildTome(String id, String name, Spell spell, String flavorText) {
        return new CasterRecipeWrapper(ArsNouveau.prefix(id + "_tome"),
                name,
                spell.serializeRecipe(),
                ItemsRegistry.CASTER_TOME.registryObject.getId(),
                flavorText,
                (CompoundTag) ANCodecs.encode(ParticleColor.CODEC.codec(), spell.color()), spell.sound());
    }

    public static CasterRecipeWrapper buildTome(String id, String name, Spell spell, String flavorText, ParticleColor color) {
        return new CasterRecipeWrapper(ArsNouveau.prefix(id + "_tome"),
                name,
                spell.serializeRecipe(),
                ItemsRegistry.CASTER_TOME.registryObject.getId(),
                flavorText,
                (CompoundTag) ANCodecs.encode(ParticleColor.CODEC.codec(), color), spell.sound());
    }

    public static UpdatedRecipeWrapper fromCode(String id, String name, String flavorText, String code) {
        Spell spell = Spell.fromBinaryBase64(code);
        return new UpdatedRecipeWrapper(ArsNouveau.prefix(id + "_tome"),
                name,
                flavorText,
                spell);
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Ars Nouveau Caster Tomes Datagen";
    }

    public record UpdatedRecipeWrapper(ResourceLocation id, String name, String flavorText, Spell spell,
                                       ResourceLocation type) {
        public UpdatedRecipeWrapper(ResourceLocation id, String name, String flavorText, Spell spell) {
            this(id, name, flavorText, spell.withName(name), ItemsRegistry.CASTER_TOME.registryObject.getId());
        }

        public CasterTomeData toData() {
            return new CasterTomeData(flavorText, spell, type);
        }
    }

    public record CasterRecipeWrapper(ResourceLocation id, String name, List<ResourceLocation> spell,
                                      ResourceLocation tomeType, String flavorText, CompoundTag particleColor,
                                      ConfiguredSpellSound sound) {
        public CasterTomeData toData() {
            return new CasterTomeData(name, spell, tomeType, flavorText, ANCodecs.decode(ParticleColor.CODEC.codec(), particleColor), sound);
        }
    }
}
