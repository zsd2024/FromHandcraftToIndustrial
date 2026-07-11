package FHTI.content;

import mindustry.type.Planet;
import mindustry.world.meta.Env;
import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.maps.planet.ErekirPlanetGenerator;

public class ModPlanets {
    public static Planet zetas;

    public static void load() {
        zetas = new Planet("zetas", Planets.sun, 1f, 3) {
            {
                generator = new ErekirPlanetGenerator(); // 生成器
                meshLoader = () -> new HexMesh(this, 5); // 网格加载器
                cloudMeshLoader = () -> new MultiMesh(
                        new HexSkyMesh(this, 2, 0.15f, 0.14f, 5, Color.valueOf("#68e9eb").a(0.75f), 2, 0.42f, 1f,
                                0.43f),
                        new HexSkyMesh(this, 3, 0.6f, 0.15f, 5, Color.valueOf("#93a1ee").a(0.75f), 2, 0.42f, 1.2f,
                                0.45f)); // 云网格加载器
                alwaysUnlocked = true; // 永远解锁
                landCloudColor = Color.valueOf("#429aed"); // 陆地云颜色
                atmosphereColor = Color.valueOf("#18acf0"); // 大气颜色
                defaultEnv = Env.terrestrial; // 默认环境
                startSector = 1; // 起始区块
                atmosphereRadIn = 0.02f; // 大气半径内
                atmosphereRadOut = 0.3f; // 大气半径外
                tidalLock = true; // 潮汐锁定
                orbitSpacing = 2f; // 轨道间距
                totalRadius += 2.6f; // 总半径
                lightSrcTo = 0.5f;
                lightDstFrom = 0.2f;
                clearSectorOnLose = true; // 失去时清除区块
                defaultCore = ModBlocks.core_primitive; // 默认核心
                iconColor = Color.valueOf("#6691ff"); // 图标颜色

                enemyBuildSpeedMultiplier = 0.4f; // 敌人建造速度倍率

                allowLaunchToNumbered = false; // 禁止发射到数字区块

                ruleSetter = r -> {
                    r.waveTeam = Team.malis;
                    r.placeRangeCheck = false;
                    r.showSpawns = true;
                    r.fog = true;
                    r.staticFog = true;
                    r.lighting = false;
                    r.coreDestroyClear = true;
                    r.onlyDepositCore = true;
                }; // 规则设置器

                unlockedOnLand.add(ModBlocks.core_primitive); // 解锁地块
            }
        };
    }
}
