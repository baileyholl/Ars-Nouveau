package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;


public abstract class AbstractEffect extends AbstractSpellPart {


    public AbstractEffect(String tag, String description) {
        super(tag, description);
    }

    // Apply the effect at the destination position.
    public abstract void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext);

    public void applyPotion(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes){
        applyPotion(entity, potionEffect, augmentTypes, 30, 8);
    }

    public void applyPotion(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes, int baseDuration, int durationBuffBase){
        if(entity == null)
            return;
        int duration = baseDuration + durationBuffBase * getDurationModifier(augmentTypes);
        int amp = getBuffCount(augmentTypes, AugmentAmplify.class);
        entity.addPotionEffect(new EffectInstance(potionEffect, duration * 20, amp));
    }

    public int getDurationModifier( List<AbstractAugment> augmentTypes){
        return getBuffCount(augmentTypes, AugmentExtendTime.class) - getBuffCount(augmentTypes, AugmentDurationDown.class);
    }

    public float getHardness(List<AbstractAugment> augments){
        float maxHardness = 5.0f + 25 * getAmplificationBonus(augments);
        int buff = getAmplificationBonus(augments);
        if(buff == -1){
            maxHardness = 2.5f;
        }else if(buff == -2){
            maxHardness = 1.0f;
        }else if(buff < -2){
            maxHardness = 0.5f;
        }
        return maxHardness;
    }

    public void dealDamage(World world, LivingEntity shooter, float damage, List<AbstractAugment> augments, Entity entity, DamageSource source){
        LivingEntity mob = (LivingEntity) entity;
        shooter = shooter == null ? FakePlayerFactory.getMinecraft((ServerWorld) world) : shooter;
        entity.attackEntityFrom(source, damage);
        if(mob.getHealth() <= 0 && !mob.removed && hasBuff(augments, AugmentFortune.class)){
            int looting = getBuffCount(augments, AugmentFortune.class);
            LootContext.Builder lootContext = LootUtil.getLootingContext((ServerWorld)world,shooter, mob, looting, DamageSource.causePlayerDamage((PlayerEntity) shooter));
            ResourceLocation lootTable = mob.getLootTableResourceLocation();
            LootTable loottable = world.getServer().getLootTableManager().getLootTableFromLocation(lootTable);
            List<ItemStack> items = loottable.generate(lootContext.build(LootParameterSets.GENERIC));
            items.forEach(mob::entityDropItem);
        }
    }

    public Vector3d safelyGetHitPos(RayTraceResult result){
        if(result instanceof EntityRayTraceResult)
            return ((EntityRayTraceResult) result).getEntity() != null ? ((EntityRayTraceResult) result).getEntity().getPositionVec() : result.getHitVec();
        return result.getHitVec();
    }

    public boolean isRealPlayer(LivingEntity entity){
        return entity instanceof PlayerEntity && isNotFakePlayer(entity);
    }

    public boolean isNotFakePlayer(LivingEntity entity){
        return !(entity instanceof FakePlayer);
    }

    // If the spell would actually do anything. Can be used for logic checks for things like the whelp.
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments){
        return true;
    }

    public boolean nonAirBlockSuccess(RayTraceResult rayTraceResult, World world){
        return rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getPos()).getMaterial() != Material.AIR;
    }

    public boolean livingEntityHitSuccess(RayTraceResult rayTraceResult){
        return rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity;
    }

    public boolean nonAirAnythingSuccess(RayTraceResult result, World world){
        return nonAirBlockSuccess(result, world) || livingEntityHitSuccess(result);
    }
}
