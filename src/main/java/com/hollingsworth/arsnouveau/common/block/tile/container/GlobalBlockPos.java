/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hollingsworth.arsnouveau.common.block.tile.container;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Objects;
import java.util.StringJoiner;

public class GlobalBlockPos implements INBTSerializable<CompoundTag> {
    //region Fields
    protected BlockPos pos;
    protected ResourceKey<Level> dimensionKey;
    //endregion Fields

    //region Initialization
    public GlobalBlockPos() {
    }

    public GlobalBlockPos(BlockPos pos, ResourceKey<Level> dimensionKey) {
        this.pos = pos;
        this.dimensionKey = dimensionKey;
    }

    public GlobalBlockPos(BlockPos pos, Level level) {
        this.pos = pos;
        this.dimensionKey = level.dimension();
    }
    //endregion Initialization

    //region Static Methods
    public static GlobalBlockPos from(CompoundTag compound) {
        GlobalBlockPos globalBlockPos = new GlobalBlockPos();
        globalBlockPos.deserializeNBT(compound);
        return globalBlockPos;
    }

    public static GlobalBlockPos from(FriendlyByteBuf buf) {
        GlobalBlockPos globalBlockPos = new GlobalBlockPos();
        globalBlockPos.decode(buf);
        return globalBlockPos;
    }
    //endregion Getter / Setter

    public static GlobalBlockPos from(BlockEntity blockEntity) {
        return new GlobalBlockPos(blockEntity.getBlockPos(), blockEntity.getLevel());
    }

    //region Getter / Setter
    public ResourceKey<Level> getDimensionKey() {
        return this.dimensionKey;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    //region Overrides
    @Override
    public int hashCode() {
        return Objects.hash(this.dimensionKey, this.pos);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        GlobalBlockPos other = (GlobalBlockPos) obj;
        if (!this.pos.equals(other.pos))
            return false;
        return this.dimensionKey.equals(other.dimensionKey);
    }
    //endregion Overrides

    @Override
    public String toString() {
        return new StringJoiner(", ", "[", "]").add( this.dimensionKey.registry().toString())
                .add("x=" + this.pos.getX()).add("y=" + this.pos.getY())
                .add("z=" + this.pos.getZ()).toString();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.write(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.read(nbt);
    }
    //endregion Static Methods

    //region Methods
    public CompoundTag write(CompoundTag compound) {
        compound.putLong("pos", this.getPos().asLong());
        compound.putString("dimension", this.dimensionKey.location().toString());
        return compound;
    }

    public void read(CompoundTag compound) {
        this.pos = BlockPos.of(compound.getLong("pos"));
        this.dimensionKey =
                ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString("dimension")));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeResourceLocation(this.dimensionKey.location());
    }

    public void decode(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.dimensionKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation());
    }
    //endregion Methods
}
