package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public record WarpScrollData(Optional<BlockPos> pos, String dimension, Vec2 rotation, boolean crossDim) implements TooltipProvider {
    public static final Codec<WarpScrollData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.optionalFieldOf("pos").forGetter(WarpScrollData::pos),
            Codec.STRING.optionalFieldOf("dimension", "").forGetter(WarpScrollData::dimension),
            ANCodecs.VEC2.fieldOf("rotation").forGetter(WarpScrollData::rotation),
            Codec.BOOL.optionalFieldOf("crossDim", false).forGetter(WarpScrollData::crossDim)
    ).apply(instance, WarpScrollData::new));

    public WarpScrollData(boolean crossDim){
        this(Optional.empty(), "", new Vec2(0, 0), crossDim);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, WarpScrollData> STREAM_CODEC = CheatSerializer.create(WarpScrollData.CODEC);

    public WarpScrollData setPos(@Nullable BlockPos pos, @Nullable String dimension) {
        return new WarpScrollData(Optional.ofNullable(pos), dimension, rotation, crossDim);
    }

    public WarpScrollData setRotation(Vec2 rotation) {
        return new WarpScrollData(pos, dimension, rotation, crossDim);
    }

    public boolean canTeleportWithDim(String dimension) {
        return this.dimension.equals(dimension) || crossDim;
    }

    public boolean canTeleportWithDim(Level level){
        return canTeleportWithDim(level.dimension().location().toString());
    }

    public boolean isValid() {
        return pos != null && pos.isPresent() && dimension != null && !dimension.isEmpty() && rotation != null;
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
        if (!isValid()) {
            pTooltipAdder.accept(Component.translatable("ars_nouveau.warp_scroll.no_location"));
            return;
        }
        var pos = this.pos.get();
        pTooltipAdder.accept(Component.translatable("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()));
        if(crossDim) {
            String dimId = dimension();
            if (dimId != null) {
                ResourceLocation resourceLocation = ResourceLocation.tryParse(dimId);
                pTooltipAdder.accept(Component.translatable(resourceLocation.getPath() + "." + resourceLocation.getNamespace() + ".name"));
            }
        }
        if (!ServerConfig.ENABLE_WARP_PORTALS.get()) {
            pTooltipAdder.accept(Component.translatable("ars_nouveau.warp_scroll.disabled_warp_portal").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarpScrollData that = (WarpScrollData) o;
        return crossDim == that.crossDim && Objects.equals(pos, that.pos) && Objects.equals(rotation, that.rotation) && Objects.equals(dimension, that.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, dimension, rotation, crossDim);
    }
}
