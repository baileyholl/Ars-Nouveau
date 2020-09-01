package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.particle.engine.ParticleEngine;
import com.hollingsworth.arsnouveau.client.particle.engine.TimedBeam;
import com.hollingsworth.arsnouveau.common.block.ArcaneRelay;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.ManaCondenserBlock;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.LazyValue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class ArcaneRelayTile extends AbstractManaTile{

    public ArcaneRelayTile() {
        super(BlockRegistry.ARCANE_RELAY_TILE);
    }

    BlockPos toPos;
    BlockPos fromPos;


    public boolean setTakeFrom(BlockPos pos){
        if(BlockUtil.distanceFrom(pos, this.pos) > 10){
            return false;
        }
        this.fromPos = pos;
        return true;
    }

    public boolean setSendTo(BlockPos pos ){
        if(BlockUtil.distanceFrom(pos, this.pos) > 10){
            return false;
        }
        this.toPos = pos;
        return true;
    }

    public void clearPos(){
        this.toPos = null;
        this.fromPos = null;
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
    public void tick() {
        if(world.isRemote)
            return;

        if(world.getGameTime() % 20 != 0 || toPos == null)
            return;

        if(fromPos != null){
            // Block has been removed
            if(!(world.getTileEntity(fromPos) instanceof AbstractManaTile)){
                fromPos = null;
                return;
            }else if(world.getTileEntity(fromPos) instanceof AbstractManaTile){
                // Transfer mana fromPos to this
                AbstractManaTile fromTile = (AbstractManaTile) world.getTileEntity(fromPos);
                if(fromTile.getCurrentMana() >= this.getTransferRate() && this.getCurrentMana() + this.getTransferRate() <= this.getMaxMana()){
                    fromTile.removeMana(this.getTransferRate());
                    this.addMana(this.getTransferRate());
//                    ParticleUtil.beam(fromPos, pos, world);
                    if(world instanceof ServerWorld)
                        ParticleEngine.getInstance().addEffect(new TimedBeam(pos, fromPos, 5,(ServerWorld)world));
                }
            }
        }
        if(!(world.getTileEntity(toPos) instanceof AbstractManaTile)){
            toPos = null;
            return;
        }
        AbstractManaTile toTile = (AbstractManaTile) this.world.getTileEntity(toPos);
        if(this.getCurrentMana() >= this.getTransferRate() && toTile.getCurrentMana() + this.getTransferRate() <= toTile.getMaxMana()){
            this.removeMana(this.getTransferRate());
            toTile.addMana(this.getTransferRate());
            ParticleEngine.getInstance().addEffect(new TimedBeam(toPos, pos, 3,(ServerWorld)world));
//            ParticleUtil.beam(toPos, pos, world);
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if(NBTUtil.hasBlockPos(tag, "to")){
            this.toPos = NBTUtil.getBlockPos(tag, "to");
        }
        if(NBTUtil.hasBlockPos(tag, "from")){
            this.fromPos = NBTUtil.getBlockPos(tag, "from");
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        if(toPos != null)
            NBTUtil.storeBlockPos(tag, "to", toPos);
        if(fromPos != null)
            NBTUtil.storeBlockPos(tag, "from", fromPos);
        return super.write(tag);
    }
}
