package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class ArcaneRelaySplitterTile extends ArcaneRelayTile{

    ArrayList<BlockPos> toList = new ArrayList<>();
    ArrayList<BlockPos> fromList = new ArrayList<>();

    public ArcaneRelaySplitterTile() {
        super(BlockRegistry.ARCANE_RELAY_SPLITTER_TILE);
    }

    @Override
    public boolean setTakeFrom(BlockPos pos) {
        return closeEnough(pos, 10) && fromList.add(pos) && update();
    }

    @Override
    public boolean setSendTo(BlockPos pos) {
        return closeEnough(pos, 10) && toList.add(pos) && update();
    }

    @Override
    public void clearPos() {
//        CompoundNBT tag = getTileData();
//        for(int i = 0; i < this.toList.size(); i++){
//            NBTUtil.removeBlockPos(tag,"to_" + i);
//        }
//        for(int i = 0; i < this.fromList.size(); i++){
//            NBTUtil.removeBlockPos(tag,"from_" + i);
//        }
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
                ParticleUtil.spawnFollowProjectile(level, fromPos, worldPosition);
            }
        }
        for(BlockPos s : stale)
            fromList.remove(s);

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
                ParticleUtil.spawnFollowProjectile(level, worldPosition, toPos);
            }
        }
        for(BlockPos s : stale)
            toList.remove(s);

    }

    @Override
    public void tick() {
        spawnParticles();
        if(level.getGameTime() % 20 != 0 || toList.isEmpty() || level.isClientSide)
            return;

        processFromList();
        processToList();
        update();
    }

    @Override
    public int getTransferRate() {
        return 500;
    }

    @Override
    public int getMaxMana() {
        return 2500;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
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
    public CompoundNBT save(CompoundNBT tag) {
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
            list.add(new TranslationTextComponent("ars_nouveau.relay.no_to").getString());
        }else{
            list.add(new TranslationTextComponent("ars_nouveau.relay.one_to", toList.size()).getString());
        }
        if(fromList == null || fromList.isEmpty()){
            list.add(new TranslationTextComponent("ars_nouveau.relay.no_from").getString());
        }else{
            list.add(new TranslationTextComponent("ars_nouveau.relay.one_from", fromList.size()).getString());
        }
        return list;
    }

}
