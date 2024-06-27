package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectSnare extends AbstractEffect implements IPotionEffect {
    public static EffectSnare INSTANCE = new EffectSnare();

    private EffectSnare() {
        super(GlyphLib.EffectSnareID, "Snare");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        if (rayTraceResult.getEntity() instanceof LivingEntity living) {
            ((IPotionEffect)this).applyConfigPotion(living, ModPotions.SNARE_EFFECT, spellStats);
            living.setDeltaMovement(0, 0, 0);
            living.hurtMarked = true;
        }else if (rayTraceResult.getEntity() instanceof EnchantedFallingBlock fallingBlock) {
            BlockPos resultPos = fallingBlock.groundBlock(true);
            if(resultPos != null) {
                ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(new Vec3(resultPos.getX(), resultPos.getY(), resultPos.getZ()), rayTraceResult.getEntity().getMotionDirection(), resultPos, false), world, shooter, spellContext, resolver);
            }
        }

    }


    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 8);
        addExtendTimeConfig(builder, 1);
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Stops entities from moving and jumping. Extend Time will increase the duration of this effect. Snaring a block created from the Focus of Block Shaping will cause it to attempt to place itself immediately.";
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 8 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 1 : EXTEND_TIME.get();
    }
}
