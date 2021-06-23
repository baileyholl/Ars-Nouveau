package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class GlideEffect extends Effect {
    protected GlideEffect() {
        super(EffectType.BENEFICIAL, 8080895);
        setRegistryName(ArsNouveau.MODID, "glide");
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity ent, int p_76394_2_) {
        super.applyEffectTick(ent, p_76394_2_);
        if(ent.level.isClientSide && ent instanceof PlayerEntity){
            GlideClientHandler.tick(ent);
        }
    }
}
