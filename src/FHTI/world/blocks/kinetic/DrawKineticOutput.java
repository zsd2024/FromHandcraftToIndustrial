package FHTI.world.blocks.kinetic;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.DrawBlock;

public class DrawKineticOutput extends DrawBlock {
    public TextureRegion kinetic, glow, top1, top2;

    public Color kineticColor = new Color(1f, 0.22f, 0.22f, 0.8f);
    public float kineticPulse = 0.3f, kineticPulseScl = 10f, glowMult = 1.2f;

    public int rotOffset = 0;
    public boolean drawGlow = true;

    public DrawKineticOutput() {
    }

    public DrawKineticOutput(int rotOffset, boolean drawGlow) {
        this.rotOffset = rotOffset;
        this.drawGlow = drawGlow;
    }

    @Override
    public void draw(Building build) {
        float rotdeg = (build.rotation + rotOffset) * 90;
        Draw.rect(Mathf.mod((build.rotation + rotOffset), 4) > 1 ? top2 : top1, build.x, build.y, rotdeg);

        if (build instanceof KineticBlock && ((KineticBlock) build).kinetic() > 0) {
            KineticBlock kineticer = (KineticBlock) build;
            Draw.z(Layer.blockAdditive);
            Draw.blend(Blending.additive);
            Draw.color(kineticColor,
                    kineticer.kineticFrac()
                            * (kineticColor.a * (1f - kineticPulse + Mathf.absin(kineticPulseScl, kineticPulse))));
            if (kinetic.found())
                Draw.rect(kinetic, build.x, build.y, rotdeg);
            Draw.color(Draw.getColor().mul(glowMult));
            if (drawGlow && glow.found())
                Draw.rect(glow, build.x, build.y);
            Draw.blend();
            Draw.color();
        }
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(Mathf.mod((plan.rotation + rotOffset), 4) > 1 ? top2 : top1, plan.drawx(), plan.drawy(),
                (plan.rotation + rotOffset) * 90);
    }

    @Override
    public void load(Block block) {
        kinetic = Core.atlas.find(block.name + "-kinetic");
        glow = Core.atlas.find(block.name + "-glow");
        top1 = Core.atlas.find(block.name + "-top1");
        top2 = Core.atlas.find(block.name + "-top2");
    }

    // TODO currently no icons due to concerns with rotation

}
