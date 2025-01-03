package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen;
import com.hollingsworth.arsnouveau.client.container.SortSettings;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class SetTerminalSettingsPacket extends AbstractPacket{

    public static final Type<SetTerminalSettingsPacket> TYPE = new Type<>(ArsNouveau.prefix("terminal_settings"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetTerminalSettingsPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(SortSettings.STREAM_CODEC),
            s -> Optional.ofNullable(s.settings),
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
            s -> Optional.ofNullable(s.selectedTab),
            SetTerminalSettingsPacket::new);

    public SortSettings settings;
    public String selectedTab;

    public SetTerminalSettingsPacket(Optional<SortSettings> settings, Optional<String> selectedTab) {
        this(settings.orElse(null), selectedTab.orElse(null));
    }

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
        if (minecraft.screen instanceof AbstractStorageTerminalScreen<?> terminalScreen) {
            terminalScreen.receiveSettings(settings);
        }
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        super.onServerReceived(minecraftServer, player);
        if (player.containerMenu instanceof StorageTerminalMenu terminalScreen){
            terminalScreen.receiveSettings(player, settings, selectedTab);
        }
    }
}
