package io.github.minnecraeft.toughenough.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.minnecraeft.toughenough.ToughEnoughComponents;
import io.github.minnecraeft.toughenough.api.debug.DebugMonitor;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.CompoundTag;

public final class DebugHudRenderer extends DrawableHelper {

    private static final int FONT_COLOR = 255 << 16 | 255 << 8 | 255;

    private final MinecraftClient client;

    private DebugHudRenderer() {
        client = MinecraftClient.getInstance();
    }

    public static void register() {
        DebugHudRenderer renderer = new DebugHudRenderer();
        HudRenderCallback.EVENT.register(renderer::render);
    }

    private void render(MatrixStack matrices, float delta) {
        DebugMonitor monitor;

        if (HudRenderHelper.skipStatusElements()) return;

        monitor = ToughEnoughComponents.DEBUGGER_MONITOR.get(HudRenderHelper.getCameraPlayer());

        if (!monitor.isDebugging()) return;

        matrices.push();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        {
            TextRenderer renderer = client.textRenderer;

            CompoundTag root = monitor.section();
            int x = 5;
            for (String name : root.getKeys()) {
                if (root.contains(name, NbtType.COMPOUND)) {
                    CompoundTag section = root.getCompound(name);
                    drawSections(matrices, renderer, x, 0, section);
                }
                x += 100;
            }
        }
        RenderSystem.disableBlend();
        matrices.pop();

    }

    private int drawSections(MatrixStack matrices, TextRenderer renderer, int x, int y, CompoundTag sections) {
        for (String name : sections.getKeys().stream().sorted().toArray(String[]::new)) {
            if (!sections.contains(name, NbtType.COMPOUND)) {
                String text = name + ": " + sections.get(name);
                drawStringWithShadow(matrices, renderer, text, x, y += 10, FONT_COLOR);
            } else {
                drawStringWithShadow(matrices, renderer, name, x, y += 10, FONT_COLOR);
                y = drawSections(matrices, renderer, x + 5, y, sections.getCompound(name));
            }
        }
        return y;
    }

}
