package com.hollingsworth.arsnouveau.client.renderer.entity;

import com.hollingsworth.arsnouveau.common.entity.Goolem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.loading.json.raw.*;
import software.bernie.geckolib.loading.math.MolangQueries;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.BoneStructure;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

public class GoolemRenderer extends GeoEntityRenderer<Goolem> {
    public GoolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GoolemModel());
    }

    public BakedGeoModel constructGeoModel() {
        ModelProperties properties = new ModelProperties(null, null, null, null, null, null, null, null, null, null, null, null, 16, 16, null, new double[0], null);
        var parentModel =  getGeoModel().getBakedModel(getGeoModel().getModelResource(animatable));

        List<GeoBone> bones = new ObjectArrayList<>();
        bones.addAll(parentModel.topLevelBones());

        var copyBone = parentModel.topLevelBones().get(0);
        GeoBone geoBone = new GeoBone(null, copyBone.getName(), copyBone.getMirror(), copyBone.getInflate(), copyBone.shouldNeverRender(), copyBone.getReset());
        geoBone.getCubes().addAll(copyBone.getCubes());
        geoBone.setPosX(geoBone.getPosX() + 16);
        bones.add(geoBone);

        GeoBone geoBone2 = new GeoBone(null, copyBone.getName(), copyBone.getMirror(), copyBone.getInflate(), copyBone.shouldNeverRender(), copyBone.getReset());
        geoBone2.getCubes().addAll(copyBone.getCubes());
        geoBone2.setPosX(geoBone2.getPosX() + 16);
        geoBone2.setPosY(geoBone2.getPosY() + 16);
        bones.add(geoBone2);
//        List<BoneStructure> boneStructures = new ObjectArrayList<>();
//        boneStructures.add(new BoneStructure(fromGeobone(parentModel.topLevelBones().get(0)), new HashMap<>()));
//        for (BoneStructure boneStructure : boneStructures) {
//            bones.add(constructBone(boneStructure, properties, null));
//        }

        return new BakedGeoModel(bones, properties);
    }

//    public Bone fromGeobone(GeoBone bone){
//        var pivotArray = new double[3];
//        pivotArray[0]= (bone.getPivotX());
//        pivotArray[1] = (bone.getPivotY());
//        pivotArray[2] = (bone.getPivotZ());
//
//        var rotationArray =  new double[3];
//        rotationArray[0] = (bone.getRotX());
//        rotationArray[1] = (bone.getRotY());
//        rotationArray[2] = (bone.getRotZ());
//        var cubes = bone.getCubes();
//        var cubeArr = new Cube[cubes.size()];
//        for(int i = 0; i < cubes.size(); i++){
//            GeoCube geoCube = cubes.get(i);
//            var origin = new double[3];
//            origin[0] = geoCube.;
//            origin[1] = geoCube.getOriginY();
//            origin[2] = geoCube.getOriginZ();
//            var pivot = geoCube
//            cubeArr[i] = new Cube(geoCube.inflate(), geoCube.mirror(), geoCube.)
//        }
//        return new Bone(new double[0], bone.getCubes().toArray(new Cube[0]), null, null, null, null, null, null, null, pivotArray, null, null, null, rotationArray, null);
//    }

    public GeoBone constructBone(BoneStructure boneStructure, ModelProperties properties, GeoBone parent) {
        Bone bone = boneStructure.self();
        GeoBone newBone = new GeoBone(parent, bone.name(), bone.mirror(), bone.inflate(), bone.neverRender(), bone.reset());
        Vec3 rotation = RenderUtil.arrayToVec(bone.rotation());
        Vec3 pivot = RenderUtil.arrayToVec(bone.pivot());

        newBone.updateRotation((float)Math.toRadians(-rotation.x), (float)Math.toRadians(-rotation.y), (float)Math.toRadians(rotation.z));
        newBone.updatePivot((float)-pivot.x, (float)pivot.y, (float)pivot.z);

        for (Cube cube : bone.cubes()) {
            newBone.getCubes().add(constructCube(cube, properties, newBone, new Vec3(16, 0, 0)));
        }

        for (BoneStructure child : boneStructure.children().values()) {
            newBone.getChildBones().add(constructBone(child, properties, newBone));
        }

        return newBone;
    }

    public GeoCube constructCube(Cube cube, ModelProperties properties, GeoBone bone, Vec3 originOffset) {
        boolean mirror = cube.mirror() == Boolean.TRUE;
        double inflate = cube.inflate() != null ? cube.inflate() / 16f : (bone.getInflate() == null ? 0 : bone.getInflate() / 16f);
        Vec3 size = RenderUtil.arrayToVec(cube.size());
        Vec3 origin = RenderUtil.arrayToVec(cube.origin()).add(originOffset);
        Vec3 rotation = RenderUtil.arrayToVec(cube.rotation());
        Vec3 pivot = RenderUtil.arrayToVec(cube.pivot());
        origin = new Vec3(-(origin.x + size.x) / 16d, origin.y / 16d, origin.z / 16d);
        Vec3 vertexSize = size.multiply(1 / 16d, 1 / 16d, 1 / 16d);

        pivot = pivot.multiply(-1, 1, 1);
        rotation = new Vec3(Math.toRadians(-rotation.x), Math.toRadians(-rotation.y), Math.toRadians(rotation.z));
        GeoQuad[] quads = buildQuads(cube.uv(), new BakedModelFactory.VertexSet(origin, vertexSize, inflate), cube, (float)properties.textureWidth(), (float)properties.textureHeight(), mirror);

        return new GeoCube(quads, pivot, rotation, size, inflate, mirror);
    }

    GeoQuad[] buildQuads(UVUnion uvUnion, BakedModelFactory.VertexSet vertices, Cube cube, float textureWidth, float textureHeight, boolean mirror) {
        GeoQuad[] quads = new GeoQuad[6];

        quads[0] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.WEST);
        quads[1] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.EAST);
        quads[2] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.NORTH);
        quads[3] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.SOUTH);
        quads[4] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.UP);
        quads[5] = buildQuad(vertices, cube, uvUnion, textureWidth, textureHeight, mirror, Direction.DOWN);

        return quads;
    }


    GeoQuad buildQuad(BakedModelFactory.VertexSet vertices, Cube cube, UVUnion uvUnion, float textureWidth, float textureHeight, boolean mirror, Direction direction) {
        if (!uvUnion.isBoxUV()) {
            FaceUV faceUV = uvUnion.faceUV().fromDirection(direction);

            if (faceUV == null)
                return null;

            return GeoQuad.build(vertices.verticesForQuad(direction, false, mirror || cube.mirror() == Boolean.TRUE), faceUV.uv(), faceUV.uvSize(),
                    textureWidth, textureHeight, mirror, direction);
        }

        double[] uv = cube.uv().boxUVCoords();
        double[] uvSize = cube.size();
        Vec3 uvSizeVec = new Vec3(Math.floor(uvSize[0]), Math.floor(uvSize[1]), Math.floor(uvSize[2]));
        double[][] uvData = switch(direction) {
            case WEST -> new double[][] {
                    new double[] {uv[0] + uvSizeVec.z + uvSizeVec.x, uv[1] + uvSizeVec.z},
                    new double[] {uvSizeVec.z, uvSizeVec.y}
            };
            case EAST -> new double[][] {
                    new double[] { uv[0], uv[1] + uvSizeVec.z },
                    new double[] { uvSizeVec.z, uvSizeVec.y }
            };
            case NORTH -> new double[][] {
                    new double[] {uv[0] + uvSizeVec.z, uv[1] + uvSizeVec.z},
                    new double[] {uvSizeVec.x, uvSizeVec.y}
            };
            case SOUTH -> new double[][] {
                    new double[] {uv[0] + uvSizeVec.z + uvSizeVec.x + uvSizeVec.z, uv[1] + uvSizeVec.z},
                    new double[] {uvSizeVec.x, uvSizeVec.y }
            };
            case UP -> new double[][] {
                    new double[] {uv[0] + uvSizeVec.z, uv[1]},
                    new double[] {uvSizeVec.x, uvSizeVec.z}
            };
            case DOWN -> new double[][] {
                    new double[] {uv[0] + uvSizeVec.z + uvSizeVec.x, uv[1] + uvSizeVec.z},
                    new double[] {uvSizeVec.x, -uvSizeVec.z}
            };
        };

        return GeoQuad.build(vertices.verticesForQuad(direction, true, mirror || cube.mirror() == Boolean.TRUE), uvData[0], uvData[1], textureWidth, textureHeight, mirror, direction);
    }

    @Override
    public void defaultRender(PoseStack poseStack, Goolem animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.25f, 0.25f, 0.25f);
        int renderColor = getRenderColor(animatable, partialTick, packedLight).argbInt();
        int packedOverlay = getPackedOverlay(animatable, 0, partialTick);
        BakedGeoModel model = constructGeoModel();// getGeoModel().getBakedModel(getGeoModel().getModelResource(animatable));

        if (renderType == null)
            renderType = getRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick);

        if (buffer == null && renderType != null)
            buffer = bufferSource.getBuffer(renderType);

        preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);

        if (firePreRenderEvent(poseStack, model, bufferSource, partialTick, packedLight)) {
            preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, packedLight, packedLight, packedOverlay);
            actuallyRender(poseStack, animatable, model, renderType,
                    bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);
            applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            postRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, renderColor);
            firePostRenderEvent(poseStack, model, bufferSource, partialTick, packedLight);
        }

        poseStack.popPose();

        renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, renderColor);
        doPostRenderCleanup();
        MolangQueries.clearActor();
    }
}
