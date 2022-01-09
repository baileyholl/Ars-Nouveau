package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EffectSmelt extends AbstractEffect {
    public static EffectSmelt INSTANCE = new EffectSmelt();

    private EffectSmelt() {
        super(GlyphLib.EffectSmeltID, "Smelt");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext);
        int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        int maxItemSmelt = 3 + 4 * aoeBuff + 4 * pierceBuff;
        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(rayTraceResult.getEntity().blockPosition()).inflate(aoeBuff + 1.0));
        smeltItems(world, itemEntities, maxItemSmelt);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        int maxItemSmelt = 3 + 4 * aoeBuff + 4 * pierceBuff;
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats);
        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(rayTraceResult.getBlockPos()).inflate(aoeBuff + 1.0));
        smeltItems(world, itemEntities, maxItemSmelt);

        for(BlockPos pos : posList) {
            if(!canBlockBeHarvested(spellStats, world, pos))
                continue;
            smeltBlock(world, pos, shooter);
        }
    }


    public void smeltBlock(Level world, BlockPos pos, LivingEntity shooter){
        BlockState state = world.getBlockState(pos);
        if(!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos))
            return;
        Optional<SmeltingRecipe> optional = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(new ItemStack(state.getBlock().asItem(), 1)),
                world);
        if (optional.isPresent()) {
            ItemStack itemstack = optional.get().getResultItem();
            if (!itemstack.isEmpty()) {
                if(itemstack.getItem() instanceof BlockItem){
                    world.setBlockAndUpdate(pos, ((BlockItem)itemstack.getItem()).getBlock().defaultBlockState());
                }else{
                    BlockUtil.destroyBlockSafely(world, pos, false, shooter);
                    world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),itemstack.copy()));
                    BlockUtil.safelyUpdateState(world, pos);
                }
            }
        }
    }


    public void smeltItems(Level world, List<ItemEntity> itemEntities, int maxItemSmelt){
        int numSmelted = 0;
        for (ItemEntity itemEntity : itemEntities) {
            if (numSmelted > maxItemSmelt)
                break;
            Optional<SmeltingRecipe> optional = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(itemEntity.getItem()),
                    world);
            if (optional.isPresent()) {
                ItemStack result = optional.get().getResultItem().copy();
                if (result.isEmpty())
                    continue;
                while (numSmelted < maxItemSmelt && !itemEntity.getItem().isEmpty()) {
                    itemEntity.getItem().shrink(1);
                    world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result.copy()));
                    numSmelted++;
                }
            }
        }
    }

    @Override
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        return rayTraceResult instanceof BlockHitResult;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentAOE.INSTANCE, AugmentPierce.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Smelts blocks and items in the world. AOE will increase the number of items and radius of blocks that can be smelted at once, while Amplify will allow Smelt to work on blocks of higher hardness.";
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @Override
    public Item getCraftingReagent() {
        return Items.BLAST_FURNACE;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_FIRE);
    }
}
