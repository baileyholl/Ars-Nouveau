package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

import net.minecraft.world.item.Item.Properties;

public class ShapersFocus extends ArsNouveauCurio implements ISpellModifierItem {

    public ShapersFocus(Properties properties) {
        super(properties);
    }

    public ShapersFocus() {
        super();
    }

    public static void tryPropagateEntitySpell(EnchantedFallingBlock fallingblockentity, Level level, LivingEntity shooter, SpellContext spellContext, SpellResolver resolver) {
        if (!resolver.hasFocus(ItemsRegistry.SHAPERS_FOCUS.get().getDefaultInstance()))
            return;
        SpellResolver newResolver = resolver.getNewResolver(spellContext.clone().withSpell(spellContext.getRemainingSpell()));
        newResolver.onResolveEffect(level, new EntityHitResult(fallingblockentity, fallingblockentity.position));
        spellContext.setCanceled(true);
    }

    public static void tryPropagateBlockSpell(BlockHitResult blockHitResult, Level level, LivingEntity shooter, SpellContext spellContext, SpellResolver resolver) {
        if (!resolver.hasFocus(ItemsRegistry.SHAPERS_FOCUS.get().getDefaultInstance()))
            return;
        SpellResolver newResolver = resolver.getNewResolver(spellContext.clone().withSpell(spellContext.getRemainingSpell()));
        newResolver.onResolveEffect(level, blockHitResult);
        spellContext.setCanceled(true);
    }

    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        builder.addDamageModifier(1.0f);
        return builder;
    }
}
