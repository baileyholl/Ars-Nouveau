package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

public class EffectSnare extends AbstractEffect {
    public static EffectSnare INSTANCE = new EffectSnare();

    private EffectSnare() {
        super(GlyphLib.EffectSnareID, "Snare");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        if (rayTraceResult.getEntity() instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(ModPotions.SNARE_EFFECT.get(), (int) (POTION_TIME.get() * 20 + 20 * EXTEND_TIME.get() * spellStats.getDurationMultiplier()), 20));
            living.setDeltaMovement(0, 0, 0);
            living.hurtMarked = true;
        }

        if (rayTraceResult.getEntity() instanceof EnchantedFallingBlock fallingBlock) {
            BlockPos resultPos = fallingBlock.groundBlock(true);
            if(resultPos != null) {
                ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(new Vec3(resultPos.getX(), resultPos.getY(), resultPos.getZ()), rayTraceResult.getEntity().getMotionDirection(), resultPos, false), world, shooter, spellContext, resolver);
            }
        }

    }


    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
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
}
