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
    public List<UpdatedRecipeWrapper> updatedTomes = new ArrayList<>();

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
                , "Fire at a body of water to create a Ice bubble in the depths.", new ParticleColor(0, 0, 255)));

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

        for (CasterRecipeWrapper g : tomes) {
            Path path = getRecipePath(output, g.id().getPath());
            saveStable(pOutput, CasterTomeData.CODEC.encodeStart(JsonOps.INSTANCE, g.toData()).getOrThrow(), path);
        }

        updatedTomes.add(fromCode("frantastic", "Simulmantic Storm", "It is unknown whether the original author of this spell survived its first casting, or if she was replaced by one of her simulacra in the ensuing chaos. Don't think too hard about it. ", "H4sIAAAAAAAA/+1X227aQBD9lWqfaWSbQAivaaS+VI1C39rKWq8Hs81erL1AEeLfO2tzD1SJg6VUxS9e7J3LnjMzPiyI1V7lZLhYdogBxksgw++EGpsq7adA/bAQ83KSlkb/Aua4ANI58tp6KbVKc2B6fnSDNhl35zYVXBVgzh1P8GLiFLomPzukpMZxJuAbl4DhEJ3FnkkdBx+OBeXmfjxGkMJPqR3XKqwQufILLWuI3TwAvOdBafS63Eb6WgZLuzYFfAr2MOx6c1o7XGzzPBIAZmkh9AwPa332cNIn00KbXV936wc8P3DJMEFHVQAYNyS9XocU4d4hGRnGgyiclCv3iVuaCUBrZzws8TqOwDrB8J5R60armtwU53bxLJUMhEgzodlTAHGqhZfoP7q6RkS5Y5Ow7i2r6rZaTOEcvsNB9507Q7lown5l2Cr9IDApw1lqcdNTe0WQdKsiiG9vqyroJ8+KYEyFhaoIdt3koCx38xBis0wi9Elz7m1AOEK4MfmZWp1u9PD5/vGenKimg/OGXVo9VuRjTzfhKPPGun+8RZuyA5I7B1UIox1dQ/a74qVD5tU92BShM15AbfwGavcGhVajYHgZuW8euUfe15+1AzpXn1vMrvG4KwydYhmkYw4ifzUFMypEVYlUhXLDkmYTqhguEQblZbr2ZusRknMTNEtILUx5Nzk80bvitBetKK05xfvZJmjyhi7730fnWWnZn35xVF/x9W0Ux/24OU0NxA2oPC21cVSkY0MlYE8Ksatz4lA2G5nTP5HGaigcNtb278JFG7erjf9G/wul6V5EqXOopyw69YY901eydPMtXiv6TqTfvrx9Rwy+pP1b6v/LmG5K02mB+zEZXPVvbrr9QaVz4+hq0O0NutevUbu9tui+aN/WtO/OtyvsUFRW7HDphQxq0/zwUZTc2A8jp43EnFjzxJd/ADOROyaBEwAA"));

        updatedTomes.add(fromCode("shoob", "Shoob's Graceful Retreat", "Need a quick getaway? Using Shoob's patented \"Tactical retreat\" technique, you can escape AND do it gracefully! (Shibe Inc is not liable for misuse or failure in the escape)", "H4sIAAAAAAAA/+1WS2/bMAz+Lzobg10s3ZZrF6CXYUWz2zAYsk2nxGRR0CNpEOS/j3LiZknVoE3RAcWiiyVR/Pj4SMkr4SjoRoxX60xYqNGAGP8U0rpSU5iDDOOZWpq70oFqRZYQwL1R5JB0UioJXrTvQteRLhuoaZk8UCnUv9OQnVHYprWOyVDP0WGFCn36ANx70E3psQPxKxNGWo+1gh+8Zmc4X6s9JbIV+rjZKol20rZQ98uOfMwSz4wl802aTdL9MqZ8D0ETo653lr6bqOkGVeBdcIdmh8PlBnC18zNhABblTNGCw3WhunkSsyZF9m+sq2EDmwPImh30UnuG5AMXo1EmZvGbiUqMi895jBS1/4pOVgpY29sAax7pDAwORnktnZ9uq3RXrn29OlJzSMq8lahOSX6v+L9kf7/cG9AuNgFbeJgWec6YssHA/uUfeNmP4uOXvCguiwuOwciF3oY5vbme3E7Ec1glfdvTh3p2Ck1VsM6faRqmo7ciaRoVz5fYqy+xhHzzUByQ6ynUd9G5c3+8g/44+gIl3pae3AN3+z+r90D4QloDTekMWXgz0i8/bTnfkv6I8lYq968534v8GO+7yaMwPXnoyuAiQNpKXwhRpmUXZZyQ+vRWWf8BdCbsTtsLAAA="));

        updatedTomes.add(fromCode("sebuss", "Sebuss's Very Hot Smelt", "Even the book is burning hot! You might need to use this spell with caution if you don't want your item to burn!", "H4sIAAAAAAAA/+1X247aMBD9FeRnipJsYVlet0j7supqWfWlrZAThuDWsSPb4VLEv3fsEAIhrJabhNTy4sSXmTNnjmfCkmiZiRHpLVdNoiBiKZDed0KVHgqZTYFmvZgv0skwVfIXRIZxIM2aZZ0AN7UrVB44AUIzw6b1qywWzAD52SQpVYZFHN4YumAC4S139nMWT4ydlOIVtORTJuL+eIxY7WQiDZPCPmEA6TNN80jNwsZZY2dVOvya2qO6OAs4C7rqXacAlj2yeGZikL94La+J73RevvtNMv9T2YET21tWuzSMHD0La3rziFYUHbFMuwPtJtEpnYm3PJbBy1P/tU8qZopghnnEy5LOfQYEzIYxlzNMiM7Cl4MxR5JLtW3rsZhgo4rJCAk0VFhl4IY2Rh2TXtBG6KEbbSqYMF+YpiG3RIwp17DCX22ONggPrG9yuENBqVyEOOaUqVP0ISRq71h53BT9jnfHv6Pf73p79BuVfZD9iGozWJeOTQ0pH/agjJmC4ZgmjC+GAXEulLuucIwVSFK8CDlARRl/P5E7RxM5Am4nrdtMRdXoctMb7tdSOOM6odISuN27NMFqOr9WvbtIuQs8b7vgdbydn3/p8nfj+crh2cUzG12YKX18o4OEGQMuUiUNLZzNSe9T8NDyOt2HLooAA2u1291u5y6wmYjR90cy3d7Os18k+POD5/sdP/jH+5zEa4Th/+9ZZ/esmvWtj4OKohJUb8hl9PudnnJyWH5w78JyY+jGQxKphV2Cq6CeIQAurwG5yIQVd2iHowCvcVXQGplFkwt8u1+6pOUdbdOnbrOMgYgmeXauc/X8u8AlvNPJJXpkDSvgXeBLb994Lhy7KGxXRDohzLT+kXlecK8b30AtGk/SNAbrv6XR6eVm9RfMXASMIw8AAA=="));

        updatedTomes.add(fromCode("treeleafs", " Treeleafs Gardener's Dream", "Just make sure your somewhere you want to be greener", "H4sIAAAAAAAA/+1WS2/bMAz+K4XORhFnS7fm2gXYZVjR9DYMBmPTjjpZ8vRIFgT+76PkPB27Q4YWw4r4YAuk+Po+UtaaGeVkxsbrOmIaU14hG39joE0ilVsguHEhVtU8MShyFnUoZk4b26kxKA23fIGdWlCvKy+0WnYq5qAX2JNyxdMfrmLfI1aBtjwV+MhLFFwSKuuj/QEPkin5gEaJBZfFJM8xtV5YKsuV9KtKq+oLVA2+duXRPXLToFfv432tvKnZ2iJJ0bSDY8mtRe3FWlnYBvvFxoPrQcRW4UtOoaB4YXFgvI2UNOms96WeplcItUzMT8czgsu42X1vSqkSSh96u9sKyHbc2iqNBekZoA1xxAp6x7cRm9H34wcPFJf2EzcwE0jGOQiDdd0qIwvdtfIRdssReYSMO+MBiAfNE7+/HcTxTTykCipYyk2Z0/vPk4cJq7t5OSi8DpPhOcbpZlZ2Q7NfnFSJZUUp1T3+Q/u0idHqiRqIi8BKLoDrfUud1R/nUCxxmfhqX43g4WgUKB6ONgwPThi22nmCu6HaJej1KRh7wsJzDAWnGrh4fjqPIpYqQxGGi5w6nbYzaqjd4bWZ7570Q/CzJ/w/ZfDPI0pT+WJDetQZ/+QgfrM0vdxJ2iJp6g0vx1oLlw79wd+gxVfTqpe7x+Xu0Xf3ACFglWQIdu75XSjhSooSX98Q2dymc5/ju1FP/E1fkFKCN2OPGlEg5OaqAE0FozZXmUYoCcX078el/g052MKVfQwAAA=="));

        for (UpdatedRecipeWrapper g : updatedTomes) {
            Path path = getRecipePath(output, g.id().getPath());
            saveStable(pOutput, CasterTomeData.CODEC.encodeStart(JsonOps.INSTANCE, g.toData()).getOrThrow(), path);
        }
    }

    protected Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_nouveau/recipe/tomes/" + str + ".json");
    }

    public CasterRecipeWrapper buildTome(String id, String name, Spell spell, String flavorText) {
        return new CasterRecipeWrapper(ArsNouveau.prefix(id + "_tome"),
                name,
                spell.serializeRecipe(),
                ItemsRegistry.CASTER_TOME.registryObject.getId(),
                flavorText,
                spell.color().serialize(), spell.sound());
    }

    public CasterRecipeWrapper buildTome(String id, String name, Spell spell, String flavorText, ParticleColor color) {
        return new CasterRecipeWrapper(ArsNouveau.prefix(id + "_tome"),
                name,
                spell.serializeRecipe(),
                ItemsRegistry.CASTER_TOME.registryObject.getId(),
                flavorText,
                color.serialize(), spell.sound());
    }

    public UpdatedRecipeWrapper fromCode(String id, String name, String flavorText, String code) {
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
            this(id, name, flavorText, spell, ItemsRegistry.CASTER_TOME.registryObject.getId());
        }

        public CasterTomeData toData() {
            return new CasterTomeData(flavorText, spell, type);
        }
    }

    public record CasterRecipeWrapper(ResourceLocation id, String name, List<ResourceLocation> spell,
                                      ResourceLocation tomeType, String flavorText, CompoundTag particleColor,
                                      ConfiguredSpellSound sound) {
        public CasterTomeData toData() {
            return new CasterTomeData(name, spell, tomeType, flavorText, particleColor, sound);
        }
    }
}
