package fifthcolumn.n.mixins;

import fifthcolumn.n.modules.FifthColumnTitleScreen;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LogoDrawer.class)
public class LogoDrawerMixin {
    @Unique
    private static final Identifier N_LOGO = new Identifier("nc:title.png");

    @Redirect(
        method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V",
            ordinal = 0
        ))
    public void redirectDrawLogo(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        FifthColumnTitleScreen fifthColumnTitleScreen = Modules.get().get(FifthColumnTitleScreen.class);
        if (fifthColumnTitleScreen.isActive()) {
            instance.drawTexture(N_LOGO, x + 5, y, u, v, width, 44, textureWidth, 44);
            return;
        }
        instance.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(
        method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V",
            ordinal = 1
        ))
    public void redirectDrawEditionLogo(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        FifthColumnTitleScreen fifthColumnTitleScreen = Modules.get().get(FifthColumnTitleScreen.class);
        if (!fifthColumnTitleScreen.isActive()) {
            instance.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
        }
    }
}
