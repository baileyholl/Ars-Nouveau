package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectDelay extends AbstractEffect {
    public EffectDelay() {
        super(ModConfig.EffectDelayID, "Delay");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        spellContext.setCanceled(true);

        if(spellContext.getCurrentIndex() >= spellContext.spell.recipe.size())
            return;
        EventQueue.getInstance().addEvent(
                new DelayedSpellEvent(20 + getBuffCount(augments, AugmentExtendTime.class) * 20,
                        spellContext.spell.recipe.subList(spellContext.getCurrentIndex(), spellContext.spell.recipe.size()),
                        rayTraceResult, world, shooter));

    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Override
    protected String getBookDescription() {
        return "Delays the resolution of effects placed to the right of this spell for a few moments. The delay may be increased with the Extend Time augment.";
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
}
