package FHTI.UI;

import mindustry.Vars;
import mindustry.gen.Icon;

public class ModSettings {
    public static void load() {
        Vars.ui.settings.addCategory("fhti-settings", Icon.crafting, t -> {
            t.checkPref("show-servicetime", false);
            t.checkPref("show-power-info", false);
        });
    }
}
