package com.hollingsworth.arsnouveau.common.light;

import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;

import java.util.function.Function;

public class DynamLightUtil {


    public static int getSectionCoord(double coord) {
        return getSectionCoord(Mth.floor(coord));
    }

    public static int getSectionCoord(int coord) {
        return coord >> 4;
    }

    private static int getLuminance(Entity entity){
        int level = 0;
        if(entity.isOnFire())
            return 15;
        if(Config.ENTITY_LIGHT_MAP.containsKey(entity.getType().getRegistryName()))
            return Config.ENTITY_LIGHT_MAP.get(entity.getType().getRegistryName());
        if(LightManager.getLightRegistry().containsKey(entity.getType())){
            for(Function<Entity, Integer> function : LightManager.getLightRegistry().get(entity.getType())){
                int val = function.apply(entity);
                level = Math.max(val, level);
            }
        }
        return Math.min(15, level);
    }

    public static boolean couldGiveLight(Entity entity){
        return LightManager.getLightRegistry().containsKey(entity.getType()) || Config.ENTITY_LIGHT_MAP.containsKey(entity.getType().getRegistryName()) || (entity instanceof Player player && getPlayerLight(player) > 0);
    }

    public static int getPlayerLight(Player player){
        int mainLight = Config.ITEM_LIGHTMAP.getOrDefault(player.getMainHandItem().getItem().getRegistryName(), 0);
        int offHandLight = Config.ITEM_LIGHTMAP.getOrDefault(player.getOffhandItem().getItem().getRegistryName(), 0);
        return Math.max(mainLight, offHandLight);
    }

    public static int lightForEntity(Entity entity){
        int light = 0;
        if(entity instanceof Player player){
            light = getPlayerLight(player);
        }
        if(light < 15 && LightManager.containsEntity(entity.getType())) {
            int entityLuminance = getLuminance(entity);
            return Math.max(entityLuminance, light);
        }

        return Math.min(15, light);
    }

    public static int fromItemLike(ItemLike itemLike){
        return Config.ITEM_LIGHTMAP.getOrDefault(itemLike.asItem().getRegistryName(), 0);
    }

}
