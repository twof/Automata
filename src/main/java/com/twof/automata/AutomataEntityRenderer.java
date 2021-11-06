package com.twof.automata;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class AutomataEntityRenderer extends LivingEntityRenderer<AutomataEntity, AutomataEntityModel> {
    public AutomataEntityRenderer(EntityRendererFactory.Context ctx, boolean slim) {
        super(ctx, new AutomataEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim), 0.5F);
        this.addFeature(new ArmorFeatureRenderer(this, new BipedEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)), new BipedEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR))));
        this.addFeature(new PlayerHeldItemFeatureRenderer(this));
        this.addFeature(new StuckArrowsFeatureRenderer(ctx, this));
        this.addFeature(new HeadFeatureRenderer(this, ctx.getModelLoader()));
        this.addFeature(new ElytraFeatureRenderer(this, ctx.getModelLoader()));
        this.addFeature(new TridentRiptideFeatureRenderer(this, ctx.getModelLoader()));
        this.addFeature(new StuckStingersFeatureRenderer(this));
    }

    public void render(AutomataEntity automataEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.setModelPose(automataEntity);
        super.render(automataEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Vec3d getPositionOffset(AutomataEntity automataEntity, float f) {
        return automataEntity.isInSneakingPose() ? new Vec3d(0.0D, -0.125D, 0.0D) : super.getPositionOffset(automataEntity, f);
    }

    private void setModelPose(AutomataEntity player) {
        AutomataEntityModel playerEntityModel = this.getModel();
        if (player.isSpectator()) {
            playerEntityModel.setVisible(false);
            playerEntityModel.head.visible = true;
            playerEntityModel.hat.visible = true;
        } else {
            playerEntityModel.setVisible(true);
//            playerEntityModel.hat.visible = player.isPartVisible(PlayerModelPart.HAT);
//            playerEntityModel.jacket.visible = player.isPartVisible(PlayerModelPart.JACKET);
//            playerEntityModel.leftPants.visible = player.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
//            playerEntityModel.rightPants.visible = player.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
//            playerEntityModel.leftSleeve.visible = player.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
//            playerEntityModel.rightSleeve.visible = player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
            playerEntityModel.sneaking = player.isInSneakingPose();
            BipedEntityModel.ArmPose armPose = getArmPose(player, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose armPose2 = getArmPose(player, Hand.OFF_HAND);
            if (armPose.isTwoHanded()) {
                armPose2 = player.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
            }

            if (player.getMainArm() == Arm.RIGHT) {
                playerEntityModel.rightArmPose = armPose;
                playerEntityModel.leftArmPose = armPose2;
            } else {
                playerEntityModel.rightArmPose = armPose2;
                playerEntityModel.leftArmPose = armPose;
            }
        }

    }

    private static BipedEntityModel.ArmPose getArmPose(AutomataEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return BipedEntityModel.ArmPose.EMPTY;
        } else {
            if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
                UseAction useAction = itemStack.getUseAction();
                if (useAction == UseAction.BLOCK) {
                    return BipedEntityModel.ArmPose.BLOCK;
                }

                if (useAction == UseAction.BOW) {
                    return BipedEntityModel.ArmPose.BOW_AND_ARROW;
                }

                if (useAction == UseAction.SPEAR) {
                    return BipedEntityModel.ArmPose.THROW_SPEAR;
                }

                if (useAction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
                    return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useAction == UseAction.SPYGLASS) {
                    return BipedEntityModel.ArmPose.SPYGLASS;
                }
            } else if (!player.handSwinging && itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
                return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
            }

            return BipedEntityModel.ArmPose.ITEM;
        }
    }

    public Identifier getTexture(AutomataEntity automataEntity) {
        return new Identifier("textures/entity/steve.png");
    }

    protected void scale(AutomataEntity automataEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375F;
        matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    protected void renderLabelIfPresent(AutomataEntity automataEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
//        double d = this.dispatcher.getSquaredDistanceToCamera(automataEntity);
        matrixStack.push();
//        if (d < 100.0D) {
//            Scoreboard scoreboard = automataEntity.getScoreboard();
//            ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(2);
//            if (scoreboardObjective != null) {
//                ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(automataEntity.getEntityName(), scoreboardObjective);
//                super.renderLabelIfPresent(automataEntity, (new LiteralText(Integer.toString(scoreboardPlayerScore.getScore()))).append(" ").append(scoreboardObjective.getDisplayName()), matrixStack, vertexConsumerProvider, i);
//                Objects.requireNonNull(this.getTextRenderer());
//                matrixStack.translate(0.0D, (double)(9.0F * 1.15F * 0.025F), 0.0D);
//            }
//        }

        super.renderLabelIfPresent(automataEntity, text, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AutomataEntity player) {
        this.renderArm(matrices, vertexConsumers, light, player, (this.model).rightArm, (this.model).rightSleeve);
    }

    public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AutomataEntity player) {
        this.renderArm(matrices, vertexConsumers, light, player, (this.model).leftArm, (this.model).leftSleeve);
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AutomataEntity player, ModelPart arm, ModelPart sleeve) {
        AutomataEntityModel playerEntityModel = this.getModel();
        this.setModelPose(player);
        playerEntityModel.handSwingProgress = 0.0F;
        playerEntityModel.sneaking = false;
        playerEntityModel.leaningPitch = 0.0F;
        playerEntityModel.setAngles(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        arm.pitch = 0.0F;
        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(new Identifier("textures/entity/steve.png"))), light, OverlayTexture.DEFAULT_UV);
        sleeve.pitch = 0.0F;
        sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(new Identifier("textures/entity/steve.png"))), light, OverlayTexture.DEFAULT_UV);
    }

    protected void setupTransforms(AutomataEntity automataEntity, MatrixStack matrixStack, float f, float g, float h) {
        float i = automataEntity.getLeaningPitch(h);
        float n;
        float k;
        if (automataEntity.isFallFlying()) {
            super.setupTransforms(automataEntity, matrixStack, f, g, h);
            n = (float)automataEntity.getRoll() + h;
            k = MathHelper.clamp(n * n / 100.0F, 0.0F, 1.0F);
            if (!automataEntity.isUsingRiptide()) {
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(k * (-90.0F - automataEntity.getPitch())));
            }

            Vec3d vec3d = automataEntity.getRotationVec(h);
            Vec3d vec3d2 = automataEntity.getVelocity();
            double d = vec3d2.horizontalLengthSquared();
            double e = vec3d.horizontalLengthSquared();
            if (d > 0.0D && e > 0.0D) {
                double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion((float)(Math.signum(m) * Math.acos(l))));
            }
        } else if (i > 0.0F) {
            super.setupTransforms(automataEntity, matrixStack, f, g, h);
            n = automataEntity.isTouchingWater() ? -90.0F - automataEntity.getPitch() : -90.0F;
            k = MathHelper.lerp(i, 0.0F, n);
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(k));
            if (automataEntity.isInSwimmingPose()) {
                matrixStack.translate(0.0D, -1.0D, 0.30000001192092896D);
            }
        } else {
            super.setupTransforms(automataEntity, matrixStack, f, g, h);
        }
    }
}
