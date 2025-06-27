package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.mixin.jar.MobAccessorMixin;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.level.NoteBlockEvent;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class JarEvents {

    @SubscribeEvent
    public static void onNoteblock(NoteBlockEvent.Play e) {
        if (e.getLevel().isClientSide())
            return;
        if (e.getLevel().getBlockEntity(e.getPos().below()) instanceof MobJarTile mobJarTile) {
            LevelAccessor level = e.getLevel();
            RandomSource random = level.getRandom();
            if (mobJarTile.getEntity() instanceof MobAccessorMixin mob) {
                SoundEvent soundEvent = mob.callGetAmbientSound();
                if (soundEvent == null)
                    return;
                e.getLevel().playSound(null, e.getPos(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
                e.setCanceled(true);
            } else if (mobJarTile.getEntity() instanceof LightningBolt bolt) {
                e.getLevel().playSound(null, e.getPos(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 10000.0F, 0.8F + random.nextFloat() * 0.2F);
                e.getLevel().playSound(null, e.getPos(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 2.0F, random.nextFloat() * 0.2F);
            }
        }
    }

    @SubscribeEvent
    public static void onRide(EntityMountEvent mountEvent) {
        if (mountEvent.isDismounting()) return;
        if (mountEvent.getLevel().isClientSide) return;
        if (mountEvent.getEntityMounting() instanceof Player) {
            Entity beingMounted = mountEvent.getEntityBeingMounted();
            if (mountEvent.getLevel().isLoaded(beingMounted.getOnPos()) && mountEvent.getLevel().getBlockEntity(beingMounted.getOnPos()) instanceof MobJarTile) {
                mountEvent.setCanceled(true);
            }
        }
    }
}
