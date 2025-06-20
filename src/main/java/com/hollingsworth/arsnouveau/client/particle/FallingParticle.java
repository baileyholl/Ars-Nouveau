package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class FallingParticle extends PropParticle{
    public Fluid type;
    public SoundEvent landingSound;

    protected FallingParticle(PropertyParticleOptions options, ClientLevel level, double x, double y, double z) {
        super(options, level, x, y, z);
    }

    public FallingParticle(PropertyParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(options, level, x, y, z, xSpeed, ySpeed, zSpeed);
        float scale = 0.5f;
        xd = xd * scale;
        yd = yd * scale;
        zd = zd * scale;
        this.setSize(0.01F, 0.01F);
        this.gravity = 0.03F;
        this.lifetime = 20;
    }

    @Override
    public @Nullable ParticleColor getDefaultColor() {
        return ParticleColor.BLUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (!this.removed) {
            this.yd = this.yd - (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.postMoveUpdate();
            if (!this.removed) {
                this.xd *= 0.98F;
                this.yd *= 0.98F;
                this.zd *= 0.98F;
                if (this.type != Fluids.EMPTY) {
                    BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
                    FluidState fluidstate = this.level.getFluidState(blockpos);
                    if (fluidstate.getType() == this.type && this.y < (double)((float)blockpos.getY() + fluidstate.getHeight(this.level, blockpos))) {
                        this.remove();
                    }
                }
            }
        }
    }

    protected void postMoveUpdate() {
        if (this.onGround && landingSound != null) {
            this.remove();
            if(this.random.nextDouble() > 0.5f) {
                float f = Mth.randomBetween(this.random, 0.3F, 1.0F);
                level.playLocalSound(x, y, z, landingSound, SoundSource.NEUTRAL, f, 1.0F, false);
            }
        }
    }

    @Override
    public boolean tinted() {
        return true;
    }
}
