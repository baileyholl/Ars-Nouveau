package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import com.hollingsworth.arsnouveau.setup.registry.AttachmentsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ManaCap implements IManaCap {

    private ManaData manaData;
    LivingEntity entity;
    public ManaCap(LivingEntity livingEntity) {
        manaData = livingEntity.getData(AttachmentsRegistry.MANA_ATTACHMENT);
        entity = livingEntity;
    }

    @Override
    public double getCurrentMana() {
        return manaData.getMana();
    }

    @Override
    public int getMaxMana() {
        return manaData.getMaxMana();
    }

    @Override
    public void setMaxMana(int maxMana) {
        manaData.setMaxMana(maxMana);
        entity.setData(AttachmentsRegistry.MANA_ATTACHMENT, manaData);
    }

    @Override
    public double setMana(double mana) {
        if (mana > getMaxMana()) {
            this.manaData.setMana(getMaxMana());
        } else if (mana < 0) {
            this.manaData.setMana(0);
        } else {
            this.manaData.setMana(mana);
        }
        entity.setData(AttachmentsRegistry.MANA_ATTACHMENT, manaData);
        return this.getCurrentMana();
    }

    @Override
    public double addMana(double manaToAdd) {
        this.setMana(this.getCurrentMana() + manaToAdd);
        return this.getCurrentMana();
    }

    @Override
    public double removeMana(double manaToRemove) {
        if (manaToRemove < 0)
            manaToRemove = 0;
        this.setMana(this.getCurrentMana() - manaToRemove);
        return this.getCurrentMana();
    }

    @Override
    public int getGlyphBonus() {
        return manaData.getGlyphBonus();
    }

    @Override
    public void setGlyphBonus(int glyphBonus) {
        manaData.setGlyphBonus(glyphBonus);
        entity.setData(AttachmentsRegistry.MANA_ATTACHMENT, manaData);
    }

    @Override
    public int getBookTier() {
        return manaData.getBookTier();
    }

    @Override
    public void setBookTier(int bookTier) {
        manaData.setBookTier(bookTier);
        entity.setData(AttachmentsRegistry.MANA_ATTACHMENT, manaData);
    }

    public float getReserve(){
        return manaData.getReservedMana();
    }

    public void setReserve(float reserve){
        manaData.setReservedMana(reserve);
        entity.setData(AttachmentsRegistry.MANA_ATTACHMENT, manaData);
    }

    public void setManaData(ManaData manaData) {
        this.manaData = manaData;
    }

    public void syncToClient(ServerPlayer player) {
        CompoundTag tag = manaData.serializeNBT(player.registryAccess());
        Networking.sendToPlayerClient(new PacketUpdateMana(tag), player);
    }
}
