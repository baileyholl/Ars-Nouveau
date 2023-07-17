/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.multiblock;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.book.LoaderRegistry;
import com.hollingsworth.arsnouveau.common.book.multiblock.matcher.Matchers;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.Map.Entry;

public class SparseMultiblock extends AbstractMultiblock {

    public static final ResourceLocation TYPE = ArsNouveau.loc("sparse");

    private final Map<BlockPos, StateMatcher> stateMatchers;
    private final Vec3i size;

    public SparseMultiblock(Map<BlockPos, StateMatcher> stateMatchers) {
        Preconditions.checkArgument(!stateMatchers.isEmpty(), "No data given to sparse multiblock!");

        //this differs from dense multiblock, where we keep a copy of the originally loaded data for serialization, but it should be fine as we have less entries.
        this.stateMatchers = ImmutableMap.copyOf(stateMatchers);
        this.size = this.calculateSize();
    }

    public static SparseMultiblock fromJson(JsonObject json) {
        var jsonMapping = GsonHelper.getAsJsonObject(json, "mapping");
        var mapping = mappingFromJson(jsonMapping);

        //        "pattern": {
//            "N": [
//            [-1, 0, -2], [0, 0, -2], [1, 0, -2]
//          ],
//            "S": [
//            [-1, 0, 2], [0, 0, 2], [1, 0, 2]
//          ],
//            "W": [
//            [-2, 0, -1], [-2, 0, 0], [-2, 0, 1]
//          ],
//            "E": [
//            [2, 0, -1], [2, 0, 0], [2, 0, 1]
//          ]
//        }

        var jsonPattern = GsonHelper.getAsJsonObject(json, "pattern");

        Map<BlockPos, StateMatcher> stateMatchers = new HashMap<>();
        for (Entry<String, JsonElement> entry : jsonPattern.entrySet()) {
            if (entry.getKey().length() != 1)
                throw new JsonSyntaxException("Pattern key needs to be only 1 character");

            var matcher = mapping.get(entry.getKey().charAt(0));

            var jsonPositions = GsonHelper.convertToJsonArray(entry.getValue(), entry.getKey());
            for (JsonElement jsonPosition : jsonPositions) {
                var jsonPos = GsonHelper.convertToJsonArray(jsonPosition, entry.getKey());
                if (jsonPos.size() != 3) {
                    throw new JsonSyntaxException("Each matcher position needs to be an array of 3 integers");
                }
                stateMatchers.put(
                        new BlockPos(jsonPos.get(0).getAsInt(), jsonPos.get(1).getAsInt(), jsonPos.get(2).getAsInt()),
                        matcher);
            }
        }

        var multiblock = new SparseMultiblock(stateMatchers);

        return additionalPropertiesFromJson(multiblock, json);
    }

    public static SparseMultiblock fromNetwork(FriendlyByteBuf buffer) {
        var symmetrical = buffer.readBoolean();
        var offX = buffer.readVarInt();
        var offY = buffer.readVarInt();
        var offZ = buffer.readVarInt();
        var viewOffX = buffer.readVarInt();
        var viewOffY = buffer.readVarInt();
        var viewOffZ = buffer.readVarInt();

        var size = buffer.readVarInt();
        var stateMatchers = new HashMap<BlockPos, StateMatcher>();
        for (int i = 0; i < size; i++) {
            var pos = buffer.readBlockPos();
            var type = buffer.readResourceLocation();
            var matcher = LoaderRegistry.getStateMatcherNetworkLoader(type).fromNetwork(buffer);
            stateMatchers.put(pos, matcher);
        }

        var multiblock = new SparseMultiblock(stateMatchers);
        multiblock.setSymmetrical(symmetrical);
        multiblock.setOffset(offX, offY, offZ);
        multiblock.setViewOffset(viewOffX, viewOffY, viewOffZ);
        return multiblock;
    }

    private Vec3i calculateSize() {
        int minX = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getX).min().getAsInt();
        int maxX = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getX).max().getAsInt();
        int minY = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getY).min().getAsInt();
        int maxY = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getY).max().getAsInt();
        int minZ = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getZ).min().getAsInt();
        int maxZ = this.stateMatchers.keySet().stream().mapToInt(BlockPos::getZ).max().getAsInt();
        return new Vec3i(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
    }

    @Override
    public Vec3i getSize() {
        return this.size;
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public Pair<BlockPos, Collection<SimulateResult>> simulate(Level world, BlockPos anchor, Rotation rotation, boolean forView, boolean disableOffset) {
        BlockPos disp = forView
                ? new BlockPos(-this.viewOffX, -this.viewOffY + 1, -this.viewOffZ).rotate(rotation)
                : new BlockPos(-this.offX, -this.offY, -this.offZ).rotate(rotation);
        if(disableOffset)
            disp = BlockPos.ZERO;

        // the local origin of this multiblock, in world coordinates
        BlockPos origin = anchor.offset(disp);
        List<SimulateResult> ret = new ArrayList<>();
        for (var e : this.stateMatchers.entrySet()) {
            BlockPos currDisp = e.getKey().rotate(rotation);
            BlockPos actionPos = origin.offset(currDisp);
            ret.add(new SimulateResultImpl(actionPos, e.getValue(), null));
        }
        return Pair.of(origin, ret);
    }

    @Override
    public boolean test(Level world, BlockPos start, int x, int y, int z, Rotation rotation) {
        this.setWorld(world);
        BlockPos checkPos = start.offset(new BlockPos(x, y, z).rotate(rotation));
        BlockState state = world.getBlockState(checkPos).rotate(AbstractMultiblock.fixHorizontal(rotation));
        StateMatcher matcher = this.stateMatchers.getOrDefault(new BlockPos(x, y, z), Matchers.ANY);
        return matcher.getStatePredicate().test(world, checkPos, state);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.symmetrical);
        buffer.writeVarInt(this.offX);
        buffer.writeVarInt(this.offY);
        buffer.writeVarInt(this.offZ);
        buffer.writeVarInt(this.viewOffX);
        buffer.writeVarInt(this.viewOffY);
        buffer.writeVarInt(this.viewOffZ);

        buffer.writeVarInt(this.stateMatchers.size());
        for (Entry<BlockPos, StateMatcher> entry : this.stateMatchers.entrySet()) {
            buffer.writeBlockPos(entry.getKey());
            buffer.writeResourceLocation(entry.getValue().getType());
            entry.getValue().toNetwork(buffer);
        }
    }
}
