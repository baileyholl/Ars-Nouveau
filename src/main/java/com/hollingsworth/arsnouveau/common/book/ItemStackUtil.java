/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

public class ItemStackUtil {


    public static Triple<ResourceLocation, Integer, CompoundTag> parseItemStackString(String res) {
        String nbt = "";
        int nbtStart = res.indexOf("{");
        if (nbtStart > 0) {
            nbt = res.substring(nbtStart).replaceAll("([^\\\\])'", "$1\"").replaceAll("\\\\'", "'");
            res = res.substring(0, nbtStart);
        }

        String[] upper = res.split("#");
        String count = "1";
        if (upper.length > 1) {
            res = upper[0];
            count = upper[1];
        }

        String[] tokens = res.split(":");
        if (tokens.length < 2) {
            throw new RuntimeException("Malformed item ID " + res);
        }

        ResourceLocation key = new ResourceLocation(tokens[0], tokens[1]);
        int countn = Integer.parseInt(count);
        CompoundTag tag = null;

        if (!nbt.isEmpty()) {
            try {
                tag = TagParser.parseTag(nbt);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException("Failed to parse ItemStack JSON", e);
            }
        }

        return ImmutableTriple.of(key, countn, tag);
    }

    public static ItemStack loadFromParsed(Triple<ResourceLocation, Integer, CompoundTag> parsed) {
        var key = parsed.getLeft();
        var count = parsed.getMiddle();
        var nbt = parsed.getRight();
        var item = ForgeRegistries.ITEMS.getValue(key);
        if (item == null) {
            throw new RuntimeException("Unknown item ID: " + key);
        }
        ItemStack stack = new ItemStack(item, count);

        if (nbt != null) {
            stack.setTag(nbt);
        }
        return stack;
    }

}
