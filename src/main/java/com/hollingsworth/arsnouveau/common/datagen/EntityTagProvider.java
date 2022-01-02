package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class EntityTagProvider extends EntityTypeTagsProvider {
    public EntityTagProvider(DataGenerator p_126517_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126517_, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "disintegration_blacklist")));
        this.tag(EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "disintegration_whitelist")));
        this.tag(EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "drygmy_blacklist")))
                .add(EntityType.IRON_GOLEM);
        this.tag(EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "magic_find")))
                .add(ModEntities.STARBUNCLE_TYPE, ModEntities.ENTITY_DRYGMY,
                        ModEntities.WHIRLISPRIG_TYPE,
                        ModEntities.ENTITY_BOOKWYRM_TYPE,
                        ModEntities.ENTITY_WIXIE_TYPE
                );
    }
}
