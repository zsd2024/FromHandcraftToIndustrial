package FHTI.world.blocks.production;

import FHTI.world.blocks.kinetic.*;
import arc.*;
import arc.math.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

/** 一个需要动能的工厂。 */
public class KineticCrafter extends GenericCrafter {
    /** 100% 效率的基础动能需求。 */
    public float kineticRequirement = 10f;
    /**
     * 当动能达到此需求后，多余的动能将按此比例缩放。
     */
    public float overkineticScale = 1f;
    /** 动能超限后的最大可能效率。 */
    public float maxEfficiency = 4f;
    /**
     * 动能状态
     */
    public StatUnit kineticUnits;

    public KineticCrafter(String name) {
        super(name);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("kinetic",
                (KineticCrafterBuild entity) -> new Bar(
                        () -> Core.bundle.format("bar.from-handwork-to-industrial-kineticpercent",
                                (int) (entity.kinetic + 0.01f),
                                (int) (entity.efficiencyScale() * 100 + 0.01f)),
                        () -> Pal.lightOrange,
                        () -> entity.kinetic / kineticRequirement));
    }

    @Override
    public void setStats() {
        super.setStats();

        kineticUnits = new StatUnit("from-handwork-to-industrial-kineticUnits",
                Core.bundle.get("icon.from-handwork-to-industrial-kinetic"));
        stats.add(Stat.input, kineticRequirement, kineticUnits);
        stats.add(Stat.maxEfficiency, (int) (maxEfficiency * 100f), StatUnit.percent);
    }

    public class KineticCrafterBuild extends GenericCrafterBuild implements KineticConsumer {
        // TODO sideKinetic 可以平滑
        public float[] sideKinetic = new float[4];
        public float kinetic = 0f;

        @Override
        public void updateTile() {
            kinetic = calculateKinetic(sideKinetic);

            super.updateTile();
        }

        @Override
        public float kineticRequirement() {
            return kineticRequirement;
        }

        @Override
        public float[] sideKinetic() {
            return sideKinetic;
        }

        @Override
        public float warmupTarget() {
            return Mathf.clamp(kinetic / kineticRequirement);
        }

        @Override
        public float efficiencyScale() {
            float over = Math.max(kinetic - kineticRequirement, 0f);
            return Math.min(Mathf.clamp(kinetic / kineticRequirement) + over / kineticRequirement * overkineticScale,
                    maxEfficiency);
        }
    }
}
