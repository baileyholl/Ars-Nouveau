package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class TimeFocus extends ArsNouveauCurio implements ISpellModifierItem {
    public static final String WAS_DELAYED = "an_was_delayed";

    public TimeFocus(Properties properties) {
        super(properties);
        withTooltip(Component.translatable("tooltip.ars_nouveau.shapers_focus"));
    }

    public static void delayNextPart(Level level, SpellResolver resolver){
        SpellContext context = resolver.spellContext.makeChildContext();
        resolver.spellContext.stop();
        // Step back one glyph because it wasn't actually ever resolved
        resolver.spellContext.setCurrentIndex(resolver.spellContext.getCurrentIndex() - 1);
        context.withSpell(resolver.spellContext.getRemainingSpell());
        EventQueue.getServerInstance().addEvent(
                new DelayedSpellEvent(5, resolver.hitResult, level, context));
    }

    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        builder.addDamageModifier(1.0f);
        return builder;
    }
}
