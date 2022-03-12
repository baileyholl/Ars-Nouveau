package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class CuriosEventHandler {

    @SubscribeEvent
    public static void playerOnTick(TickEvent.PlayerTickEvent event) {
        if(event.phase != TickEvent.Phase.END)
            return;
        CuriosUtil.getAllWornItems(event.player).ifPresent(e ->{
            for(int i = 0; i < e.getSlots(); i++){
                Item item = e.getStackInSlot(i).getItem();
                if(item instanceof ArsNouveauCurio)
                    ((ArsNouveauCurio) item).wearableTick(event.player);

            }

        });
    }
}
