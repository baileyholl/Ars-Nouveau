package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.entity.ChangeableBehavior;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyPotionBehavior;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class BehaviorRegistry {
    private static final Map<ResourceLocation, CreateFromTag> REGISTRY = new HashMap<>();

    public static void register(ResourceLocation name, CreateFromTag creator){
        REGISTRY.put(name, creator);
    }

    public static ChangeableBehavior create(Entity entity, CompoundTag tag){
        CreateFromTag create = REGISTRY.get(ResourceLocation.tryParse(tag.getString("id")));
        return create == null ? null : create.create(entity, tag);
    }

    static{
        register(StarbyTransportBehavior.TRANSPORT_ID, (entity, tag) -> new StarbyTransportBehavior((Starbuncle) entity, tag));
        register(StarbyPotionBehavior.POTION_ID, (entity, tag) -> new StarbyPotionBehavior((Starbuncle) entity, tag));
    }

    public interface CreateFromTag{
        ChangeableBehavior create(Entity entity, CompoundTag tag);
    }

    private BehaviorRegistry(){}
}
