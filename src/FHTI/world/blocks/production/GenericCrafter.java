package FHTI.world.blocks.production;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

import FHTI.world.ModBlock;

public class GenericCrafter extends ModBlock {
    /** 如果 outputItems 为 null，则作为单元素数组写入 outputItems。 */
    public @Nullable ItemStack outputItem;
    /** Overwrites outputItem if not null. */
    public @Nullable ItemStack[] outputItems;

    /**
     * 如果 outputLiquids 为 null，则作为单元素数组写入 outputLiquids。
     */
    public @Nullable LiquidStack outputLiquid;
    /** 如果 outputLiquid 不为 null，则覆盖 outputLiquid。 */
    public @Nullable LiquidStack[] outputLiquids;
    /**
     * 液体输出方向，按与 outputLiquids 相同的顺序指定。使用 -1 表示在每个方向上倾倒。旋转相对于块。
     */
    public int[] liquidOutputDirections = { -1 };

    /**
     * 如果为 true，则具有多个液体输出的工厂在至少有一种液体类型仍有空间时会倾倒多余的液体
     */
    public boolean dumpExtraLiquid = true;
    public boolean ignoreLiquidFullness = false;

    public float craftTime = 80;
    public Effect craftEffect = Fx.none;
    public Effect updateEffect = Fx.none;
    public float updateEffectChance = 0.04f;
    public float warmupSpeed = 0.019f;
    /** Only used for legacy cultivator blocks. */
    public boolean legacyReadWarmup = false;

    public DrawBlock drawer = new DrawDefault();

    public GenericCrafter(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
        drawArrow = false;
    }

    @Override
    public void setStats() {
        stats.timePeriod = craftTime;
        super.setStats();
        if ((hasItems && itemCapacity > 0) || outputItems != null) {
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }

        if (outputItems != null) {
            stats.add(Stat.output, StatValues.items(craftTime, outputItems));
        }

        if (outputLiquids != null) {
            stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
        }
    }

    @Override
    public void setBars() {
        super.setBars();

        // 设置液体输出的液体条
        if (outputLiquids != null && outputLiquids.length > 0) {
            // 不需要动态液体条
            removeBar("liquid");

            // 然后显示输出缓冲区
            for (LiquidStack stack : outputLiquids) {
                addLiquidBar(stack.liquid);
            }
        }
    }

    @Override
    public boolean rotatedOutput(int x, int y) {
        return false;
    }

    @Override
    public void load() {
        super.load();

        drawer.load(this);
    }

    @Override
    public void init() {
        if (outputItems == null && outputItem != null) {
            outputItems = new ItemStack[] { outputItem };
        }
        if (outputLiquids == null && outputLiquid != null) {
            outputLiquids = new LiquidStack[] { outputLiquid };
        }
        // 将 outputLiquid 写回，因为它有助于感知
        if (outputLiquid == null && outputLiquids != null && outputLiquids.length > 0) {
            outputLiquid = outputLiquids[0];
        }
        outputsLiquid = outputLiquids != null;

        if (outputItems != null)
            hasItems = true;
        if (outputLiquids != null)
            hasLiquids = true;

        super.init();
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    @Override
    public boolean outputsItems() {
        return outputItems != null;
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out) {
        drawer.getRegionsToOutline(this, out);
    }

    @Override
    public void drawOverlay(float x, float y, int rotation) {
        if (outputLiquids != null) {
            for (int i = 0; i < outputLiquids.length; i++) {
                int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

                if (dir != -1) {
                    Draw.rect(
                            outputLiquids[i].liquid.fullIcon,
                            x + Geometry.d4x(dir + rotation) * (size * tilesize / 2f + 4),
                            y + Geometry.d4y(dir + rotation) * (size * tilesize / 2f + 4),
                            8f, 8f);
                }
            }
        }
    }

    public class GenericCrafterBuild extends ModBuilding {
        public float progress;
        public float totalProgress;
        public float warmup;

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public void drawLight() {
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public boolean shouldConsume() {
            if (outputItems != null) {
                for (ItemStack output : outputItems) {
                    if (items.get(output.item) + output.amount > itemCapacity) {
                        return false;
                    }
                }
            }
            if (outputLiquids != null && !ignoreLiquidFullness) {
                boolean allFull = true;
                for (LiquidStack output : outputLiquids) {
                    if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
                        if (!dumpExtraLiquid) {
                            return false;
                        }
                    } else {
                        // 如果仍有空间，则所有液体都未满
                        allFull = false;
                    }
                }

                // 如果没有任何液体有空间，则无法生产
                if (allFull) {
                    return false;
                }
            }

            return enabled;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if (efficiency > 0) {

                progress += getProgressIncrease(craftTime);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                // 根据效率连续输出
                if (outputLiquids != null) {
                    float inc = getProgressIncrease(1f);
                    for (LiquidStack output : outputLiquids) {
                        handleLiquid(this, output.liquid,
                                Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if (wasVisible && Mathf.chanceDelta(updateEffectChance)) {
                    updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                }
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            // 可能看起来不好，如果是这样请恢复到 edelta()
            totalProgress += warmup * Time.delta;

            if (progress >= 1f) {
                craft();
            }

            dumpOutputs();
        }

        @Override
        public float getProgressIncrease(float baseTime) {
            if (ignoreLiquidFullness) {
                return super.getProgressIncrease(baseTime);
            }

            // 通过它可以生产的最大液体量限制进度增加
            float scaling = 1f, max = 1f;
            if (outputLiquids != null) {
                max = 0f;
                for (LiquidStack s : outputLiquids) {
                    float value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta());
                    scaling = Math.min(scaling, value);
                    max = Math.max(max, value);
                }
            }

            // 当倾倒多余液体时，取最大值而不是最小值。
            return super.getProgressIncrease(baseTime) * (dumpExtraLiquid ? Math.min(max, 1f) : scaling);
        }

        public float warmupTarget() {
            return 1f;
        }

        @Override
        public float warmup() {
            return warmup;
        }

        @Override
        public float totalProgress() {
            return totalProgress;
        }

        public void craft() {
            consume();

            if (outputItems != null) {
                for (ItemStack output : outputItems) {
                    for (int i = 0; i < output.amount; i++) {
                        offload(output.item);
                    }
                }
            }

            if (wasVisible) {
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        public void dumpOutputs() {
            if (outputItems != null && timer(timerDump, dumpTime / timeScale)) {
                for (ItemStack output : outputItems) {
                    dump(output.item);
                }
            }

            if (outputLiquids != null) {
                for (int i = 0; i < outputLiquids.length; i++) {
                    int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

                    dumpLiquid(outputLiquids[i].liquid, 2f, dir);
                }
            }
        }

        @Override
        public double sense(LAccess sensor) {
            if (sensor == LAccess.progress)
                return progress();
            // 尝试防止总液体波动，至少对于工厂来说
            if (sensor == LAccess.totalLiquids && outputLiquid != null)
                return liquids.get(outputLiquid.liquid);
            return super.sense(sensor);
        }

        @Override
        public float progress() {
            return Mathf.clamp(progress);
        }

        @Override
        public int getMaximumAccepted(Item item) {
            return itemCapacity;
        }

        @Override
        public boolean shouldAmbientSound() {
            return efficiency > 0;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
            write.f(warmup);
            if (legacyReadWarmup)
                write.f(0f);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            progress = read.f();
            warmup = read.f();
            if (legacyReadWarmup)
                read.f();
        }
    }
}
