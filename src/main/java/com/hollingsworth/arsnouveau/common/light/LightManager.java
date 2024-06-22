package com.hollingsworth.arsnouveau.common.light;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * This code is taken from LambDynamicLights, an MIT fabric mod: <a href="https://github.com/LambdAurora/LambDynamicLights">Github Link</a>
 */
public class LightManager {

    private final static Set<LambDynamicLight> dynamicLightSources = new HashSet<>();
    private final static ReentrantReadWriteLock lightSourcesLock = new ReentrantReadWriteLock();

    public static long lastUpdate = System.currentTimeMillis();

    public static List<Integer> jarHoldingEntityList = new ArrayList<>();
    public static int lastUpdateCount = 0;
    private static final Map<EntityType<?>, List<Function<?, Integer>>> LIGHT_REGISTRY = new HashMap<>();

    public static void init() {

        register(EntityType.PLAYER, (p -> {
            NonNullList<ItemStack> list = p.inventory.items;
            for (int i = 0; i < 9; i++) {
                ItemStack jar = list.get(i);
                if (jar.getItem() == ItemsRegistry.JAR_OF_LIGHT.asItem()) {
                    return 15;
                }
            }
            return p != ArsNouveau.proxy.getPlayer() && LightManager.jarHoldingEntityList.contains(p.getId()) ? 15 : 0;
        }));

        register(EntityType.FALLING_BLOCK, (p) -> {
            return p.getBlockState().getLightEmission(p.level, p.blockPosition());
        });

        register(ModEntities.ENCHANTED_FALLING_BLOCK.get(), p -> {
            return p.getBlockState().getLightEmission(p.level, p.blockPosition());
        });
        register(ModEntities.ENCHANTED_MAGE_BLOCK.get(), p -> {
            return p.getBlockState().getLightEmission(p.level, p.blockPosition());
        });
        register(ModEntities.ENTITY_FLYING_ITEM.get(), (p -> 10));
        register(ModEntities.ENTITY_FOLLOW_PROJ.get(), (p -> 10));
        register(ModEntities.SPELL_PROJ.get(), (p -> 15));
        register(ModEntities.ORBIT_SPELL.get(), (p -> 15));
        register(ModEntities.LINGER_SPELL.get(), (p -> 15));
        register(ModEntities.WALL_SPELL.get(), (p -> 15));
        register(ModEntities.STARBUNCLE_TYPE.get(), (p -> {
            if (p.level.getBrightness(LightLayer.BLOCK, p.blockPosition()) < 6) {
                return 10;
            }
            return 0;
        }));

        register(ModEntities.ENTITY_FAMILIAR_STARBUNCLE.get(), (p -> {
            if (p.level.getBrightness(LightLayer.BLOCK, p.blockPosition()) < 6) {
                return 10;
            }
            return 0;
        }));
        register(EntityType.ENDERMAN, (enderMan -> {

            if (enderMan.getCarriedBlock() != null) {
                return DynamLightUtil.fromItemLike(enderMan.getCarriedBlock().getBlock());
            }
            return 0;
        }));

        register(EntityType.ITEM, (p) -> DynamLightUtil.fromItemLike((p.getItem().getItem())));
        register(EntityType.ITEM_FRAME, (p) -> DynamLightUtil.fromItemLike((p.getItem().getItem())));
        register(EntityType.GLOW_ITEM_FRAME, (p) -> Math.max(14, DynamLightUtil.fromItemLike((p.getItem().getItem()))));
        register(EntityType.GLOW_SQUID, (p) -> (int) Mth.clampedLerp(0.f, 12.f, 1.f - p.getDarkTicksRemaining() / 10.f));
        register(ModEntities.ANIMATED_BLOCK.get(), (p) -> p.getBlockState().getLightEmission(p.level, p.blockPosition()));
    }

    public static < T extends Entity> void register(EntityType<T> type, Function<T, Integer> luminanceFunction) {
        if (!LIGHT_REGISTRY.containsKey(type)) {
            LIGHT_REGISTRY.put(type, new ArrayList<>());
        }
        LIGHT_REGISTRY.get(type).add(luminanceFunction);
    }


    public static <T extends Entity> Map<EntityType<?>, List<Function<?, Integer>>> getLightRegistry(){
        return LIGHT_REGISTRY;
    }

    public static <T extends Entity> int getValue(T entity)
    {
        int val = 0;
        if(!LIGHT_REGISTRY.containsKey(entity.getType()))
            return val;
        EntityType<?> type = entity.getType();
        for(Function<?, Integer> function : LIGHT_REGISTRY.get(type))
        {
            var fun = (Function<T,Integer>)function;
            Integer value = fun.apply(entity);
            if(value > val){
                val = value;
            }
        }
        return val;
    }

    public static boolean containsEntity(EntityType<? extends Entity> type){
        return LIGHT_REGISTRY.containsKey(type) || Config.ENTITY_LIGHT_MAP.containsKey(ForgeRegistries.ENTITY_TYPES.getKey(type));
    }

    /**
     * Adds the light source to the tracked light sources.
     *
     * @param lightSource the light source to add
     */
    public static void addLightSource(LambDynamicLight lightSource) {
        if (!lightSource.getDynamicLightWorld().isClientSide())
            return;
        if (!shouldUpdateDynamicLight())
            return;
        if (containsLightSource(lightSource))
            return;
        lightSourcesLock.writeLock().lock();
        dynamicLightSources.add(lightSource);
        lightSourcesLock.writeLock().unlock();
    }

    /**
     * Returns whether the light source is tracked or not.
     *
     * @param lightSource the light source to check
     * @return {@code true} if the light source is tracked, else {@code false}
     */
    public static boolean containsLightSource(@NotNull LambDynamicLight lightSource) {
        if (!lightSource.getDynamicLightWorld().isClientSide())
            return false;

        boolean result;
        lightSourcesLock.readLock().lock();
        result = dynamicLightSources.contains(lightSource);
        lightSourcesLock.readLock().unlock();
        return result;
    }

    /**
     * Returns the number of dynamic light sources that currently emit lights.
     *
     * @return the number of dynamic light sources emitting light
     */
    public int getLightSourcesCount() {
        int result;

        lightSourcesLock.readLock().lock();
        result = dynamicLightSources.size();
        lightSourcesLock.readLock().unlock();

        return result;
    }

    /**
     * Removes the light source from the tracked light sources.
     *
     * @param lightSource the light source to remove
     */
    public static void removeLightSource(LambDynamicLight lightSource) {
        lightSourcesLock.writeLock().lock();

        var sourceIterator = dynamicLightSources.iterator();
        LambDynamicLight it;
        while (sourceIterator.hasNext()) {
            it = sourceIterator.next();
            if (it.equals(lightSource)) {
                sourceIterator.remove();
                if (Minecraft.getInstance().level != null)
                    lightSource.lambdynlights$scheduleTrackedChunksRebuild(Minecraft.getInstance().levelRenderer);
                break;
            }
        }

        lightSourcesLock.writeLock().unlock();
    }

    /**
     * Clears light sources.
     */
    public static void clearLightSources() {
        lightSourcesLock.writeLock().lock();

        var sourceIterator = dynamicLightSources.iterator();
        LambDynamicLight it;
        while (sourceIterator.hasNext()) {
            it = sourceIterator.next();
            sourceIterator.remove();
            if (Minecraft.getInstance().levelRenderer != null) {
                if (it.getLuminance() > 0)
                    it.resetDynamicLight();
                it.lambdynlights$scheduleTrackedChunksRebuild(Minecraft.getInstance().levelRenderer);
            }
        }
        LightManager.jarHoldingEntityList = new ArrayList<>();

        lightSourcesLock.writeLock().unlock();
    }


    /**
     * Schedules a chunk rebuild at the specified chunk position.
     *
     * @param renderer the renderer
     * @param chunkPos the chunk position
     */
    public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, @NotNull BlockPos chunkPos) {
        scheduleChunkRebuild(renderer, chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
    }

    /**
     * Schedules a chunk rebuild at the specified chunk position.
     *
     * @param renderer the renderer
     * @param chunkPos the packed chunk position
     */
    public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, long chunkPos) {
        scheduleChunkRebuild(renderer, BlockPos.getX(chunkPos), BlockPos.getY(chunkPos), BlockPos.getZ(chunkPos));
    }

    public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, int x, int y, int z) {
        if (Minecraft.getInstance().level != null)
            renderer.setSectionDirty(x, y, z);
    }

    /**
     * Updates all light sources.
     *
     * @param renderer the renderer
     */
    public static void updateAll(LevelRenderer renderer) {
        long now = System.currentTimeMillis();


        lastUpdate = now;
        lastUpdateCount = 0;

        lightSourcesLock.readLock().lock();
        for (var lightSource : dynamicLightSources) {
            if (lightSource.lambdynlights$updateDynamicLight(renderer)) {
                lastUpdateCount++;
            }
        }
        lightSourcesLock.readLock().unlock();

    }

    /**
     * Updates the tracked chunk sets.
     *
     * @param chunkPos the packed chunk position
     * @param old      the set of old chunk coordinates to remove this chunk from it
     * @param newPos   the set of new chunk coordinates to add this chunk to it
     */
    public static void updateTrackedChunks(@NotNull BlockPos chunkPos, @Nullable LongOpenHashSet old, @Nullable LongOpenHashSet newPos) {
        if (old != null || newPos != null) {
            long pos = chunkPos.asLong();
            if (old != null)
                old.remove(pos);
            if (newPos != null)
                newPos.add(pos);
        }
    }

    public static int getLightmapWithDynamicLight(@NotNull BlockPos pos, int lightmap) {
        return getLightmapWithDynamicLight(getDynamicLightLevel(pos), lightmap);
    }

    /**
     * Returns the lightmap with combined light levels.
     *
     * @param dynamicLightLevel the dynamic light level
     * @param lightmap          the vanilla lightmap coordinates
     * @return the modified lightmap coordinates
     */
    public static int getLightmapWithDynamicLight(double dynamicLightLevel, int lightmap) {
        if (dynamicLightLevel > 0) {
            // lightmap is (skyLevel << 20 | blockLevel << 4)

            // Get vanilla block light level.
            int blockLevel = getBlockLightNoPatch(lightmap);
            if (dynamicLightLevel > blockLevel) {
                // Equivalent to a << 4 bitshift with a little quirk: this one ensure more precision (more decimals are saved).
                int luminance = (int) (dynamicLightLevel * 16.0);
                lightmap &= 0xfff00000;
                lightmap |= luminance & 0x000fffff;
            }
        }

        return lightmap;
    }

    public static int getBlockLightNoPatch(int light) { // Reverts the forge patch to LightTexture.block
        return light >> 4 & '\uffff';
    }

    /**
     * Returns the dynamic light level at the specified position.
     *
     * @param pos the position
     * @return the dynamic light level at the specified position
     */
    public static double getDynamicLightLevel(@NotNull BlockPos pos) {
        double result = 0;
        lightSourcesLock.readLock().lock();
        for (var lightSource : dynamicLightSources) {
            result = maxDynamicLightLevel(pos, lightSource, result);
        }
        lightSourcesLock.readLock().unlock();

        return Mth.clamp(result, 0, 15);
    }

    private static final double MAX_RADIUS = 7.75;
    private static final double MAX_RADIUS_SQUARED = MAX_RADIUS * MAX_RADIUS;

    /**
     * Returns the dynamic light level generated by the light source at the specified position.
     *
     * @param pos               the position
     * @param lightSource       the light source
     * @param currentLightLevel the current surrounding dynamic light level
     * @return the dynamic light level at the specified position
     */
    public static double maxDynamicLightLevel(@NotNull BlockPos pos, @NotNull LambDynamicLight lightSource, double currentLightLevel) {
        int luminance = lightSource.getLuminance();
        if (luminance > 0) {
            // Can't use Entity#squaredDistanceTo because of eye Y coordinate.
            double dx = pos.getX() - lightSource.getDynamicLightX() + 0.5;
            double dy = pos.getY() - lightSource.getDynamicLightY() + 0.5;
            double dz = pos.getZ() - lightSource.getDynamicLightZ() + 0.5;

            double distanceSquared = dx * dx + dy * dy + dz * dz;
            // 7.75 because else we would have to update more chunks and that's not a good idea.
            // 15 (max range for blocks) would be too much and a bit cheaty.
            if (distanceSquared <= MAX_RADIUS_SQUARED) {
                double multiplier = 1.0 - Math.sqrt(distanceSquared) / MAX_RADIUS;
                double lightLevel = multiplier * luminance;
                if (lightLevel > currentLightLevel) {
                    return lightLevel;
                }
            }
        }
        return currentLightLevel;
    }

    /**
     * Updates the dynamic lights tracking.
     *
     * @param lightSource the light source
     */
    public static void updateTracking(@NotNull LambDynamicLight lightSource) {
        boolean enabled = lightSource.isDynamicLightEnabled();
        int luminance = lightSource.getLuminance();
        if (!enabled && luminance > 0) {
            lightSource.setDynamicLightEnabled(true);
        } else if (enabled && luminance < 1) {
            lightSource.setDynamicLightEnabled(false);
        }
    }

    public static boolean shouldUpdateDynamicLight() {
        return Config.DYNAMIC_LIGHTS_ENABLED != null && Config.DYNAMIC_LIGHTS_ENABLED.get();
    }

    public static void toggleLightsAndConfig(boolean enabled) {
        Config.DYNAMIC_LIGHTS_ENABLED.set(enabled);
        Config.DYNAMIC_LIGHTS_ENABLED.save();
        if (!enabled) {
            clearLightSources();
        }
    }
}
