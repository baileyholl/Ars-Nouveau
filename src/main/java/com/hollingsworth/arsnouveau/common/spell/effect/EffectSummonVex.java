package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityAllyVex;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectSummonVex extends AbstractEffect {
    public static EffectSummonVex INSTANCE = new EffectSummonVex();

    private EffectSummonVex() {
        super(GlyphLib.EffectSummonVexID, "Summon Vex");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(!canSummon(shooter))
            return;

        Vec3 vector3d = safelyGetHitPos(rayTraceResult);
        int ticks = (int) (20 * (GENERIC_INT.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier()));
        BlockPos pos = new BlockPos(vector3d);

        for(int i = 0; i < 3; ++i) {
            BlockPos blockpos = pos.offset(-2 + shooter.getRandom().nextInt(5), 2, -2 + shooter.getRandom().nextInt(5));
            EntityAllyVex vexentity = new EntityAllyVex(world, shooter);
            vexentity.moveTo(blockpos, 0.0F, 0.0F);
            vexentity.finalizeSpawn((ServerLevelAccessor) world, world.getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, null, null);
            vexentity.setOwner(shooter);
            vexentity.setBoundOrigin(blockpos);
            vexentity.setLimitedLife(ticks);
            world.addFreshEntity(vexentity);
        }
        shooter.addEffect(new MobEffectInstance(ModPotions.SUMMONING_SICKNESS, ticks));
    }


    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 15, "Base duration in seconds", "duration");
        addExtendTimeConfig(builder, 10);
    }

    @Override
    public int getDefaultManaCost() {
        return 150;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.TOTEM_OF_UNDYING;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return SUMMON_AUGMENTS;
    }

    @Override
    public String getBookDescription() {
        return "Summons three Vex allies that will attack nearby hostile enemies. These Vex will last a short time until they begin to take damage, but time may be extended with the " +
                "Extend Time augment.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION);
    }
}
