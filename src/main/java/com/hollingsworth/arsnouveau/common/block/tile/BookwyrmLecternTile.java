package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.BookwyrmLectern;
import com.hollingsworth.arsnouveau.common.block.ManaBlock;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.isTreeBlock;

public class BookwyrmLecternTile extends SummoningTile implements IWandable {
    int tier;
    public boolean isOff;
    int taskIndex;

    public BookwyrmLecternTile() {
        super(BlockRegistry.BOOKWYRM_LECTERN_TILE);
        tier = 1;
    }

    @Override
    public void convertedEffect() {
        super.convertedEffect();
        if (tickCounter >= 120 && !level.isClientSide) {
            converted = true;
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(SummoningTile.CONVERTED, true));
            EntityBookwyrm bookwyrm = new EntityBookwyrm(level, worldPosition);
            bookwyrm.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5);
            level.addFreshEntity(bookwyrm);
            ParticleUtil.spawnPoof((ServerWorld) level, worldPosition.above());
            tickCounter = 0;
            return;
        }
        if (tickCounter % 10 == 0 && !level.isClientSide) {
            Random r = level.random;
            int min = -2;
            int max = 2;
            EntityFollowProjectile proj1 = new EntityFollowProjectile(level, worldPosition.offset(r.nextInt(max - min) + min, 3, r.nextInt(max - min) + min), worldPosition, r.nextInt(255), r.nextInt(255), r.nextInt(255));
            level.addFreshEntity(proj1);
        }
    }

    public void changeTier(PlayerEntity entity){
        if(tier == 1 || tier == 0){
            tier = 2;
            entity.sendMessage(new TranslationTextComponent("ars_nouveau.summoning_crystal.set", "5 x 5"), Util.NIL_UUID);
        }else if(tier == 2){
            tier = 3;
            entity.sendMessage(new TranslationTextComponent("ars_nouveau.summoning_crystal.set", "9 x 9"), Util.NIL_UUID);
        }else if(tier == 3){
            tier = 4;
            entity.sendMessage(new TranslationTextComponent("ars_nouveau.summoning_crystal.set", "13 x 13"), Util.NIL_UUID);
        }else if(tier == 4){
            tier = 5;
            entity.sendMessage(new TranslationTextComponent("ars_nouveau.summoning_crystal.set", "17 x 17"), Util.NIL_UUID);
        }else if(tier == 5){
            tier = 1;
            entity.sendMessage(new TranslationTextComponent("ars_nouveau.summoning_crystal.adjacent"), Util.NIL_UUID);
        }
    }

    public boolean enoughMana(@Nullable Spell spell){
        if(spell == null)
            return false;
        return ManaUtil.hasManaNearby(worldPosition, level, 7, spell.getCastingCost() / 4);
    }

    public boolean removeManaAround(@Nullable Spell spell){
        if(spell == null)
            return false;
        return ManaUtil.takeManaNearbyWithParticles(worldPosition, level, 7, spell.getCastingCost() / 4) != null;
    }

    public @Nullable BlockPos getNextTaskLoc(@Nullable Spell spell, EntityBookwyrm caster){
        if(isOff || spell == null)
            return null;

        List<BlockPos> posList = getTargets();
        if(posList == null || posList.isEmpty()) {
            return null;
        }
        BlockPos taskPos = null;
        boolean wouldSucceed = false;
        for(int i = 0; i < posList.size(); i++){
            if(taskIndex >= posList.size()){
                taskIndex = 0;
            }
            taskPos = posList.get(taskIndex);
            taskIndex += 1;

            // If the block above is not air
            if (level.getBlockState(taskPos.above()).getMaterial() != Material.AIR && !isTreeBlock(level.getBlockState(taskPos).getBlock())){
                for(int j = 1; j < 4; j++) {
                    if (level.getBlockState(taskPos.above(i)).getMaterial() != Material.AIR || isTreeBlock(level.getBlockState(taskPos.above()).getBlock())){
                        taskPos = taskPos.above(i);
                        break;
                    }
                }
            }
            Block block = level.getBlockState(taskPos).getBlock();
            if(block instanceof BookwyrmLectern || block instanceof ContainerBlock || block instanceof ManaBlock || block instanceof IInventory)
                continue;

            if(caster.getEntityData().get(EntityBookwyrm.STRICT_MODE)){
                SpellResolver resolver = new SpellResolver(new SpellContext(spell, caster));
                if(!resolver.wouldCastOnBlockSuccessfully(new BlockRayTraceResult(new Vector3d(taskPos.getX(), taskPos.getY(), taskPos.getZ()), Direction.UP,taskPos, false ), caster)) {
                    continue;
                }
            }
            wouldSucceed = true;
            break;
        }

        return wouldSucceed ? taskPos : null;
    }

    public List<BlockPos> getTargets(){
        List<BlockPos> positions = new ArrayList<>();
        if(tier == 1){
            positions.add(getBlockPos().north().below());
            positions.add(getBlockPos().south().below());
            positions.add(getBlockPos().east().below());
            positions.add(getBlockPos().west().below());
        }
        if(tier == 2){
            BlockPos.betweenClosedStream(getBlockPos().north(2).east(2).below(1), getBlockPos().south(2).west(2).below()).forEach(t -> positions.add(new BlockPos(t)));
        }
        if(tier == 3){
            BlockPos.betweenClosedStream(getBlockPos().north(4).east(4).below(1), getBlockPos().south(4).west(4).below()).forEach(t -> positions.add(new BlockPos(t)));
        }
        if(tier == 4){
            BlockPos.betweenClosedStream(getBlockPos().north(6).east(6).below(1), getBlockPos().south(6).west(6).below()).forEach(t -> positions.add(new BlockPos(t)));
        }
        if(tier == 5){
            BlockPos.betweenClosedStream(getBlockPos().north(8).east(8).below(1), getBlockPos().south(8).west(8).below()).forEach(t -> positions.add(new BlockPos(t)));
        }
        return positions;
    }

    public ItemStack insertItem(ItemStack stack){
        return BlockUtil.insertItemAdjacent(level, worldPosition, stack);
    }

    public ItemStack getItem(Item item){
        return BlockUtil.getItemAdjacent(level, worldPosition, stack -> stack.getItem() == item);
    }

    @Override
    public void onWanded(PlayerEntity playerEntity) {
        this.changeTier(playerEntity);
    }


    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state,tag);
        taskIndex = tag.getInt("task_index");
        tier = tag.getInt("tier");
        isOff = tag.getBoolean("is_off");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putInt("task_index", taskIndex);
        tag.putInt("tier", tier);
        tag.putBoolean("is_off", isOff);
        return super.save(tag);
    }
}
