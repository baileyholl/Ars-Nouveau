package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.ManaBloomCrop;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class ManaCondenserTile extends AbstractManaTile implements ITickableTileEntity {
    public boolean isDisabled = false;
    public ManaCondenserTile() {
        super(BlockRegistry.MANA_CONDENSER_TILE);
        MinecraftForge.EVENT_BUS.register(this);
        setMaxMana(500);
    }


    @Override
    public void tick() {
        if(world.isRemote || isDisabled) {
            return;
        }
        if(world.getTileEntity(pos.down()) instanceof ManaJarTile ) {
            ManaJarTile jar = (ManaJarTile) world.getTileEntity(pos.down());
            if(jar.canAcceptMana() && world.getGameTime() % 20 == 0 ) {
                transferMana(this, jar);
            }
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        isDisabled = tag.getBoolean("disabled");
        super.read(state, tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putBoolean("disabled", isDisabled);
        return super.write(tag);
    }
    @SubscribeEvent
    public void cropGrow(BlockEvent.CropGrowEvent.Post event) {
        if(!world.isRemote && world.getTileEntity(pos) == null){
            MinecraftForge.EVENT_BUS.unregister(this);
            return;
        }
        if(isDisabled)
            return;
        if(BlockUtil.distanceFrom(pos, event.getPos()) <= 15) {
            int mana = 200;
            if(world.getBlockState(event.getPos()).getBlock() instanceof ManaBloomCrop) {
                mana += 100;
            }
            this.addMana(mana);

            Networking.sendToNearby(world, pos, new PacketANEffect(PacketANEffect.EffectType.TIMED_GLOW, pos.getX(), pos.getY(), pos.getZ(), event.getPos().getX(),
                    event.getPos().getY(), event.getPos().getZ(),5));
        }
    }

    @SubscribeEvent
    public void babySpawnEvent(BabyEntitySpawnEvent event) {
        if(!world.isRemote && world.getTileEntity(pos) == null){
            MinecraftForge.EVENT_BUS.unregister(this);
            return;
        }
        if(isDisabled)
            return;

        if(event.getChild() == null)
            return;
        if(BlockUtil.distanceFrom(pos, event.getChild().getPosition()) <= 10)
            this.addMana(100);
    }

    @SubscribeEvent
    public void livingDeath(LivingDeathEvent e) {
        if(!world.isRemote && world.getTileEntity(pos) == null){
            MinecraftForge.EVENT_BUS.unregister(this);
            return;
        }
        if(e.getEntityLiving().world.isRemote || isDisabled)
            return;


        if(e.getEntity() instanceof EntityWhelp)
            return;
        if(BlockUtil.distanceFrom(pos, e.getEntity().getPosition()) <= 15) {
            Networking.sendToNearby(world, pos, new PacketANEffect(PacketANEffect.EffectType.TIMED_GLOW, pos.getX(), pos.getY(), pos.getZ(), e.getEntity().getPosition().getX(),
                    e.getEntity().getPosition().getY(), e.getEntity().getPosition().getZ(),10));
            this.addMana(150);

        }
    }

    @Override
    public int getTransferRate() {
        return 20;
    }
}
