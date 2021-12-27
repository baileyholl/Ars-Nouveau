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

    public Set<FamiliarData> familiars = new HashSet<>();

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
        return familiars.add(new FamiliarData(holderID.id));
    }

    @Override
    public boolean ownsFamiliar(AbstractFamiliarHolder holderID) {
        return familiars.stream().anyMatch(f -> f.familiarHolder.getId().equals(holderID.id));
    }

    @Override
    public Collection<FamiliarData> getUnlockedFamiliars() {
        return familiars;
    }

    @Override
    public FamiliarData getFamiliarData(String id) {
        return this.familiars.stream().filter(f -> f.familiarHolder.id.equals(id)).findFirst().orElse(null);
    }

    @Override
    public void setUnlockedFamiliars(Collection<FamiliarData> familiars) {
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
        for(FamiliarData f : familiars){
            tag.put("familiar_" + f.familiarHolder.id, f.toTag());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        glyphs.addAll(NBTUtil.readStrings(nbt, "glyph_").stream()
                .map(s -> ArsNouveauAPI.getInstance().getSpellpartMap().get(s)).collect(Collectors.toList()));
        for(String s : nbt.getAllKeys()){
            if(s.contains("familiar_")){
                familiars.add(new FamiliarData(nbt.getCompound(s)));
            }
        }
    }

    public static ANPlayerDataCap deserialize(CompoundTag tag){
        ANPlayerDataCap cap = new ANPlayerDataCap();
        cap.deserializeNBT(tag);
        return cap;
    }
}
