package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.entity.EntityLingeringSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectLinger extends AbstractEffect {
    public static EffectLinger INSTANCE = new EffectLinger();

    private EffectLinger() {
        super(GlyphLib.EffectLingerID, "Linger");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext);
        IMana mana = ManaCapability.getMana(shooter).orElse(null);
        System.out.println(mana.getMaxMana());
        Vector3d hit = safelyGetHitPos(rayTraceResult);
        EntityLingeringSpell entityLingeringSpell = new EntityLingeringSpell(world, shooter);
        spellContext.setCanceled(true);
        if(spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size())
            return;
        Spell newSpell =  new Spell(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size()));
        entityLingeringSpell.setAoe(spellStats.getBuffCount(AugmentAOE.INSTANCE));
        entityLingeringSpell.setSensitive(spellStats.hasBuff(AugmentSensitive.INSTANCE));
        entityLingeringSpell.setAccelerates(spellStats.getBuffCount(AugmentAccelerate.INSTANCE));
        entityLingeringSpell.setAccelerates((int) spellStats.getDurationMultiplier());
        entityLingeringSpell.spellResolver = new SpellResolver(new SpellContext(newSpell, shooter).withColors(spellContext.colors));
        entityLingeringSpell.setPos(hit.x, hit.y, hit.z);
        entityLingeringSpell.setColor(spellContext.colors);
     //   entityLingeringSpell.setLanded(true);
        world.addFreshEntity(entityLingeringSpell);
    }


    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.DRAGON_BREATH;
    }

    @Override
    public int getManaCost() {
        return 500;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return setOf(AugmentSensitive.INSTANCE, AugmentAOE.INSTANCE, AugmentSensitive.INSTANCE);
    }
}
