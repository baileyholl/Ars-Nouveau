package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static net.minecraft.world.item.crafting.RecipeType.SMOKING;

public class EffectSmelt extends AbstractEffect {
    public static EffectSmelt INSTANCE = new EffectSmelt();

    private EffectSmelt() {
        super(GlyphLib.EffectSmeltID, "Smelt");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        double aoeBuff = spellStats.getAoeMultiplier();
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        int maxItemSmelt = (int) Math.round(4 * (1 + aoeBuff + pierceBuff));

        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(rayTraceResult.getEntity().blockPosition()).inflate(aoeBuff + 1.0));
        smeltItems(world, itemEntities, maxItemSmelt, spellStats);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        double aoeBuff = spellStats.getAoeMultiplier();
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        int maxItemSmelt = (int) Math.round(4 * (1 + aoeBuff + pierceBuff));
        if (spellStats.isSensitive()) {
            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(rayTraceResult.getBlockPos()).inflate(aoeBuff + 1.0));
            smeltItems(world, itemEntities, maxItemSmelt, spellStats);
        } else {
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats);

            for (BlockPos pos : posList) {
                smeltBlock(world, pos, shooter, rayTraceResult, spellStats, spellContext, resolver);
            }
        }
    }


    public void smeltBlock(Level world, BlockPos pos, LivingEntity shooter, BlockHitResult hitResult, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!canBlockBeHarvested(spellStats, world, pos)) return;
        BlockState state = world.getBlockState(pos);
        if (!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos)) return;
        Optional<RecipeHolder<SmeltingRecipe>> optional = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(new ItemStack(state.getBlock().asItem(), 1)), world);
        if (optional.isPresent()) {
            ItemStack itemstack = optional.get().value().getResultItem(world.registryAccess());
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof BlockItem) {
                    world.setBlockAndUpdate(pos, ((BlockItem) itemstack.getItem()).getBlock().defaultBlockState());
                } else {
                    BlockUtil.destroyBlockSafely(world, pos, false, shooter);
                    world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemstack.copy()));
                    BlockUtil.safelyUpdateState(world, pos);
                }
                ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), hitResult.getDirection(), pos, false), world, shooter, spellContext, resolver);
            }
        }
    }


    public void smeltItems(Level world, List<ItemEntity> itemEntities, int maxItemSmelt, SpellStats spellStats) {
        int numSmelted = 0;
        for (ItemEntity itemEntity : itemEntities) {
            if (numSmelted > maxItemSmelt) break;
            Optional optional;

            if (spellStats.hasBuff(AugmentDampen.INSTANCE)) {
                optional = world.getRecipeManager().getRecipeFor(SMOKING, new SingleRecipeInput(itemEntity.getItem()), world);
            } else if (spellStats.hasBuff(AugmentAmplify.INSTANCE)) {
                optional = world.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SingleRecipeInput(itemEntity.getItem()), world);
            } else {
                optional = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(itemEntity.getItem()), world);
            }

            if (optional.isPresent()) {
                ItemStack result = ((RecipeHolder<?>)(optional.get())).value().getResultItem(world.registryAccess()).copy();
                if (result.isEmpty()) continue;
                while (numSmelted < maxItemSmelt && !itemEntity.getItem().isEmpty()) {
                    itemEntity.getItem().shrink(1);
                    world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result.copy()));
                    numSmelted++;
                }
            }
        }
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentSensitive.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Smelts blocks and items in the world. AOE will increase the number of items and radius of blocks that can be smelted at once, while Amplify will allow Smelt to work on blocks of higher hardness, Sensitive will make it only smelt items and not blocks. Dampen will cause Smelt to use smoking recipes.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_FIRE);
    }
}
