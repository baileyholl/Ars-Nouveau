package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.common.mixin.LivingAccessor;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import top.theillusivec4.curios.api.SlotContext;

public class JumpingRing extends ArsNouveauCurio {


    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        if(slotContext.entity().isOnGround()){

        }
    }

    public static void doJump(Player player){
        if(CuriosUtil.hasItem(player, ItemsRegistry.JUMP_RING.get())){
            LivingAccessor accessor = (LivingAccessor) player;
            double d0 = (double)accessor.callGetJumpPower() + player.getJumpBoostPower() + 0.1f;
            Vec3 vec3 = player.getDeltaMovement().scale(1.3f);
            Vec3 lookVec = player.getLookAngle();
            double lookScale = 0.7;
            player.setDeltaMovement(lookVec.x * lookScale, d0, lookVec.z * lookScale);
            player.hasImpulse = true;
            player.hurtMarked = true;
            player.fallDistance = 0;
            ForgeHooks.onLivingJump(player);
        }
    }
}
