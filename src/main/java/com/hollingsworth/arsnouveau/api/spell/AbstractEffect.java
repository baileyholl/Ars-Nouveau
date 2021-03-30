package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantments;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;


public abstract class AbstractEffect extends AbstractSpellPart {


    public AbstractEffect(String tag, String description) {
        super(tag, description);
    }

    // Apply the effect at the destination position.
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext){
        if(rayTraceResult instanceof BlockRayTraceResult)
            onResolveBlock((BlockRayTraceResult) rayTraceResult, world, shooter, augments, spellContext);

        if(rayTraceResult instanceof EntityRayTraceResult)
            onResolveEntity((EntityRayTraceResult) rayTraceResult, world, shooter, augments, spellContext);
    }

    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext){}


    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext){}

    public void applyPotion(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes){
        applyPotion(entity, potionEffect, augmentTypes, 30, 8);
    }

    public boolean canSummon(LivingEntity playerEntity){
        return isRealPlayer(playerEntity) && playerEntity.getActivePotionEffect(ModPotions.SUMMONING_SICKNESS) == null;
    }
    public void applySummoningSickness(LivingEntity playerEntity, int time){
        playerEntity.addPotionEffect(new EffectInstance(ModPotions.SUMMONING_SICKNESS, time));
    }

    public void summonLivingEntity(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext, ISummon summon){
        if(summon.getLivingEntity() != null)
            world.addEntity(summon.getLivingEntity());
        MinecraftForge.EVENT_BUS.post(new SummonEvent(rayTraceResult, world, shooter, augments, spellContext, summon));
    }

    public void applyPotionWithCap(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes, int baseDuration, int durationBuffBase, int cap){
        if(entity == null)
            return;
        int duration = baseDuration + durationBuffBase * getDurationModifier(augmentTypes);
        int amp = Math.min(cap, getAmplificationBonus(augmentTypes));
        entity.addPotionEffect(new EffectInstance(potionEffect, duration * 20, amp));
    }

    public void applyPotion(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes, int baseDuration, int durationBuffBase){
        if(entity == null)
            return;
        int duration = baseDuration + durationBuffBase * getDurationModifier(augmentTypes);
        int amp = getAmplificationBonus(augmentTypes);
        entity.addPotionEffect(new EffectInstance(potionEffect, duration * 20, amp));
    }

    public int getDurationModifier( List<AbstractAugment> augmentTypes){
        return getBuffCount(augmentTypes, AugmentExtendTime.class) - getBuffCount(augmentTypes, AugmentDurationDown.class);
    }

    public PlayerEntity getPlayer(LivingEntity entity, ServerWorld world){
        return entity instanceof PlayerEntity ? (PlayerEntity) entity : FakePlayerFactory.getMinecraft(world);
    }

    public int getBaseHarvestLevel(List<AbstractAugment> augments){
        return 2 + getAmplificationBonus(augments);
    }

    public boolean canBlockBeHarvested(List<AbstractAugment> augments, World world, BlockPos pos){
        return world.getBlockState(pos).getBlockHardness(world, pos) >= 0 && getBaseHarvestLevel(augments) >= world.getBlockState(pos).getHarvestLevel();
    }

    public void dealDamage(World world, LivingEntity shooter, float damage, List<AbstractAugment> augments, Entity entity, DamageSource source){

        shooter = shooter == null ? FakePlayerFactory.getMinecraft((ServerWorld) world) : shooter;
        entity.attackEntityFrom(source, damage);
        if(!(entity instanceof LivingEntity))
            return;
        LivingEntity mob = (LivingEntity) entity;

        if(mob.getHealth() <= 0 && !mob.removed && hasBuff(augments, AugmentFortune.class)){
            int looting = getBuffCount(augments, AugmentFortune.class);
            LootContext.Builder lootContext = LootUtil.getLootingContext((ServerWorld)world,shooter, mob, looting, DamageSource.causePlayerDamage((PlayerEntity) shooter));
            ResourceLocation lootTable = mob.getLootTableResourceLocation();
            LootTable loottable = world.getServer().getLootTableManager().getLootTableFromLocation(lootTable);
            List<ItemStack> items = loottable.generate(lootContext.build(LootParameterSets.GENERIC));
            items.forEach(mob::entityDropItem);
        }
    }

    public DamageSource buildDamageSource(World world, LivingEntity shooter){
        shooter = shooter == null ? FakePlayerFactory.getMinecraft((ServerWorld) world) : shooter;
        return DamageSource.causePlayerDamage((PlayerEntity) shooter);
    }

    public Vector3d safelyGetHitPos(RayTraceResult result){
        return result instanceof EntityRayTraceResult ? ((EntityRayTraceResult) result).getEntity().getPositionVec() : result.getHitVec();
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

    public void applyEnchantments(List<AbstractAugment> augments, ItemStack stack){
        if(hasBuff(augments, AugmentExtract.class)){
            stack.addEnchantment(Enchantments.SILK_TOUCH, 1);
        }
        if(hasBuff(augments, AugmentFortune.class)){
            stack.addEnchantment(Enchantments.FORTUNE, getBuffCount(augments, AugmentExtract.class));
        }
    }
}
