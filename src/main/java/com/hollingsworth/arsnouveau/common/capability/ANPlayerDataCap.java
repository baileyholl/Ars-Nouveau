package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;

import java.util.*;
import java.util.stream.Collectors;

public class ANPlayerDataCap implements IPlayerCap{

    public Set<AbstractSpellPart> glyphs = new HashSet<>();

    public Set<AbstractFamiliarHolder> familiars = new HashSet<>();

    public ANPlayerDataCap(){}

    @Override
    public Collection<AbstractSpellPart> getKnownGlyphs() {
        return glyphs;
    }

    @Override
    public boolean unlockGlyph(AbstractSpellPart spellPart) {
        return glyphs.add(spellPart);
    }

    @Override
    public boolean knowsGlyph(AbstractSpellPart spellPart) {
        return glyphs.contains(spellPart);
    }

    @Override
    public boolean unlockFamiliar(AbstractFamiliarHolder holderID) {
        return familiars.add(holderID);
    }

    @Override
    public boolean ownsFamiliar(AbstractFamiliarHolder holderID) {
        return familiars.contains(holderID);
    }

    @Override
    public Collection<AbstractFamiliarHolder> getUnlockedFamiliars() {
        return familiars;
    }

    @Override
    public void setUnlockedFamiliars(Collection<AbstractFamiliarHolder> familiars) {
        this.familiars = new HashSet<>(familiars);
    }

    @Override
    public boolean removeFamiliar(AbstractFamiliarHolder holderID) {
        return this.familiars.remove(holderID);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        NBTUtil.writeStrings(tag, "glyph_", glyphs.stream().map(s -> s.tag).collect(Collectors.toList()));
        NBTUtil.writeStrings(tag, "familiar_", familiars.stream().map(s -> s.id).collect(Collectors.toList()));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        glyphs.addAll(NBTUtil.readStrings(nbt, "glyph_").stream()
                .map(s -> ArsNouveauAPI.getInstance().getSpellpartMap().get(s)).collect(Collectors.toList()));
        familiars.addAll(NBTUtil.readStrings(nbt, "familiar_").stream()
                .map(s -> ArsNouveauAPI.getInstance().getFamiliarHolderMap().get(s)).collect(Collectors.toList()));
    }

    public static ANPlayerDataCap deserialize(CompoundTag tag){
        ANPlayerDataCap cap = new ANPlayerDataCap();
        cap.deserializeNBT(tag);
        return cap;
    }
}
