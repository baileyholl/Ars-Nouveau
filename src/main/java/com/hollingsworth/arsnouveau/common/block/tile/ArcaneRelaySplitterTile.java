package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

public class ArcaneRelaySplitterTile extends ArcaneRelayTile{

    ArrayList<BlockPos> toList = new ArrayList<>();
    ArrayList<BlockPos> fromList = new ArrayList<>();

    public ArcaneRelaySplitterTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANE_RELAY_SPLITTER_TILE, pos, state);
    }

    public ArcaneRelaySplitterTile(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(type, pos, state);
    }

    @Override
    public boolean setTakeFrom(BlockPos pos) {
        return closeEnough(pos) && fromList.add(pos) && update();
    }

    @Override
    public boolean setSendTo(BlockPos pos) {
        return closeEnough(pos) && toList.add(pos) && update();
    }

    @Override
    public void clearPos() {
        this.toList.clear();
        this.fromList.clear();
        update();
    }

    public void processFromList(){
        if(fromList.isEmpty())
            return;
        ArrayList<BlockPos> stale = new ArrayList<>();
        int ratePer = getTransferRate() / fromList.size();
        for(BlockPos fromPos : fromList){
            if(!(level.getBlockEntity(fromPos) instanceof AbstractManaTile)){
                stale.add(fromPos);
                continue;
            }
            AbstractManaTile fromTile = (AbstractManaTile) level.getBlockEntity(fromPos);
            if(transferMana(fromTile, this, ratePer) > 0){
                createParticles(fromPos, worldPosition);
            }
        }
        for(BlockPos s : stale)
            fromList.remove(s);

    }

    public void createParticles(BlockPos from, BlockPos to){
        ParticleUtil.spawnFollowProjectile(level, from, to);
    }

    public void processToList(){
        if(toList.isEmpty())
            return;
        ArrayList<BlockPos> stale = new ArrayList<>();
        int ratePer = getTransferRate() / toList.size();
        for(BlockPos toPos : toList){
            if(!(level.getBlockEntity(toPos) instanceof AbstractManaTile)){
                stale.add(toPos);
                continue;
            }
            AbstractManaTile toTile = (AbstractManaTile) level.getBlockEntity(toPos);
            int transfer = transferMana(this, toTile, ratePer);
            if(transfer > 0){
                createParticles(worldPosition, toPos);
            }
        }
        for(BlockPos s : stale)
            toList.remove(s);
    }

    @Override
    public void tick() {
        if(level.getGameTime() % 20 != 0 || toList.isEmpty() || level.isClientSide)
            return;

        processFromList();
        processToList();
        update();
    }

    @Override
    public int getTransferRate() {
        return 2500;
    }

    @Override
    public int getMaxMana() {
        return 2500;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fromList = new ArrayList<>();
        toList = new ArrayList<>();
        int counter = 0;

        while(NBTUtil.hasBlockPos(tag, "from_" + counter)){
            BlockPos pos = NBTUtil.getBlockPos(tag, "from_" + counter);
            if(!this.fromList.contains(pos))
                this.fromList.add(pos);
            counter++;
        }

        counter = 0;
        while(NBTUtil.hasBlockPos(tag, "to_" + counter)){
            BlockPos pos = NBTUtil.getBlockPos(tag, "to_" + counter);
            if(!this.toList.contains(pos))
                this.toList.add(NBTUtil.getBlockPos(tag, "to_" + counter));
            counter++;
        }

    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        int counter = 0;
        for(BlockPos p : this.fromList){
            NBTUtil.storeBlockPos(tag, "from_" +counter, p);
            counter++;
        }
        counter = 0;
        for(BlockPos p : this.toList){
            NBTUtil.storeBlockPos(tag, "to_" +counter, p);
            counter ++;
        }
        return super.save(tag);
    }

    @Override
    public List<String> getTooltip() {
        List<String> list = new ArrayList<>();
        if(toList == null || toList.isEmpty()){
            list.add(new TranslatableComponent("ars_nouveau.relay.no_to").getString());
        }else{
            list.add(new TranslatableComponent("ars_nouveau.relay.one_to", toList.size()).getString());
        }
        if(fromList == null || fromList.isEmpty()){
            list.add(new TranslatableComponent("ars_nouveau.relay.no_from").getString());
        }else{
            list.add(new TranslatableComponent("ars_nouveau.relay.one_from", fromList.size()).getString());
        }
        return list;
    }

}
