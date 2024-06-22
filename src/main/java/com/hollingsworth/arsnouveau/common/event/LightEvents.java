package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSyncLitEntities;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import net.neoforged.neoforge.network.PacketDistributor;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class LightEvents {

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent e) {
        if (e.player.level.isClientSide)
            return;
        if (e.player.level.getGameTime() % 100 == 0 && e.player.getServer() != null && e.phase == TickEvent.Phase.END && e.player instanceof ServerPlayer serverPlayer) {
            List<Integer> litID = new ArrayList<>();
            for (ServerPlayer player : e.player.getServer().getPlayerList().getPlayers()) {
                NonNullList<ItemStack> list = player.inventory.items;
                for (int i = 0; i < 9; i++) {
                    ItemStack jar = list.get(i);
                    if (jar.getItem() == ItemsRegistry.JAR_OF_LIGHT.asItem()) {
                        litID.add(player.getId());
                        break;
                    }
                }
            }
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new PacketSyncLitEntities(litID));
        }
    }
}
