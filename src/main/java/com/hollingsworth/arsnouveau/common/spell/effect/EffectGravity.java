package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectGravity extends AbstractEffect {
    public static EffectGravity INSTANCE = new EffectGravity();

    private EffectGravity() {
        super(GlyphLib.EffectGravityID, "Gravity");
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        BlockPos pos = rayTraceResult.getBlockPos();
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, spellStats.getBuffCount(AugmentAOE.INSTANCE), spellStats.getBuffCount(AugmentPierce.INSTANCE));

        for(BlockPos pos1 : posList) {
            if(world.getBlockEntity(pos1) != null || !canBlockBeHarvested(spellStats, world, pos1) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos1))
                continue;

            FallingBlockEntity blockEntity = new FallingBlockEntity(world,pos1.getX() +0.5, pos1.getY(), pos1.getZ() +0.5, world.getBlockState(pos1));
            world.addFreshEntity(blockEntity);
        }
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(rayTraceResult.getEntity() instanceof LivingEntity){
            if(spellStats.hasBuff(AugmentExtendTime.INSTANCE)){
                applyConfigPotion((LivingEntity) rayTraceResult.getEntity(), ModPotions.GRAVITY_EFFECT, spellStats);
            }else{
                Entity entity = rayTraceResult.getEntity();
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, -1.0 - spellStats.getDurationMultiplier(), 0));
                entity.hurtMarked = true;
            }
        }
    }

    @Override
    public int getManaCost() {
        return 15;
    }

    @Override
    public Item getCraftingReagent() {
        return Items.ANVIL;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 30);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentAOE.INSTANCE,
                AugmentPierce.INSTANCE,
                AugmentExtendTime.INSTANCE,
                AugmentDurationDown.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Causes blocks and entities to fall. When augmented with Extend Time, players will have their flight disabled and will obtain the Gravity effect. While afflicted with Gravity, entities will rapidly fall and take double falling damage.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
