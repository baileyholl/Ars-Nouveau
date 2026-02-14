package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.capability.ANPlayerData;
import com.hollingsworth.arsnouveau.common.capability.ManaData;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public class AttachmentsRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ArsNouveau.MODID);

    public static final Supplier<AttachmentType<ManaData>> MANA_ATTACHMENT =
            ATTACHMENT_TYPES.register("mana_cap",
                    () -> AttachmentType.serializable(ManaData::new)
                            .copyOnDeath()
                            .build());


    public static final Supplier<AttachmentType<ANPlayerData>> PLAYER_DATA =
            ATTACHMENT_TYPES.register("player_cap",
                    () -> AttachmentType.serializable(ANPlayerData::new)
                            .copyOnDeath()
                            .build());

    public static final Supplier<AttachmentType<Pair<BlockPos, Optional<Direction>>>> LINKED_SOURCE_PROVIDER =
            ATTACHMENT_TYPES.register("linked_source_provider",
                    () -> AttachmentType.builder(() -> Pair.<BlockPos, Optional<Direction>>of(BlockPos.ZERO, null))
                            .serialize(ANCodecs.BLOCKPOS_DIRECTION_PAIR_CODEC)
                            .sync(ANCodecs.BLOCKPOS_DIRECTION_PAIR_STREAM_CDOEC)
                            .build());
}
