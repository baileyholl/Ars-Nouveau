package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class ShapersFocus extends ArsNouveauCurio implements ISpellModifierItem {

    public ShapersFocus(Properties properties) {
        super(properties);
        withTooltip(Component.translatable("tooltip.ars_nouveau.shapers_focus"));
    }


    public static @Nullable SpellContext tryPropagateEntitySpell(EnchantedFallingBlock fallingblockentity, Level level, Entity shooter, SpellContext spellContext, SpellResolver resolver) {
        if (!resolver.hasFocus(ItemsRegistry.SHAPERS_FOCUS.get()))
            return null;
        SpellContext context = spellContext.makeChildContext();
        spellContext.setCanceled(true);
        fallingblockentity.context = context;
        EntityHitResult hitResult = new EntityHitResult(fallingblockentity, fallingblockentity.position);
        SpellResolver newResolver = resolver.getNewResolver(context);
        if (spellContext.isDelayed()) {
            var currenDelay = spellContext.getDelayedSpellEvent();
            newResolver.hitResult = hitResult;
            DelayedSpellEvent delayedSpellEvent = new DelayedSpellEvent(currenDelay.duration, hitResult, level, newResolver);
            EventQueue.getServerInstance().addEvent(delayedSpellEvent);
            context.delay(delayedSpellEvent);
        } else {
            newResolver.onResolveEffect(level, new EntityHitResult(fallingblockentity, fallingblockentity.position));
        }
        return context;
    }

    /**
     * @return the new context
     */
    public static @Nullable SpellContext tryPropagateBlockSpell(BlockHitResult blockHitResult, Level level, Entity shooter, SpellContext spellContext, SpellResolver resolver) {
        if (!resolver.hasFocus(ItemsRegistry.SHAPERS_FOCUS.get()))
            return null;
        SpellContext context = spellContext.makeChildContext();
        spellContext.setCanceled(true);
        SpellResolver newResolver = resolver.getNewResolver(context);
        if (spellContext.isDelayed()) {
            var currenDelay = spellContext.getDelayedSpellEvent();
            newResolver.hitResult = blockHitResult;
            DelayedSpellEvent delayedSpellEvent = new DelayedSpellEvent(currenDelay.duration, blockHitResult, level, newResolver);
            EventQueue.getServerInstance().addEvent(delayedSpellEvent);
            context.delay(delayedSpellEvent);
        } else {
            newResolver.onResolveEffect(level, blockHitResult);
        }

        return context;
    }

    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        builder.addDamageModifier(1.0f);
        return builder;
    }
}
