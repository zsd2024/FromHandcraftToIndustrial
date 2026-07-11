package FHTI.world;

import java.util.Arrays;

import FHTI.world.blocks.kinetic.KineticBlock;
import FHTI.world.blocks.kinetic.KineticConductor;
import FHTI.world.blocks.kinetic.KineticConductor.KineticConductorBuild;
import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.IntSet;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.Stat;

public class ModBlock extends Block {
    /**
     * 该可磨损方块的使用寿命
     */
    public float serviceLife;

    public ModBlock(String name) {
        super(name);
        update = true;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("wearlevel", (ModBuilding entity) -> new Bar(
                () -> Core.bundle.get("stat.from-handwork-to-industrial-wearlevel"),
                () -> Color.HSVtoRGB((1.0f - entity.wearLevelf()) * 0.5f * 360, 100,
                        100),
                entity::wearLevelf));
    }

    @Override
    public void setStats() {
        super.setStats();
        if (serviceLife < 60f)
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-seconds"), serviceLife);
        else if (serviceLife < 3600f)
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-minutes"),
                    (int) (serviceLife / 60f), serviceLife % 60f);
        else if (serviceLife < 86400f)
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-hours"),
                    (int) (serviceLife / 3600f), (int) (serviceLife % 3600f / 60f), serviceLife % 60f);
        else if (serviceLife < 604800f)
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-days"),
                    (int) (serviceLife / 86400f), (int) (serviceLife % 86400f / 3600f),
                    (int) (serviceLife % 3600f / 60f), serviceLife % 60f);
        else if (serviceLife < 31536000f)
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-weeks"),
                    (int) (serviceLife / 604800f), (int) (serviceLife % 604800f / 86400f),
                    (int) (serviceLife % 86400f / 3600f), (int) (serviceLife % 3600f / 60f), serviceLife % 60f);
        else
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-years"),
                    (int) (serviceLife / 31536000f), (int) (serviceLife % 31536000f / 604800f),
                    (int) (serviceLife % 604800f / 86400f), (int) (serviceLife % 86400f / 3600f),
                    (int) (serviceLife % 3600f / 60f), serviceLife % 60f);
    }

    public class ModBuilding extends Building {

        /// WearableBlockBuild Code Start

        /**
         * 该可磨损方块的使用时间
         */
        public double serviceTime;

        /**
         * 上次更新时间
         */
        private long lastUpdate = -1;

        /**
         * 该可磨损方块的磨损百分比
         *
         * @return 磨损百分比
         */
        public float wearLevelf() {
            return (float) (serviceTime / serviceLife);
        }

        /**
         * 获取基础使用寿命增量
         *
         * @return 基础使用寿命增量
         */
        public float getUsageIncrementBesic() {
            return 1.0f;
        }

        /**
         * 获取每秒使用寿命增量
         *
         * @return 每秒使用寿命增量
         */
        public float getUsageIncrementPerSecond() {
            return getUsageIncrementBesic() / healthf()
                    * ((1.0f / Core.graphics.getFramesPerSecond()) / Core.graphics.getDeltaTime());
        }

        /**
         * 更新使用寿命
         */
        public void updateServiceTime() {
            if (lastUpdate == -1) {
                lastUpdate = System.nanoTime();
            } else {
                // Log.info("lastUpdate: " + lastUpdate);
                serviceTime += (System.nanoTime() - lastUpdate) * 1e-9 * getUsageIncrementPerSecond();

                lastUpdate = System.nanoTime();
            }
            // Log.info("Current serviceTime: " + serviceTime);
            if (serviceTime >= serviceLife) {
                kill();
            }
        }

        /// WearableBlockBuild Code End

        /// Kinetic Calc Code Start

        public float calculateKinetic(float[] sideKinetic) {
            return calculateKinetic(sideKinetic, null);
        }

        public float calculateKinetic(float[] sideKinetic, IntSet cameFrom) {
            Arrays.fill(sideKinetic, 0f);
            if (cameFrom != null)
                cameFrom.clear();

            float kinetic = 0f;

            for (Building build : proximity) {
                if (build != null && build.team == team && build instanceof KineticBlock) {
                    KineticBlock kineticer = (KineticBlock) build;
                    // boolean split = build.block instanceof KineticConductor cond &&
                    // cond.splitKinetic;
                    boolean split = build.block instanceof KineticConductor;
                    if (split) {
                        KineticConductor cond = (KineticConductor) build.block;
                        split = cond.splitKinetic;
                    }
                    // 非路由器必须面向我们，路由器必须面向相反 - 在重定向器旁边，它们将被强制面向相反方向
                    if (!build.block.rotate || (!split && (relativeTo(build) + 2) % 4 == build.rotation)
                            || (split && relativeTo(build) != build.rotation)) { // TODO hacky

                        // 如果存在环路，忽略其动能
                        if (!(build instanceof KineticConductorBuild
                                && ((KineticConductorBuild) build).cameFrom.contains(id()))) {
                            // x/y 坐标差异在接触点
                            float diff = (Math.min(Math.abs(build.x - x), Math.abs(build.y - y)) / Vars.tilesize);
                            // 该块与其他块的接触点数
                            int contactPoints = Math.min((int) (block.size / 2f + build.block.size / 2f - diff),
                                    Math.min(build.block.size, block.size));

                            // 动能被分配到建筑的尺寸
                            float add = kineticer.kinetic() / build.block.size * contactPoints;
                            if (split) {
                                // 动能路由器将动能分配到 3 个表面
                                add /= 3f;
                            }

                            sideKinetic[Mathf.mod(relativeTo(build), 4)] += add;
                            kinetic += add;
                        }

                        // 记录遍历的环路
                        if (cameFrom != null) {
                            cameFrom.add(build.id);
                            if (build instanceof KineticConductorBuild) {
                                cameFrom.addAll(((KineticConductorBuild) build).cameFrom);
                            }
                        }

                        // 一个巨大的 hack 但是 我不在乎了
                        if (build instanceof KineticConductorBuild) {
                            ((KineticConductorBuild) build).updateKinetic();
                        }
                    }
                }
            }
            return kinetic;
        }

        /// Kinetic Calc Code End

        @Override
        public void updateTile() {
            super.updateTile();
            updateServiceTime();
        }
    }
}
