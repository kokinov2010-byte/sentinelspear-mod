package com.cuferius.sentinelspear.client;

import com.cuferius.sentinelspear.item.SentinelChainSpearItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

/**
 * Cosmetic render layer: draws chain-link coils wound around the player's main-hand forearm
 * whenever the Sentinel Chain Spear is equipped. The chain mesh is parented to the arm's
 * ModelPart so it follows the arm through swing/attack/idle animations automatically.
 *
 * (Minecraft 1.21.1 player-renderer API: RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>)
 */
public class ChainArmLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation CHAIN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("sentinelspear", "textures/entity/chain_wrap.png");

    public ChainArmLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                        AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                        float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        boolean holdingSpear = player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SentinelChainSpearItem;
        if (!holdingSpear) return;

        boolean rightHandMain = player.getMainArm() == HumanoidArm.RIGHT;
        ModelPart arm = rightHandMain ? getParentModel().rightArm : getParentModel().leftArm;

        poseStack.pushPose();
        arm.translateAndRotate(poseStack);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(CHAIN_TEXTURE));
        renderChainCoils(poseStack, consumer, packedLight);

        poseStack.popPose();
    }

    /**
     * Draws several stacked quads around the forearm to simulate wound chain-link coils.
     * A production-quality version would swap this for a small baked cuboid "ring" ModelPart
     * (defined alongside the player model layer) textured with an actual chain-link texture;
     * this procedural approach keeps the mod self-contained without extra model assets.
     */
    private void renderChainCoils(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        int coils = 5;
        for (int i = 0; i < coils; i++) {
            poseStack.pushPose();
            poseStack.translate(0, -2.0 + i * 1.6, 0);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(i * 35f));

            float size = 2.2f;
            var matrix = poseStack.last().pose();
            var normal = poseStack.last().normal();

            consumer.addVertex(matrix, -size, 0, 0).setColor(255, 255, 255, 255)
                    .setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, size, 0, 0).setColor(255, 255, 255, 255)
                    .setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, size, 1.6f, 0).setColor(255, 255, 255, 255)
                    .setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, -size, 1.6f, 0).setColor(255, 255, 255, 255)
                    .setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(normal, 0, 1, 0);

            poseStack.popPose();
        }
    }
}
