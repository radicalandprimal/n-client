# n-client
Copenheimer's/The Fifth Column's client for Meteor Client 1.20.6

Credit to <https://github.com/hazelwhitlock> for the intial leak of N Client.

## Installing
- Install the [Fabric API](https://modrinth.com/mod/fabric-api/version/0.99.0+1.20.6) to your mods folder.
- Install [Meteor Client](https://meteorclient.com/) to your mods folder.
- Install [N Client](https://github.com/radicalandprimal/n-client/releases/tag/1.20.6-1) to your mods folder.

## Modules
- AntiAim: Goofy rotations, interferes with some placement stuff
- AutoCutie: Automatically adds all online griefers to meteor friends
- AutoLava: no clue
- AutoSign: Places signs to lead victims into the discord server. Also writes Ticket IDs for victims.
- AutoTranslate: Translates incoming/outgoing messages to the target language.
- AutoWither: Automates the process of building withers.
    1. Simply gather the resources (4 soul sand and 3 wither skulls)
    2. Enable the module, and place soul sand anywhere to start the build.
- BanEvasion: Uses alt accounts to evade a ban. Uses the Copenheimer API in order to receive an MSA alt account.
- BetterFlight: Basically the flight module.
    - Configurable base/max speed.
    - Velocity speed (move slowly and then move faster as you go)
    - Anti Kick mode: `NONE`, `FALL`, `BOB`, `PACKET`
        - `FALL` avoids vanilla flight kick by moving you down ever so slowly.
- BuildPoop: Module used to make the process of building lavacasts easier. Use in conjunction with BetterFlight to make it faster.
- ChestStealerAura: Searches all the chests and takes the target item
- FastProjectile: Instakill with bows ;\)
- GameModeNotifier: Alerts you when someone changes gamemode
- GrieferTracer: Tracers to fellow Griefers. Disabled on 2b2t.org.
- Gun: Replaces the bow with a custom model with sound effects.
- InventoryDupe: Adds Dupe button which allows you to dupe on 1.17 servers
- LarpModule: Make all griefers larp as another player
- LecternCrash: Op crash, put book in lectern and press button
- OriginsModule: You will need to set a version
- SitBypass: The SIT plugin
- TranslateSigns (unimplemented): Translates signs to targeted language.
- WaypointSync: Syncs your waypoints. Disabled on 2b2t.org.

## Commands
- `.ip`: Copies the current server IP to clipboard
- `.column`: Sends the Fifth Column Discord invite in the chat.
- `.tp <x> <y> <z>`: Teleports to the provided location. There may be some boundaries to this though.

## HUD
- "Social Engineering HUD":
    - Lists all of the current players that are apart of the Fifth Column as a HUD element.

## Building
Run `gradlew build` and your jar will be in the `build/libs/` folder.

---

*ily 5c*