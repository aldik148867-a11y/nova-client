package nova;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NovaMenu extends Screen {
    public NovaMenu() { super(Component.literal("Nova Client")); }

    String t(String n, boolean v) { return n + ": " + (v ? "ON" : "OFF"); }

    @Override
    protected void init() {
        int x = width / 2 - 100, y = height / 2 - 55;
        addRenderableWidget(Button.builder(Component.literal(t("Keystrokes", Config.keystrokes)), b -> { Config.keystrokes = !Config.keystrokes; b.setMessage(Component.literal(t("Keystrokes", Config.keystrokes))); }).bounds(x, y, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal(t("CPS", Config.cps)), b -> { Config.cps = !Config.cps; b.setMessage(Component.literal(t("CPS", Config.cps))); }).bounds(x, y + 24, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal(t("FPS", Config.fps)), b -> { Config.fps = !Config.fps; b.setMessage(Component.literal(t("FPS", Config.fps))); }).bounds(x, y + 48, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal(t("Crosshair", Config.crosshair)), b -> { Config.crosshair = !Config.crosshair; b.setMessage(Component.literal(t("Crosshair", Config.crosshair))); }).bounds(x, y + 72, 200, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Crosshair color"), b -> {
            int[] c = {0xFF00FFAA, 0xFFFF3355, 0xFFFFFFFF, 0xFFFFDD00, 0xFF33AAFF};
            int i = 0; for (int k = 0; k < c.length; k++) if (c[k] == Config.crosshairColor) i = k;
            Config.crosshairColor = c[(i + 1) % c.length];
        }).bounds(x, y + 96, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float d) {
        g.fillGradient(0, 0, width, height, 0xDD0B1020, 0xDD1B0B30);
        g.drawCenteredString(font, "NOVA CLIENT", width / 2, height / 2 - 80, 0xFF00FFAA);
        super.render(g, mx, my, d);
    }
}
