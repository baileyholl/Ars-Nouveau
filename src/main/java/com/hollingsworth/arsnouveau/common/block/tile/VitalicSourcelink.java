package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class VitalicSourcelink extends SourcelinkTile{
    public boolean isDisabled = false;
    public VitalicSourcelink() {
        super(BlockRegistry.MANA_CONDENSER_TILE);
        MinecraftForge.EVENT_BUS.register(this);
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


}
