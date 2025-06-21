package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.SoundProperty;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MethodSelf extends AbstractCastMethod {
    public static MethodSelf INSTANCE = new MethodSelf();

    private MethodSelf() {
        super(GlyphLib.MethodSelfID, "Self");
    }

    @Override
    public CastResolveType onCast(ItemStack stack, LivingEntity caster, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), new EntityHitResult(caster));
        ParticleEmitter emitter = resolveEmitter(context, caster.getHitbox().getCenter());
        emitter.tick(world);
        playResolveSound(context, caster.level(), caster.getHitbox().getCenter());
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = context.getLevel();
        resolver.onResolveEffect(world, new EntityHitResult(context.getPlayer()));
        ParticleEmitter emitter = resolveEmitter(spellContext, context.getPlayer().getHitbox().getCenter());
        emitter.tick(world);
        playResolveSound(spellContext, world, context.getPlayer().getHitbox().getCenter());
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = caster.level;
        resolver.onResolveEffect(world, new EntityHitResult(caster));
        ParticleEmitter emitter = resolveEmitter(spellContext, caster.getHitbox().getCenter());
        emitter.tick(world);
        playResolveSound(spellContext, world, caster.getHitbox().getCenter());
        return CastResolveType.SUCCESS;
    }

    @Override
    public CastResolveType onCastOnEntity(ItemStack stack, LivingEntity playerIn, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Level world = playerIn.level;
        resolver.onResolveEffect(world, new EntityHitResult(playerIn));
        ParticleEmitter emitter = resolveEmitter(spellContext, playerIn.getHitbox().getCenter());
        emitter.tick(world);
        playResolveSound(spellContext, world, target.position());
        return CastResolveType.SUCCESS;
    }

    public ParticleEmitter resolveEmitter(SpellContext spellContext, Vec3 position) {
        TimelineEntryData entryData = spellContext.getParticleTimeline(ParticleTimelineRegistry.SELF_TIMELINE.get()).onResolvingEffect;
        return createStaticEmitter(entryData, position);
    }

    public void playResolveSound(SpellContext spellContext, Level level, Vec3 position) {
        SoundProperty soundProperty = spellContext.getParticleTimeline(ParticleTimelineRegistry.SELF_TIMELINE.get()).resolveSound;
        soundProperty.sound.playSound(level, position.x, position.y, position.z);
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "A spell you start with. Applies spells on the caster.";
    }


    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }
}
