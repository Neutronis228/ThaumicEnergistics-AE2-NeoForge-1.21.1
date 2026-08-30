package com.neutronis.thaumicenergistics.client;

import appeng.api.client.AEKeyRenderHandler;
import appeng.api.client.AEKeyRendering;
import com.mojang.blaze3d.vertex.PoseStack;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKey;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKeyType;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import thaumcraft.api.aspects.Aspect;

/** Renders native EssentiaKey values using Thaumcraft's aspect artwork and colors. */
public final class EssentiaKeyRenderer implements AEKeyRenderHandler<EssentiaKey> {
    private static final int ASPECT_TEXTURE_SIZE = 32;

    public static void initialize(IEventBus bus) {
        bus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(() ->
                AEKeyRendering.register(EssentiaKeyType.INSTANCE, EssentiaKey.class, new EssentiaKeyRenderer())));
    }

    @Override
    public void drawInGui(Minecraft minecraft, GuiGraphics graphics, int x, int y, EssentiaKey key) {
        Aspect aspect = key.aspect();
        if (aspect == null) {
            return;
        }

        int color = aspect.getColor();
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        graphics.setColor(red, green, blue, 1.0F);
        graphics.blit(
                aspect.getImage(),
                x,
                y,
                16,
                16,
                0.0F,
                0.0F,
                ASPECT_TEXTURE_SIZE,
                ASPECT_TEXTURE_SIZE,
                ASPECT_TEXTURE_SIZE,
                ASPECT_TEXTURE_SIZE);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void drawOnBlockFace(
            PoseStack poseStack,
            MultiBufferSource buffers,
            EssentiaKey key,
            float scale,
            int combinedLight,
            Level level) {
        Aspect aspect = key.aspect();
        if (aspect == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, 0.01F);
        scale -= 0.05F;

        float x0 = -scale / 2.0F;
        float y0 = scale / 2.0F;
        float x1 = scale / 2.0F;
        float y1 = -scale / 2.0F;
        int argb = 0xFF000000 | aspect.getColor();

        var transform = poseStack.last().pose();
        var buffer = buffers.getBuffer(RenderType.entityCutout(aspect.getImage()));
        buffer.addVertex(transform, x0, y1, 0.0F)
                .setColor(argb)
                .setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(combinedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
        buffer.addVertex(transform, x1, y1, 0.0F)
                .setColor(argb)
                .setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(combinedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
        buffer.addVertex(transform, x1, y0, 0.0F)
                .setColor(argb)
                .setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(combinedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
        buffer.addVertex(transform, x0, y0, 0.0F)
                .setColor(argb)
                .setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(combinedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
        poseStack.popPose();
    }

    @Override
    public Component getDisplayName(EssentiaKey key) {
        return key.getDisplayName();
    }

    @Override
    public List<Component> getTooltip(EssentiaKey key) {
        return List.of(
                key.getDisplayName(),
                Component.translatable("keytype.thaumicenergistics.essentia"));
    }
}
