package com.hollingsworth.arsnouveau.client.container;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SortSettings(int controlMode, boolean reverseSort, int sortType, int searchType, boolean expanded) {

    public static final Codec<SortSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("controlMode").forGetter(SortSettings::controlMode),
            Codec.BOOL.fieldOf("reverseSort").forGetter(SortSettings::reverseSort),
            Codec.INT.fieldOf("sortType").forGetter(SortSettings::sortType),
            Codec.INT.fieldOf("searchType").forGetter(SortSettings::searchType),
            Codec.BOOL.fieldOf("expanded").forGetter(SortSettings::expanded)
    ).apply(instance, SortSettings::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SortSettings> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            SortSettings::controlMode,
            ByteBufCodecs.BOOL,
            SortSettings::reverseSort,
            ByteBufCodecs.INT,
            SortSettings::sortType,
            ByteBufCodecs.INT,
            SortSettings::searchType,
            ByteBufCodecs.BOOL,
            SortSettings::expanded,
            SortSettings::new
    );

    public SortSettings(){
        this(0, false, 0, 0, false);
    }
}
