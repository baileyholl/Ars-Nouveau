package com.hollingsworth.arsnouveau.common.spell.effect;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.destroyBlockSafely;

public class EffectBreak extends AbstractEffect {

    public EffectBreak() {
        super(ModConfig.EffectBreakID, "Break");
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(!world.isRemote && rayTraceResult instanceof BlockRayTraceResult){
            BlockPos pos = new BlockPos(((BlockRayTraceResult) rayTraceResult).getPos());
            BlockState state;
            float maxHardness = 5.0f + 25 * getAmplificationBonus(augments);
            int buff = getAmplificationBonus(augments);
            if(buff == -1){
                maxHardness = 2.5f;
            }else if(buff == -2){
                maxHardness = 1.0f;
            }else if(buff < -2){
                maxHardness = 0.5f;
            }

            int aoeBuff = getBuffCount(augments, AugmentAOE.class);
            ImmutableList<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, (BlockRayTraceResult)rayTraceResult,1 + aoeBuff, 1 + aoeBuff, 1, -1);
            for(BlockPos pos1 : posList) {
                state = world.getBlockState(pos1);
                // Iron block or lower unpowered
                if(!(state.getBlockHardness(world, pos1) <= maxHardness && state.getBlockHardness(world, pos1) >= 0)){
                    continue;
                }
                if (hasBuff(augments, AugmentExtract.class)) {
                    destroyBlockSafely(world, pos1, false, shooter);
                    state.getDrops(LootUtil.getSilkContext((ServerWorld) world, pos1,  shooter)).forEach(i -> world.addEntity(new ItemEntity(world,pos.getX(), pos.getY(), pos.getZ(), i )));

                } else if (hasBuff(augments, AugmentFortune.class)) {
                    destroyBlockSafely(world, pos1, false, shooter);
                    state.getDrops(LootUtil.getFortuneContext((ServerWorld) world, pos1, shooter, getBuffCount(augments, AugmentFortune.class))).forEach(i -> world.addEntity(new ItemEntity(world,pos.getX(), pos.getY(), pos.getZ(),i )));;
                } else {
                    destroyBlockSafely(world, pos1, true, shooter);
                }
                world.notifyBlockUpdate(pos1, state, state, 3);
            }
        }
    }


    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.IRON_PICKAXE;
    }

    @Override
    protected String getBookDescription() {
        return "Breaks blocks of an average hardness. Can be amplified to break harder blocks or can break multiple blocks with the AOE augment.";
    }
}
