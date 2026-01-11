package fi.septicuss.tooltips.utils;

public class CompatibilityUtils {

    private CompatibilityUtils() { }

    public static boolean SUPPORTS_SHADOWS = true;

    static {
        try {
            // Try to load the problematic classes/method
            Class.forName("net.kyori.adventure.text.minimessage.tag.standard.StandardTags").getMethod("shadowColor");
            Class.forName("net.kyori.adventure.text.format.StyleSetter");
        } catch (Throwable t) {
            SUPPORTS_SHADOWS = false;
        }
    }

}
