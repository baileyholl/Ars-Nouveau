package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class EntityTagProvider extends EntityTypeTagsProvider {
    public EntityTagProvider(DataGenerator p_126517_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126517_, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(EntityTags.DISINTEGRATION_BLACKLIST);
        this.tag(EntityTags.DISINTEGRATION_WHITELIST);
        this.tag(EntityTags.DRYGMY_BLACKLIST).add(EntityType.IRON_GOLEM);
        this.tag(EntityTags.MAGIC_FIND)
                .add(ModEntities.STARBUNCLE_TYPE.get(), ModEntities.ENTITY_DRYGMY.get(),
                        ModEntities.WHIRLISPRIG_TYPE.get(),
                        ModEntities.ENTITY_BOOKWYRM_TYPE.get(),
                        ModEntities.ENTITY_WIXIE_TYPE.get()
                );
        this.tag(EntityTags.SPELL_CAN_HIT);
        this.tag(EntityTags.HOSTILE_MOBS)
                .add(ModEntities.WILDEN_HUNTER.get(), ModEntities.WILDEN_GUARDIAN.get(),
                        ModEntities.WILDEN_STALKER.get()
                );
        this.tag(EntityTags.FAMILIAR).add(ModEntities.ENTITY_FAMILIAR_STARBUNCLE.get(), ModEntities.ENTITY_FAMILIAR_SYLPH.get(),
                        ModEntities.ENTITY_FAMILIAR_WIXIE.get(), ModEntities.ENTITY_FAMILIAR_DRYGMY.get(),
                        ModEntities.ENTITY_FAMILIAR_BOOKWYRM.get());
        this.tag(EntityTags.JAR_BLACKLIST).addTag(EntityTags.FAMILIAR);
        this.tag(EntityTags.JAR_WHITELIST).addOptional(new ResourceLocation("create:contraption"))
                .add(EntityType.ITEM).add(EntityType.END_CRYSTAL)
                .add(EntityType.BOAT).add(EntityType.CHEST_BOAT);
    }

    private static TagKey<EntityType<?>> create(ResourceLocation pName) {
        return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, pName);
    }
}
