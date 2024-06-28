package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PotionSyncPacket extends AbstractPacket{

    public int entity;
    public int duration;
    public ResourceLocation effect;

    public PotionSyncPacket(int entity, ResourceLocation effect, int duration) {
        this.entity = entity;
        this.effect = effect;
        this.duration = duration;
    }

    public PotionSyncPacket(int entity, MobEffect effect, int duration) {
        this.entity = entity;
        this.effect = getRegistryName(effect);
        this.duration = duration;
    }

    public void toBytes(RegistryFriendlyByteBuf RegistryFriendlyByteBuf) {
        RegistryFriendlyByteBuf.writeInt(this.entity);
        RegistryFriendlyByteBuf.writeResourceLocation(this.effect);
        RegistryFriendlyByteBuf.writeInt(this.duration);
    }

    public PotionSyncPacket(RegistryFriendlyByteBuf RegistryFriendlyByteBuf) {
        this.entity = RegistryFriendlyByteBuf.readInt();
        this.effect = RegistryFriendlyByteBuf.readResourceLocation();
        this.duration = RegistryFriendlyByteBuf.readInt();
    }

    @Override
    public void onClientReceived(Minecraft mc, Player player) {
        if (mc.level.getEntity(this.entity) instanceof LivingEntity living && living != ArsNouveau.proxy.getPlayer()) {
            Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.getHolder(this.effect).orElse(null);
            if (effect == null) return;
            if (duration > 0) {
                living.addEffect(new MobEffectInstance(effect, duration, 0, false, false, false));
            } else {
                living.removeEffect(effect);
            }
        }
    }

    public static ResourceLocation getRegistryName(MobEffect effect) {
        return BuiltInRegistries.MOB_EFFECT.getKey(effect);
    }

    public static final Type<PotionSyncPacket> TYPE = new Type<>(ArsNouveau.prefix("potion_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PotionSyncPacket> CODEC = StreamCodec.ofMember(PotionSyncPacket::toBytes, PotionSyncPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
