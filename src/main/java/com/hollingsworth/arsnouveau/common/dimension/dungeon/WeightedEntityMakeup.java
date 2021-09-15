package com.hollingsworth.arsnouveau.common.dimension.dungeon;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.WeightedList;

import java.util.Random;

public class WeightedEntityMakeup {

    public WeightedList<Data> weightedEntityList = new WeightedList<>();
    public String id;
    public WeightedEntityMakeup(){
        addEntry(new Data(30, EntityType.ZOMBIE), 200);
        addEntry(new Data(35, EntityType.SKELETON), 100);
        addEntry(new Data(100, EntityType.BLAZE), 20);
        id = "base";
    }

    public void addEntry(Data data, int weight){
        weightedEntityList.add(data, weight);
    }

    public Data getNext(Random random){
        return weightedEntityList.getOne(random);
    }

    public static class Data{
        public int cost;
        public EntityType<? extends LivingEntity> entityType;

        public Data(int cost, EntityType<? extends LivingEntity> entityType){
            this.cost = cost;
            this.entityType = entityType;
        }
    }
}
