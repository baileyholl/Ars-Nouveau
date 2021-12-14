package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.VoidJar;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;


public abstract class AbstractEffect extends AbstractSpellPart {


    public AbstractEffect(String tag, String description) {
        super(tag, description);
    }

    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext){
        if(rayTraceResult instanceof BlockHitResult){
            onResolveBlock((BlockHitResult) rayTraceResult, world, shooter, spellStats, spellContext);
        }else if(rayTraceResult instanceof EntityHitResult){
            onResolveEntity((EntityHitResult) rayTraceResult, world, shooter, spellStats, spellContext);
        }
    }

    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext){}

    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext){}

    public void applyConfigPotion(LivingEntity entity, MobEffect potionEffect, SpellStats spellStats){
        applyConfigPotion(entity, potionEffect, spellStats, true);
    }

    public void applyConfigPotion(LivingEntity entity, MobEffect potionEffect, SpellStats spellStats, boolean showParticles){
        applyPotion(entity, potionEffect, spellStats, POTION_TIME == null ? 30 : POTION_TIME.get(), EXTEND_TIME == null ? 8 : EXTEND_TIME.get(), showParticles);
    }

    public void applyPotionWithCap(LivingEntity entity, MobEffect potionEffect, SpellStats stats, int baseDuration, int durationBuffBase, int cap){
        if(entity == null)
            return;
        int duration = (int) (baseDuration + durationBuffBase * stats.getDurationMultiplier());
        int amp = Math.min(cap, (int)stats.getAmpMultiplier());
        entity.addEffect(new MobEffectInstance(potionEffect, duration * 20, amp));
    }

    public void applyPotion(LivingEntity entity, MobEffect potionEffect, SpellStats stats, int baseDurationSeconds, int durationBuffSeconds, boolean showParticles){
        if(entity == null)
            return;
        int ticks = baseDurationSeconds * 20 + durationBuffSeconds * stats.getDurationInTicks();
        int amp = (int) stats.getAmpMultiplier();
        entity.addEffect(new MobEffectInstance(potionEffect, ticks, amp, false, showParticles, true));
    }

    public boolean canSummon(LivingEntity playerEntity){
        return isRealPlayer(playerEntity) && playerEntity.getEffect(ModPotions.SUMMONING_SICKNESS) == null;
    }

    public void applySummoningSickness(LivingEntity playerEntity, int time){
        playerEntity.addEffect(new MobEffectInstance(ModPotions.SUMMONING_SICKNESS, time));
    }

    public void summonLivingEntity(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats augments, SpellContext spellContext, ISummon summon){
        if(shooter != null)
            summon.setOwnerID(shooter.getUUID());
        if(summon.getLivingEntity() != null)
            world.addFreshEntity(summon.getLivingEntity());

        MinecraftForge.EVENT_BUS.post(new SummonEvent(rayTraceResult, world, shooter, augments, spellContext, summon));
    }

    public Player getPlayer(LivingEntity entity, ServerLevel world){
        return entity instanceof Player ? (Player) entity : FakePlayerFactory.getMinecraft(world);
    }

    public int getBaseHarvestLevel(SpellStats stats){
        return (int) (2 + stats.getAmpMultiplier());
    }

    public boolean canBlockBeHarvested(SpellStats stats, Level world, BlockPos pos){
        return world.getBlockState(pos).getDestroySpeed(world, pos) >= 0 && SpellUtil.isCorrectHarvestLevel(getBaseHarvestLevel(stats), world.getBlockState(pos));
    }

    public void dealDamage(Level world, LivingEntity shooter, float baseDamage, SpellStats stats, Entity entity, DamageSource source){
        shooter = shooter == null ? FakePlayerFactory.getMinecraft((ServerLevel) world) : shooter;
        float totalDamage = (float) (baseDamage + stats.getDamageModifier());

        if((entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= 0) || totalDamage <= 0)
            return;

        entity.hurt(source, totalDamage);
        Player playerContext = shooter instanceof Player ? (Player) shooter : ANFakePlayer.getPlayer((ServerLevel) world);
        if(!(entity instanceof LivingEntity mob) )
            return;
        if(mob.getHealth() <= 0 && !mob.isRemoved() && stats.hasBuff(AugmentFortune.INSTANCE)){
            int looting = stats.getBuffCount(AugmentFortune.INSTANCE);
            LootContext.Builder lootContext = LootUtil.getLootingContext((ServerLevel)world,shooter, mob, looting, DamageSource.playerAttack(playerContext));
            ResourceLocation lootTable = mob.getLootTable();
            LootTable loottable = world.getServer().getLootTables().get(lootTable);
            List<ItemStack> items = loottable.getRandomItems(lootContext.create(LootContextParamSets.ALL_PARAMS));
            items.forEach(mob::spawnAtLocation);
        }
    }

    public DamageSource buildDamageSource(Level world, LivingEntity shooter){
        shooter = !(shooter instanceof Player) ? FakePlayerFactory.getMinecraft((ServerLevel) world) : shooter;
        return DamageSource.playerAttack((Player) shooter);
    }

    public Vec3 safelyGetHitPos(HitResult result){
        return result instanceof EntityHitResult ? ((EntityHitResult) result).getEntity().position() : result.getLocation();
    }

    public boolean isRealPlayer(LivingEntity entity){
        return entity instanceof Player && isNotFakePlayer(entity);
    }

    public boolean isNotFakePlayer(LivingEntity entity){
        return !(entity instanceof FakePlayer);
    }

    // If the spell would actually do anything. Can be used for logic checks for things like the whelp.
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext){
        return true;
    }

    public boolean nonAirBlockSuccess(HitResult rayTraceResult, Level world){
        return rayTraceResult instanceof BlockHitResult && world.getBlockState(((BlockHitResult) rayTraceResult).getBlockPos()).getMaterial() != Material.AIR;
    }

    public boolean livingEntityHitSuccess(HitResult rayTraceResult){
        return rayTraceResult instanceof EntityHitResult && ((EntityHitResult) rayTraceResult).getEntity() instanceof LivingEntity;
    }

    public boolean nonAirAnythingSuccess(HitResult result, Level world){
        return nonAirBlockSuccess(result, world) || livingEntityHitSuccess(result);
    }

    public void applyEnchantments(SpellStats stats, ItemStack stack){

        if(stats.hasBuff(AugmentExtract.INSTANCE)){
            stack.enchant(Enchantments.SILK_TOUCH, 1);
        }

        if(stats.hasBuff(AugmentFortune.INSTANCE)){
            stack.enchant(Enchantments.BLOCK_FORTUNE, stats.getBuffCount(AugmentFortune.INSTANCE));
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

    public ItemStack getItemFromCaster(@Nullable LivingEntity shooter, SpellContext spellContext, Predicate<ItemStack> predicate){
        if(spellContext.castingTile instanceof IInventoryResponder){
            return ((IInventoryResponder) spellContext.castingTile).getItem(predicate);
        }else if(shooter instanceof IInventoryResponder){
            return ((IInventoryResponder) shooter).getItem(predicate);
        }else if(shooter instanceof Player playerEntity){
            NonNullList<ItemStack> list = playerEntity.inventory.items;
            for(int i = 0; i < 9; i++){
                ItemStack stack = list.get(i);
                if(predicate.test(stack)){
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getItemFromCaster(@Nullable LivingEntity shooter, SpellContext spellContext, Item item){
        return getItemFromCaster(shooter, spellContext, (i) -> i.sameItem(new ItemStack(item)));
    }

    public ItemStack extractStackFromCaster(@Nullable LivingEntity shooter, SpellContext spellContext, Predicate<ItemStack> predicate, int maxExtract){
        IInventoryResponder responder = null;
        if(spellContext.castingTile instanceof IInventoryResponder) {
            responder = (IInventoryResponder) spellContext.castingTile;
        }else if(shooter instanceof IInventoryResponder){
            responder = (IInventoryResponder) shooter;
        }
        if(responder != null){
            return responder.extractItem(predicate, maxExtract);
        }else if(shooter instanceof Player playerEntity){
            NonNullList<ItemStack> list = playerEntity.inventory.items;
            for(int i = 0; i < 9; i++){
                ItemStack stack = list.get(i);
                if(predicate.test(stack)){
                    return stack.split(maxExtract);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack insertStackToCaster(@Nullable LivingEntity shooter, SpellContext spellContext, ItemStack stack){
        IPickupResponder responder = null;
        if(spellContext.castingTile instanceof IPickupResponder) {
            responder = (IPickupResponder) spellContext.castingTile;
        }else if(shooter instanceof IInventoryResponder){
            responder = (IPickupResponder) shooter;
        }
        if(responder != null){
            return responder.onPickup(stack);
        }
        if(isRealPlayer(shooter)){
            Player player = (Player) shooter;
            VoidJar.tryVoiding(player, stack);
            if(!player.addItem(stack)){
                ItemEntity i = new ItemEntity(shooter.level,player.getX(), player.getY(), player.getZ(), stack);
                shooter.level.addFreshEntity(i);
            }
        }
        return stack;
    }
}
