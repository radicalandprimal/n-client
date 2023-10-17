package fifthcolumn.n.client;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://github.com/collarmc/molib/blob/master/src/main/java/com/collarmc/molib/Validation.java
public final class Input {
    private static final Pattern STRIP_PATTERN = Pattern.compile("(?<!<@)[&\u00a7](?i)[0-9a-fklmnorx]");
    private static final Pattern ADD_UUID_PATTERN = Pattern.compile("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)");
    private static final Pattern VALID_MC_NAME = Pattern.compile("^\\w{3,16}$");

    /**
     * Strips input of all Minecraft formatting goop
     * @param input to strip
     * @return clean string
     */
    public static String stripMinecraft(String input) {
        if (input == null) {
            return "";
        }
        return StringUtils.trimToEmpty(STRIP_PATTERN.matcher(input).replaceAll(""));
    }

    /**
     * Checks if minecraft formatted
     * @param input to test
     * @return valid
     */
    public static boolean isMinecraftFormatted(String input) {
        return STRIP_PATTERN.matcher(input).matches();
    }

    /**
     * Checks if minecraft formatted
     * @param input to test
     * @return valid
     */
    public static boolean isValidMinecraftUsername(String input) {
        return !Input.isMinecraftFormatted(input) && VALID_MC_NAME.matcher(input).matches();
    }

    /**
     * Makes sure any Mojang UUIDs are parsed correctly
     * @param possibleUUID to parse
     * @return uuid
     */
    public static UUID parseUUID(String possibleUUID) {
        try {
            return UUID.fromString(possibleUUID);
        } catch (IllegalArgumentException e) {
            Matcher matcher = ADD_UUID_PATTERN.matcher(possibleUUID);
            if (matcher.matches()) {
                return UUID.fromString(matcher.replaceAll("$1-$2-$3-$4-$5"));
            }
            throw e;
        }
    }
}
