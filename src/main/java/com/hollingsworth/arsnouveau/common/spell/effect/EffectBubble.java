package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.BubbleEntity;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectBubble extends AbstractEffect {

    public static final EffectBubble INSTANCE = new EffectBubble();

    public EffectBubble() {
        super(GlyphLib.EffectBubbleID, "Bubble");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        var bubble = new BubbleEntity(world, (int) (100 + spellStats.getDurationMultiplier() * EXTEND_TIME.getAsInt()), (float) (DAMAGE.getAsDouble() + spellStats.getAmpMultiplier() * AMP_VALUE.getAsDouble()));
        bubble.setPos(rayTraceResult.getLocation().x, rayTraceResult.getLocation().y, rayTraceResult.getLocation().z);
        world.addFreshEntity(bubble);
    }

    @Override
    protected int getDefaultManaCost() {
        return 20;
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 5.0);
        addAmpConfig(builder, 2.0);
        addExtendTimeConfig(builder, 3);
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of(AugmentExtendTime.INSTANCE, AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentDurationDown.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Captures mobs and entities it touches, causing them to float upwards. If the bubble has been alive for at least one tick, damaging the entity trapped in the bubble will cause it to pop, dealing bonus damage to the entity inside. Extend time and amplify can be used to increase the duration and damage of the bubble.";
    }
}
