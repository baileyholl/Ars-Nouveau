package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ArcaneRelayTile extends AbstractManaTile implements ITooltipProvider, IWandable {

    public ArcaneRelayTile() {
        super(BlockRegistry.ARCANE_RELAY_TILE);
    }

    public ArcaneRelayTile(TileEntityType<?> type){
        super(type);
    }

    private BlockPos toPos;
    private BlockPos fromPos;

    public boolean closeEnough(BlockPos pos, int distance){
        return BlockUtil.distanceFrom(pos, this.pos) <= distance;
    }

    public boolean setTakeFrom(BlockPos pos){
        if(BlockUtil.distanceFrom(pos, this.pos) > 10){
            return false;
        }
        this.fromPos = pos;
        update();
        return true;
    }

    public boolean setSendTo(BlockPos pos ){
        if(BlockUtil.distanceFrom(pos, this.pos) > 10){
            return false;
        }
        this.toPos = pos;
        update();
        return true;
    }

    public void clearPos(){
        this.toPos = null;
        this.fromPos = null;
        update();
    }

    @Override
    public int getTransferRate() {
        return 100;
    }

    @Override
    public int getMaxMana() {
        return 200;
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, PlayerEntity playerEntity) {
        if(storedPos == null || world.isRemote)
            return;
        // Let relays take from us, no action needed.

        this.setSendTo(storedPos.toImmutable());

        playerEntity.sendMessage(new StringTextComponent("Relay set to send to " + DominionWand.getPosString(storedPos)));
        ParticleUtil.beam(storedPos,pos, (ServerWorld) world);


    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, PlayerEntity playerEntity) {
        if(storedPos == null)
            return;
        if(world.getTileEntity(storedPos) instanceof ArcaneRelayTile)
            return;
        this.setTakeFrom(storedPos.toImmutable());
        playerEntity.sendMessage(new StringTextComponent("Relay set to take from " + DominionWand.getPosString(storedPos)));
    }

    @Override
    public void tick() {
        if(world.isRemote){
            return;
        }

        if( world.getGameTime() % 20 != 0 ||toPos == null)
            return;

        if(fromPos != null){
            // Block has been removed
            if(!(world.getTileEntity(fromPos) instanceof AbstractManaTile)){
                fromPos = null;
                world.notifyBlockUpdate(this.pos, world.getBlockState(pos),  world.getBlockState(pos), 2);
                return;
            }else if(world.getTileEntity(fromPos) instanceof AbstractManaTile){
                // Transfer mana fromPos to this
                AbstractManaTile fromTile = (AbstractManaTile) world.getTileEntity(fromPos);
                if(fromTile.getCurrentMana() >= this.getTransferRate() && this.getCurrentMana() + this.getTransferRate() <= this.getMaxMana()) {
                    fromTile.removeMana(this.getTransferRate());
                    this.addMana(this.getTransferRate());
                    EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(world, fromPos, pos);
                    world.addEntity(aoeProjectile);
                }
            }
        }
        if(!(world.getTileEntity(toPos) instanceof AbstractManaTile)){
            toPos = null;
            update();
            return;
        }
        AbstractManaTile toTile = (AbstractManaTile) this.world.getTileEntity(toPos);
        if(this.getCurrentMana() >= this.getTransferRate() && toTile.getCurrentMana() + this.getTransferRate() <= toTile.getMaxMana()){
            this.removeMana(this.getTransferRate());
            toTile.addMana(this.getTransferRate());
            EntityFollowProjectile aoeProjectile = new EntityFollowProjectile(world, pos, toPos);
            world.addEntity(aoeProjectile);
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        if(NBTUtil.hasBlockPos(tag, "to")){
            this.toPos = NBTUtil.getBlockPos(tag, "to");
        }
        if(NBTUtil.hasBlockPos(tag, "from")){
            this.fromPos = NBTUtil.getBlockPos(tag, "from");
        }
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        if(toPos != null)
            NBTUtil.storeBlockPos(tag, "to", toPos);
        if(fromPos != null)
            NBTUtil.storeBlockPos(tag, "from", fromPos);
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
    public List<String> getTooltip() {
        List<String> list = new ArrayList<>();
        if(toPos == null){
            list.add(new TranslationTextComponent("ars_nouveau.relay.no_to").getString());
        }else{
            list.add(new TranslationTextComponent("ars_nouveau.relay.one_to", 1).getString());
        }
        if(fromPos == null){
            list.add(new TranslationTextComponent("ars_nouveau.relay.no_from").getString());
        }else{
            list.add(new TranslationTextComponent("ars_nouveau.relay.one_from", 1).getString());
        }
        return list;
    }
}
