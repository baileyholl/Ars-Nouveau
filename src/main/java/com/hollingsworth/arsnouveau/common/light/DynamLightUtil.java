package com.hollingsworth.arsnouveau.common.light;

import com.google.common.collect.Iterables;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;


public class DynamLightUtil {
    public static int getJarOfLightLuminance(Player p) {
        for (int i = 0; i < 9; i++) {
            ItemStack jar = p.getInventory().getItem(i);
            if (jar.is(ItemsRegistry.JAR_OF_LIGHT.asItem())) {
                return 15;
            }
        }
        return p != ArsNouveau.proxy.getPlayer() && LightManager.jarHoldingEntityList.contains(p.getId()) ? 15 : 0;
    }

    public static int getSectionCoord(double coord) {
        return getSectionCoord(Mth.floor(coord));
    }

    public static int getSectionCoord(int coord) {
        return coord >> 4;
    }


    private static int getLuminance(Entity entity) {
        if (entity.isOnFire() || entity.isCurrentlyGlowing()) {
            return 15;
        }
        if (Config.ENTITY_LIGHT_MAP.containsKey(keyFor(entity)))
            return Config.ENTITY_LIGHT_MAP.get(keyFor(entity));
        return Math.min(15, LightManager.getValue(entity));
    }

    public static boolean couldGiveLight(Entity entity) {
        return LightManager.getLightRegistry().containsKey(entity.getType()) || Config.ENTITY_LIGHT_MAP.containsKey(keyFor(entity)) || (entity instanceof Player player && getPlayerLight(player) > 0) || entity.isOnFire() || entity.isCurrentlyGlowing();
    }

    public static int getPlayerLight(Player player) {
        int max = 0;
        // 1.21.11: getArmorSlots()/getHandSlots() removed from LivingEntity; iterate via EquipmentSlot directly
        Iterable<ItemStack> armorItems = () -> java.util.Arrays.stream(net.minecraft.world.entity.EquipmentSlot.values())
                .filter(s -> s.getType() == net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR || s.getType() == net.minecraft.world.entity.EquipmentSlot.Type.HAND)
                .map(player::getItemBySlot).iterator();
        for (ItemStack item : Iterables.concat(player.getInventory().getNonEquipmentItems(), armorItems)) {
            if (item.isEmpty()) continue;
            max = Math.max(max, Config.ITEM_LIGHTMAP.getOrDefault(keyFor(item.getItem()), 0));
        }
        return max;
    }

    public static int lightForEntity(Entity entity) {
        int light = 0;
        if (entity instanceof Player player) {
            light = getPlayerLight(player);
        }
        if (entity.isOnFire() || entity.isCurrentlyGlowing()) {
            return 15;
        }
        if (light < 15 && LightManager.containsEntity(entity.getType())) {
            int entityLuminance = getLuminance(entity);
            return Math.max(entityLuminance, light);
        }

        return Math.min(15, light);
    }

    public static int fromItemLike(ItemLike itemLike) {
        return Config.ITEM_LIGHTMAP.getOrDefault(keyFor(itemLike), 0);
    }

    public static Identifier keyFor(Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
    }

    public static Identifier keyFor(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem());
    }

}
