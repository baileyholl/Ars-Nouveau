package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ManaBloomCrop;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
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
        if(level.isClientSide || isDisabled) {
            return;
        }
        if(level.getGameTime() % 20 == 0 && level.getBlockEntity(worldPosition.below()) instanceof ManaJarTile ) {
            ManaJarTile jar = (ManaJarTile) level.getBlockEntity(worldPosition.below());
            if(jar != null && jar.canAcceptMana()) {
                transferMana(this, jar);
            }
        }
    }

    @Override
    public int getCurrentMana() {
        return super.getCurrentMana();
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        isDisabled = tag.getBoolean("disabled");
        super.load(state, tag);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putBoolean("disabled", isDisabled);
        return super.save(tag);
    }
    @SubscribeEvent
    public void cropGrow(BlockEvent.CropGrowEvent.Post event) {
        if(isDisabled)
            return;
        if(BlockUtil.distanceFrom(worldPosition, event.getPos()) <= 15) {
            int mana = 50;
            if(event.getWorld().getBlockState(event.getPos()).getBlock() instanceof ManaBloomCrop) {
                mana += 25;
            }
            this.addMana(mana);
            ParticleUtil.spawnFollowProjectile((World) event.getWorld(), event.getPos(), worldPosition);
        }
    }

    @SubscribeEvent
    public void babySpawnEvent(BabyEntitySpawnEvent event) {
        if(isDisabled || event.getChild() == null || level == null)
            return;

        if(BlockUtil.distanceFrom(worldPosition, event.getParentA().blockPosition()) <= 15) {
            this.addMana(1000);
            ParticleUtil.spawnFollowProjectile(event.getChild().level, event.getParentA().blockPosition(), worldPosition);
        }
    }

    @SubscribeEvent
    public void livingDeath(LivingDeathEvent e) {
        if(e.getEntityLiving().level.isClientSide || isDisabled || e.getEntity() instanceof IDispellable || e.getEntity() instanceof ISummon || level == null)
            return;

        if(BlockUtil.distanceFrom(worldPosition, e.getEntity().blockPosition()) <= 15) {
            ParticleUtil.spawnFollowProjectile(e.getEntityLiving().level, e.getEntity().blockPosition(), worldPosition);
            this.addMana(200);

        }
    }

    @Override
    public int getTransferRate() {
        return 500;
    }
}
