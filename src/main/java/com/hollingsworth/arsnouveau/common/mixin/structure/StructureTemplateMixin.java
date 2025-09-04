package com.hollingsworth.arsnouveau.common.mixin.structure;

import com.hollingsworth.arsnouveau.setup.registry.StructureRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {


    @Inject(
            method = "placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z",
            at = @At(value = "HEAD")
    )
    private void ars_nouveau_preventAutoWaterlogging(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos1,
                                                     BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings,
                                                     RandomSource random, int flag, CallbackInfoReturnable<Boolean> cir) {

        if (structurePlaceSettings.getProcessors().stream().anyMatch(processor ->
                ((StructureProcessorAccessor) processor).callGetType() == StructureRegistry.WATERLOGGING_FIX_PROCESSOR.get())) {
            structurePlaceSettings.setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
        }
    }

}