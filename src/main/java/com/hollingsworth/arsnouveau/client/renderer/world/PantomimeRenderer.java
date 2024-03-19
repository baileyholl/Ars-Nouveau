package com.hollingsworth.arsnouveau.client.renderer.world;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.spell.method.MethodPantomime;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PantomimeRenderer {
    public static void renderOutline(PoseStack poseStack) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ICasterTool)) return;

        ISpellCaster caster = CasterUtil.getCaster(heldItem);
        Spell selectedSpell = caster.getSpell();

        if (!(selectedSpell.getCastMethod() instanceof MethodPantomime pantomime)) return;

        BlockPos pos = pantomime.findPosition(player).getBlockPos();
        poseStack.pushPose();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

        RenderType lineType = RenderType.lines();
        OutlineBufferSource buffer = Minecraft.getInstance().renderBuffers().outlineBufferSource();
        VertexConsumer lines = buffer.getBuffer(lineType);
        int color = selectedSpell.color.getColor();
        LevelRenderer.renderLineBox(poseStack, lines, 0, 0, 0, 1, 1, 1, FastColor.ARGB32.red(color) / 255F, FastColor.ARGB32.green(color) / 255F, FastColor.ARGB32.blue(color) / 255F, 1);

        poseStack.popPose();
    }
}
