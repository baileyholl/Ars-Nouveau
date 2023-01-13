package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectCut extends AbstractEffect implements IDamageEffect {

    public static EffectCut INSTANCE = new EffectCut();

    private EffectCut() {
        super(GlyphLib.EffectCutID, "Cut");
    }

    @Override
    public boolean canDamage(LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver, Entity entity) {
        return IDamageEffect.super.canDamage(shooter, stats, spellContext, resolver, entity) && !(entity instanceof IForgeShearable);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof IForgeShearable shearable) {
            ItemStack shears = new ItemStack(Items.SHEARS);
            applyEnchantments(spellStats, shears);
            if (shearable.isShearable(shears, world, entity.blockPosition())) {
                List<ItemStack> items = shearable.onSheared(getPlayer(shooter, (ServerLevel) world), shears, world, entity.blockPosition(), spellStats.getBuffCount(AugmentFortune.INSTANCE));
                items.forEach(i -> world.addFreshEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), i)));
            }
        } else {
            float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
            attemptDamage(world, shooter, spellStats, spellContext, resolver, entity, buildDamageSource(world, shooter), damage);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        for (BlockPos p : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE))) {
            if(spellStats.getBuffCount(AugmentAmplify.INSTANCE) > 0){
                doStrip(p, rayTraceResult, world, shooter, spellStats, spellContext, resolver);
            }else{
                doShear(p, rayTraceResult, world, shooter, spellStats, spellContext, resolver);
            }
        }
    }

    private boolean dupeCheck(Level world, BlockPos pos){
        BlockEntity be = world.getBlockEntity(pos);
        return be != null && (world.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent() || be instanceof Container);
    }

    public void doStrip(BlockPos p, BlockHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver){
        ItemStack axe = new ItemStack(Items.DIAMOND_AXE);
        applyEnchantments(spellStats, axe);
        Player entity = ANFakePlayer.getPlayer((ServerLevel) world);
        entity.setItemInHand(InteractionHand.MAIN_HAND, axe);
        // TODO Replace with AN shears
        if (dupeCheck(world, p)) return;
        entity.setPos(p.getX(), p.getY(), p.getZ());
        world.getBlockState(p).use(world, entity, InteractionHand.MAIN_HAND, rayTraceResult);
        axe.useOn(new UseOnContext(entity, InteractionHand.MAIN_HAND, rayTraceResult));
    }

    public void doShear(BlockPos p, BlockHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver){
        ItemStack shears = new ItemStack(Items.SHEARS);
        applyEnchantments(spellStats, shears);
        if (world.getBlockState(p).getBlock() instanceof IForgeShearable shearable && shearable.isShearable(shears, world, p)) {
            List<ItemStack> items = shearable.onSheared(getPlayer(shooter, (ServerLevel) world), shears, world, p, spellStats.getBuffCount(AugmentFortune.INSTANCE));
            items.forEach(i -> world.addFreshEntity(new ItemEntity(world, p.getX(), p.getY(), p.getZ(), i)));
        }
        Player entity = ANFakePlayer.getPlayer((ServerLevel) world);
        entity.setItemInHand(InteractionHand.MAIN_HAND, shears);
        // TODO Replace with AN shears
        if (dupeCheck(world, p)) return;
        entity.setPos(p.getX(), p.getY(), p.getZ());
        world.getBlockState(p).use(world, entity, InteractionHand.MAIN_HAND, rayTraceResult);
        shears.useOn(new UseOnContext(entity, InteractionHand.MAIN_HAND, rayTraceResult));
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 1.0);
        addAmpConfig(builder, 1.0);
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentExtract.INSTANCE, AugmentFortune.INSTANCE,
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE
        );
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public String getBookDescription() {
        return "Simulates using shears on entities and blocks, or damages non-shearable entities for a small amount. Amplify will simulate using an Axe instead of Shears. For simulating breaking with shears, see Break and Sensitive. Costs nothing.";
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
