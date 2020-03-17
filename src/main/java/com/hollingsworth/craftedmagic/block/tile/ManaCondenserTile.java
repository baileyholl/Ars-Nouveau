package com.hollingsworth.craftedmagic.block.tile;

import com.hollingsworth.craftedmagic.api.util.BlockUtil;
import com.hollingsworth.craftedmagic.block.ManaCondenserBlock;
import com.hollingsworth.craftedmagic.block.BlockRegistry;
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
            if(isDisabled)
                System.out.println("Disabled");
           // world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
            return;
        }


        if(world.getTileEntity(pos.down()) instanceof ManaJarTile ) {
            if(this.getCurrentMana() < getMaxMana()){
                counter += 1;
                if(counter > 8)
                    counter = 1;
                BlockState state = world.getBlockState(pos);
                world.setBlockState(pos, state.with(ManaCondenserBlock.stage, counter), 3);
            }
            ManaJarTile jar = (ManaJarTile) world.getTileEntity(pos.down());
            if(jar.canAcceptMana() && world.getGameTime() % 20 == 0 ) {
                transferMana(this, jar);
            }
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        isDisabled = tag.getBoolean("disabled");
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putBoolean("disabled", isDisabled);
        return super.write(tag);
    }
    @SubscribeEvent
    public void cropGrow(BlockEvent.CropGrowEvent event) {
        if(BlockUtil.distanceFrom(pos, event.getPos()) <= 10)
            this.addMana(50);
    }

    @SubscribeEvent
    public void babySpawnEvent(BabyEntitySpawnEvent event) {
        if(event.getChild() == null)
            return;
        if(BlockUtil.distanceFrom(pos, event.getChild().getPosition()) <= 10)
            this.addMana(300);
    }

    @SubscribeEvent
    public void livingDeath(LivingDeathEvent e) {
        if(BlockUtil.distanceFrom(pos, e.getEntity().getPosition()) <= 10)
            this.addMana(100);
    }

    @Override
    public int getTransferRate() {
        return 20;
    }
}
