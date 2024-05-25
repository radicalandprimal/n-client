package fifthcolumn.n.mixins;

import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(RotatingCubeMapRenderer.class)
public abstract class RotatingCubeMapRendererMixin {
    @Unique
    private final Identifier backgroundId = new Identifier(
        "nc:" + ThreadLocalRandom.current().nextInt(1, 26) + ".png");

    @Redirect(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;OVERLAY_TEXTURE:Lnet/minecraft/util/Identifier;"
        )
    )
    private Identifier n$modifyPanoramaOverlay() {
        return this.backgroundId;
    }
}
