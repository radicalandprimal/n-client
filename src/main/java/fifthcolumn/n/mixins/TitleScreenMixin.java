package fifthcolumn.n.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;

@Environment(EnvType.CLIENT)
@Mixin(value = TitleScreen.class, priority = 1001)
public abstract class TitleScreenMixin extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger(TitleScreenMixin.class);
    private static final int BG_AMT = 25;

    @Shadow
    @Nullable
    private SplashTextRenderer splashText;

    private final Identifier backgroundId = new Identifier("nc:" + ThreadLocalRandom.current().nextInt(1, 26) + ".jpg");

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void n$modifySplashText(CallbackInfo ci) {
        if (this.splashText == null) {
            this.splashText = new SplashTextRenderer("Grief. Cope. Seethe. Repeat.");
        }
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/TitleScreen;PANORAMA_OVERLAY:Lnet/minecraft/util/Identifier;"))
    private Identifier n$modifyPanoramaOverlay() {
        return this.backgroundId;
    }
}
