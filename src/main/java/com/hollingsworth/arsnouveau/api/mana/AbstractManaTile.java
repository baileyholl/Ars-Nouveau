package com.hollingsworth.arsnouveau.api.mana;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

import static com.hollingsworth.arsnouveau.api.NbtTags.MANA_TAG;
import static com.hollingsworth.arsnouveau.api.NbtTags.MAX_MANA_TAG;

public abstract class AbstractManaTile extends TileEntity implements IManaTile, ITickableTileEntity {
    private int mana = 0;
    private int maxMana = 0;
    public AbstractManaTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        mana = tag.getInt(MANA_TAG);
        maxMana = tag.getInt(MAX_MANA_TAG);
        super.load(state, tag);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putInt(MANA_TAG, getCurrentMana());
        tag.putInt(MAX_MANA_TAG, getMaxMana());
        return super.save(tag);
    }

    @Override
    public int setMana(int mana) {
        this.mana = mana;
        if(this.mana > this.getMaxMana())
            this.mana = this.getMaxMana();
        if(this.mana < 0)
            this.mana = 0;
        update();
        return this.mana;
    }

    @Override
    public int addMana(int manaToAdd) {
        return this.setMana(this.getCurrentMana() + manaToAdd);
    }

    @Override
    public int getCurrentMana() {
        return this.mana;
    }

    @Override
    public int removeMana(int manaToRemove) {
        this.setMana(this.getCurrentMana() - manaToRemove);
        update();
        return this.getCurrentMana();
    }

    @Override
    public void setMaxMana(int max) {
        this.maxMana = max;
        update();
    }

    public boolean update(){
        if(this.worldPosition != null && this.level != null){
            level.sendBlockUpdated(this.worldPosition, level.getBlockState(worldPosition),  level.getBlockState(worldPosition), 2);
            return true;
        }
        return false;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    public boolean canAcceptMana(){ return this.getCurrentMana() < this.getMaxMana(); }

    public int manaCanAccept(IManaTile tile){return tile.getMaxMana() - tile.getCurrentMana();}

    public int transferMana(IManaTile from, IManaTile to){
        int transferRate = getTransferRate(from, to);
        from.removeMana(transferRate);
        to.addMana(transferRate);
        return transferRate;
    }

    public int getTransferRate(IManaTile from, IManaTile to){
        return Math.min(Math.min(from.getTransferRate(), from.getCurrentMana()), to.getMaxMana() - to.getCurrentMana());
    }

    public int transferMana(IManaTile from, IManaTile to, int fromTransferRate){
        int transferRate = getTransferRate(from, to, fromTransferRate);
        from.removeMana(transferRate);
        to.addMana(transferRate);
        return transferRate;
    }

    public int getTransferRate(IManaTile from, IManaTile to, int fromTransferRate){
        return Math.min(Math.min(fromTransferRate, from.getCurrentMana()), to.getMaxMana() - to.getCurrentMana());
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(level.getBlockState(worldPosition),pkt.getTag());
    }
}

