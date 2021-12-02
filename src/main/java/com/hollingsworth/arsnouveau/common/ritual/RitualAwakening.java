package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.WealdWalker;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RitualAwakening extends AbstractRitual {
    List<BlockPos> treePos = new ArrayList<>();
    EntityType<WealdWalker> entity = null;
    BlockPos foundPos;

    public void destroyTree(Level world, Set<BlockPos> set){
        for(BlockPos p : set){
            BlockUtil.destroyBlockSafelyWithoutSound(world, p, false);
        }
    }

    public void findTree(Level world){
        for(BlockPos p : BlockPos.betweenClosed(getPos().east(3).south(3).below(1), getPos().west(3).north(3).above(1))){
            Set<BlockPos> blazing = SpellUtil.DFSBlockstates(world, p, 350, (b) -> b.getBlock() == BlockRegistry.BLAZING_LOG || b.getBlock() == BlockRegistry.BLAZING_LEAVES);
            if(blazing.size() >= 50){
                entity = ModEntities.ENTITY_BLAZING_WEALD;
                foundPos = p;
                destroyTree(world, blazing);
                return;
            }
            Set<BlockPos> flourishing = SpellUtil.DFSBlockstates(world, p, 350, (b) -> b.getBlock() == BlockRegistry.FLOURISHING_LOG || b.getBlock() == BlockRegistry.FLOURISHING_LEAVES);
            if(flourishing.size() >= 50){
                entity = ModEntities.ENTITY_FLOURISHING_WEALD;
                foundPos = p;
                destroyTree(world, flourishing);
                return;
            }
            Set<BlockPos> vexing = SpellUtil.DFSBlockstates(world, p, 350, (b) -> b.getBlock() == BlockRegistry.VEXING_LOG || b.getBlock() == BlockRegistry.VEXING_LEAVES);
            if(vexing.size() >= 50){
                entity = ModEntities.ENTITY_VEXING_WEALD;
                foundPos = p;
                destroyTree(world, vexing);
                return;
            }

            Set<BlockPos> cascading = SpellUtil.DFSBlockstates(world, p, 350, (b) -> b.getBlock() == BlockRegistry.CASCADING_LOG || b.getBlock() == BlockRegistry.CASCADING_LEAVE);
            if(cascading.size() >= 50){
                entity = ModEntities.ENTITY_CASCADING_WEALD;
                foundPos = p;
                destroyTree(world, cascading);
                return;
            }
        }
    }

    @Override
    protected void tick() {
        Level world = getWorld();
        if(world.isClientSide){
            BlockPos pos = getPos();

            for(int i =0; i< 100; i++){
                Vec3 particlePos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5,5,5));
                world.addParticle(ParticleLineData.createData(getCenterColor()),
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        pos.getX()  +0.5, pos.getY() + 1  , pos.getZ() +0.5);
            }
        }
        if(!world.isClientSide && world.getGameTime() % 20 == 0) {
            incrementProgress();
            if(getProgress() > 5){
                findTree(world);
                if(entity != null){
                    ParticleUtil.spawnPoof((ServerLevel) world, foundPos);
                    WealdWalker walker =  entity.create(world);
                    walker.setPos(foundPos.getX(), foundPos.getY(), foundPos.getZ());
                    world.addFreshEntity(walker);
                    setFinished();
                }
            }
        }
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(50, 200, 50);
    }

    @Override
    public void write(CompoundTag tag) {
        super.write(tag);
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

    }

    @Override
    public String getLangName() {
        return "Awakening";
    }

    @Override
    public String getLangDescription() {
        return "Awakens nearby Archwood trees into Weald Walkers. Weald Walkers can be given a position in the world to guard against hostile mobs. They will heal over time, and turn into Weald Waddlers if they die. To create a Weald Walker, perform this ritual near the base of an Archwood Tree.";
    }

    @Override
    public String getID() {
        return RitualLib.AWAKENING;
    }
}
