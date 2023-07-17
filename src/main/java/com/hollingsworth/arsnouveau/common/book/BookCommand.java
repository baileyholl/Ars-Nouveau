/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

public class BookCommand {
    protected ResourceLocation id;
    protected Book book;

    protected String command;
    protected int permissionLevel;
    /**
     * -1 is unlimited.
     */
    protected int maxUses;

    /**
     * If set, this message will be displayed if the command fails.
     * If not set, the default failure message will be displayed.
     * Should be a translation key.
     */
    @Nullable
    protected String failureMessage;

    /**
     * If set, this message will be displayed when the command succeeds.
     * If not set, no success message will be displayed.
     * Should be a translation key.
     */
    @Nullable
    protected String successMessage;

    public BookCommand(ResourceLocation id, String command, int permissionLevel, int maxUses, @Nullable String failureMessage, @Nullable String successMessage) {
        this.id = id;
        this.command = command;
        this.permissionLevel = permissionLevel;
        this.maxUses = maxUses;
        this.failureMessage = failureMessage;
        this.successMessage = successMessage;
    }

    public static BookCommand fromJson(ResourceLocation id, JsonObject json) {
        var command = GsonHelper.getAsString(json, "command");
        var permissionLevel = GsonHelper.getAsInt(json, "permission_level", ModonomiconConstants.Data.Command.DEFAULT_PERMISSION_LEVEL);
        var maxUses = GsonHelper.getAsInt(json, "max_uses", ModonomiconConstants.Data.Command.DEFAULT_MAX_USES);
        var failureMessage = GsonHelper.getAsString(json, "failure_message", null);
        var successMessage = GsonHelper.getAsString(json, "success_message", null);

        return new BookCommand(id, command, permissionLevel, maxUses, failureMessage, successMessage);
    }

    public static BookCommand fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var command = buffer.readUtf();
        var permissionLevel = (int) buffer.readByte();
        var maxUses = buffer.readVarInt();
        var failureMessage = buffer.readNullable(FriendlyByteBuf::readUtf);
        var successMessage = buffer.readNullable(FriendlyByteBuf::readUtf);
        return new BookCommand(id, command, permissionLevel, maxUses, failureMessage, successMessage);
    }

    /**
     * call after loading the book jsons to finalize.
     */
    public void build(Book book) {
        this.book = book;
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.command);
        buffer.writeByte(this.permissionLevel);
        buffer.writeVarInt(this.maxUses);
        buffer.writeNullable(this.failureMessage, FriendlyByteBuf::writeUtf);
        buffer.writeNullable(this.successMessage, FriendlyByteBuf::writeUtf);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Book getBook() {
        return this.book;
    }

    public String getCommand() {
        return this.command;
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public @Nullable String getFailureMessage() {
        return this.failureMessage;
    }

    public @Nullable String getSuccessMessage() {
        return this.successMessage;
    }

    public void execute(ServerPlayer player){
        if(!BookUnlockCapability.canRunFor(player, this)){
            var failureMessage = this.failureMessage == null ? ModonomiconConstants.I18n.Command.DEFAULT_FAILURE_MESSAGE : this.failureMessage;

            player.sendSystemMessage(Component.translatable(failureMessage).withStyle(ChatFormatting.RED));
            return;
        } else {
            var commandSourceStack = new CommandSourceStack(player, player.position(),player.getRotationVector(), player.getLevel(), this.permissionLevel, player.getName().getString(), player.getDisplayName(), player.server, player);

            BookUnlockCapability.setRunFor(player, this);

            try{
                player.server.getCommands().performPrefixedCommand(commandSourceStack, this.command);

                if(this.successMessage != null){
                    player.sendSystemMessage(Component.translatable(this.successMessage).withStyle(ChatFormatting.GREEN));
                }
            }
            catch (Exception e){
                Modonomicon.LOGGER.error("Running command [" + this.id.toString() + "] failed: ", e);
            }
        }

        //Even if the command fails we sync the capability.
        //This allows us to "Pretend" success clientside and disable the command source (button/link/etc) so the player cannot spam-click it.
        //spam-clicking would not allow abuse anyway, but would lead to error messages sent back to the player.
        BookUnlockCapability.syncFor(player);
    }
}
