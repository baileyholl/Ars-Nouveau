package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import dev.kosmx.playerAnim.api.AnimUtils;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAnimatePlayer {
    String animation;
    public PacketAnimatePlayer(String s){
        this.animation = s;
    }

    // Decoder
    public PacketAnimatePlayer(FriendlyByteBuf buf) {
        this.animation = buf.readUtf();
    }

    // Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(animation);
    }

    // Handler
    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            AnimUtils.disableFirstPersonAnim = false;
            KeyframeAnimation animation1 = PlayerAnimationRegistry.getAnimation(new ResourceLocation(ArsNouveau.MODID, animation));
            System.out.println(animation1);
            AnimationStack stack = PlayerAnimationAccess.getPlayerAnimLayer(Minecraft.getInstance().player);
            stack.addAnimLayer(1, new KeyframeAnimationPlayer(animation1));
            System.out.println("anim");
        });
        ctx.setPacketHandled(true);
    }
}
