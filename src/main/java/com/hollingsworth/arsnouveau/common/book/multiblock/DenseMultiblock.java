/*
 * SPDX-FileCopyrightText: 2022 Authors of Patchouli
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.multiblock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.book.LoaderRegistry;
import com.hollingsworth.arsnouveau.common.book.multiblock.matcher.Matchers;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class DenseMultiblock extends AbstractMultiblock {

    public static final ResourceLocation TYPE = ArsNouveau.loc("dense");

    private static final Gson GSON = new GsonBuilder().create();

    private final String[][] pattern;
    private final Vec3i size;
    /**
     * Keep only for serialization
     */
    private final Map<Character, StateMatcher> targets;
    private StateMatcher[][][] stateMatchers;

    public DenseMultiblock(String[][] pattern, Map<Character, StateMatcher> targets) {
        this.pattern = pattern;
        this.targets = targets;

        if (!targets.containsKey('_')) {
            targets.put('_', Matchers.ANY);
        }
        if (!targets.containsKey(' ')) {
            targets.put(' ', Matchers.AIR);
        }
        if (!targets.containsKey('0')) {
            targets.put('0', Matchers.AIR);
        }

        this.size = this.build(targets, getPatternDimensions(pattern));
    }

    public static DenseMultiblock fromNetwork(FriendlyByteBuf buffer) {
        var symmetrical = buffer.readBoolean();
        var offX = buffer.readVarInt();
        var offY = buffer.readVarInt();
        var offZ = buffer.readVarInt();
        var viewOffX = buffer.readVarInt();
        var viewOffY = buffer.readVarInt();
        var viewOffZ = buffer.readVarInt();

        var sizeX = buffer.readVarInt();
        var sizeY = buffer.readVarInt();
        var pattern = new String[sizeY][sizeX];
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                pattern[y][x] = buffer.readUtf();
            }
        }

        var targets = new HashMap<Character, StateMatcher>();
        var targetCount = buffer.readVarInt();
        for (int i = 0; i < targetCount; i++) {
            var key = buffer.readChar();
            var type = buffer.readResourceLocation();
            var stateMatcher = LoaderRegistry.getStateMatcherNetworkLoader(type).fromNetwork(buffer);
            targets.put(key, stateMatcher);
        }

        var multiblock = new DenseMultiblock(pattern, targets);
        multiblock.setSymmetrical(symmetrical);
        multiblock.setOffset(offX, offY, offZ);
        multiblock.setViewOffset(viewOffX, viewOffY, viewOffZ);
        return multiblock;
    }

    public static DenseMultiblock fromJson(JsonObject json) {
        var pattern = GSON.fromJson(json.get("pattern"), String[][].class);

        var jsonMapping = GsonHelper.getAsJsonObject(json, "mapping");
        var mapping = mappingFromJson(jsonMapping);

        var multiblock = new DenseMultiblock(pattern, mapping);
        return additionalPropertiesFromJson(multiblock, json);
    }

    private static Vec3i getPatternDimensions(String[][] pattern) {
        int expectedLenX = -1;
        int expectedLenZ = -1;
        for (String[] arr : pattern) {
            if (expectedLenX == -1) {
                expectedLenX = arr.length;
            }
            if (arr.length != expectedLenX) {
                throw new IllegalArgumentException("Inconsistent array length. Expected" + expectedLenX + ", got " + arr.length);
            }

            for (String s : arr) {
                if (expectedLenZ == -1) {
                    expectedLenZ = s.length();
                }
                if (s.length() != expectedLenZ) {
                    throw new IllegalArgumentException("Inconsistent array length. Expected" + expectedLenX + ", got " + arr.length);
                }
            }
        }

        return new Vec3i(expectedLenX, pattern.length, expectedLenZ);
    }

    private Vec3i build(Map<Character, StateMatcher> stateMap, Vec3i dimensions) {
        boolean foundCenter = false;

        this.stateMatchers = new StateMatcher[dimensions.getX()][dimensions.getY()][dimensions.getZ()];
        for (int y = 0; y < dimensions.getY(); y++) {
            for (int x = 0; x < dimensions.getX(); x++) {
                for (int z = 0; z < dimensions.getZ(); z++) {
                    char c = this.pattern[y][x].charAt(z);
                    if (!stateMap.containsKey(c)) {
                        throw new IllegalArgumentException("Character " + c + " isn't mapped");
                    }

                    StateMatcher matcher = stateMap.get(c);
                    if (c == '0') {
                        if (foundCenter) {
                            throw new IllegalArgumentException("A structure can't have two centers");
                        }
                        foundCenter = true;
                        this.offX = x;
                        this.offY = dimensions.getY() - y - 1;
                        this.offZ = z;
                        this.setViewOffset();
                    }

                    this.stateMatchers[x][dimensions.getY() - y - 1][z] = matcher;
                }
            }
        }

        if (!foundCenter) {
            throw new IllegalArgumentException("A structure can't have no center");
        }
        return dimensions;
    }

    @Override
    public ResourceLocation getType() {
        return TYPE;
    }

    @Override
    public Pair<BlockPos, Collection<SimulateResult>> simulate(Level world, BlockPos anchor, Rotation rotation, boolean forView, boolean disableOffset ) {
        BlockPos disp = forView
                ? new BlockPos(-this.viewOffX, -this.viewOffY + 1, -this.viewOffZ).rotate(rotation)
                : new BlockPos(-this.offX, -this.offY, -this.offZ).rotate(rotation);
        if(disableOffset)
            disp = BlockPos.ZERO;

        // the local origin of this multiblock, in world coordinates
        BlockPos origin = anchor.offset(disp);
        List<SimulateResult> ret = new ArrayList<>();
        for (int x = 0; x < this.size.getX(); x++) {
            for (int y = 0; y < this.size.getY(); y++) {
                for (int z = 0; z < this.size.getZ(); z++) {
                    BlockPos currDisp = new BlockPos(x, y, z).rotate(rotation);
                    BlockPos actionPos = origin.offset(currDisp);
                    char currC = this.pattern[y][x].charAt(z);
                    ret.add(new SimulateResultImpl(actionPos, this.stateMatchers[x][y][z], currC));
                }
            }
        }
        return Pair.of(origin, ret);
    }

    @Override
    public boolean test(Level world, BlockPos start, int x, int y, int z, Rotation rotation) {
        this.setWorld(world);
        if (x < 0 || y < 0 || z < 0 || x >= this.size.getX() || y >= this.size.getY() || z >= this.size.getZ()) {
            return false;
        }
        BlockPos checkPos = start.offset(new BlockPos(x, y, z).rotate(AbstractMultiblock.fixHorizontal(rotation)));
        TriPredicate<BlockGetter, BlockPos, BlockState> pred = this.stateMatchers[x][y][z].getStatePredicate();
        BlockState state = world.getBlockState(checkPos).rotate(rotation);

        return pred.test(world, checkPos, state);
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

        buffer.writeVarInt(this.size.getX());
        buffer.writeVarInt(this.size.getY());
        for (int y = 0; y < this.size.getY(); y++) {
            for (int x = 0; x < this.size.getX(); x++) {
                buffer.writeUtf(this.pattern[y][x]);
            }
        }

        buffer.writeVarInt(this.targets.size());
        for (var entry : this.targets.entrySet()) {
            buffer.writeChar(entry.getKey());
            buffer.writeResourceLocation(entry.getValue().getType());
            entry.getValue().toNetwork(buffer);
        }
    }

    @Override
    public Vec3i getSize() {
        return this.size;
    }

}
