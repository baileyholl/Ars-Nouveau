package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StarbyListBehavior extends StarbyBehavior {

    public List<BlockPos> FROM_LIST = new ArrayList<>();

    public List<BlockPos> TO_LIST = new ArrayList<>();

    public Map<Integer, Direction> FROM_DIRECTION_MAP = new HashMap<>();
    public Map<Integer, Direction> TO_DIRECTION_MAP = new HashMap<>();

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

        for (String key : tag.getAllKeys()) {
            if (key.startsWith("from_direction_")) {
                int hash = Integer.parseInt(key.substring(15));
                FROM_DIRECTION_MAP.put(hash, Direction.from3DDataValue(tag.getInt(key)));
            }
            if (key.startsWith("to_direction_")) {
                int hash = Integer.parseInt(key.substring(13));
                TO_DIRECTION_MAP.put(hash, Direction.from3DDataValue(tag.getInt(key)));
            }
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
        FROM_DIRECTION_MAP = new HashMap<>();
        TO_DIRECTION_MAP = new HashMap<>();
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.cleared"));
        syncTag();
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        for (BlockPos toPos : TO_LIST) {
            list.add(ColorPos.centered(toPos, ParticleColor.TO_HIGHLIGHT));
        }
        for (BlockPos fromPos : FROM_LIST) {
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

    public void addFromPos(BlockPos fromPos, Direction direction) {
        if (!FROM_LIST.contains(fromPos) || FROM_DIRECTION_MAP.get(fromPos.hashCode()) != direction) {
            FROM_LIST.add(fromPos.immutable());
            FROM_DIRECTION_MAP.put(fromPos.hashCode(), direction);
            syncTag();
        }
    }

    public void addToPos(BlockPos toPos, Direction direction) {
        if (!TO_LIST.contains(toPos) || TO_DIRECTION_MAP.get(toPos.hashCode()) != direction) {
            TO_LIST.add(toPos.immutable());
            TO_DIRECTION_MAP.put(toPos.hashCode(), direction);
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
        for (Map.Entry<Integer, Direction> entry : FROM_DIRECTION_MAP.entrySet()) {
            if (entry.getValue() != null) tag.putInt("from_direction_" + entry.getKey(), entry.getValue().ordinal());
        }
        for (Map.Entry<Integer, Direction> entry : TO_DIRECTION_MAP.entrySet()) {
            if (entry.getValue() != null) tag.putInt("to_direction_" + entry.getKey(), entry.getValue().ordinal());
        }
        return super.toTag(tag);
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( "starby_list");
    }
}
