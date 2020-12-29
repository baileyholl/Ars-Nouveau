package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ManaBloomCrop;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class ManaCondenserTile extends AbstractManaTile {
    public boolean isDisabled = false;
    public ManaCondenserTile() {
        super(BlockRegistry.MANA_CONDENSER_TILE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getMaxMana() {
        return 1000;
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
    public int getCurrentMana() {
        return super.getCurrentMana();
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
        if(isDisabled)
            return;
        if(BlockUtil.distanceFrom(pos, event.getPos()) <= 15) {
            int mana = 50;
            if(world.getBlockState(event.getPos()).getBlock() instanceof ManaBloomCrop) {
                mana += 25;
            }
            this.addMana(mana);
            ParticleUtil.spawnFollowProjectile(world, event.getPos(), pos);
        }
    }

    @SubscribeEvent
    public void babySpawnEvent(BabyEntitySpawnEvent event) {
        if(isDisabled || event.getChild() == null)
            return;

        if(BlockUtil.distanceFrom(pos, event.getParentA().getPosition()) <= 15) {
            this.addMana(1000);
            ParticleUtil.spawnFollowProjectile(world, event.getParentA().getPosition(), pos);
        }
    }

    @SubscribeEvent
    public void livingDeath(LivingDeathEvent e) {
        if(e.getEntityLiving().world.isRemote || isDisabled || e.getEntity() instanceof IDispellable)
            return;

        if(BlockUtil.distanceFrom(pos, e.getEntity().getPosition()) <= 15) {
            ParticleUtil.spawnFollowProjectile(world, e.getEntity().getPosition(), pos);
            this.addMana(200);

        }
    }

    @Override
    public int getTransferRate() {
        return 500;
    }
}
