package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetAllies;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class PlayerAlliesList extends ObjectSelectionList<PlayerAlliesList.PlayerListEntry> {

    Set<UUID> allies;

    public Map<UUID, Optional<GameProfile>> gameProfiles;

    public PlayerAlliesList(Minecraft minecraft, int width, int height, int y, int itemHeight, Set<UUID> allies, Map<UUID, Optional<GameProfile>> gameProfileCache) {
        super(minecraft, width, height, y, itemHeight);
        this.allies = allies;
        this.gameProfiles = gameProfileCache;
        updateEntries();
    }

    @Override
    protected boolean removeEntry(PlayerListEntry entry) {
        boolean flag = allies.remove(entry.playerUUID);
        if (flag && entry == this.getSelected()) {
            this.setSelected(null);
        }
        if (flag) updateEntries();
        return flag;
    }

    public void updateEntries() {
        clearEntries();
        for (UUID ally : allies) {
            addEntry(new PlayerListEntry(ally, this));
        }
        if (minecraft.player != null)
            Networking.sendToServer(new PacketSetAllies(minecraft.player.getUUID(), allies));
    }

    @Override
    protected int getScrollbarPosition() {
        return getX() - 6; // Adjust scrollbar position
    }

    @Override
    public int getRowWidth() {
        return width; // Adjust row width
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderListSeparators(@NotNull GuiGraphics guiGraphics) {

    }

    @Override
    protected void renderListBackground(@NotNull GuiGraphics guiGraphics) {
        ResourceLocation resourcelocation = prefix("textures/gui/menu_paper.png");
        guiGraphics.blit(
                resourcelocation,
                this.getX(),
                this.getY(),
                0,
                0,
                108,
                108,
                108,
                108
        );
    }

    static class PlayerListEntry extends ObjectSelectionList.Entry<PlayerListEntry> {
        private final UUID playerUUID;
        private final PlayerAlliesList playerAlliesList;

        public PlayerListEntry(@NotNull UUID playerUUID, PlayerAlliesList playerAlliesList) {
            this.playerUUID = playerUUID;
            this.playerAlliesList = playerAlliesList;
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            Minecraft instance = Minecraft.getInstance();
            // try to get player's name from server using playerUUID
            Optional<GameProfile> cachedProfile = playerAlliesList.gameProfiles.getOrDefault(playerUUID, Optional.empty());
            graphics.drawString(instance.font, cachedProfile.isPresent() ? cachedProfile.get().getName() : "Unknown Player", x + 8, y + 5, 0, false);
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.empty();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) { // Left-click to remove the entry
                return playerAlliesList.removeEntry(this);
            }
            return false;
        }
    }
}
