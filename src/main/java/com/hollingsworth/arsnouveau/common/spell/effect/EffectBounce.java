package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectBounce extends AbstractEffect implements IPotionEffect {

    public static EffectBounce INSTANCE = new EffectBounce();

    private EffectBounce() {
        super(GlyphLib.EffectBounceID, "Bounce");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity living) {
            ((IPotionEffect)this).applyConfigPotion(living, ModPotions.BOUNCE_EFFECT, spellStats);
        }
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 30);
        addExtendTimeConfig(builder, 8);
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }


   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ABJURATION);
    }

    @Override
    public String getBookDescription() {
        return "Gives players the Bounce effect, causing them to bounce upwards upon falling. Amplification of Bounce will preserve additional forward facing motion per bounce.";
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return getPotionAugments();
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 30 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 8 : EXTEND_TIME.get();
    }
}
