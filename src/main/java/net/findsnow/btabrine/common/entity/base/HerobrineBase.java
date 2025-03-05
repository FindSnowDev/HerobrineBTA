package net.findsnow.btabrine.common.entity.base;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.MobPathfinder;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class HerobrineBase extends MobPathfinder {
	public HerobrineBase(@Nullable World world) {
		super(world);
		this.textureIdentifier = NamespaceID.getPermanent("btabrine", "herobrine");
		this.fireImmune = true;
		this.setSize(0.6F, 1.8F);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(20, (byte) 1, Byte.class); // For glowing eyes
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("Eyes", this.getEyesGlow());
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setEyesGlow(tag.getBoolean("Eyes"));
	}

	public boolean getEyesGlow() {
		return (this.entityData.getByte(20) & 1) != 0;
	}

	public void setEyesGlow(boolean flag) {
		if (flag) {
			this.entityData.set(20, (byte) 1);
		} else {
			this.entityData.set(20, (byte) 0);
		}
	}

	@Override
	protected String getHurtSound() {
		return null;
	}

	@Override
	protected String getDeathSound() {
		return null;
	}
}
