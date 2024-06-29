package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

public record MultiPotionContents(int charges, PotionContents contents){
    public static Codec<MultiPotionContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("charges").forGetter(MultiPotionContents::charges),
            PotionContents.CODEC.fieldOf("contents").forGetter(MultiPotionContents::contents)
    ).apply(instance, MultiPotionContents::new));

    public static StreamCodec<RegistryFriendlyByteBuf, MultiPotionContents> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, MultiPotionContents::charges, PotionContents.STREAM_CODEC, MultiPotionContents::contents, MultiPotionContents::new);

    public MultiPotionContents withCharges(int charges){
        return new MultiPotionContents(charges, contents);
    }

    public void applyEffects(ItemStack stack, Entity source, Entity inDirectSource, LivingEntity target) {
        Item item = stack.getItem();

        for (MobEffectInstance effectinstance : contents.getAllEffects()) {
            effectinstance = item instanceof PotionFlask flask ? flask.getEffectInstance(effectinstance) : effectinstance;
            if (effectinstance.getEffect().value().isInstantenous()) {
                effectinstance.getEffect().value().applyInstantenousEffect(source, inDirectSource, target, effectinstance.getAmplifier(), 1.0D);
            } else {
                target.addEffect(new MobEffectInstance(effectinstance), source);
            }
        }
    }
}
