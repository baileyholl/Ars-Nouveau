package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EffectSmelt extends AbstractEffect {
    public static EffectSmelt INSTANCE = new EffectSmelt();

    private EffectSmelt() {
        super(GlyphLib.EffectSmeltID, "Smelt");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(!(rayTraceResult instanceof BlockRayTraceResult))
            return;


        int aoeBuff = getBuffCount(augments, AugmentAOE.class);
        int pierceBuff = getBuffCount(augments, AugmentPierce.class);
        int maxItemSmelt = 3 + 4 * aoeBuff + 4 * pierceBuff;

        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, ((BlockRayTraceResult) rayTraceResult).getBlockPos(), (BlockRayTraceResult)rayTraceResult,aoeBuff, pierceBuff);
        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(((BlockRayTraceResult) rayTraceResult).getBlockPos()).inflate(aoeBuff + 1.0));
        smeltItems(world, itemEntities, maxItemSmelt);

        for(BlockPos pos : posList) {
            if(!canBlockBeHarvested(augments, world, pos))
                continue;
            smeltBlock(world, pos, shooter);
        }

    }

    public void smeltBlock(World world, BlockPos pos, LivingEntity shooter){
        BlockState state = world.getBlockState(pos);
        if(!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos))
            return;
        Optional<FurnaceRecipe> optional = world.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(new ItemStack(state.getBlock().asItem(), 1)),
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


    public void smeltItems(World world, List<ItemEntity> itemEntities, int maxItemSmelt){
        int numSmelted = 0;
        for (ItemEntity itemEntity : itemEntities) {
            if (numSmelted > maxItemSmelt)
                break;
            Optional<FurnaceRecipe> optional = world.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(itemEntity.getItem()),
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
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof BlockRayTraceResult;
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
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Override
    public Item getCraftingReagent() {
        return Items.BLAST_FURNACE;
    }
}
