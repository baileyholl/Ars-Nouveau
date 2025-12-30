package com.hollingsworth.arsnouveau.common.mixin.structure;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureTemplate.class)
public interface StructureTemplateAccessor {

    @Accessor("entityInfoList")
    List<StructureTemplate.StructureEntityInfo> getEntityInfoList();

    @Accessor
    List<StructureTemplate.Palette> getPalettes();

}
