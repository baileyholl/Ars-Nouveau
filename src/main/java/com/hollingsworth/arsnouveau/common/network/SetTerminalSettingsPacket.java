package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalScreen;
import com.hollingsworth.arsnouveau.client.container.SortSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class SetTerminalSettingsPacket extends AbstractPacket {

    public static final Type<SetTerminalSettingsPacket> TYPE = new Type<>(ArsNouveau.prefix("terminal_settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetTerminalSettingsPacket> CODEC = StreamCodec.composite(
            SortSettings.STREAM_CODEC,
            s -> s.settings,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
            s -> Optional.ofNullable(s.selectedTab),
            (settings, selectedTab) -> new SetTerminalSettingsPacket(settings, selectedTab.orElse(null)));

    public SortSettings settings;
    public String selectedTab;

    public SetTerminalSettingsPacket(SortSettings settings, String selectedTab) {
        this.settings = settings;
        this.selectedTab = selectedTab;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        super.onClientReceived(minecraft, player);
        if (minecraft.screen instanceof CraftingTerminalScreen terminalScreen) {
            terminalScreen.receiveSettings(settings);
        }
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        super.onServerReceived(minecraftServer, player);
        if (player.containerMenu instanceof CraftingTerminalMenu terminalScreen && terminalScreen.stillValid(player)) {
            terminalScreen.receiveSettings(settings, selectedTab);
        }
    }
}
