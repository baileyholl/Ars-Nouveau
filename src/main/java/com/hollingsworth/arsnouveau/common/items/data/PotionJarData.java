package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.Objects;

public record PotionJarData(int fill, PotionContents contents, boolean locked) {

    public static final Codec<PotionJarData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("fill").forGetter(PotionJarData::fill),
            PotionContents.CODEC.fieldOf("contents").forGetter(PotionJarData::contents),
            Codec.BOOL.fieldOf("locked").forGetter(PotionJarData::locked)
    ).apply(instance, PotionJarData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PotionJarData> STREAM = StreamCodec.composite(
            ByteBufCodecs.INT,
            PotionJarData::fill,
            PotionContents.STREAM_CODEC,
            PotionJarData::contents,
            ByteBufCodecs.BOOL,
            PotionJarData::locked,
            PotionJarData::new
    );

    public boolean canAccept(PotionContents otherData, int amount, int maxFill) {
        if (otherData == null || !this.validContentTypeForJar(otherData)) {
            return false;
        }

        return (!this.locked && this.fill <= 0) || (amount <= (maxFill - this.fill) && PotionUtil.arePotionContentsEqual(otherData, this.contents));
    }

    public boolean validContentTypeForJar(PotionContents otherData){
        return !otherData.is(Potions.WATER) && !otherData.is(Potions.MUNDANE);
    }

    public ObjectIntPair<PotionJarData> add(PotionContents other, int amount, int maxFill) {
        if (!this.validContentTypeForJar(other)) {
            return ObjectIntPair.of(this, amount);
        }

        int fill = this.fill;
        PotionContents contents = this.contents;

        if (this.fill == 0) {
            if (!this.contents.equals(other) || this.contents.equals(PotionContents.EMPTY)) {
                contents = other;
            }
            fill += amount;
        } else {
            fill = this.fill + amount;
        }

        int remainder = Math.max(0, fill - maxFill);
        fill = Math.min(fill, maxFill);

        return ObjectIntPair.of(new PotionJarData(fill, contents, this.locked), remainder);
    }

    public ObjectIntPair<PotionJarData> remove(int amount) {
        int removed = Math.min(this.fill, amount);
        int fill = this.fill - removed;
        PotionContents contents = this.contents;

        if(fill == 0 && !this.locked){
            contents = PotionContents.EMPTY;
        }

        return ObjectIntPair.of(new PotionJarData(fill, contents, this.locked), removed);
    }

    public PotionJarData withFill(int fill) {
        return new PotionJarData(fill, this.contents, this.locked);
    }

    public PotionJarData withContents(PotionContents contents) {
        return new PotionJarData(this.fill, contents, this.locked);
    }

    public PotionJarData withLocked(boolean locked) {
        return new PotionJarData(this.fill, this.contents, locked);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PotionJarData that = (PotionJarData) o;
        return fill == that.fill && locked == that.locked && Objects.equals(contents, that.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fill, contents, locked);
    }
}
