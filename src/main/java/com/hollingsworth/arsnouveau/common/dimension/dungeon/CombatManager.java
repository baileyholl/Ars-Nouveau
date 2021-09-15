package com.hollingsworth.arsnouveau.common.dimension.dungeon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.Locale;

public class CombatManager {

    public int budget;
    public CombatType combatType;
    public WeightedEntityMakeup weightedEntityMakeup = new WeightedEntityMakeup();

    public CombatManager(){
        budget = 500;
        combatType = CombatType.BALANCED;
    }

    public LivingEntity getNextEntity(ServerWorld world){
        WeightedEntityMakeup.Data weightedEntity = weightedEntityMakeup.getNext(world.random);
        budget -= weightedEntity.cost;
        return weightedEntity.entityType.create(world);
    }


    public CombatManager(CompoundNBT tag){
        this.budget = tag.getInt("budget");
        this.combatType = CombatType.getByName(tag.getString("combat"));
    }

    public CompoundNBT serialize(){
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("budget", budget);
        tag.putString("combat", combatType.name().toLowerCase(Locale.ROOT));
        return tag;
    }

    public enum CombatType{
        SWARM,
        BUFFED,
        BALANCED;

        private static final CombatManager.CombatType[] VALUES = values();

        private static CombatManager.CombatType getByName(String p_221275_0_) {
            for (CombatManager.CombatType state : VALUES) {
                if (p_221275_0_.equalsIgnoreCase(state.name())) {
                    return state;
                }
            }

            return BALANCED;
        }
    }
}
