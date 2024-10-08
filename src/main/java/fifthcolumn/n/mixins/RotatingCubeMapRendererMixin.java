package fifthcolumn.n.mixins;

import fifthcolumn.n.modules.FifthColumnTitleScreen;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(RotatingCubeMapRenderer.class)
public abstract class RotatingCubeMapRendererMixin {
    @Shadow
    @Final
    public static Identifier OVERLAY_TEXTURE;

    @Unique
    private final Identifier backgroundId = Identifier.of(
        "nc:" + ThreadLocalRandom.current().nextInt(1, 26) + ".png");

    @Redirect(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;OVERLAY_TEXTURE:Lnet/minecraft/util/Identifier;"
        )
    )
    private Identifier n$modifyPanoramaOverlay() {
        FifthColumnTitleScreen fifthColumnTitleScreen = Modules.get().get(FifthColumnTitleScreen.class);
        if (fifthColumnTitleScreen.isActive()) {
            return this.backgroundId;
        }
        return OVERLAY_TEXTURE;
    }
}
