/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

public class EntityUtil {

    private static Pair<String, String> splitNameAndNBT(String entityId) {
        int nbtStart = entityId.indexOf("{");
        String nbtStr = "";
        if (nbtStart > 0) {
            nbtStr = entityId.substring(nbtStart).replaceAll("([^\\\\])'", "$1\"").replaceAll("\\\\'", "'");
            entityId = entityId.substring(0, nbtStart);
        }

        return Pair.of(entityId, nbtStr);
    }

    public static String getEntityName(String entityId) {
        Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
        var type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(nameAndNbt.getLeft()));

        return type.getDescriptionId();
    }

    public static Function<Level, Entity> getEntityLoader(String entityId) {
        Pair<String, String> nameAndNbt = splitNameAndNBT(entityId);
        entityId = nameAndNbt.getLeft();
        String nbtStr = nameAndNbt.getRight();
        CompoundTag nbt = null;

        if (!nbtStr.isEmpty()) {
            try {
                nbt = TagParser.parseTag(nbtStr);
            } catch (CommandSyntaxException e) {
//                Modonomicon.LOGGER.error("Failed to load entity data", e);
            }
        }

        ResourceLocation key = new ResourceLocation(entityId);
        var type = ForgeRegistries.ENTITY_TYPES.getValue(key);
        if (type == null) {
            throw new RuntimeException("Unknown entity id: " + entityId);
        }

        final CompoundTag useNbt = nbt;
        final String useId = entityId;
        return (world) -> {
            Entity entity;
            try {
                entity = type.create(world);
                if (useNbt != null) {
                    entity.load(useNbt);
                }

                return entity;
            } catch (Exception e) {
                throw new IllegalArgumentException("Can't load entity " + useId, e);
            }
        };
    }
}
