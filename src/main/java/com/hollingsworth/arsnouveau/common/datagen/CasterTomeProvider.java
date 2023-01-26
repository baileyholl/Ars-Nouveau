package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.*;
import com.hollingsworth.arsnouveau.common.tomes.CasterTomeData;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CasterTomeProvider implements DataProvider {

    public final DataGenerator generator;
    public List<CasterTomeData> tomes = new ArrayList<>();

    public CasterTomeProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    @Override
    public void run(CachedOutput cache) throws IOException {

        tomes.add(buildTome("glow","Glow Trap", new Spell()
                        .add(MethodTouch.INSTANCE)
                        .add(EffectRune.INSTANCE)
                        .add(EffectSnare.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                        .add(EffectLight.INSTANCE)
                , "Snares the target and grants other targets Glowing."));

        tomes.add(buildTome("bailey","Bailey's Bovine Rocket", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectLaunch.INSTANCE)
                        .add(AugmentAmplify.INSTANCE, 2)
                        .add(EffectDelay.INSTANCE)
                        .add(EffectExplosion.INSTANCE)
                        .add(AugmentAmplify.INSTANCE)
                , "To the MOOn")); //TODO FlavourText here?

        tomes.add(buildTome("arachne","Arachne's Weaving", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(AugmentSplit.INSTANCE, 2)
                        .add(EffectSnare.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE)
                , "Creates three snaring projectiles."));

        tomes.add(buildTome("warp_impact","Warp Impact", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectBlink.INSTANCE)
                        .add(EffectExplosion.INSTANCE)
                        .add(AugmentAOE.INSTANCE)
                , "Teleportation, with style!"));

        tomes.add(buildTome("farfalla","Farfalla's Frosty Flames", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectIgnite.INSTANCE)
                        .add(EffectDelay.INSTANCE)
                        .add(EffectConjureWater.INSTANCE)
                        .add(EffectFreeze.INSTANCE)
                , "Creates a fire that quickly freezes to ice."));

        tomes.add(buildTome("gootastic","Gootastic's Telekinetic Fishing Rod", new Spell()
                .add(MethodProjectile.INSTANCE)
                .add(EffectLaunch.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2)
                .add(EffectDelay.INSTANCE)
                .add(EffectPull.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2), "The squid's Lovecraftian roots appear to make it immune."
        ));

        tomes.add(buildTome("toxin","Potent Toxin", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectHex.INSTANCE)
                        .add(EffectHarm.INSTANCE)
                        .add(AugmentExtendTime.INSTANCE),
                "Poisons that target and causes them to take additional damage from all sources."
        ));
        tomes.add(buildTome("shadow","The Shadow's Temporary Tunnel", new Spell()
                        .add(MethodTouch.INSTANCE)
                        .add(EffectIntangible.INSTANCE)
                        .add(AugmentAOE.INSTANCE, 2)
                        .add(AugmentPierce.INSTANCE, 5)
                        .add(AugmentExtendTime.INSTANCE),
                "Creates a temporary tunnel of blocks."
        ));

        tomes.add(buildTome("vault","Vault", new Spell()
                        .add(MethodSelf.INSTANCE)
                        .add(EffectLaunch.INSTANCE)
                        .add(EffectDelay.INSTANCE)
                        .add(EffectLeap.INSTANCE)
                        .add(EffectSlowfall.INSTANCE),
                "Sometimes you just need to get over that wall."
        ));

        tomes.add(buildTome("fireball","Fireball!", new Spell()
                        .add(MethodProjectile.INSTANCE)
                        .add(EffectIgnite.INSTANCE)
                        .add(EffectExplosion.INSTANCE)
                        .add(AugmentAmplify.INSTANCE, 2)
                        .add(AugmentAOE.INSTANCE, 2),
                "A classic."
        ));
        tomes.add(buildTome("renew_rune","Rune of Renewing", new Spell()
                        .add(MethodTouch.INSTANCE)
                        .add(EffectRune.INSTANCE)
                        .add(EffectDispel.INSTANCE)
                        .add(EffectHeal.INSTANCE)
                        .add(AugmentAmplify.INSTANCE),
                "Cures status effects and heals the user."
        ));

        tomes.add(buildTome("yeet","Knocked out of Orbit", new Spell()
                .add(MethodOrbit.INSTANCE)
                .add(EffectLaunch.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2)
                .add(EffectDelay.INSTANCE)
                .add(EffectKnockback.INSTANCE)
                .add(AugmentAmplify.INSTANCE, 2), "Summons orbiting projectiles that will launch nearby enemies.")
        );

        tomes.add(buildTome("takeoff","Takeoff!", new Spell().add(MethodSelf.INSTANCE)
                .add(EffectLaunch.INSTANCE, 2)
                .add(EffectGlide.INSTANCE)
                .add(AugmentDurationDown.INSTANCE), "Launches the caster into the air and grants temporary elytra flight!"));

        tomes.add(buildTome("kirin","KirinDave's Sinister Switch", new Spell()
                .add(MethodSelf.INSTANCE)
                .add(EffectSummonDecoy.INSTANCE)
                .add(EffectBlink.INSTANCE)
                .add(AugmentAmplify.INSTANCE), "Heroes are so straightforward, so easily befuddled...", new ParticleColor(25, 255, 255)));

        tomes.add(buildTome("xacris","Xacris' Tiny Hut", new Spell()
                        .add(MethodUnderfoot.INSTANCE)
                        .add(EffectPhantomBlock.INSTANCE)
                        .add(AugmentAOE.INSTANCE, 3)
                        .add(AugmentPierce.INSTANCE, 3)
                , "Builds a small hut around the user."));

        tomes.add(buildTome("xacris_2","Xacris's Firework Display", new Spell()
                .add(MethodProjectile.INSTANCE)
                .add(EffectWall.INSTANCE)
                .add(AugmentSensitive.INSTANCE)
                .add(AugmentAOE.INSTANCE)
                .add(EffectFirework.INSTANCE)
                .add(AugmentExtendTime.INSTANCE, 4)
                .add(AugmentAmplify.INSTANCE), "Light up the sky", new ParticleColor(25, 255, 255)));

        tomes.add(buildTome("othy","Othy's Death By 100 Pricks", new Spell()
                .add(MethodProjectile.INSTANCE)
                .add(AugmentPierce.INSTANCE, 2)
                .add(EffectLinger.INSTANCE)
                .add(AugmentSensitive.INSTANCE)
                .add(EffectSummonVex.INSTANCE)
                .add(AugmentExtendTime.INSTANCE), "Swarm your enemies with bladed spirits.", new ParticleColor(255, 255, 255)));

        Path output = this.generator.getOutputFolder();
        for (CasterTomeData g : tomes) {
            Path path = getRecipePath(output, g.getId().getPath());
            DataProvider.saveStable(cache, g.toJson(), path);
        }
    }

    protected Path getRecipePath(Path pathIn, String str) {
        return pathIn.resolve("data/ars_nouveau/recipes/tomes/" + str + ".json");
    }

    public CasterTomeData buildTome(String id, String name, Spell spell, String flavorText) {
        return new CasterTomeData(new ResourceLocation(ArsNouveau.MODID, id + "_tome"),
                name,
                spell.serializeRecipe(),
                ItemsRegistry.CASTER_TOME.registryObject.getId(),
                flavorText,
                ParticleColor.defaultParticleColor().getColor(), ConfiguredSpellSound.DEFAULT);
    }

    public CasterTomeData buildTome(String id, String name, Spell spell, String flavorText, ParticleColor color) {
        CasterTomeData data = buildTome(id, name, spell, flavorText);
        data.particleColor = color.getColor();
        return data;
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Ars Nouveau Caster Tomes Datagen";
    }
}
