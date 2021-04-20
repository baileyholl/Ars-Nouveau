package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.entity.EntityAllyVex;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class EffectSummonVex extends AbstractEffect {
    public EffectSummonVex() {
        super(GlyphLib.EffectSummonVexID, "Summon Vex");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(isRealPlayer(shooter) && shooter != null && shooter.getEffect(ModPotions.SUMMONING_SICKNESS) == null){
            summonEntities(shooter, world, augments, rayTraceResult.getEntity().blockPosition());
        }
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(isRealPlayer(shooter) && shooter != null && shooter.getEffect(ModPotions.SUMMONING_SICKNESS) == null){
            summonEntities(shooter, world, augments, rayTraceResult.getBlockPos());
        }
    }

    public void summonEntities(LivingEntity shooter, World world, List<AbstractAugment> augments, BlockPos pos){
        int ticks = 20 * (15 + 10 * getBuffCount(augments, AugmentExtendTime.class));
        for(int i = 0; i < 3; ++i) {
            BlockPos blockpos = pos.offset(-2 + shooter.getRandom().nextInt(5), 2, -2 + shooter.getRandom().nextInt(5));
            EntityAllyVex vexentity = new EntityAllyVex(world, shooter);
            vexentity.moveTo(blockpos, 0.0F, 0.0F);
            vexentity.finalizeSpawn((IServerWorld) world, world.getCurrentDifficultyAt(blockpos), SpawnReason.MOB_SUMMONED, (ILivingEntityData)null, (CompoundNBT)null);
            vexentity.setOwner(shooter);
            vexentity.setBoundOrigin(blockpos);
            vexentity.setLimitedLife(ticks);
            world.addFreshEntity(vexentity);
        }
        shooter.addEffect(new EffectInstance(ModPotions.SUMMONING_SICKNESS, ticks));
    }


    @Override
    public int getManaCost() {
        return 75;
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

    @Override
    public String getBookDescription() {
        return "Summons three Vex allies that will attack nearby hostile enemies. These Vex will last a short time until they begin to take damage, but time may be extended with the " +
                "Extend Time augment.";
    }
}
