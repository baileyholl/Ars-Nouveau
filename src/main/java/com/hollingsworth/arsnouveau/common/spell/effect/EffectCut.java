package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectCut extends AbstractEffect {

    public static EffectCut INSTANCE = new EffectCut();

    private EffectCut() {
        super(GlyphLib.EffectCutID, "Cut");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof IForgeShearable shearable) {
            ItemStack shears = new ItemStack(Items.SHEARS);
            applyEnchantments(spellStats, shears);
            if (shearable.isShearable(shears, world, entity.blockPosition())) {
                List<ItemStack> items = shearable.onSheared(getPlayer(shooter, (ServerLevel) world), shears, world, entity.blockPosition(), spellStats.getBuffCount(AugmentFortune.INSTANCE));
                items.forEach(i -> world.addFreshEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), i)));
            }
        } else {
            dealDamage(world, shooter, (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier()), spellStats, entity, DamageSource.playerAttack(getPlayer(shooter, (ServerLevel) world)));
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        for (BlockPos p : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE))) {
            ItemStack shears = new ItemStack(Items.SHEARS);
            applyEnchantments(spellStats, shears);
            if (world.getBlockState(p).getBlock() instanceof IForgeShearable shearable) {

                if (shearable.isShearable(shears, world, p)) {
                    List<ItemStack> items = shearable.onSheared(getPlayer(shooter, (ServerLevel) world), shears, world, p, spellStats.getBuffCount(AugmentFortune.INSTANCE));
                    items.forEach(i -> world.addFreshEntity(new ItemEntity(world, p.getX(), p.getY(), p.getZ(), i)));
                }
            }
            Player entity = ANFakePlayer.getPlayer((ServerLevel) world);
            entity.setItemInHand(InteractionHand.MAIN_HAND, shears);
            // TODO Replace with AN shears
            if(world.getBlockEntity(p) != null && (world.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent() ||
                    world.getBlockEntity(p) instanceof ArcanePedestalTile || world.getBlockEntity(p) instanceof ScribesTile))
                continue;
            entity.setPos(p.getX(), p.getY(), p.getZ());
            world.getBlockState(p).use(world, entity, InteractionHand.MAIN_HAND, rayTraceResult);
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 1.0);
        addAmpConfig(builder, 1.0);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentExtract.INSTANCE, AugmentFortune.INSTANCE,
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Simulates using shears on entities and blocks, or damages non-shearable entities for a small amount. For simulating breaking with shears, see Break and Sensitive. Costs nothing.";
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
