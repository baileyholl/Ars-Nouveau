package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;

public class RitualOvergrowth extends AbstractRitual {
    @Override
    protected void tick() {
        World world = getWorld();
        BlockPos pos = getPos();


        if(getWorld().isClientSide){
            ParticleUtil.spawnRitualAreaEffect(tile, rand, getCenterColor(), 5);
        }else{
            if(getWorld().getGameTime() % 200 != 0)
                return;

            if(isAnimalGrowth()){
                List<AgeableEntity> animals = getWorld().getEntitiesOfClass(AgeableEntity.class, new AxisAlignedBB(getPos()).inflate(5));
                boolean didWorkOnce = false;
                for(AgeableEntity a : animals){
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
                        if(BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, b, FakePlayerFactory.getMinecraft((ServerWorld) world))) {
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
        return "Occasionally bone meals blocks in the area around it. This ritual requires Mana to operate. If augmented with a Bone Block, this ritual will instead force baby animals to grow faster.";
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
