package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
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
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectDelay extends AbstractEffect {
    public static EffectDelay INSTANCE = new EffectDelay();

    private EffectDelay() {
        super(GlyphLib.EffectDelayID, "Delay");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        spellContext.setCanceled(true);

        if(spellContext.getCurrentIndex() >= spellContext.spell.recipe.size())
            return;
        EventQueue.getInstance().addEvent(
                new DelayedSpellEvent(GENERIC_INT.get() + EXTEND_TIME.get() * getBuffCount(augments, AugmentExtendTime.class) * 20,
                        spellContext.spell.recipe.subList(spellContext.getCurrentIndex(), spellContext.spell.recipe.size()),
                        rayTraceResult, world, shooter));

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

    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE);
    }

    @Override
    public String getBookDescription() {
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
