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

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class MachineReference implements INBTSerializable<CompoundTag> {
    //extract is a potentially separate output block entity
    public GlobalBlockPos extractGlobalPos;
    public ResourceLocation extractRegistryName;
    public boolean extractChunkLoaded;
    public Direction extractFacing = Direction.DOWN;
    //insert is the managed machine itself
    public GlobalBlockPos insertGlobalPos;
    public ResourceLocation insertRegistryName;
    public boolean insertChunkLoaded;
    public Direction insertFacing = Direction.UP;
    public String customName = null;
    protected ItemStack cachedExtractItemStack = ItemStack.EMPTY;
    protected Item cachedExtractItem = null;
    protected ItemStack cachedInsertItemStack = ItemStack.EMPTY;
    protected Item cachedInsertItem = null;

    public MachineReference() {

    }

    public MachineReference(GlobalBlockPos extractGlobalPos, ResourceLocation extractRegistryName, boolean extractChunkLoaded,
                            GlobalBlockPos insertGlobalPos, ResourceLocation insertRegistryName, boolean insertChunkLoaded) {
        this.extractGlobalPos = extractGlobalPos;
        this.extractRegistryName = extractRegistryName;
        this.extractChunkLoaded = extractChunkLoaded;
        this.insertGlobalPos = insertGlobalPos;
        this.insertRegistryName = insertRegistryName;
        this.insertChunkLoaded = insertChunkLoaded;
    }

    /**
     * @param extractBlockEntity the block entity to extract from
     * @param insertBlockEntity  the block entity to insert into, this is the managed machine
     * @return
     */
    public static MachineReference from(BlockEntity extractBlockEntity, BlockEntity insertBlockEntity) {
        var extractPos = GlobalBlockPos.from(extractBlockEntity);
        BlockState extractState = extractBlockEntity.getLevel().getBlockState(extractPos.getPos());
        ItemStack extractItem = extractState.getBlock().getCloneItemStack(extractBlockEntity.getLevel(), extractPos.getPos(), extractState);
        boolean extractIsLoaded = extractBlockEntity.getLevel().isLoaded(extractPos.getPos());


        var insertPos = GlobalBlockPos.from(insertBlockEntity);
        BlockState insertState = extractBlockEntity.getLevel().getBlockState(insertPos.getPos());
        ItemStack insertItem = insertState.getBlock().getCloneItemStack(extractBlockEntity.getLevel(), insertPos.getPos(), insertState);
        boolean insertIsLoaded = insertBlockEntity.getLevel().isLoaded(insertPos.getPos());

        return new MachineReference(extractPos,
                ForgeRegistries.ITEMS.getKey(extractItem.getItem()), extractIsLoaded, insertPos,
                ForgeRegistries.ITEMS.getKey(insertItem.getItem()), insertIsLoaded);
    }

    public static MachineReference from(CompoundTag compound) {
        MachineReference reference = new MachineReference();
        reference.deserializeNBT(compound);
        return reference;
    }

    public static MachineReference from(FriendlyByteBuf buf) {
        MachineReference reference = new MachineReference();
        reference.decode(buf);
        return reference;
    }

    public Item getExtractItem() {
        if (this.cachedExtractItem == null)
            this.cachedExtractItem = ForgeRegistries.ITEMS.getValue(this.extractRegistryName);
        return this.cachedExtractItem;
    }

    public ItemStack getExtractItemStack() {
        if (this.cachedExtractItemStack.isEmpty())
            this.cachedExtractItemStack = new ItemStack(this.getExtractItem());
        return this.cachedExtractItemStack;
    }

    public Item getInsertItem() {
        if (this.cachedInsertItem == null)
            this.cachedInsertItem = ForgeRegistries.ITEMS.getValue(this.insertRegistryName);
        return this.cachedInsertItem;
    }

    public ItemStack getInsertItemStack() {
        if (this.cachedInsertItemStack.isEmpty())
            this.cachedInsertItemStack = new ItemStack(this.getInsertItem());
        return this.cachedInsertItemStack;
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.write(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.read(nbt);
    }

    public CompoundTag write(CompoundTag compound) {
        if (this.insertGlobalPos != null)
            compound.put("insertGlobalPos", this.insertGlobalPos.serializeNBT());
        if (this.insertRegistryName != null)
            compound.putString("insertRegistryName", this.insertRegistryName.toString());
        compound.putBoolean("insertChunkLoaded", this.insertChunkLoaded);
        compound.putByte("insertFacing", (byte) this.insertFacing.get3DDataValue());


        if (this.extractGlobalPos != null)
            compound.put("extractGlobalPos", this.extractGlobalPos.serializeNBT());
        if (this.extractRegistryName != null)
            compound.putString("extractRegistryName", this.extractRegistryName.toString());
        compound.putBoolean("extractChunkLoaded", this.extractChunkLoaded);
        compound.putByte("extractFacing", (byte) this.extractFacing.get3DDataValue());

        if (!StringUtils.isBlank(this.customName))
            compound.putString("customName", this.customName);

        return compound;
    }

    public void read(CompoundTag compound) {

        //recover nbt saved in old versions
        if (compound.contains("globalPos")) {
            this.insertGlobalPos = GlobalBlockPos.from(compound.getCompound("globalPos"));
            this.extractGlobalPos = GlobalBlockPos.from(compound.getCompound("globalPos"));
        }
        if (compound.contains("registryName")) {
            this.insertRegistryName = new ResourceLocation(compound.getString("registryName"));
            this.extractRegistryName = new ResourceLocation(compound.getString("registryName"));
        }
        this.insertChunkLoaded = compound.getBoolean("isChunkLoaded");
        this.extractChunkLoaded = compound.getBoolean("isChunkLoaded");


        //then load actual nbt
        if (compound.contains("insertGlobalPos"))
            this.insertGlobalPos = GlobalBlockPos.from(compound.getCompound("insertGlobalPos"));
        if (compound.contains("insertRegistryName"))
            this.insertRegistryName = new ResourceLocation(compound.getString("insertRegistryName"));
        if (compound.contains("insertChunkLoaded"))
            this.insertChunkLoaded = compound.getBoolean("insertChunkLoaded");
        this.insertFacing = Direction.from3DDataValue(compound.getInt("insertFacing"));


        if (compound.contains("extractGlobalPos"))
            this.extractGlobalPos = GlobalBlockPos.from(compound.getCompound("extractGlobalPos"));
        if (compound.contains("extractRegistryName"))
            this.extractRegistryName = new ResourceLocation(compound.getString("extractRegistryName"));
        if (compound.contains("extractChunkLoaded"))
            this.extractChunkLoaded = compound.getBoolean("extractChunkLoaded");
        this.extractFacing = Direction.from3DDataValue(compound.getInt("extractFacing"));

        if (compound.contains("customName"))
            this.customName = compound.getString("customName");
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.write(new CompoundTag()));
    }

    public void decode(FriendlyByteBuf buf) {
        this.deserializeNBT(buf.readNbt());
    }

    public BlockEntity getExtractBlockEntity(Level level) {
        return BlockEntityUtil.get(level, this.extractGlobalPos);
    }

    public BlockEntity getInsertBlockEntity(Level level) {
        return BlockEntityUtil.get(level, this.insertGlobalPos);
    }

    public boolean isValidFor(Level level) {
        return this.getExtractBlockEntity(level) != null && this.getInsertBlockEntity(level) != null;
    }
}
