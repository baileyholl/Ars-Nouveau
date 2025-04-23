package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Map;
import java.util.Optional;

public class PropMap{
    public static final Codec<Map<IPropertyType<?>, Object>> VALUE_MAP_CODEC = Codec.dispatchedMap(ParticlePropertyRegistry.CODEC, IPropertyType::normalCodec);


    public static Codec<PropMap> CODEC = makeCodecFromMap(VALUE_MAP_CODEC);

    public static StreamCodec<RegistryFriendlyByteBuf, PropMap> STREAM_CODEC = new StreamCodec<>() {
        public PropMap decode(RegistryFriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            Reference2ObjectMap<IPropertyType<?>, Object> reference2objectmap = new Reference2ObjectArrayMap<>(Math.min(i, 65536));

            for (int l = 0; l < i; l++) {
                IPropertyType<?> datacomponenttype = IPropertyType.STREAM_CODEC.decode(buffer);
                Object object = datacomponenttype.streamCodec().decode(buffer);
                reference2objectmap.put(datacomponenttype, object);
            }
            return new PropMap(reference2objectmap);
        }

        public void encode(RegistryFriendlyByteBuf buffer, PropMap value) {
            int i = value.properties.size();
            buffer.writeVarInt(i);

            for (var entry1 : Reference2ObjectMaps.fastIterable(value.properties)) {
                Object optional = entry1.getValue();
                IPropertyType<?> datacomponenttype = entry1.getKey();
                IPropertyType.STREAM_CODEC.encode(buffer, datacomponenttype);
                encodeComponent(buffer, datacomponenttype, optional);
            }
        }

        private static <T extends BaseProperty> void encodeComponent(RegistryFriendlyByteBuf buffer, IPropertyType<T> component, Object value) {
            component.streamCodec().encode(buffer, (T) value);
        }
    };

    private static Codec<PropMap> makeCodecFromMap(Codec<Map<IPropertyType<?>, Object>> codec) {
        return codec.flatComapMap(PropMap::new, p_337448_ -> {
            int i = p_337448_.properties.size();
            if (i == 0) {
                return DataResult.success(Reference2ObjectMaps.emptyMap());
            } else {
                Reference2ObjectMap<IPropertyType<?>, Object> reference2objectmap = new Reference2ObjectArrayMap<>(i);

                reference2objectmap.putAll(p_337448_.properties);

                return DataResult.success(reference2objectmap);
            }
        });
    }

    private Reference2ObjectArrayMap<IPropertyType<?>, Object> properties;

    public PropMap() {
        this.properties = new Reference2ObjectArrayMap<>();
    }

    public PropMap(Map<IPropertyType<?>, Object> map){
        this.properties = new Reference2ObjectArrayMap<>(map);
    }

    public <T extends BaseProperty> T get(IPropertyType<T> type) {
        return (T) properties.get(type);
    }

    public <T extends BaseProperty> Optional<T> getOptional(IPropertyType<T> type) {
        return Optional.ofNullable((T) properties.get(type));
    }

    public boolean has(IPropertyType<?> type) {
        return properties.containsKey(type);
    }

    public <T extends BaseProperty> T getOrDefault(IPropertyType<T> type, T defaultValue) {
        return (T) properties.getOrDefault(type, defaultValue);
    }

    public <T extends BaseProperty> void set(IPropertyType<T> type, T value) {
        properties.put(type, value);
    }
}
