package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
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
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;


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

    @Deprecated // Use config-sensitive method
    public void applyPotion(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes){
        applyPotion(entity, potionEffect, augmentTypes, 30, 8);
    }

    public void applyConfigPotion(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes){
        applyPotion(entity, potionEffect, augmentTypes, POTION_TIME == null ? 30 : POTION_TIME.get(), EXTEND_TIME == null ? 8 : EXTEND_TIME.get());
    }

    public boolean canSummon(LivingEntity playerEntity){
        return isRealPlayer(playerEntity) && playerEntity.getEffect(ModPotions.SUMMONING_SICKNESS) == null;
    }
    public void applySummoningSickness(LivingEntity playerEntity, int time){
        playerEntity.addEffect(new EffectInstance(ModPotions.SUMMONING_SICKNESS, time));
    }

    public void summonLivingEntity(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext, ISummon summon){
        if(shooter != null)
            summon.setOwnerID(shooter.getUUID());
        if(summon.getLivingEntity() != null)
            world.addFreshEntity(summon.getLivingEntity());

        MinecraftForge.EVENT_BUS.post(new SummonEvent(rayTraceResult, world, shooter, augments, spellContext, summon));
    }

    public void applyPotionWithCap(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes, int baseDuration, int durationBuffBase, int cap){
        if(entity == null)
            return;
        int duration = baseDuration + durationBuffBase * getDurationModifier(augmentTypes);
        int amp = Math.min(cap, getAmplificationBonus(augmentTypes));
        entity.addEffect(new EffectInstance(potionEffect, duration * 20, amp));
    }

    @Deprecated // Use config-sensitive method. Will become private
    public void applyPotion(LivingEntity entity, Effect potionEffect, List<AbstractAugment> augmentTypes, int baseDuration, int durationBuffBase){
        if(entity == null)
            return;
        int duration = baseDuration + durationBuffBase * getDurationModifier(augmentTypes);
        int amp = getAmplificationBonus(augmentTypes);
        entity.addEffect(new EffectInstance(potionEffect, duration * 20, amp));
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
        return world.getBlockState(pos).getDestroySpeed(world, pos) >= 0 && getBaseHarvestLevel(augments) >= world.getBlockState(pos).getHarvestLevel();
    }

    public void dealDamage(World world, LivingEntity shooter, float damage, List<AbstractAugment> augments, Entity entity, DamageSource source){

        shooter = shooter == null ? FakePlayerFactory.getMinecraft((ServerWorld) world) : shooter;
        if(entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= 0)
            return;

        entity.hurt(source, damage);
        if(!(entity instanceof LivingEntity) )
            return;
        LivingEntity mob = (LivingEntity) entity;


        if(mob.getHealth() <= 0 && !mob.removed && hasBuff(augments, AugmentFortune.class)){
            int looting = getBuffCount(augments, AugmentFortune.class);
            LootContext.Builder lootContext = LootUtil.getLootingContext((ServerWorld)world,shooter, mob, looting, DamageSource.playerAttack((PlayerEntity) shooter));
            ResourceLocation lootTable = mob.getLootTable();
            LootTable loottable = world.getServer().getLootTables().get(lootTable);
            List<ItemStack> items = loottable.getRandomItems(lootContext.create(LootParameterSets.ALL_PARAMS));
            items.forEach(mob::spawnAtLocation);
        }
    }

    public DamageSource buildDamageSource(World world, LivingEntity shooter){
        shooter = shooter == null ? FakePlayerFactory.getMinecraft((ServerWorld) world) : shooter;
        return DamageSource.playerAttack((PlayerEntity) shooter);
    }

    public Vector3d safelyGetHitPos(RayTraceResult result){
        return result instanceof EntityRayTraceResult ? ((EntityRayTraceResult) result).getEntity().position() : result.getLocation();
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
        return rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getBlockPos()).getMaterial() != Material.AIR;
    }

    public boolean livingEntityHitSuccess(RayTraceResult rayTraceResult){
        return rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity;
    }

    public boolean nonAirAnythingSuccess(RayTraceResult result, World world){
        return nonAirBlockSuccess(result, world) || livingEntityHitSuccess(result);
    }

    public void applyEnchantments(List<AbstractAugment> augments, ItemStack stack){
        if(hasBuff(augments, AugmentExtract.class)){
            stack.enchant(Enchantments.SILK_TOUCH, 1);
        }
        if(hasBuff(augments, AugmentFortune.class)){
            stack.enchant(Enchantments.BLOCK_FORTUNE, getBuffCount(augments, AugmentExtract.class));
        }
    }

    protected Set<AbstractAugment> POTION_AUGMENTS = augmentSetOf(
            AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
            AugmentAmplify.INSTANCE
    );

    protected Set<AbstractAugment> SUMMON_AUGMENTS = augmentSetOf(
            AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE
    );

    public ForgeConfigSpec.DoubleValue DAMAGE;
    public ForgeConfigSpec.DoubleValue AMP_VALUE;
    public ForgeConfigSpec.IntValue POTION_TIME;
    public ForgeConfigSpec.IntValue EXTEND_TIME;
    public ForgeConfigSpec.IntValue GENERIC_INT;
    public ForgeConfigSpec.DoubleValue GENERIC_DOUBLE;

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        super.buildAugmentLimitsConfig(builder, getDefaultAugmentLimits());
    }

    public void addDamageConfig(ForgeConfigSpec.Builder builder, double defaultValue){
        DAMAGE = builder.defineInRange("damage", defaultValue, 0, Integer.MAX_VALUE);
    }

    public void addAmpConfig(ForgeConfigSpec.Builder builder, double defaultValue){
        AMP_VALUE = builder.defineInRange("amplify", defaultValue, 0, Integer.MAX_VALUE);
    }

    public void addPotionConfig(ForgeConfigSpec.Builder builder, int defaultTime){
        POTION_TIME = builder.comment("Potion duration, in seconds").defineInRange("potion_time", defaultTime, 0, Integer.MAX_VALUE);
    }

    public void addExtendTimeConfig(ForgeConfigSpec.Builder builder, int defaultTime){
        EXTEND_TIME = builder.comment("Extend time duration, in seconds").defineInRange("extend_time", defaultTime, 0, Integer.MAX_VALUE);
    }

    public void addGenericInt(ForgeConfigSpec.Builder builder, int val, String comment, String path){
        GENERIC_INT = builder.comment(comment).defineInRange(path, val, 0, Integer.MAX_VALUE);
    }

    public void addGenericDouble(ForgeConfigSpec.Builder builder, double val, String comment, String path){
        GENERIC_DOUBLE = builder.comment(comment).defineInRange(path, val, 0.0, Double.MAX_VALUE);
    }

    public void addDefaultPotionConfig(ForgeConfigSpec.Builder builder){
        addPotionConfig(builder, 30);
        addExtendTimeConfig(builder, 8);
    }
}
