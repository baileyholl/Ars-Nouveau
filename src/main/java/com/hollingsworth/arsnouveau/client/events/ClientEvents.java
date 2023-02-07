package com.hollingsworth.arsnouveau.client.events;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.PatchouliTooltipEvent;
import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class ClientEvents {
    @SubscribeEvent
    public static void TooltipEvent(RenderTooltipEvent.Pre e){
        try {
            // Uses patchouli internals, don't crash if they change something :)
            PatchouliTooltipEvent.onTooltip(e.getPoseStack(), e.getItemStack(), e.getX(), e.getY());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void highlightBlockEvent(RenderHighlightEvent.Block e){
        Level level = Minecraft.getInstance().level;
        if(level != null && level.getBlockEntity(e.getTarget().getBlockPos()) instanceof SkyBlockTile skyTile){
            if(!skyTile.showFacade()){
                e.setCanceled(true);
            }
        }
    }
}
