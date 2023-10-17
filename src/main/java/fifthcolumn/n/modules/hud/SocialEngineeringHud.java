package fifthcolumn.n.modules.hud;

import fifthcolumn.n.NMod;
import fifthcolumn.n.client.Input;
import fifthcolumn.n.copenheimer.CopeService;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SocialEngineeringHud extends HudElement {
    public static final HudElementInfo<SocialEngineeringHud> INFO = new HudElementInfo<>(Hud.GROUP, "Social engineering", "Im friends with Chris", SocialEngineeringHud::new);

    private static final Color RED = new Color(255, 0, 0);

    private final List<CopeService.ServerPlayer> playerList = new ArrayList<>();

    private final CopeService copeService = NMod.getCopeService();
    private final MinecraftClient mc = MinecraftClient.getInstance();

    private boolean isCracked;

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<SettingColor> textColor = this.sgGeneral.add(new ColorSetting.Builder()
        .name("text-color")
        .description("A.")
        .defaultValue(new SettingColor())
        .build()
    );

    public SocialEngineeringHud() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        super.tick(renderer);

        double width = renderer.textWidth("Players:");
        double height = renderer.textHeight();
        if (this.mc.world == null) {
            this.box.setSize(width, height);
            return;
        }

        List<CopeService.ServerPlayer> list = this.playerList;
        synchronized (list) {
            for (CopeService.ServerPlayer entity : this.playerList) {
                String text = entity.name;
                width = Math.max(width, renderer.textWidth(text));
                height += renderer.textHeight() + 2.0;
            }
        }

        this.box.setSize(width, height);

        this.copeService.findHistoricalPlayers(serverPlayers -> {
            synchronized (list) {
                this.playerList.clear();
                this.playerList.addAll(this.filterList(serverPlayers));
                this.isCracked = this.playerList.stream().anyMatch(serverPlayer -> serverPlayer.isValid != null && serverPlayer.isValid == false || !Input.isValidMinecraftUsername(serverPlayer.name));
            }
        });
    }

    private List<CopeService.ServerPlayer> filterList(List<CopeService.ServerPlayer> list) {
        Stream<CopeService.ServerPlayer> playerListStream = list.stream();
        if (list.size() > 50) {
            playerListStream = playerListStream.filter(serverPlayer -> serverPlayer.isValid != null && serverPlayer.isValid != false);
        }
        return playerListStream.limit(100L).collect(Collectors.toList());
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = this.x;
        double y = this.y;
        renderer.text("Historical Players " + (this.isCracked ? "(cracked)" : ""), x, y, this.isCracked ? RED : this.textColor.get(), true);

        if (this.mc.world == null) return;

        List<CopeService.ServerPlayer> list = this.playerList;
        synchronized (list) {
            for (CopeService.ServerPlayer entity : this.playerList) {
                x = this.x;
                String text = entity.name;
                Color color = entity.isValid == null ? Color.ORANGE : (entity.isValid != false ? Color.GREEN : Color.RED);
                renderer.text(text, x, y += renderer.textHeight() + 2.0, color, true);
            }
        }
    }
}
