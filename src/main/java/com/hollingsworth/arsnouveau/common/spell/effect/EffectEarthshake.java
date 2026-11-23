package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.event.timed.ShockwaveEvent;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectEarthshake extends AbstractEffect {

    public static final EffectEarthshake INSTANCE = new EffectEarthshake();

    private EffectEarthshake() {
        super(GlyphLib.EffectEarthshakeID, "Earthshake");
    }

    @Override
    public void onResolveBlock(BlockHitResult hitResult, Level level, @NotNull LivingEntity shooter, SpellStats stats, SpellContext context, SpellResolver resolver) {
        createWave(level, hitResult.getLocation(), hitResult.getBlockPos().relative(hitResult.getDirection()), hitResult.getDirection(), stats, context, resolver);
    }

    @Override
    public void onResolveEntity(EntityHitResult hitResult, Level level, @NotNull LivingEntity shooter, SpellStats stats, SpellContext context, SpellResolver resolver) {
        createWave(level, hitResult.getEntity().position(), hitResult.getEntity().blockPosition(), Direction.UP, stats, context, resolver);
    }

    private void createWave(Level level, Vec3 origin, BlockPos center, Direction face, SpellStats stats, SpellContext context, SpellResolver resolver) {
        if (!(level instanceof ServerLevel serverLevel) || context.getRemainingSpell().isEmpty()) {
            return;
        }
        SpellContext childContext = context.makeChildContext();
        context.setCanceled(true);
        EventQueue.getServerInstance().addEvent(new ShockwaveEvent(serverLevel, origin, center, face, 5.0 + stats.getAoeMultiplier(),
                stats.isSensitive(), stats.getBuffCount(AugmentPierce.INSTANCE), resolver.getNewResolver(childContext)));
    }

    @Override
    protected int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentSensitive.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        map.put(AugmentAOE.INSTANCE, "Increases the maximum radius by one block.");
        map.put(AugmentSensitive.INSTANCE, "Targets blocks instead of entities.");
        map.put(AugmentPierce.INSTANCE, "Increases depth radius by one block");
    }

    @Override
    public String getBookDescription() {
        return "";
    }
}
