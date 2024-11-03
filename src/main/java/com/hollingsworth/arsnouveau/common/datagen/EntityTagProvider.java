package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EntityTagProvider extends EntityTypeTagsProvider {


    public EntityTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider,  @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, ArsNouveau.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(Tags.EntityTypes.BOSSES).add(ModEntities.WILDEN_BOSS.get());
        this.tag(EntityTags.REWIND_BLACKLIST).addTags(Tags.EntityTypes.BOSSES);
        this.tag(EntityTags.DISINTEGRATION_BLACKLIST);
        this.tag(EntityTags.DISINTEGRATION_WHITELIST);
        this.tag(EntityTags.DRYGMY_BLACKLIST).add(EntityType.IRON_GOLEM);
        this.tag(EntityTags.MAGIC_FIND)
                .add(ModEntities.STARBUNCLE_TYPE.get(), ModEntities.ENTITY_DRYGMY.get(),
                        ModEntities.WHIRLISPRIG_TYPE.get(),
                        ModEntities.ENTITY_BOOKWYRM_TYPE.get(),
                        ModEntities.ENTITY_WIXIE_TYPE.get(),
                        ModEntities.GIFT_STARBY.get(),
                        ModEntities.WILDEN_GUARDIAN.get(),
                        ModEntities.WILDEN_BOSS.get(),
                        ModEntities.WILDEN_STALKER.get(),
                        ModEntities.WILDEN_HUNTER.get(),
                        ModEntities.ALAKARKINOS_TYPE.get()
                );
        this.tag(EntityTags.SPELL_CAN_HIT);
        this.tag(EntityTags.HOSTILE_MOBS)
                .add(ModEntities.WILDEN_HUNTER.get(), ModEntities.WILDEN_GUARDIAN.get(),
                        ModEntities.WILDEN_STALKER.get()
                );
        this.tag(EntityTags.FAMILIAR).add(ModEntities.ENTITY_FAMILIAR_STARBUNCLE.get(), ModEntities.ENTITY_FAMILIAR_SYLPH.get(),
                ModEntities.ENTITY_FAMILIAR_WIXIE.get(), ModEntities.ENTITY_FAMILIAR_DRYGMY.get(),
                ModEntities.ENTITY_FAMILIAR_BOOKWYRM.get());
        this.tag(EntityTags.JAR_BLACKLIST).addTag(EntityTags.FAMILIAR).addTag(Tags.EntityTypes.CAPTURING_NOT_SUPPORTED);

        this.tag(EntityTags.JAR_WHITELIST)
                .add(EntityType.ITEM)
                .add(EntityType.END_CRYSTAL)
                .add(EntityType.BOAT)
                .add(EntityType.CHEST_BOAT)
                .add(EntityType.ARROW)
                .add(EntityType.SNOWBALL)
                .add(EntityType.EGG)
                .add(EntityType.ENDER_PEARL)
                .add(EntityType.EYE_OF_ENDER)
                .add(EntityType.POTION)
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.LIGHTNING_BOLT)
                .add(ModEntities.LIGHTNING_ENTITY.get())
                .add(EntityType.TRIDENT).addOptional(ResourceLocation.parse("create:contraption"));
        this.tag(EntityTags.LINGERING_BLACKLIST)
                .add(ModEntities.LIGHTNING_ENTITY.get(), ModEntities.LINGER_SPELL.get(), ModEntities.WALL_SPELL.get());
        this.tag(EntityTags.BERRY_BLACKLIST)
                .add(ModEntities.STARBUNCLE_TYPE.get(), ModEntities.WHIRLISPRIG_TYPE.get(), EntityType.FOX, EntityType.BEE);

        this.tag(EntityTags.JAR_RELEASE_BLACKLIST).add(EntityType.ENDER_DRAGON);

        this.tag(EntityTags.ANIMAL_SUMMON_BLACKLIST).add(ModEntities.GIFT_STARBY.get());
        this.tag(EntityTags.BURST_WHITELIST).add(ModEntities.BUBBLE.get(), EntityType.ARROW);
    }
}
