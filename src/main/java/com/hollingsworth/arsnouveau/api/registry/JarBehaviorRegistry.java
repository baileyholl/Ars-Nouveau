package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class JarBehaviorRegistry {
    public static final List<JarBehavior<?>> DEFAULT_BEHAVIOR = List.of(new JarBehavior<>());
    private static final ConcurrentHashMap<EntityType<?>, List<JarBehavior<?>>> BEHAVIOR_REGISTRY = new ConcurrentHashMap<>();

    public static <T extends Entity> void register(EntityType<T> type, JarBehavior<T> jarBehavior) {
        if (!BEHAVIOR_REGISTRY.containsKey(type)) {
            BEHAVIOR_REGISTRY.put(type, new ArrayList<>());
        }
        BEHAVIOR_REGISTRY.get(type).add(jarBehavior);
    }

    public static void forEach(Entity entity, Consumer<JarBehavior<? extends Entity>> consumer){
        List<JarBehavior<?>> jarBehaviors = BEHAVIOR_REGISTRY.getOrDefault(entity.getType(), DEFAULT_BEHAVIOR);
        for(JarBehavior<?> jarBehavior : jarBehaviors){
            consumer.accept(jarBehavior);
        }
    }

    public static boolean containsEntity(Entity entity){
        return BEHAVIOR_REGISTRY.containsKey(entity.getType());
    }
}
