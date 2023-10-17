package fifthcolumn.n.modules;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.NoFall;
import meteordevelopment.meteorclient.systems.modules.player.AntiHunger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.List;
import java.util.stream.Collectors;

public class FastProjectile extends Module {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final List<Item> BOWS = List.of(Items.BOW, Items.CROSSBOW);
    private static final List<Module> CONFLICTING_MODULES = List.of(FastProjectile.getModule(NoFall.class), FastProjectile.getModule(AntiHunger.class));

    private final SettingGroup sgGeneral;

    public final Setting<Integer> packetFactor;

    public FastProjectile() {
        super(NAddOn.FIFTH_COLUMN_CATEGORY, "FastProjectile", "Instakill with bows ;)");

        this.sgGeneral = this.settings.getDefaultGroup();

        this.packetFactor = this.sgGeneral.add(new IntSetting.Builder()
            .name("packet-factor")
            .description("How many packets to send, more packets means more power.")
            .defaultValue(100)
            .min(1)
            .max(200)
            .sliderRange(1, 200)
            .build()
        );
    }

    public boolean shouldEngage() {
        return this.isActive() && BOWS.contains(FastProjectile.mc.player.getMainHandStack().getItem());
    }

    public void engage() {
        List<Module> modules = FastProjectile.disengageConflictingModules();
        FastProjectile.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(FastProjectile.mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        for (int i = 0; i < (Integer) FastProjectile.getModule(FastProjectile.class).settings.get("Packet Factor").get(); ++i) {
            FastProjectile.mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(FastProjectile.mc.player.getX(), FastProjectile.mc.player.getY() - 1.0E-9, FastProjectile.mc.player.getZ(), true));
            FastProjectile.mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(FastProjectile.mc.player.getX(), FastProjectile.mc.player.getY() + 1.0E-9, FastProjectile.mc.player.getZ(), false));
        }
        FastProjectile.reengageModules(FastProjectile.disengageConflictingModules());
    }

    public static List<Module> disengageConflictingModules() {
        List<Module> modules = CONFLICTING_MODULES.stream().filter(Module::isActive).collect(Collectors.toList());
        modules.forEach(Module::toggle);
        return modules;
    }

    public static void reengageModules(List<Module> modules) {
        modules.forEach(Module::toggle);
    }

    private static Module getModule(Class module) {
        return Modules.get().get(module);
    }
}
