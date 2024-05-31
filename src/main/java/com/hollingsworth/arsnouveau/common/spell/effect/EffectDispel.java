package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.lib.PotionEffectTags;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class EffectDispel extends AbstractEffect {
    public static EffectDispel INSTANCE = new EffectDispel();

    private EffectDispel() {
        super(GlyphLib.EffectDispelID, "Dispel");
    }


    @Override
    public void onResolveEntity(@NotNull EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity entity) {
            Collection<MobEffectInstance> effects = entity.getActiveEffects();
            MobEffectInstance[] array = effects.toArray(new MobEffectInstance[0]);
            Optional<HolderSet.Named<MobEffect>> blacklist = world.registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTag(PotionEffectTags.DISPEL_DENY);
            Optional<HolderSet.Named<MobEffect>> whitelist =  world.registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTag(PotionEffectTags.DISPEL_ALLOW);
            for (MobEffectInstance e : array) {
                if (e.isCurativeItem(new ItemStack(Items.MILK_BUCKET))) {
                    if (blacklist.isPresent() && blacklist.get().stream().anyMatch(effect -> effect.get() == e.getEffect()))
                        continue;
                    entity.removeEffect(e.getEffect());
                } else if (whitelist.isPresent() && whitelist.get().stream().anyMatch(effect -> effect.get() == e.getEffect())) {
                    entity.removeEffect(e.getEffect());
                }
            }
            if (!entity.isAlive() || entity.getHealth() <= 0 || entity.isRemoved()) {
                //TODO dispel loot table?
                return;
            }
            if (NeoForge.EVENT_BUS.post(new DispelEvent.Pre(rayTraceResult, world, shooter, spellStats, spellContext)))
                return;
            if (entity instanceof IDispellable iDispellable) {
                iDispellable.onDispel(shooter);
            }
            NeoForge.EVENT_BUS.post(new DispelEvent.Post(rayTraceResult, world, shooter, spellStats, spellContext));
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (NeoForge.EVENT_BUS.post(new DispelEvent.Pre(rayTraceResult, world, shooter, spellStats, spellContext)))
            return;
        if (world.getBlockState(rayTraceResult.getBlockPos()) instanceof IDispellable dispellable) {
            dispellable.onDispel(shooter);
        }
        if (world.getBlockEntity(rayTraceResult.getBlockPos()) instanceof IDispellable dispellable) {
            dispellable.onDispel(shooter);
        }
        NeoForge.EVENT_BUS.post(new DispelEvent.Post(rayTraceResult, world, shooter, spellStats, spellContext));
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        // Augments were sent with the DispelEvent, but there's no use of its augments field.
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Removes any potion effects on the target. When used on a witch at half health, the witch will vanish in return for a Wixie shard. Will also dispel tamed summons back into their charm.";
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ABJURATION);
    }
}
