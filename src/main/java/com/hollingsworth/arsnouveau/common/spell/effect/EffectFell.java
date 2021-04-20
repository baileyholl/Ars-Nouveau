package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class EffectFell extends AbstractEffect {
    public static ITag.INamedTag<Block> FELLABLE =  BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "harvest/fellable"));

    public EffectFell() {
        super(GlyphLib.EffectFellID, "Fell");
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult ray, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        BlockPos blockPos = ray.getBlockPos();
        BlockState state = world.getBlockState(blockPos);
        if (isTree(state)) {
            Set<BlockPos> list = getTree(world, blockPos, 50 + 50 * getBuffCount(augments, AugmentAOE.class));
            list.forEach(listPos -> {
                if (!BlockUtil.destroyRespectsClaim(shooter, world, listPos))
                    return;
                if (hasBuff(augments, AugmentExtract.class)) {
                    world.getBlockState(listPos).getDrops(LootUtil.getSilkContext((ServerWorld) world, listPos, shooter)).forEach(i -> world.addFreshEntity(new ItemEntity(world, listPos.getX(), listPos.getY(), listPos.getZ(), i)));
                    BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, false);
                } else if (hasBuff(augments, AugmentFortune.class)) {
                    world.getBlockState(listPos).getDrops(LootUtil.getFortuneContext((ServerWorld) world, listPos, shooter, getBuffCount(augments, AugmentFortune.class))).forEach(i -> world.addFreshEntity(new ItemEntity(world, listPos.getX(), listPos.getY(), listPos.getZ(), i)));
                    BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, false);
                } else {
                    BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, true);
                }
            });
            world.levelEvent(2001, blockPos, Block.getId(state));
        }
    }

    public boolean isTree(BlockState blockstate){
        return blockstate.getBlock().is(FELLABLE);
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
                BlockPos.betweenClosedStream(current.offset(1, 1, 1), current.offset(-1, -1, -1)).forEach(neighborMutable -> {
                    if (searched.contains(neighborMutable)) return;
                    BlockPos neighbor = neighborMutable.immutable();
                    searched.add(neighbor);
                    searchQueue.add(neighbor);
                });
            }
        }
        return found;
    }

    @Override
    public int getManaCost() {
        return 150;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.DIAMOND_AXE;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Harvests entire trees, mushrooms, cactus, and other vegetation. Can be amplified with Amplify to break materials of higher hardness. AOE will increase the number of blocks that may be broken at one time.";
    }
}
