package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.IManaTile;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

import static com.hollingsworth.arsnouveau.api.NbtTags.MANA_TAG;
import static com.hollingsworth.arsnouveau.api.NbtTags.MAX_MANA_TAG;

public abstract class AbstractManaTile extends AnimatedTile  implements IManaTile {
    private int mana = 0;
    private int maxMana = 0;
    public AbstractManaTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void read(CompoundNBT tag) {
        mana = tag.getInt(MANA_TAG);
        maxMana = tag.getInt(MAX_MANA_TAG);
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putInt(MANA_TAG, mana);
        tag.putInt(MAX_MANA_TAG, maxMana);
        return super.write(tag);
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getNbtCompound());
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
        if(world != null && this.pos != null){
            world.notifyBlockUpdate(this.pos, world.getBlockState(pos),  world.getBlockState(pos), 2);
            return true;
        }
        return false;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    public boolean canAcceptMana(){ return this.getCurrentMana() < this.getMaxMana(); }

    public void transferMana(IManaTile from, IManaTile to){
        if(from.getCurrentMana() >= from.getTransferRate()){
            from.removeMana(from.getTransferRate());
            to.addMana(from.getTransferRate());
            update();
        }
    }
}
