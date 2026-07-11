package FHTI.others;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import arc.Core;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.mod.Mods;

public class ChangeModName {
    final static String[] tips_zh_cn = new String[] {
            "你发现了吗？这行字会变",
            "你知道吗？使用[yellow]原始核心[]时不能生产单位",
            "这个模组的[yellow]煤炭[]和原版的[yellow]煤炭[]是不一样的",
            "你有没有注意到，这个模组的名字也会根据你的语言变化"
    };
    final static String[] tips_en_us = new String[] {
            "You found it? This line will change",
            "Do you know? You can't produce units when using [yellow]Primitive Core[]",
            "This mod's [yellow]Coal[] and the original [yellow]Coal[] are different",
            "Have you noticed? The mod's name will change according to your language"
    };
    final static String basic_discription_zh_cn = "";
    final static String basic_discription_en_us = "";
    final static int changeTime = 60;
    static int index = Mathf.random(tips_zh_cn.length - 1);

    public static void load() {
        Mods.LoadedMod mod = Vars.mods.getMod("from-handwork-to-industrial");
        Locale locale = Core.bundle.getLocale();
        if (mod != null) {
            if (locale.getCountry() == "CN" && locale.getLanguage() == "zh") {
                mod.meta.displayName = "从手工到工业";
            } else {
                mod.meta.displayName = "From Handwork to Industrial";
            }
        }
        {
            index = Mathf.random(tips_zh_cn.length - 1);
            if (locale.getCountry() == "CN" && locale.getLanguage() == "zh") {
                mod.meta.description = basic_discription_zh_cn + "\n\n" + "小提示：" + tips_zh_cn[index];
            } else {
                mod.meta.description = basic_discription_en_us + "\n\n" + "Tip:" + tips_en_us[index];
            }
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                index = Mathf.random(tips_zh_cn.length - 1);
                if (locale.getCountry() == "CN" && locale.getLanguage() == "zh") {
                    mod.meta.description = basic_discription_zh_cn + "\n\n" + "小提示：" + tips_zh_cn[index];
                } else {
                    mod.meta.description = basic_discription_en_us + "\n\n" + "Tip:" + tips_en_us[index];
                }
            }
        }, changeTime * 1000);
    }
}
