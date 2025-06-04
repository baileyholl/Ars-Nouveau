package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ModConfigSpec;


import java.util.List;

public class RitualOvergrowth extends AbstractRitual {
    public ModConfigSpec.IntValue ANIMAL_RANGE;
    public ModConfigSpec.IntValue CROP_RANGE;
    @Override
    protected void tick() {
        Level world = getWorld();
        BlockPos pos = getPos();


        if (getWorld().isClientSide) {
            ParticleUtil.spawnRitualAreaEffect(getPos(), getWorld(), rand, getCenterColor(), 5);
        } else {
            if (getWorld().getGameTime() % 200 != 0)
                return;

            if (isAnimalGrowth()) {
                List<AgeableMob> animals = getWorld().getEntitiesOfClass(AgeableMob.class, new AABB(getPos()).inflate(getAnimalRange()));
                boolean didWorkOnce = false;
                for (AgeableMob a : animals) {
                    if (a.isBaby()) {
                        a.ageUp(500, true);
                        didWorkOnce = true;
                    }
                }
                if (didWorkOnce)
                    setNeedsSource(true);
            } else {
                int range = getCropRange();
                boolean didWorkOnce = false;
                for (BlockPos b : BlockPos.betweenClosed(pos.offset(range, -1, range), pos.offset(-range, 1, -range))) {
                    BlockState state = world.getBlockState(b);

                    if (state.getBlock() instanceof FarmBlock || world.getBlockState(b.above()).getBlock() instanceof BonemealableBlock) {
                        b = b.above();
                    }

                    if (rand.nextInt(25) == 0)
                        if (BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, b, ANFakePlayer.getPlayer((ServerLevel) world))) {
                            didWorkOnce = true;
                        }
                }
                if (didWorkOnce)
                    setNeedsSource(true);
            }
        }

    }

    public boolean isAnimalGrowth() {
        return didConsumeItem(Items.BONE_BLOCK);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        ANIMAL_RANGE = builder
                .comment("The range in blocks around the ritual where baby animals will grow faster")
                .defineInRange("animal_range", 5, 1, 20);
        CROP_RANGE = builder
                .comment("The range in blocks around the ritual where crops will be bone-mealed")
                .defineInRange("crop_range", 5, 1, 20);
    }

    private int getAnimalRange() {
        return ANIMAL_RANGE.get();
    }
    
    private int getCropRange() {
        return CROP_RANGE.get();
    }
    
    @Override
    public int getDefaultSourceCost() {
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
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.OVERGROWTH);
    }

    @Override
    public ParticleColor getCenterColor() {
        return ParticleColor.makeRandomColor(20, 255, 20, rand);
    }
}
