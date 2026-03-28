package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSyncLitEntities;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class LightEvents {

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post e) {
        if (e.getEntity().level().isClientSide())
            return;
        if (e.getEntity().level().getGameTime() % 100 == 0 && e.getEntity() instanceof ServerPlayer serverPlayer) {
            List<Integer> litID = new ArrayList<>();
            for (ServerPlayer player : serverPlayer.level().getServer().getPlayerList().getPlayers()) {
                for (int i = 0; i < 9; i++) {
                    ItemStack jar = player.getInventory().getItem(i);
                    if (jar.getItem() == ItemsRegistry.JAR_OF_LIGHT.asItem()) {
                        litID.add(player.getId());
                        break;
                    }
                }
            }
            Networking.sendToPlayerClient(new PacketSyncLitEntities(litID), serverPlayer);
        }
    }
}
