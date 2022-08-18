package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public class EffectDispel extends AbstractEffect {
    public static EffectDispel INSTANCE = new EffectDispel();

    private EffectDispel() {
        super(GlyphLib.EffectDispelID, "Dispel");
    }


    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity entity) {
            Collection<MobEffectInstance> effects = entity.getActiveEffects();
            MobEffectInstance[] array = effects.toArray(new MobEffectInstance[0]);
            for (MobEffectInstance e : array) {
                if (e.isCurativeItem(new ItemStack(Items.MILK_BUCKET)))
                    entity.removeEffect(e.getEffect());
            }
            if(!entity.isAlive() || entity.getHealth() <= 0 || entity.isRemoved()){
                return;
            }
            if (MinecraftForge.EVENT_BUS.post(new DispelEvent.Pre(rayTraceResult, world, shooter, spellStats, spellContext)))
                return;
            if (entity instanceof IDispellable iDispellable) {
                iDispellable.onDispel(shooter);
            }
            MinecraftForge.EVENT_BUS.post(new DispelEvent.Post(rayTraceResult, world, shooter, spellStats, spellContext));
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (MinecraftForge.EVENT_BUS.post(new DispelEvent.Pre(rayTraceResult, world, shooter, spellStats, spellContext)))
            return;
        if (world.getBlockState(rayTraceResult.getBlockPos()) instanceof IDispellable dispellable) {
            dispellable.onDispel(shooter);
        }
        MinecraftForge.EVENT_BUS.post(new DispelEvent.Post(rayTraceResult, world, shooter, spellStats, spellContext));
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        // Augments were sent with the DispelEvent, but there's no use of its augments field.
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Removes any potion effects on the target. When used on a witch at half health, the witch will vanish in return for a Wixie shard. Will also dispel tamed summons back into their charm.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ABJURATION);
    }
}
