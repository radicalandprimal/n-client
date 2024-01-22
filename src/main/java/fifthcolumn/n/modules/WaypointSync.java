package fifthcolumn.n.modules;

import fifthcolumn.n.NMod;
import fifthcolumn.n.copenheimer.CopeService;
import fifthcolumn.n.events.GrieferUpdateEvent;
import fifthcolumn.n.events.PlayerSpawnPositionEvent;
import fifthcolumn.n.events.SpawnPlayerEvent;
import fifthcolumn.n.utils.BlockPosUtils;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class WaypointSync extends Module {
    public WaypointSync() {
        super(NAddOn.FIFTH_COLUMN_CATEGORY, "Waypoint Sync", "Syncs your waypoints. Disabled on 2b2t.org.");
    }

    @EventHandler
    public void spawnPosition(PlayerSpawnPositionEvent event) {
        this.mc.execute(() -> {
            Waypoints waypoints = Waypoints.get();
            waypoints.add(new Waypoint.Builder().name("Spawn").pos(event.blockPos()).build());
            waypoints.save();
        });
    }

    @EventHandler
    private void onPlayerSeen(SpawnPlayerEvent event) {
        this.mc.world.getPlayers().stream().filter(player -> player.getGameProfile().getId().equals(event.uuid())).findFirst().ifPresent(player -> {
            long count = NMod.getCopeService().griefers().stream().filter(griefer -> griefer.playerId.equals(event.uuid())).count();
            if (count > 0L) return;

            Waypoints waypoints = Waypoints.get();
            String playerName = "Player " + player.getEntityName();
            Waypoint existingWaypoint = waypoints.get(playerName);
            if (existingWaypoint != null) return;

            Waypoint.Builder waypointBuilder = new Waypoint.Builder();
            waypointBuilder.name(playerName);
            waypointBuilder.pos(event.blockPos());
            waypointBuilder.icon("5c");

            Waypoint waypoint = waypointBuilder.build();
            waypoints.add(waypoint);
            waypoints.save();
        });
    }

    @EventHandler
    public void griefersUpdated(GrieferUpdateEvent event) {
        if (!Modules.get().get(WaypointSync.class).isActive()) return;

        ServerInfo currentServer = this.mc.getCurrentServerEntry();
        if (currentServer == null || this.mc.player == null) return;

        Map<String, CopeService.Waypoint> remoteWaypoints = event.griefers.stream()
            .filter(griefer -> !griefer.playerName.equalsIgnoreCase(this.mc.player.getEntityName()))
            .filter(griefer -> griefer.serverAddress.equalsIgnoreCase(currentServer.address))
            .flatMap(griefer -> griefer.waypoints.stream())
            .collect(Collectors.toMap(waypoint -> waypoint.name, waypoint -> waypoint, (waypoint, waypoint2) -> waypoint));

        this.mc.execute(() -> {
            Waypoints waypoints = Waypoints.get();
            boolean waypointUpdated = false;

            for (Map.Entry<String, CopeService.Waypoint> entry : remoteWaypoints.entrySet()) {
                String s = entry.getKey();
                CopeService.Waypoint remoteWaypoint = entry.getValue();

                Waypoint existingWaypoint = waypoints.get(remoteWaypoint.name);
                if (existingWaypoint != null && existingWaypoint.name.get().equals(remoteWaypoint.name)) continue;

                Waypoint.Builder waypointBuilder = new Waypoint.Builder();
                waypointBuilder.name(remoteWaypoint.name);
                waypointBuilder.pos(BlockPosUtils.from(remoteWaypoint.position));
                waypointBuilder.icon("5c");
                waypointBuilder = remoteWaypoint.position.dimension.equals("OVERWORLD")
                    ? waypointBuilder.dimension(Dimension.Nether)
                    : (remoteWaypoint.position.dimension.equals("END")
                        ? waypointBuilder.dimension(Dimension.End)
                        : waypointBuilder.dimension(Dimension.Overworld));

                Waypoint waypoint = waypointBuilder.build();
                waypoints.add(waypoint);
                waypointUpdated = true;
            }

            if (waypointUpdated) {
                waypoints.save();
            }
        });
    }

    static {
        try (InputStream inputStream = WaypointSync.class.getResourceAsStream("/assets/nc/5c.png")) {
            Waypoints.get().icons.put("5c", new NativeImageBackedTexture(NativeImage.read(Objects.requireNonNull(inputStream))));
        } catch (IOException e) {
            throw new RuntimeException("did not load 5c icon");
        }
    }
}
