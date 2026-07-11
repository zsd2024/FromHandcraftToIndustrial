package FHTI.content;

import arc.graphics.Color;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.CommandAI;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class ModUnits {
    public static UnitType primitive_silicon_based_life;

    public static void load() {
        primitive_silicon_based_life = new UnitType("primitive-silicon-based-life") {
            {
                aiController = () -> new BuilderAI(true, 400f); // 单位AI
                controller = u -> u.team.isAI() ? aiController.get() : new CommandAI(); // 单位控制器
                constructor = UnitEntity::create; // 单位实体
                isEnemy = false; // 是否是敌人
                lowAltitude = true; // （低空单位）视为敌人，可以被攻击
                flying = true; // 是飞行单位
                mineSpeed = 1f; // 采矿速度
                mineTier = 1; // 采矿等级
                buildSpeed = 0.5f; // 建造速度
                drag = 0.05f; // 运动阻力
                speed = 3f; // 速度
                rotateSpeed = 10; // 旋转速度
                accel = 0.1f; // 加速度
                fogRadius = 0f; // 雾化半径
                itemCapacity = 10; // 物品容量
                health = 100f; // 生命值
                engineOffset = 6f; // 发动机向后偏移位置
                hitSize = 8f; // 碰撞体积
                alwaysUnlocked = true; // 是否始终解锁

                weapons.add(new Weapon("beam-weapon") {
                    { // 武器名称：
                        top = false; // 不在顶部绘制轮廓
                        shake = 1f; // 屏幕震动
                        shootY = 4f; // 偏移距离
                        x = 6.5f; // 位置偏移
                        reload = 60f; // 装填时间（以帧为单位）
                        recoil = 4f; // 击退
                        shootSound = Sounds.laser; // 射击音效

                        bullet = new LaserBulletType() {
                            { // 激光弹
                                damage = 45f; // 伤害
                                recoil = 1f; // 击退
                                sideAngle = 45f; // 侧面角度
                                sideWidth = 1f;// 侧面宽度
                                sideLength = 70f; // 侧面长度
                                healPercent = 10f; // 治疗百分比
                                collidesTeam = true; // 是否会与同类型子弹碰撞
                                length = 100f; // 激光长度
                                colors = new Color[] { Pal.heal.cpy().a(0.4f), Pal.heal, Color.white }; // 激光颜色
                            }
                        };
                    }
                });
            }
        };
    }
}
