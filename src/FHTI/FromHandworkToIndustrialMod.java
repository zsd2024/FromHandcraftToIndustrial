package FHTI;

import FHTI.others.ChangeModName;
// import FHTI.UI.ModSettings;
import FHTI.content.ModBlocks;
import FHTI.content.ModItems;
import FHTI.content.ModPlanets;
import FHTI.content.ModUnits;
import FHTI.content.ZetasTechTree;
import mindustry.mod.Mod;

public class FromHandworkToIndustrialMod extends Mod {
    public FromHandworkToIndustrialMod() {
    }

    @Override
    public void loadContent() {
        ChangeModName.load();
        ModUnits.load();
        ModItems.load();
        ModBlocks.load();
        ModPlanets.load();
        ZetasTechTree.load();
    }

    @Override
    public void init() {
        // ModSettings.load();
    }
}
