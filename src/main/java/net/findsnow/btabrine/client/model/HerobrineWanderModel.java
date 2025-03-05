package net.findsnow.btabrine.client.model;

import net.minecraft.client.render.model.Cube;
import net.minecraft.client.render.model.ModelBase;
import net.minecraft.core.util.helper.MathHelper;

public class HerobrineWanderModel extends ModelBase {

	public Cube head;
	public Cube body;
	public Cube rightArm;
	public Cube leftArm;
	public Cube rightLeg;
	public Cube leftLeg;

	private boolean isSitting = false;
	private boolean isSprinting = false;

	public HerobrineWanderModel(float expandAmount) {
		head = new Cube(0, 0);
		head.addBox(-4, -8f, -4f, 8, 8, 8, expandAmount);
		head.setRotationPoint(0f, 0f, 0f);

		body = new Cube(16, 16);
		body.addBox(-4F, 0F, -2F, 8, 12, 4, expandAmount);
		body.setRotationPoint(0F, 0F, 0F);

		rightArm = new Cube(40, 16);
		rightArm.addBox(-3F, -2F, -2F, 4, 12, 4, expandAmount);
		rightArm.setRotationPoint(-5F, 2F, 0F);

		leftArm = new Cube(40, 16);
		leftArm.addBox(-1F, -2F, -2F, 4, 12, 4, expandAmount);
		leftArm.setRotationPoint(5F, 2F, 0F);

		rightLeg = new Cube(0, 16);
		rightLeg.addBox(-2F, 0F, -2F, 4, 12, 4, expandAmount);
		rightLeg.setRotationPoint(-2F, 12F, 0F);

		leftLeg = new Cube(0, 16);
		leftLeg.addBox(-2F, 0F, -2F, 4, 12, 4, expandAmount);
		leftLeg.setRotationPoint(2F, 12F, 0F);
	}

	@Override
	public void render(float limbSwing, float limbYaw, float limbPitch, float headYaw, float headPitch, float scale) {
		super.render(limbSwing, limbYaw, limbPitch, headYaw, headPitch, scale);
		this.setRotationAngles(limbSwing, limbYaw, limbPitch, headYaw, headPitch, scale);
		this.head.renderWithRotation(scale);
		this.body.render(scale);
		this.rightArm.render(scale);
		this.leftArm.render(scale);
		this.rightLeg.render(scale);
		this.leftLeg.render(scale);
	}

	public void setRotationAngles(float limbSwing, float limbYaw, float limbPitch, float headYaw, float headPitch, float scale) {
		head.xRot = headPitch / (180F / (float)Math.PI);
		head.yRot = headYaw / (180F / (float)Math.PI);

		if (!getSitting()) {
			if (getSprinting()) {
				float armSwing = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbYaw;
				float oppositeArmSwing = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbYaw;

				rightArm.xRot = oppositeArmSwing;
				leftArm.xRot = armSwing;
				rightLeg.xRot = armSwing;
				leftLeg.xRot = oppositeArmSwing;
				body.xRot = 0.3F;
			} else {
				rightArm.xRot = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbYaw * 0.5F;
				leftArm.xRot = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbYaw * 0.5F;
				rightLeg.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbYaw;
				leftLeg.xRot = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbYaw;
				body.xRot = 0.0F;
			}
		} else {
			rightArm.xRot = 0.0F;
			leftArm.xRot = 0.0F;
			rightLeg.xRot = -1.4137167F;
			leftLeg.xRot = -1.4137167F;
			rightLeg.yRot = 0.31415927F;
			leftLeg.yRot = -0.31415927F;
		}
	}

	@Override
	public void setupAnimation(float limbSwing, float limbYaw, float limbPitch, float headYaw, float headPitch, float scale) {
		head.xRot = headPitch / (180F / (float)Math.PI);
		head.yRot = headYaw / (180F / (float)Math.PI);

		if (!getSitting()) {
			if (getSprinting()) {
				float armSwing = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbYaw;
				float oppositeArmSwing = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbYaw;

				rightArm.xRot = oppositeArmSwing;
				leftArm.xRot = armSwing;
				rightLeg.xRot = armSwing;
				leftLeg.xRot = oppositeArmSwing;
				body.xRot = 0.3F;
			} else {
				rightArm.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.0F * limbYaw;
				leftArm.xRot = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.0F * limbYaw;
				rightLeg.xRot = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.0F * limbYaw;
				leftLeg.xRot = MathHelper.cos(limbSwing * 0.6662F) * 1.0F * limbYaw;

				body.xRot = 0.0F;
			}
		} else {
			rightArm.xRot = 0.0F;
			leftArm.xRot = 0.0F;
			rightLeg.xRot = -1.4137167F;
			leftLeg.xRot = -1.4137167F;
			rightLeg.yRot = 0.31415927F;
			leftLeg.yRot = -0.31415927F;
		}

		rightArm.zRot = 0.0F;
		leftArm.zRot = 0.0F;
	}

	public boolean getSitting() {
		return this.isSitting;
	}

	public boolean getSprinting() {
		return this.isSprinting;
	}

	public void setSitting(boolean sitting) {
		this.isSitting = sitting;
	}

	public void setSprinting(boolean sprinting) {
		this.isSprinting = sprinting;
	}
}
