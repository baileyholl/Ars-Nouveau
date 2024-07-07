package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PacketUpdateSpellSoundAll extends PacketSetSound {

    public static final Type<PacketUpdateSpellSoundAll> TYPE = new Type<>(ArsNouveau.prefix("update_spell_sound_all"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateSpellSoundAll> CODEC = StreamCodec.ofMember(PacketUpdateSpellSoundAll::toBytes, PacketUpdateSpellSoundAll::new);

    public PacketUpdateSpellSoundAll(int castSlot, ConfiguredSpellSound sound, boolean mainHand) {
        super(castSlot, sound, mainHand);
    }

    public PacketUpdateSpellSoundAll(RegistryFriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ItemStack stack = player.getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        if (stack.getItem() instanceof SpellBook) {
            SpellCaster caster = SpellCasterRegistry.from(stack);
            for (int i = 0; i < caster.getMaxSlots(); i++) {
                caster = caster.setSound(sound, i);
            }
            caster.saveToStack(stack);
            Networking.sendToPlayerClient(new PacketUpdateBookGUI(stack), player);
            Networking.sendToPlayerClient(new PacketOpenSpellBook(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND), player);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
