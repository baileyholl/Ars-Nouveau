package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class RitualPillagerRaid extends AbstractRitual {
    @Override
    protected void tick() {
        if(!getWorld().isClientSide){

            ServerWorld world = (ServerWorld) getWorld();
            List<ServerPlayerEntity> players =  world.getEntitiesOfClass(ServerPlayerEntity.class, new AxisAlignedBB(getPos()).inflate(5.0));
            if(players.size() > 0){
                Raid raid = world.getRaids().createOrExtendRaid(players.get(0));
                if(raid != null){
                    this.setFinished();
                }
            }
        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return true;
    }

    @Override
    public void onItemConsumed(ItemStack stack) {
        super.onItemConsumed(stack);
    }


    @Override
    public String getID() {
        return RitualLib.RAID;
    }
}
