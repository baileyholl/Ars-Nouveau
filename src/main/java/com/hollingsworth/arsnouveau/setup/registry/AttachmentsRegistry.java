package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.capability.ANPlayerData;
import com.hollingsworth.arsnouveau.common.capability.ManaData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

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
}
