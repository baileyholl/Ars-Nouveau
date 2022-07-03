package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ChangeableBehavior;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;

public class StarbyBehavior extends ChangeableBehavior {
    public Starbuncle starbuncle;

    public StarbyBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        this.starbuncle = entity;
        goals.add(new WrappedGoal(4, new GoToBedGoal(starbuncle, this)));
        goals.add(new WrappedGoal(8, new LookAtPlayerGoal(starbuncle, Player.class, 3.0F, 0.01F)));
        goals.add(new WrappedGoal(8, new NonHoggingLook(starbuncle, Mob.class, 3.0F, 0.01f)));
        goals.add(new WrappedGoal(1, new OpenDoorGoal(starbuncle, true)));
    }

    public boolean canGoToBed(){
        return true;
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, "starby");
    }
}
