package nova;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;

public class NovaClient implements ClientModInitializer {
    static KeyMapping menuKey;
    static final ArrayDeque<Long> lmb = new ArrayDeque<>(), rmb = new ArrayDeque<>();
    static boolean lPrev, rPrev;

    @Override
    public void onInitializeClient() {
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.novaclient.menu",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (menuKey.consumeClick()) mc.setScreen(new NovaMenu());
            long w = mc.getWindow().handle();
            boolean l = GLFW.glfwGetMouseButton(w, 0) == 1, r = GLFW.glfwGetMouseButton(w, 1) == 1;
            long now = System.currentTimeMillis();
            if (l && !lPrev) lmb.add(now);
            if (r && !rPrev) rmb.add(now);
            lPrev = l; rPrev = r;
            while (!lmb.isEmpty() && now - lmb.peek() > 1000) lmb.poll();
            while (!rmb.isEmpty() && now - rmb.peek() > 1000) rmb.poll();
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.CROSSHAIR,
                Identifier.fromNamespaceAndPath("novaclient", "hud"), NovaClient::render);
    }

    static void key(GuiGraphics g, Minecraft mc, String t, int x, int y, int w, int h, boolean down) {
        g.fill(x, y, x + w, y + h, down ? 0xCCFFFFFF : 0x88000000);
        g.drawString(mc.font, t, x + (w - mc.font.width(t)) / 2, y + (h - 8) / 2, down ? 0xFF000000 : 0xFFFFFFFF, false);
    }

    static int frames, fpsVal; static long fpsT;
    static int fps() { frames++; long n = System.currentTimeMillis(); if (n - fpsT >= 1000) { fpsVal = frames; frames = 0; fpsT = n; } return fpsVal; }

    static void render(GuiGraphics g, net.minecraft.client.DeltaTracker dt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;
        var o = mc.options;
        if (Config.crosshair) {
            int cx = g.guiWidth() / 2, cy = g.guiHeight() / 2, c = Config.crosshairColor;
            g.fill(cx - 6, cy - 1, cx - 2, cy + 1, c); g.fill(cx + 2, cy - 1, cx + 6, cy + 1, c);
            g.fill(cx - 1, cy - 6, cx + 1, cy - 2, c); g.fill(cx - 1, cy + 2, cx + 1, cy + 6, c);
        }
        if (Config.fps) g.drawString(mc.font, fps() + " FPS", 6, 6, 0xFFFFFFFF, true);
        if (Config.keystrokes) {
            int x = 6, y = 24, s = 20, p = 2;
            key(g, mc, "W", x + s + p, y, s, s, o.keyUp.isDown());
            key(g, mc, "A", x, y + s + p, s, s, o.keyLeft.isDown());
            key(g, mc, "S", x + s + p, y + s + p, s, s, o.keyDown.isDown());
            key(g, mc, "D", x + 2 * (s + p), y + s + p, s, s, o.keyRight.isDown());
            key(g, mc, "Space", x, y + 2 * (s + p), 3 * s + 2 * p, 14, o.keyJump.isDown());
            if (Config.cps) {
                key(g, mc, "LMB " + lmb.size(), x, y + 2 * (s + p) + 16, (3 * s + p) / 2, 16, lPrev);
                key(g, mc, "RMB " + rmb.size(), x + (3 * s + p) / 2 + p, y + 2 * (s + p) + 16, (3 * s + p) / 2, 16, rPrev);
            }
        }
    }
}
