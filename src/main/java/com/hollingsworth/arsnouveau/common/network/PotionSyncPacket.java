package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.registries.ForgeRegistries;
import java.util.function.Supplier;

public class PotionSyncPacket {

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

    public void toBytes(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(this.entity);
        friendlyByteBuf.writeResourceLocation(this.effect);
        friendlyByteBuf.writeInt(this.duration);
    }

    public PotionSyncPacket(FriendlyByteBuf friendlyByteBuf) {
        this.entity = friendlyByteBuf.readInt();
        this.effect = friendlyByteBuf.readResourceLocation();
        this.duration = friendlyByteBuf.readInt();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {

        if (contextSupplier.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) return;
        contextSupplier.get().enqueueWork(() -> {

            Minecraft mc = ArsNouveau.proxy.getMinecraft();
            if (mc != null && mc.level != null)
                if (mc.level.getEntity(this.entity) instanceof LivingEntity living && living != ArsNouveau.proxy.getPlayer()) {
                    MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(this.effect);
                    if (effect == null) return;
                    if (duration > 0) {
                        living.addEffect(new MobEffectInstance(effect, duration, 0, false, false, false));
                    } else {
                        living.removeEffect(effect);
                    }
                }
        });
        contextSupplier.get().setPacketHandled(true);

    }

    public static ResourceLocation getRegistryName(MobEffect effect) {
        return ForgeRegistries.MOB_EFFECTS.getKey(effect);
    }
}
