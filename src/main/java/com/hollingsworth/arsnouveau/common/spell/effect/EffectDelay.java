package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketClientDelayEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectDelay extends AbstractEffect {
    public static EffectDelay INSTANCE = new EffectDelay();

    private EffectDelay() {
        super(GlyphLib.EffectDelayID, "Delay");
    }

    public void sendPacket(World world, RayTraceResult rayTraceResult, @Nullable LivingEntity shooter, SpellContext spellContext, SpellStats spellStats,BlockRayTraceResult blockResult, Entity hitEntity){
        spellContext.setCanceled(true);
        if(spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size())
            return;
        Spell newSpell =  new Spell(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size()));
        SpellContext newContext = new SpellContext(newSpell, shooter).withColors(spellContext.colors);
        int duration = GENERIC_INT.get() + EXTEND_TIME.get() * getBuffCount(spellStats.getAugments(), AugmentExtendTime.class) * 20 - (EXTEND_TIME.get() / 2) * getBuffCount(spellStats.getAugments(), AugmentDurationDown.class) * 20;
        EventQueue.getServerInstance().addEvent(
                new DelayedSpellEvent(duration , newSpell, rayTraceResult, world, shooter, newContext));
        Networking.sendToNearby(world, new BlockPos(safelyGetHitPos(rayTraceResult)),
                new PacketClientDelayEffect(duration, shooter, newSpell, newContext, blockResult, hitEntity));
    }


    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        sendPacket(world, rayTraceResult, shooter, spellContext, spellStats, rayTraceResult, null);
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        sendPacket(world, rayTraceResult, shooter, spellContext, spellStats, null, rayTraceResult.getEntity());
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addExtendTimeConfig(builder, 1);
        addGenericInt(builder, 20, "Base duration in ticks.", "base_duration");
    }

    @Override
    public int getManaCost() {
        return 0;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Delays the resolution of effects placed to the right of this spell for a few moments. The delay may be increased with the Extend Time augment, or decreased with Duration Down.";
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.REPEATER;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
