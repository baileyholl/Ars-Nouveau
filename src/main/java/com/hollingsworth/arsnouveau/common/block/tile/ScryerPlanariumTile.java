package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.items.data.ScryPosData;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.BlockEntityTypeRegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ScryerPlanariumTile extends ModdedTile implements GeoBlockEntity, ITickable {
    GlobalPos scryerPos;
    StructureTemplate template;
//
//    public ScryerPlanariumTile(BlockPos pos, BlockState state) {
//        super(BlockRegistry.SCRYER_PLANARIUM_TILE, pos, state);
//    }

    public ScryerPlanariumTile(BlockEntityTypeRegistryWrapper<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public void bindScroll(ItemStack stack, @Nullable Player player) {
        ScryPosData data = stack.get(DataComponentRegistry.SCRY_DATA);
        System.out.println(data);
        this.scryerPos = data.pos().orElse(null);
        if (scryerPos == null) {
            return;
        }
        Level crystalLevel = level.getServer().getLevel(scryerPos.dimension());
        if (crystalLevel == null) {
            return;
        }


    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (template != null) {
            CompoundTag templateTag = new CompoundTag();
            template.save(templateTag);
            tag.put("template", templateTag);
        }
        if (this.scryerPos != null) {
            Tag scyerTag = ANCodecs.encode(GlobalPos.CODEC, this.scryerPos);
            tag.put("scryerPos", scyerTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("scryerPos")) {
            this.scryerPos = ANCodecs.decode(GlobalPos.CODEC, tag.get("scryerPos"));
        }
        if (tag.contains("template")) {
            CompoundTag templateTag = tag.getCompound("template");
            this.template = new StructureTemplate();
            this.template.load(BuiltInRegistries.BLOCK.asLookup(), templateTag);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }


    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

}
