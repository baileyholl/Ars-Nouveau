package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityLingeringSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

public class EffectLinger extends AbstractEffect {
    public static EffectLinger INSTANCE = new EffectLinger();

    private EffectLinger() {
        super(GlyphLib.EffectLingerID, "Linger");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext);
        Vector3d hit = safelyGetHitPos(rayTraceResult);
        EntityLingeringSpell entityLingeringSpell = new EntityLingeringSpell(world, shooter);
        spellContext.setCanceled(true);
        if(spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size())
            return;
        Spell newSpell = new Spell(new ArrayList<>(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
        entityLingeringSpell.setAoe(spellStats.getBuffCount(AugmentAOE.INSTANCE));
        entityLingeringSpell.setSensitive(spellStats.hasBuff(AugmentSensitive.INSTANCE));
        entityLingeringSpell.setAccelerates(spellStats.getBuffCount(AugmentAccelerate.INSTANCE));
        entityLingeringSpell.extendedTime = spellStats.getDurationMultiplier();
        entityLingeringSpell.spellResolver = new SpellResolver(spellContext.withSpellResetCounter(newSpell).withCaster(shooter));
        entityLingeringSpell.setPos(hit.x, hit.y, hit.z);
        entityLingeringSpell.setColor(spellContext.colors);
        world.addFreshEntity(entityLingeringSpell);
    }


    @Override
    public String getBookDescription() {
        return "Creates a lingering field that applies spells on nearby entities for a short time. Applying Sensitive will make this spell target blocks instead. AOE will expand the effective range, Accelerate will cast spells faster, and Extend Time will increase the duration.";
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
        return setOf(AugmentSensitive.INSTANCE, AugmentAOE.INSTANCE, AugmentAccelerate.INSTANCE, AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1,1);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
