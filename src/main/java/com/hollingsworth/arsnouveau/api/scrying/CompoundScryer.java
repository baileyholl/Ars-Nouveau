package com.hollingsworth.arsnouveau.api.scrying;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompoundScryer implements IScryer {
    public static final CompoundScryer INSTANCE = new CompoundScryer();

    List<IScryer> scryerList = new ArrayList<>();

    public CompoundScryer(IScryer... scryerList) {
        Collections.addAll(this.scryerList, scryerList);
    }

    @Override
    public boolean shouldRevealBlock(BlockState state, BlockPos p, Player player) {
        return scryerList.stream().anyMatch(scryer -> scryer.shouldRevealBlock(state, p, player));
    }

    @Override
    public IScryer fromTag(CompoundTag tag) {
        CompoundScryer compoundScryer = new CompoundScryer();
        int count = tag.getIntOr("scryer_count", 0);
        for (int i = 0; i < count; i++) {
            // 1.21.11: CompoundTag.getCompound() returns Optional<CompoundTag>
            CompoundTag scryerTag = tag.getCompoundOrEmpty("scryer_" + i);
            String id = scryerTag.getStringOr("id", "");
            IScryer scryer = ArsNouveauAPI.getInstance().getScryer(Identifier.tryParse(id));
            if (scryer != null) {
                compoundScryer.scryerList.add(scryer.fromTag(scryerTag));
            }
        }
        return compoundScryer;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("scryer_count", scryerList.size());
        for (int i = 0; i < scryerList.size(); i++) {
            tag.put("scryer_" + i, scryerList.get(i).toTag(new CompoundTag()));
        }
        return IScryer.super.toTag(tag);
    }

    @Override
    public Identifier getRegistryName() {
        return ArsNouveau.prefix("compound_scryer");
    }
}
