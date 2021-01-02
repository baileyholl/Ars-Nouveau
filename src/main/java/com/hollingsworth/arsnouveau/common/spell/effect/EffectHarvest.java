package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

public class EffectHarvest extends AbstractEffect {

    public static ITag.INamedTag<Block> FELLABLE =  BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "harvest/fellable"));

    public EffectHarvest() {
        super(ModConfig.EffectHarvestID, "Harvest");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof BlockRayTraceResult){
            BlockRayTraceResult ray = (BlockRayTraceResult) rayTraceResult;
            if(world.isRemote)
                return;
            for(BlockPos blockpos : SpellUtil.calcAOEBlocks(shooter, ray.getPos(), ray, getBuffCount(augments, AugmentAOE.class))){
                BlockState state = world.getBlockState(blockpos);
                if(isTree(state)){
                    Set<BlockPos> list = getTree(world, ray.getPos(), 500);
                    list.forEach(listPos -> {
                        if(!BlockUtil.destroyRespectsClaim(shooter, world, listPos))
                            return;
                        if (hasBuff(augments, AugmentExtract.class)) {
                            world.getBlockState(listPos).getDrops(LootUtil.getSilkContext((ServerWorld) world, listPos,  shooter)).forEach(i -> world.addEntity(new ItemEntity(world,listPos.getX(), listPos.getY(), listPos.getZ(), i )));
                            BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, false);
                        } else if (hasBuff(augments, AugmentFortune.class)) {
                            world.getBlockState(listPos).getDrops(LootUtil.getFortuneContext((ServerWorld) world, listPos, shooter, getBuffCount(augments, AugmentFortune.class))).forEach(i -> world.addEntity(new ItemEntity(world,listPos.getX(), listPos.getY(), listPos.getZ(),i )));
                            BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, false);
                        } else {
                            BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, true);
                        }
                    });
                    world.playEvent(2001, blockpos, Block.getStateId(state));
                    return;
                }

                if(state.getBlock() instanceof FarmlandBlock || world.getBlockState(blockpos.up()).getBlock() instanceof CropsBlock){
                    blockpos = blockpos.up();
                    state = world.getBlockState(blockpos);
                }

                if(!(state.getBlock() instanceof CropsBlock))
                    continue;
                CropsBlock cropsBlock = (CropsBlock)world.getBlockState(blockpos).getBlock();

                if(!cropsBlock.isMaxAge(state) || !(world instanceof ServerWorld))
                    continue;

                List<ItemStack> cropDrops = Block.getDrops(state, (ServerWorld)world, blockpos, world.getTileEntity(blockpos));

                if(hasBuff(augments, AugmentFortune.class)){
                    cropDrops = state.getDrops(LootUtil.getFortuneContext((ServerWorld) world, blockpos, shooter, getBuffCount(augments, AugmentFortune.class)));
                }
                BlockPos finalBlockpos = blockpos;
                cropDrops.forEach(d -> {
                    if(d.getItem() == BlockRegistry.MANA_BLOOM_CROP.asItem()){
                        return;
                    }
                    world.addEntity(new ItemEntity(world, finalBlockpos.getX(), finalBlockpos.getY(), finalBlockpos.getZ(), d));
                });
                world.setBlockState(blockpos,cropsBlock.withAge(1));
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(!(rayTraceResult instanceof BlockRayTraceResult))
            return false;

        BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getPos();
        BlockState state = world.getBlockState(pos);
        if(isTree(state))
            return true;
        if(state.getBlock() instanceof FarmlandBlock || world.getBlockState(pos.up()).getBlock() instanceof CropsBlock){
            pos = pos.up();
            state = world.getBlockState(pos);
        }
        if(!(state.getBlock() instanceof CropsBlock))
            return false;


        CropsBlock cropsBlock = (CropsBlock)world.getBlockState(pos).getBlock();
        return cropsBlock.isMaxAge(state) && world instanceof ServerWorld;
    }

    public boolean isTree(BlockState blockstate){
        return blockstate.getBlock().isIn(FELLABLE);
    }

    public Set<BlockPos> getTree(World world, BlockPos start, int maxBlocks) {
        return getTree(world, Collections.singleton(start), maxBlocks);
    }

    public Set<BlockPos> getTree(World world, Collection<BlockPos> start, int maxBlocks) {
        LinkedList<BlockPos> searchQueue = new LinkedList<>(start);
        HashSet<BlockPos> searched = new HashSet<>(start);
        HashSet<BlockPos> found = new HashSet<>();
        while(!searchQueue.isEmpty() && found.size() < maxBlocks) {
            BlockPos current = searchQueue.removeFirst();
            BlockState state = world.getBlockState(current);
            if (isTree(state)) {
                found.add(current);
                BlockPos.getAllInBox(current.add(1, 1, 1), current.add(-1, -1, -1)).forEach(neighborMutable -> {
                    if (searched.contains(neighborMutable)) return;
                    BlockPos neighbor = neighborMutable.toImmutable();
                    searched.add(neighbor);
                    searchQueue.add(neighbor);
                });
            }
        }
        return found;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.DIAMOND_AXE;
    }

    @Override
    public int getManaCost() {
        return 30;
    }

    @Override
    protected String getBookDescription() {
        return "Harvests grown crops and trees. When used on grown crops, this spell will obtain the fully grown product without destroying the plant.";
    }
}
