package com.hollingsworth.arsnouveau.common.dimension.dungeon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.Locale;

public class SpawnManager {

    public int budget;
    public CombatType combatType;
    public WeightedEntityMakeup weightedEntityMakeup = new WeightedEntityMakeup();
    ServerWorld world;
    public int spawnDelayTicks;
    DungeonEvent event;
    public int wave;

    public SpawnManager(ServerWorld world, DungeonEvent event){
        budget = 500;
        combatType = CombatType.BALANCED;
        this.world = world;
        this.event = event;
    }


    public void tick(){
        if(spawnDelayTicks > 0)
            spawnDelayTicks--;

        for(int i = 0; i < 5; i++) {
            if (spawnDelayTicks == 0 && budget > 0) {
                LivingEntity entity = getNextEntity(world);
                entity.setPos(0, 105, 0);
                world.addFreshEntity(entity);
                entity.getPersistentData().put("an_dungeon", new CompoundNBT());
                this.event.addAttacker(entity, true);
                if(i >= 4){
                    spawnDelayTicks = 100;
                }
            }else{
                break;
            }
        }
    }

    public LivingEntity getNextEntity(ServerWorld world){
        WeightedEntityMakeup.Data weightedEntity = weightedEntityMakeup.getNext(world.random);
        budget -= weightedEntity.cost;
        return weightedEntity.entityType.create(world);
    }

    public void waveComplete(){
        wave++;
        budget = 500 + 50 * wave;
        spawnDelayTicks = 0;
    }


    public SpawnManager(CompoundNBT tag, ServerWorld world, DungeonEvent event){
        this.budget = tag.getInt("budget");
        this.combatType = CombatType.getByName(tag.getString("combat"));
        this.world = world;
        this.event = event;
        this.wave = tag.getInt("wave");
        this.spawnDelayTicks = tag.getInt("spawnDelayTicks");
    }

    public CompoundNBT serialize(){
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("budget", budget);
        tag.putString("combat", combatType.name().toLowerCase(Locale.ROOT));
        tag.putInt("wave", wave);
        tag.putInt("spawnDelayTicks", spawnDelayTicks);
        return tag;
    }

    public enum CombatType{
        SWARM,
        BUFFED,
        BALANCED;

        private static final SpawnManager.CombatType[] VALUES = values();

        private static SpawnManager.CombatType getByName(String p_221275_0_) {
            for (SpawnManager.CombatType state : VALUES) {
                if (p_221275_0_.equalsIgnoreCase(state.name())) {
                    return state;
                }
            }

            return BALANCED;
        }
    }
}
