package fifthcolumn.n.collar;

import com.collarmc.api.authentication.AuthenticationService;
import com.collarmc.rest.RESTClient;
import com.google.gson.Gson;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public final class CollarLogin {
    private static final UUID COPE_GROUP_ID = UUID.fromString("fe2b0ae3-8984-414b-8a5f-e972736bb77c");

    private static final Logger LOGGER = LoggerFactory.getLogger(CollarLogin.class);
    private static final Gson GSON = new Gson();
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static String getMembershipToken() {
        try {
            return CollarSettings.read().membershipToken;
        } catch (IOException e) {
            LOGGER.error("Unable to read Collar group membership token", e);
            throw new IllegalStateException(e);
        }
    }

    public static boolean refreshSession() {
        CollarSettings settings;
        RESTClient client = CollarLogin.createClient();
        try {
            settings = CollarSettings.read();
        } catch (Throwable e) {
            LOGGER.error("Unable to read Collar settings", e);
            return false;
        }
        LoginResult loginResult = CollarLogin.loginAndSave(settings.email, settings.password);
        if (loginResult.success) {
            return client.validateGroupMembershipToken(settings.membershipToken, COPE_GROUP_ID).isPresent();
        }
        LOGGER.error("Collar group membership validation unsuccessful");
        return false;
    }

    public static LoginResult loginAndSave(String email, String password) {
        RESTClient client = CollarLogin.createClient();
        return client.login(AuthenticationService.LoginRequest.emailAndPassword(email, password))
            .map(loginResponse -> loginResponse.token)
            .map(token -> client.createGroupMembershipToken(token, COPE_GROUP_ID).map(resp -> {
                CollarSettings settings = new CollarSettings();
                settings.email = email;
                settings.password = password;
                settings.membershipToken = resp.token;
                try {
                    settings.save();
                } catch (IOException e) {
                    LOGGER.error("Could not save collar settings");
                    return new LoginResult(false, e.getMessage());
                }
                return new LoginResult(true, null);
            }).orElse(new LoginResult(false, "Login failed")))
            .orElse(new LoginResult(false, "Login failed"));
    }

    private static RESTClient createClient() {
        return new RESTClient("https://api.collarmc.com");
    }

    public static final class CollarSettings {
        public String email;
        public String password;
        public String membershipToken;

        public void save() throws IOException {
            File file = new File(CollarLogin.mc.runDirectory, "collar.json");
            String contents = GSON.toJson(this);
            Files.writeString(file.toPath(), contents);
        }

        public static CollarSettings read() throws IOException {
            File file = new File(CollarLogin.mc.runDirectory, "collar.json");
            String contents = Files.readString(file.toPath());
            return GSON.fromJson(contents, CollarSettings.class);
        }
    }

    public static final class LoginResult {
        public final boolean success;
        public final String reason;

        public LoginResult(boolean success, String reason) {
            this.success = success;
            this.reason = reason;
        }
    }
}
