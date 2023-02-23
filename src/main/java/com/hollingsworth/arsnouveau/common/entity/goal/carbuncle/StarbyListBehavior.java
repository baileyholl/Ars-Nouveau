package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.util.ColorPos;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class StarbyListBehavior extends StarbyBehavior{

    public List<BlockPos> FROM_LIST = new ArrayList<>();

    public List<BlockPos> TO_LIST = new ArrayList<>();

    public StarbyListBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        int counter = 0;

        while (NBTUtil.hasBlockPos(tag, "from_" + counter)) {
            BlockPos pos = NBTUtil.getBlockPos(tag, "from_" + counter);
            if (!this.FROM_LIST.contains(pos))
                this.FROM_LIST.add(pos);
            counter++;
        }

        counter = 0;
        while (NBTUtil.hasBlockPos(tag, "to_" + counter)) {
            BlockPos pos = NBTUtil.getBlockPos(tag, "to_" + counter);
            if (!this.TO_LIST.contains(pos))
                this.TO_LIST.add(pos);
            counter++;
        }
    }

    @Override
    public boolean clearOrRemove() {
        return FROM_LIST.isEmpty() && TO_LIST.isEmpty();
    }

    @Override
    public void onWanded(Player playerEntity) {
        super.onWanded(playerEntity);
        FROM_LIST = new ArrayList<>();
        TO_LIST = new ArrayList<>();
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.cleared"));
        syncTag();
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        for(BlockPos toPos : TO_LIST){
            list.add(ColorPos.centered(toPos, ParticleColor.TO_HIGHLIGHT));
        }
        for(BlockPos fromPos : FROM_LIST){
            list.add(ColorPos.centered(fromPos, ParticleColor.FROM_HIGHLIGHT));
        }
        return list;
    }

    public void addFromPos(BlockPos fromPos) {
        if (!FROM_LIST.contains(fromPos)) {
            FROM_LIST.add(fromPos.immutable());
            syncTag();
        }
    }

    public void addToPos(BlockPos toPos) {
        if (!TO_LIST.contains(toPos)) {
            TO_LIST.add(toPos.immutable());
            syncTag();
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        int counter = 0;
        for (BlockPos p : FROM_LIST) {
            NBTUtil.storeBlockPos(tag, "from_" + counter, p);
            counter++;
        }
        counter = 0;
        for (BlockPos p : TO_LIST) {
            NBTUtil.storeBlockPos(tag, "to_" + counter, p);
            counter++;
        }
        return super.toTag(tag);
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, "starby_list");
    }
}
