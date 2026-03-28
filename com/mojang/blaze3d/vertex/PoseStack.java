package com.mojang.blaze3d.vertex;

import com.mojang.math.MatrixUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@OnlyIn(Dist.CLIENT)
public class PoseStack implements net.neoforged.neoforge.client.extensions.IPoseStackExtension {
    private final List<PoseStack.Pose> poses = new ArrayList<>(16);
    private int lastIndex;

    public PoseStack() {
        this.poses.add(new PoseStack.Pose());
    }

    public void translate(double p_85838_, double p_85839_, double p_85840_) {
        this.translate((float)p_85838_, (float)p_85839_, (float)p_85840_);
    }

    public void translate(float p_254202_, float p_253782_, float p_254238_) {
        this.last().translate(p_254202_, p_253782_, p_254238_);
    }

    public void translate(Vec3 p_364964_) {
        this.translate(p_364964_.x, p_364964_.y, p_364964_.z);
    }

    public void scale(float p_85842_, float p_85843_, float p_85844_) {
        this.last().scale(p_85842_, p_85843_, p_85844_);
    }

    public void mulPose(Quaternionfc p_404632_) {
        this.last().rotate(p_404632_);
    }

    public void rotateAround(Quaternionfc p_405292_, float p_273581_, float p_272655_, float p_273275_) {
        this.last().rotateAround(p_405292_, p_273581_, p_272655_, p_273275_);
    }

    public void pushPose() {
        PoseStack.Pose posestack$pose = this.last();
        this.lastIndex++;
        if (this.lastIndex >= this.poses.size()) {
            this.poses.add(posestack$pose.copy());
        } else {
            this.poses.get(this.lastIndex).set(posestack$pose);
        }
    }

    public void popPose() {
        if (this.lastIndex == 0) {
            throw new NoSuchElementException();
        } else {
            this.lastIndex--;
        }
    }

    public PoseStack.Pose last() {
        return this.poses.get(this.lastIndex);
    }

    public boolean isEmpty() {
        return this.lastIndex == 0;
    }

    public void setIdentity() {
        this.last().setIdentity();
    }

    public void mulPose(Matrix4fc p_405091_) {
        this.last().mulPose(p_405091_);
    }

    @OnlyIn(Dist.CLIENT)
    public static final class Pose {
        private final Matrix4f pose = new Matrix4f();
        private final Matrix3f normal = new Matrix3f();
        private boolean trustedNormals = true;

        private void computeNormalMatrix() {
            this.normal.set(this.pose).invert().transpose();
            this.trustedNormals = false;
        }

        public void set(PoseStack.Pose p_393654_) {
            this.pose.set(p_393654_.pose);
            this.normal.set(p_393654_.normal);
            this.trustedNormals = p_393654_.trustedNormals;
        }

        public Matrix4f pose() {
            return this.pose;
        }

        public Matrix3f normal() {
            return this.normal;
        }

        public Vector3f transformNormal(Vector3fc p_405499_, Vector3f p_324099_) {
            return this.transformNormal(p_405499_.x(), p_405499_.y(), p_405499_.z(), p_324099_);
        }

        public Vector3f transformNormal(float p_324226_, float p_324133_, float p_323766_, Vector3f p_324001_) {
            Vector3f vector3f = this.normal.transform(p_324226_, p_324133_, p_323766_, p_324001_);
            return this.trustedNormals ? vector3f : vector3f.normalize();
        }

        public Matrix4f translate(float p_405328_, float p_405694_, float p_405239_) {
            return this.pose.translate(p_405328_, p_405694_, p_405239_);
        }

        public void scale(float p_405219_, float p_405598_, float p_404747_) {
            this.pose.scale(p_405219_, p_405598_, p_404747_);
            if (Math.abs(p_405219_) == Math.abs(p_405598_) && Math.abs(p_405598_) == Math.abs(p_404747_)) {
                if (p_405219_ < 0.0F || p_405598_ < 0.0F || p_404747_ < 0.0F) {
                    this.normal.scale(Math.signum(p_405219_), Math.signum(p_405598_), Math.signum(p_404747_));
                }
            } else {
                this.normal.scale(1.0F / p_405219_, 1.0F / p_405598_, 1.0F / p_404747_);
                this.trustedNormals = false;
            }
        }

        public void rotate(Quaternionfc p_404809_) {
            this.pose.rotate(p_404809_);
            this.normal.rotate(p_404809_);
        }

        public void rotateAround(Quaternionfc p_405434_, float p_404758_, float p_405683_, float p_404743_) {
            this.pose.rotateAround(p_405434_, p_404758_, p_405683_, p_404743_);
            this.normal.rotate(p_405434_);
        }

        public void setIdentity() {
            this.pose.identity();
            this.normal.identity();
            this.trustedNormals = true;
        }

        public void mulPose(Matrix4fc p_405608_) {
            this.pose.mul(p_405608_);
            if (!MatrixUtil.isPureTranslation(p_405608_)) {
                if (MatrixUtil.isOrthonormal(p_405608_)) {
                    this.normal.mul(new Matrix3f(p_405608_));
                } else {
                    this.computeNormalMatrix();
                }
            }
        }

        public PoseStack.Pose copy() {
            PoseStack.Pose posestack$pose = new PoseStack.Pose();
            posestack$pose.set(this);
            return posestack$pose;
        }
    }
}
