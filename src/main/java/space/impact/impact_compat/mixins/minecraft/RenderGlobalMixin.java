package space.impact.impact_compat.mixins.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.impact.impact_compat.events.RenderDestroyBlockProgressEvent;
import space.impact.impact_compat.events.RenderSelectionBoxEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static net.minecraft.client.renderer.RenderGlobal.drawOutlinedBoundingBox;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {
	
	@Unique
	private final Map<Integer, DestroyBlockProgress[]> impact_compat$progressBlocks = new HashMap<>();
	@Shadow
	private int cloudTickCounter;
	@Shadow
	private WorldClient theWorld;
	@Final
	@Shadow
	private TextureManager renderEngine;
	@Shadow
	private RenderBlocks renderBlocksRg;
	@Shadow
	private IIcon[] destroyBlockIcons;
	
	@Inject(method = "destroyBlockPartially", at = @At(value = "HEAD"), cancellable = true)
	public void destroyBlockPartially$Compat(int miningPlayerEntId, int partialBlockX, int partialBlockY, int partialBlockZ, int progress, CallbackInfo ci) {
		if (progress >= 0 && progress < 10) {
			
			DestroyBlockProgress[] blocks = impact_compat$progressBlocks.get(miningPlayerEntId);
			
			if (blocks == null) {
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				if (player.getEntityId() == miningPlayerEntId) {
					DestroyBlockProgress block = new DestroyBlockProgress(miningPlayerEntId, partialBlockX, partialBlockY, partialBlockZ);
					RenderDestroyBlockProgressEvent event = new RenderDestroyBlockProgressEvent(player, block);
					MinecraftForge.EVENT_BUS.post(event);
					if (event.isCanceled()) {
						impact_compat$progressBlocks.put(miningPlayerEntId, new DestroyBlockProgress[]{block});
					} else {
						impact_compat$progressBlocks.put(miningPlayerEntId, event.getBlocks());
					}
				} else {
					DestroyBlockProgress block = new DestroyBlockProgress(miningPlayerEntId, partialBlockX, partialBlockY, partialBlockZ);
					impact_compat$progressBlocks.put(miningPlayerEntId, new DestroyBlockProgress[]{block});
				}
			}
			if (blocks != null) for (DestroyBlockProgress block : blocks) {
				block.setPartialBlockDamage(progress);
				block.setCloudUpdateTick(this.cloudTickCounter);
			}
		} else {
			impact_compat$progressBlocks.remove(miningPlayerEntId);
		}
		ci.cancel();
	}
	
	@Inject(method = "updateClouds", at = @At(value = "HEAD"), cancellable = true)
	public void updateClouds$Compat(CallbackInfo ci) {
		++this.cloudTickCounter;
		if (this.cloudTickCounter % 20 == 0) {
			Iterator<DestroyBlockProgress[]> iterator = impact_compat$progressBlocks.values().iterator();
			while (iterator.hasNext()) {
				try {
					for (DestroyBlockProgress block : iterator.next()) {
						int i = block.getCreationCloudUpdateTick();
						if (this.cloudTickCounter - i > 400) {
							iterator.remove();
						}
					}
				} catch (Exception ignore) {}
			}
		}
		ci.cancel();
	}
	
	@Inject(method = "drawBlockDamageTexture(Lnet/minecraft/client/renderer/Tessellator;Lnet/minecraft/entity/EntityLivingBase;F)V", at = @At(value = "HEAD"), cancellable = true, remap = false)
	public void drawBlockDamageTexture$Compat(Tessellator tes, EntityLivingBase entity, float fov, CallbackInfo ci) {
		double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) fov;
		double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) fov;
		double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) fov;
		
		if (!this.impact_compat$progressBlocks.isEmpty()) {
			OpenGlHelper.glBlendFunc(774, 768, 1, 0);
			this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
			GL11.glPushMatrix();
			GL11.glPolygonOffset(-3.0F, -3.0F);
			GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			tes.startDrawingQuads();
			tes.setTranslation(-d0, -d1, -d2);
			tes.disableColor();
			Iterator<DestroyBlockProgress[]> iterator = this.impact_compat$progressBlocks.values().iterator();
			while (iterator.hasNext()) {
				for (DestroyBlockProgress destroyblockprogress : iterator.next()) {
					double d3 = (double) destroyblockprogress.getPartialBlockX() - d0;
					double d4 = (double) destroyblockprogress.getPartialBlockY() - d1;
					double d5 = (double) destroyblockprogress.getPartialBlockZ() - d2;
					if (d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D) {
						iterator.remove();
					} else {
						Block block = this.theWorld.getBlock(destroyblockprogress.getPartialBlockX(), destroyblockprogress.getPartialBlockY(), destroyblockprogress.getPartialBlockZ());
						if (block.getMaterial() != Material.air) {
							this.renderBlocksRg.renderBlockUsingTexture(block, destroyblockprogress.getPartialBlockX(), destroyblockprogress.getPartialBlockY(), destroyblockprogress.getPartialBlockZ(), this.destroyBlockIcons[destroyblockprogress.getPartialBlockDamage()]);
						}
					}
				}
			}
			tes.draw();
			tes.setTranslation(0.0D, 0.0D, 0.0D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glPolygonOffset(0.0F, 0.0F);
			GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glDepthMask(true);
			GL11.glPopMatrix();
		}
		ci.cancel();
	}
	
	@Unique
	private void renderOutLine$Compat(EntityPlayer player, int x, int y, int z, float tick) {
		float f1 = 0.002F;
		Block block = this.theWorld.getBlock(x, y, z);
		if (block.getMaterial() != Material.air) {
			block.setBlockBoundsBasedOnState(this.theWorld, x, y, z);
			double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) tick;
			double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) tick;
			double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) tick;
			drawOutlinedBoundingBox(
					block.getSelectedBoundingBoxFromPool(this.theWorld, x, y, z)
							.expand(f1, f1, f1)
							.getOffsetBoundingBox(-d0, -d1, -d2),
					-1
			);
		}
	}
	
	@Inject(method = "drawSelectionBox", at = @At(value = "HEAD"), cancellable = true)
	public void drawSelectionBox$Compat(EntityPlayer player, MovingObjectPosition mop, int flag, float tick, CallbackInfo ci) {
		if (flag == 0 && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);
			
			RenderSelectionBoxEvent event = new RenderSelectionBoxEvent(player, new RenderSelectionBoxEvent.BlockCoordinate(mop.blockX, mop.blockY, mop.blockZ));
			MinecraftForge.EVENT_BUS.post(event);
			
			if (event.isCanceled()) {
				renderOutLine$Compat(player, mop.blockX, mop.blockY, mop.blockZ, tick);
			} else {
				for (RenderSelectionBoxEvent.BlockCoordinate block : event.getBlocks()) {
					renderOutLine$Compat(player, block.getX(), block.getY(), block.getZ(), tick);
				}
			}
			
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
		ci.cancel();
	}
}
