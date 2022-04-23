package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class RitualOvergrowth extends AbstractRitual {
    @Override
    protected void tick() {
        Level world = getWorld();
        BlockPos pos = getPos();


        if(getWorld().isClientSide){
            ParticleUtil.spawnRitualAreaEffect(getPos(), getWorld(), rand, getCenterColor(), 5);
        }else{
            if(getWorld().getGameTime() % 200 != 0)
                return;

            if(isAnimalGrowth()){
                List<AgeableMob> animals = getWorld().getEntitiesOfClass(AgeableMob.class, new AABB(getPos()).inflate(5));
                boolean didWorkOnce = false;
                for(AgeableMob a : animals){
                    if(a.isBaby()){
                        a.ageUp(500, true);
                        didWorkOnce = true;
                    }
                }
                if(didWorkOnce)
                    setNeedsMana(true);
            }else{
                int range = 5;
                boolean didWorkOnce = false;
                for(BlockPos b : BlockPos.betweenClosed(pos.offset(range, -1, range), pos.offset(-range, 1, -range))){
                    if(rand.nextInt(25) == 0)
                        if(BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, b, ANFakePlayer.getPlayer((ServerLevel) world))) {
                            didWorkOnce = true;
                        }
                }
                if(didWorkOnce)
                    setNeedsMana(true);
            }
        }

    }

    public boolean isAnimalGrowth(){
        return didConsumeItem(Items.BONE_BLOCK);
    }

    @Override
    public int getManaCost() {
        return 500;
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return getConsumedItems().isEmpty() && stack.getItem() == Items.BONE_BLOCK;
    }

    @Override
    public String getLangName() {
        return "Overgrowth";
    }

    @Override
    public String getLangDescription() {
        return "Occasionally bone meals blocks in the area around it. This ritual requires source to operate. If augmented with a Bone Block, this ritual will instead force baby animals to grow faster.";
    }

    @Override
    public String getID() {
        return RitualLib.OVERGROWTH;
    }

    @Override
    public ParticleColor getCenterColor() {
        return ParticleColor.makeRandomColor(20, 255, 20, rand);
    }
}
