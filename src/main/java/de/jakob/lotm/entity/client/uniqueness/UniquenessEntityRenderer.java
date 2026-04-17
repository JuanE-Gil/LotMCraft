package de.jakob.lotm.entity.client.uniqueness;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.jakob.lotm.LOTMCraft;
import de.jakob.lotm.entity.custom.uniqueness.UniquenessEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Renders the UniquenessEntity as a floating, slowly rotating item on the ground,
 * using the corresponding pathway uniqueness item texture.
 */
@OnlyIn(Dist.CLIENT)
public class UniquenessEntityRenderer extends EntityRenderer<UniquenessEntity> {

    public UniquenessEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(UniquenessEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        String pathway = entity.getPathway();
        if (pathway.isEmpty()) return;

        // Get the item for this pathway uniqueness
        Item uniquenessItem = getUniquenessItem(pathway);
        if (uniquenessItem == null) return;

        poseStack.pushPose();

        // Bob up and down like ground items
        float tick = entity.getTicksExisted() + partialTick;
        float bobOffset = (float) Math.sin(tick * 0.1f) * 0.1f;

        // Translate to sit just above the ground
        poseStack.translate(0, 0.25 + bobOffset, 0);

        // Slowly rotate
        poseStack.mulPose(Axis.YP.rotationDegrees(tick * 3f));

        // Render the item
        this.entityRenderDispatcher.getItemInHandRenderer().renderItem(
                null,
                new ItemStack(uniquenessItem),
                ItemDisplayContext.GROUND,
                false,
                poseStack,
                bufferSource,
                packedLight
        );

        poseStack.popPose();
    }

    private Item getUniquenessItem(String pathway) {
        try {
            return net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                    ResourceLocation.fromNamespaceAndPath(LOTMCraft.MOD_ID, pathway + "_uniqueness")
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(UniquenessEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
