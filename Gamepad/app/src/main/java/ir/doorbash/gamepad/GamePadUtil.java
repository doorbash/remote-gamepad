package ir.doorbash.gamepad;

/**
 * Created by Milad Doorbash on 5/9/16.
 */
public class GamePadUtil {

    public static boolean isGamepadSupported(int vendor, int product) {
        if (vendor == 2064 && (product == 1 || product == 2 || product == 0xe301)) return true;
        if (vendor == 121 && (product == 17 || product == 6)) return true;
        if (vendor == 2079 && product == 58369) return true;
        return vendor == 4797 && product == 40993;
    }
}
