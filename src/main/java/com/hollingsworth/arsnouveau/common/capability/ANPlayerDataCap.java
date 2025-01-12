package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSyncPlayerCap;
import com.hollingsworth.arsnouveau.setup.registry.AttachmentsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

public class ANPlayerDataCap implements IPlayerCap {

    private ANPlayerData playerData;
    LivingEntity entity;
    public ANPlayerDataCap(LivingEntity livingEntity){
        playerData = livingEntity.getData(AttachmentsRegistry.PLAYER_DATA);
        entity = livingEntity;
    }

    @Override
    public Collection<AbstractSpellPart> getKnownGlyphs() {
        return playerData.glyphs;
    }

    @Override
    public void setKnownGlyphs(Collection<AbstractSpellPart> glyphs) {
        playerData.glyphs = new HashSet<>(glyphs);
        entity.setData(AttachmentsRegistry.PLAYER_DATA, playerData);
    }

    @Override
    public boolean unlockGlyph(AbstractSpellPart spellPart) {
        var result = playerData.glyphs.add(spellPart);
        if(result){
            entity.setData(AttachmentsRegistry.PLAYER_DATA, playerData);
        }
        return result;
    }

    @Override
    public boolean knowsGlyph(AbstractSpellPart spellPart) {
        return playerData.glyphs.contains(spellPart);
    }

    @Override
    public boolean unlockFamiliar(AbstractFamiliarHolder holderID) {
        var result = playerData.familiars.add(new FamiliarData(holderID.getRegistryName()));
        if(result){
            entity.setData(AttachmentsRegistry.PLAYER_DATA, playerData);
        }
        return result;
    }

    @Override
    public boolean ownsFamiliar(AbstractFamiliarHolder holderID) {
        return playerData.familiars.stream().anyMatch(f -> f.familiarHolder.getRegistryName().equals(holderID.getRegistryName()));
    }

    @Override
    public Collection<FamiliarData> getUnlockedFamiliars() {
        return playerData.familiars;
    }

    @Override
    @Nullable
    public FamiliarData getFamiliarData(ResourceLocation id) {
        return playerData.familiars.stream().filter(f -> f.familiarHolder.getRegistryName().equals(id)).findFirst().orElse(null);
    }

    @Nullable
    @Override
    public FamiliarData getLastSummonedFamiliar() {
        return playerData.lastSummonedFamiliar == null ? null : getFamiliarData(playerData.lastSummonedFamiliar);
    }

    public void setLastSummonedFamiliar(ResourceLocation lastSummonedFamiliar) {
        playerData.lastSummonedFamiliar = lastSummonedFamiliar;
        entity.setData(AttachmentsRegistry.PLAYER_DATA, playerData);
    }

    @Override
    public void setUnlockedFamiliars(Collection<FamiliarData> familiars) {
        playerData.familiars = new HashSet<>(familiars);
        entity.setData(AttachmentsRegistry.PLAYER_DATA, playerData);
    }

    @Override
    public boolean removeFamiliar(AbstractFamiliarHolder holderID) {
        var result = playerData.familiars.removeIf(f -> f.familiarHolder.getRegistryName().equals(holderID.getRegistryName()));
        if(result){
            entity.setData(AttachmentsRegistry.PLAYER_DATA, playerData);
        }
        return result;
    }

    public void setPlayerData(ANPlayerData data){
        this.playerData = data;
    }

    public void syncToClient(ServerPlayer player){
        CompoundTag tag = this.playerData.serializeNBT(player.registryAccess());
        Networking.sendToPlayerClient(new PacketSyncPlayerCap(tag), player);
    }
}
