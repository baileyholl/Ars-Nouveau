package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.PlayerAlliesList;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectableButton;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSummonDog;
import com.hollingsworth.arsnouveau.common.network.PacketUnsummonDog;
import com.hollingsworth.arsnouveau.common.world.saved_data.AlliesSavedData;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import javax.annotation.Nullable;
import java.util.*;

import static com.hollingsworth.arsnouveau.client.gui.PlayerAlliesList.gameProfileCache;

public class GuiSettingsScreen extends BaseBook {

    private PlayerAlliesList playerList;
    private Set<UUID> allies = new HashSet<>();
    private EditBox uuidInputBox;

    public GuiSettingsScreen(@Nullable Screen parent) {
        super();
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookRight - 71, bookBottom - 12, 0, 0, 41, 12, 41, 12, "textures/gui/clear_icon.png", (e) -> Minecraft.getInstance().setScreen(parent)));
        SelectableButton dynamicButton = new SelectableButton(bookLeft + 20, bookTop + 34, 0, 0, 16, 16, 16, 16, ArsNouveau.prefix("textures/gui/settings_dynamic_light_off.png"),
                ArsNouveau.prefix("textures/gui/settings_dynamic_light_on.png"), (b) -> {
            SelectableButton button = (SelectableButton) b;
            button.isSelected = !button.isSelected;
            LightManager.toggleLightsAndConfig(!Config.DYNAMIC_LIGHTS_ENABLED.get());
            button.withTooltip(Component.translatable(button.isSelected ? "ars_nouveau.dynamic_lights.button_on" : "ars_nouveau.dynamic_lights.button_off"));
        });
        dynamicButton.isSelected = Config.DYNAMIC_LIGHTS_ENABLED.get();
        dynamicButton.withTooltip(Component.translatable(dynamicButton.isSelected ? "ars_nouveau.dynamic_lights.button_on" : "ars_nouveau.dynamic_lights.button_off"));

        addRenderableWidget(dynamicButton);
        if (ClientInfo.isSupporter) {
            GuiImageButton lilyButton = new GuiImageButton(bookLeft + 40, bookTop + 34, 0, 0, 16, 16, 16, 16, ArsNouveau.prefix("textures/gui/settings_summon_lily.png"), (b) -> Networking.sendToServer(new PacketSummonDog(PacketSummonDog.DogType.LILY)));
            lilyButton.withTooltip(Component.translatable("ars_nouveau.settings.summon_lily"));

            GuiImageButton unsummonLily = new GuiImageButton(bookLeft + 60, bookTop + 34, 0, 0, 16, 16, 16, 16, ArsNouveau.prefix("textures/gui/settings_unsummon_lily.png"), (b) -> Networking.sendToServer(new PacketUnsummonDog(PacketSummonDog.DogType.LILY)));
            unsummonLily.withTooltip(Component.translatable("ars_nouveau.settings.unsummon_lily"));

            addRenderableWidget(lilyButton);
            addRenderableWidget(unsummonLily);

            GuiImageButton nookButton = new GuiImageButton(bookLeft + 80, bookTop + 34, 0, 0, 16, 16, 16, 16, ArsNouveau.prefix("textures/gui/settings_summon_nook.png"), (b) -> Networking.sendToServer(new PacketSummonDog(PacketSummonDog.DogType.NOOK)));
            nookButton.withTooltip(Component.translatable("ars_nouveau.settings.summon_nook"));

            GuiImageButton unsummonNook = new GuiImageButton(bookLeft + 100, bookTop + 34, 0, 0, 16, 16, 16, 16, ArsNouveau.prefix("textures/gui/settings_unsummon_nook.png"), (b) -> Networking.sendToServer(new PacketUnsummonDog(PacketSummonDog.DogType.NOOK)));
            unsummonNook.withTooltip(Component.translatable("ars_nouveau.settings.unsummon_nook"));

            addRenderableWidget(nookButton);
            addRenderableWidget(unsummonNook);
        }

        // Initialize UUID input text box
        this.uuidInputBox = new EditBox(font, bookRight - 132, bookTop + 36, 80, 20, Component.translatable("gui.enter_uuid")) {
            @Override
            public boolean charTyped(char codePoint, int modifiers) {
                // if the text color is red, reset it to white since the user is typing again
                uuidInputBox.setTextColor(0xFFFFFF);
                return super.charTyped(codePoint, modifiers);
            }
        };

        this.uuidInputBox.setMaxLength(36); // UUIDs are 36 characters (including dashes)
        addRenderableWidget(uuidInputBox);

        // Button to add a player
        GuiImageButton addAllyButton = new GuiImageButton(bookRight - 48, bookTop + 40, 0, 0, 16, 12, 50, 12, "textures/gui/create_icon.png", this::addAlly);
        addAllyButton.withTooltip(Component.translatable("ars_nouveau.settings.add_ally"));
        addRenderableWidget(addAllyButton);


        // Fetch the allies, we need to load the level data
        try {
            if (minecraft != null && minecraft.player != null) {
                allies = AlliesSavedData.getLocalAllies();
                allies.forEach(playerUUID -> {
                    // if not found, fetch the game profile from the server and cache it
                    // to avoid fetching the same profile multiple times, put an empty optional in the map
                    if (!gameProfileCache.containsKey(playerUUID)) {
                        gameProfileCache.put(playerUUID, Optional.empty());
                        SkullBlockEntity.fetchGameProfile(playerUUID).whenComplete(
                                (profile, throwable) -> profile.ifPresent(gameProfile -> gameProfileCache.put(playerUUID, Optional.of(gameProfile)))
                        );
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch allies from server");
        }

        // Initialize the scrollable player list
        this.playerList = new PlayerAlliesList(minecraft, 108, 108, bookTop + 64, 20, allies);
        playerList.setX(bookRight - 132);
        addRenderableWidget(playerList);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(ArsNouveau.prefix("textures/gui/create_paper.png"), 216, 175, 0, 0, 56, 15, 56, 15);
        graphics.drawString(font, Component.translatable("ars_nouveau.settings.title").getString(), 51, 24, -8355712, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.close"), 238, 180, -8355712, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.settings.allies").getString(), 51 + 128, 24, -8355712, false);
    }

    private void addAlly(Button button) {
        String inputBoxValue = uuidInputBox.getValue();
        if (inputBoxValue.isEmpty()) {
            return;
        }
        // if the input is a valid UUID, add it to the allies list
        try {
            UUID uuid = UUID.fromString(inputBoxValue);
            allies.add(uuid);
            playerList.updateEntries();
            // Clear the input box
            uuidInputBox.setValue("");
        } catch (IllegalArgumentException e) {
            // Invalid UUID
            // We check if the input is a player name and convert it to a UUID
            SkullBlockEntity.fetchGameProfile(inputBoxValue).whenComplete(
                    (profile, throwable) -> {
                        if (profile.isPresent()) {
                            allies.add(profile.get().getId());
                            gameProfileCache.put(profile.get().getId(), profile);
                            playerList.updateEntries();
                            // Clear the input box
                            uuidInputBox.setValue("");
                        } else {
                            // Invalid player name, set text color to red
                            uuidInputBox.setTextColor(0xFF0000);
                        }
                    }
            );
        }
    }

}
