package com.blestep.footballlife.utils;

import android.content.Context;

import com.blestep.footballlife.BTConstants;

import java.math.BigDecimal;

/**
 * Created by lwz on 2016/5/14 0014.
 */
public class SportDataUtils {
    public static int MAX_STEP = 15000;
    public static int MAX_DISTANCE = 8000;
    public static int MAX_CALORIE = 3000;
    public static float MAX_BMI = 125;
    public static float MAX_BFR = 167.6f;
    public static float MAX_BMR = 447;
    public static float MAX_SPEED = 40;
    public static float MAX_POWER = 1387;
    public static float MAX_EXPLOSIVE = 5395;
    public static float MAX_ENDURANCE = 10000;
    public static float MAX_SPIRIT = 20000;

    public static float speed_factor = 4.1f;

    // km/h
    public static float getSpeed(float distance, float duration) {
        float speed = new BigDecimal(distance / (duration / 60) * speed_factor).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return speed;
    }

    // m/s*weight
    public static float getPower(Context context, float speed) {
        int weight = SPUtiles.getInstance(context).getInt(BTConstants.SP_KEY_USER_WEIGHT, 0);
        if (weight == 0)
            return 0;
        float power = new BigDecimal(weight * speed / 3.6f).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return power;
    }

    // m/s * N
    public static float getExplosive(float power, float speed) {
        float explosive = new BigDecimal(power * speed / 3.6f).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return explosive;
    }

    // m*h
    public static float getEndurance(float distance, float duration) {
        float endurance = new BigDecimal(distance * (duration / 60)).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return endurance;
    }

    // m/s + N + P + E
    public static float getSpirit(float speed, float power, float explosive, float endurance) {
        float spirit = new BigDecimal(speed / 3.6f + power + explosive + endurance).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return spirit;
    }

    // kg/m*m
    public static float getBMI(Context context) {
        int weight = SPUtiles.getInstance(context).getInt(BTConstants.SP_KEY_USER_WEIGHT, 0);
        int height = SPUtiles.getInstance(context).getInt(BTConstants.SP_KEY_USER_HEIGHT, 0);
        if (weight == 0)
            return 0;
        float bmi = new BigDecimal(weight / (height / 100) / (height / 100)).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return bmi;
    }

    // 1.2*bmi+0.23*age-5.4-10.8*sex
    public static float getBFR(Context context, float bmi) {
        int age = SPUtiles.getInstance(context).getInt(BTConstants.SP_KEY_USER_AGE, 0);
        int gender = SPUtiles.getInstance(context).getInt(BTConstants.SP_KEY_USER_GENDER, 0);
        if (age == 0)
            return 0;
        float bfr = new BigDecimal(1.2 * bmi + 0.23 * age - 5.4 - 10.8 * (gender == 0 ? 1 : 0)).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return bfr;
    }

    // 347.4/(0.0061*cm+0.0128*kg-0.1529)
    public static float getBMR(Context context) {
        int weight = SPUtiles.getInstance(context).getInt(BTConstants.SP_KEY_USER_WEIGHT, 0);
        int height = SPUtiles.getInstance(context).getInt(BTConstants.SP_KEY_USER_HEIGHT, 0);
        float bfr = new BigDecimal(347.4 / (0.0061 * height + 0.0128 * weight - 0.1529)).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return bfr;
    }
}