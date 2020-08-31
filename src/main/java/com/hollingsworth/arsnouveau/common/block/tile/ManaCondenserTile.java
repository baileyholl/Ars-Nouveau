package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.particle.engine.ParticleEngine;
import com.hollingsworth.arsnouveau.client.particle.engine.TimedBeam;
import com.hollingsworth.arsnouveau.common.block.ManaBloomCrop;
import com.hollingsworth.arsnouveau.common.block.ManaCondenserBlock;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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
    public void cropGrow(BlockEvent.CropGrowEvent.Post event) {

        if(BlockUtil.distanceFrom(pos, event.getPos()) <= 11) {
            int mana = 200;
            if(world.getBlockState(event.getPos()).getBlock() instanceof ManaBloomCrop) {
                mana += 100;
            }
            this.addMana(mana);

            if(world instanceof ServerWorld){
                ParticleEngine.getInstance().addEffect(new TimedBeam(pos, event.getPos(), 5, (ServerWorld) world));
            }
        }
    }

    @SubscribeEvent
    public void babySpawnEvent(BabyEntitySpawnEvent event) {
        if(event.getChild() == null)
            return;
        System.out.println("spawning");
        if(BlockUtil.distanceFrom(pos, event.getChild().getPosition()) <= 10)
            this.addMana(100);
    }

    @SubscribeEvent
    public void livingDeath(LivingDeathEvent e) {

        if(BlockUtil.distanceFrom(pos, e.getEntity().getPosition()) <= 10) {
            if(world instanceof ServerWorld){
                ParticleEngine.getInstance().addEffect(new TimedBeam(pos, e.getEntity().getPosition(), 1, (ServerWorld) world));
            }
            this.addMana(150);

        }
    }

    @Override
    public int getTransferRate() {
        return 20;
    }
}
