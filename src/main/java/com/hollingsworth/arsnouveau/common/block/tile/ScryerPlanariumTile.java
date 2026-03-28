package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.items.data.ScryPosData;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.BlockEntityTypeRegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ScryerPlanariumTile extends ModdedTile implements GeoBlockEntity, ITickable {
    GlobalPos scryerPos;
    StructureTemplate template;

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

    // 1.21.11: saveAdditional/loadAdditional changed to ValueOutput/ValueInput
    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        if (template != null) {
            CompoundTag templateTag = new CompoundTag();
            template.save(templateTag);
            tag.store("template", CompoundTag.CODEC, templateTag);
        }
        if (this.scryerPos != null) {
            tag.store("scryerPos", GlobalPos.CODEC, this.scryerPos);
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        tag.read("scryerPos", GlobalPos.CODEC).ifPresent(pos -> this.scryerPos = pos);
        // 1.21.11: getCompound returns Optional; BuiltInRegistries.BLOCK is itself HolderGetter<Block>
        tag.read("template", CompoundTag.CODEC).ifPresent(templateTag -> {
            this.template = new StructureTemplate();
            this.template.load(BuiltInRegistries.BLOCK, templateTag);
        });
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
