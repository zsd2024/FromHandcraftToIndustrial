package FHTI.world.blocks.defense;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

import FHTI.world.ModBlock;

public class Wall extends ModBlock {

    /** 闪电几率。-1表示禁用 */
    public float lightningChance = -1f;
    /** 闪电伤害 */
    public float lightningDamage = 20f;
    /** 闪电长度 */
    public int lightningLength = 17;
    /** 闪电颜色 */
    public Color lightningColor = Pal.surge;
    /** 闪电声音 */
    public Sound lightningSound = Sounds.spark;

    /** 子弹偏转几率。-1表示禁用 */
    public float chanceDeflect = -1f;
    /** 是否闪光击中 */
    public boolean flashHit;
    /** 闪光颜色 */
    public Color flashColor = Color.white;
    /** 偏转声音 */
    public Sound deflectSound = Sounds.none;

    public Wall(String name) {
        super(name);
        solid = true; // 实心
        destructible = true; // 可破坏
        group = BlockGroup.walls; // 组别为墙
        buildCostMultiplier = 6f; // 建造成本倍数
        canOverdrive = false; // 不能超速
        drawDisabled = false; // 不禁用绘制
        crushDamageMultiplier = 5f; // 压碎伤害倍数
        priority = TargetPriority.wall; // 优先级为墙

        // 当然，作为墙，它在任何环境下都支持
        envEnabled = Env.any;
    }

    @Override
    public void setStats() {
        super.setStats();
        if (chanceDeflect > 0f)
            stats.add(Stat.baseDeflectChance, chanceDeflect, StatUnit.none);
        if (lightningChance > 0f) {
            stats.add(Stat.lightningChance, lightningChance * 100f, StatUnit.percent);
            stats.add(Stat.lightningDamage, lightningDamage, StatUnit.none);
        }
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[] { Core.atlas.find(Core.atlas.has(name) ? name : name + "1") };
    }

    public class WallBuild extends ModBuilding {

        /** 击中效果 */
        public float hit;

        @Override
        public void draw() {
            super.draw();

            // 如果启用了闪光击中，则绘制闪光白色覆盖层
            if (flashHit) {
                if (hit < 0.0001f)
                    return;

                Draw.color(flashColor);
                Draw.alpha(hit * 0.5f);
                Draw.blend(Blending.additive);
                Fill.rect(x, y, tilesize * size, tilesize * size);
                Draw.blend();
                Draw.reset();

                if (!state.isPaused()) {
                    hit = Mathf.clamp(hit - Time.delta / 10f);
                }
            }
        }

        @Override
        public boolean collision(Bullet bullet) {
            super.collision(bullet);

            hit = 1f;

            // 如果需要，创建闪电
            if (lightningChance > 0f) {
                if (Mathf.chance(lightningChance)) {
                    Lightning.create(team, lightningColor, lightningDamage, x, y, bullet.rotation() + 180f,
                            lightningLength);
                    lightningSound.at(tile, Mathf.random(0.9f, 1.1f));
                }
            }

            // 如果需要，偏转子弹
            if (chanceDeflect > 0f) {
                // 慢速子弹不会被偏转
                if (bullet.vel.len() <= 0.1f || !bullet.type.reflectable)
                    return true;

                // 子弹反射几率取决于子弹伤害
                if (!Mathf.chance(chanceDeflect / bullet.damage()))
                    return true;

                // 播放声音
                deflectSound.at(tile, Mathf.random(0.9f, 1.1f));

                // 将子弹位置回退到碰撞前
                bullet.trns(-bullet.vel.x, -bullet.vel.y);

                float penX = Math.abs(x - bullet.x), penY = Math.abs(y - bullet.y);

                if (penX > penY) {
                    bullet.vel.x *= -1;
                } else {
                    bullet.vel.y *= -1;
                }

                bullet.owner = this;
                bullet.team = team;
                bullet.time += 1f;

                // 通过返回false禁用子弹碰撞
                return false;
            }

            return true;
        }
    }
}
