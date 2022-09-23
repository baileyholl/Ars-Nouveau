package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.mixin.jar.MobAccessorMixin;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
            if(mobJarTile.getEntity() instanceof MobAccessorMixin mob){
                SoundEvent soundEvent = mob.callGetAmbientSound();
                if(soundEvent == null)
                    return;
                e.getLevel().playSound(null, e.getPos(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
                e.setCanceled(true);
            }
        }
    }
}
