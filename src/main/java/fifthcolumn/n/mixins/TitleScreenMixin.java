package fifthcolumn.n.mixins;

import fifthcolumn.n.modules.FifthColumnTitleScreen;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = TitleScreen.class, priority = 1001)
public abstract class TitleScreenMixin extends Screen {
    private static final Logger LOGGER = LoggerFactory.getLogger(TitleScreenMixin.class);
    private static final int BG_AMT = 25;

    @Shadow
    @Nullable
    private SplashTextRenderer splashText;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void n$modifySplashText(CallbackInfo ci) {
        if (this.splashText == null) {
            FifthColumnTitleScreen fifthColumnTitleScreen = Modules.get().get(FifthColumnTitleScreen.class);
            if (fifthColumnTitleScreen.isActive()) {
                this.splashText = new SplashTextRenderer("Grief. Cope. Seethe. Repeat.");
            }
        }
    }
}
