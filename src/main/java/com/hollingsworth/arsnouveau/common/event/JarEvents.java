package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.mixin.jar.MobAccessorMixin;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class JarEvents {

    @SubscribeEvent
    public static void onNoteblock(NoteBlockEvent.Play e) {
        if(e.getLevel().isClientSide())
            return;
        if(e.getLevel().getBlockEntity(e.getPos().below()) instanceof MobJarTile mobJarTile){
            LevelAccessor level = e.getLevel();
            RandomSource random = level.getRandom();
            if(mobJarTile.getEntity() instanceof MobAccessorMixin mob){
                SoundEvent soundEvent = mob.callGetAmbientSound();
                if(soundEvent == null)
                    return;
                e.getLevel().playSound(null, e.getPos(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
                e.setCanceled(true);
            }else if(mobJarTile.getEntity() instanceof LightningBolt bolt){
                e.getLevel().playSound(null, e.getPos(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 10000.0F, 0.8F + random.nextFloat() * 0.2F);
                e.getLevel().playSound(null, e.getPos(),  SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 2.0F,random.nextFloat() * 0.2F);
            }
        }
    }
}
