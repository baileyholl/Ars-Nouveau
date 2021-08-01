package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.SourcelinkEventQueue;
import com.hollingsworth.arsnouveau.common.block.ManaBloomCrop;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class AgronomicSourcelinkTile extends SourcelinkTile {

    public AgronomicSourcelinkTile() {
        super(BlockRegistry.MANA_CONDENSER_TILE);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public int getMaxMana() {
        return 1000;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public int getCurrentMana() {
        return super.getCurrentMana();
    }

    @SubscribeEvent
    public static void cropGrow(BlockEvent.CropGrowEvent.Post event) {
        int mana = 50;
        if(event.getWorld().getBlockState(event.getPos()).getBlock() instanceof ManaBloomCrop) {
            mana += 25;
        }
        SourcelinkEventQueue.addManaEvent(event.getWorld(), AgronomicSourcelinkTile.class, mana, event, event.getPos());
    }

    @Override
    public boolean usesEventQueue() {
        return true;
    }

    @Override
    public int getTransferRate() {
        return 1000;
    }
}
