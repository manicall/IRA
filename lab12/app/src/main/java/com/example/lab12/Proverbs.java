package com.example.lab12;

import java.util.ArrayList;

public class Proverbs {
    private static int index;
    private static String[] proverbs = new String[]{
        "Не учи учёного.",
        "Волос долог, да ум короток.",
        "Не рой яму другому – сам в нее попадешь.",
        "Нет дыма без огня.",
        "С волками жить — по-волчьи выть.",
        "Сделал дело — гуляй смело.",
        "Семь пятниц на неделе.",
        "Смелому и море по колено.",
        "Терпенье и труд всё перетру.",
        "Копейка рубль бережет.",
        "На воре и шапка горит.",
        "Один в поле не воин.",
        "Одного поля ягоды.",
        "Первый блин — комом.",
        "Пришла беда – отворяй ворота.",
        "У страха глаза велики.",
        "Уговор дороже денег.",
        "Цыплят по осени считают.",
        "Что в лоб, что по лбу.",
        "Шила в мешке не утаишь.",
        "Яблоко от яблони недалеко падает.",
        "Язык без костей."
    };

    public static String getRandomProverb() {
        int rndIndex;
        do {
            rndIndex = rnd(0, proverbs.length - 1);
        } while (rndIndex == index);

        index = rndIndex;
        return proverbs[index];
    }

    // функция возвращаяющая случайное число в заданном диапазоне
    private static int rnd(int min, int max) {
        max -= min;
        return ((int) (Math.random() * ++max) + min);
    }
}
