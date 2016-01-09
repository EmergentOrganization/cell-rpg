package com.emergentorganization.cellrpg.tools;

import com.emergentorganization.cellrpg.core.EntityID;
import com.emergentorganization.cellrpg.core.SoundEffect;

import java.util.*;

/**
 * Created by brian on 11/19/15.
 */
public class Resources {
    // DIR
    public static final String DIR_IMG_GAME = "game/";
    public static final String DIR_IMG_BLDG = DIR_IMG_GAME + "buildings/";
    public static final String DIR_IMG_ENV = DIR_IMG_GAME + "environment/";
    public static final String DIR_SOUNDS = FileStructure.RESOURCE_DIR + "sounds/";

    // SFX
    public static final Map<SoundEffect, String> SFX_FILENAME_MAP;

    // ANIM
    public static final List<String> ANIM_PLAYER;
    public static final List<String> ANIM_PLAYER_SHIELD;
    public static final List<String> ANIM_CIV1_BLINKER;
    public static final List<String> ANIM_VYRAPUFFER;

    // GAME
    public static final String TEX_BULLET = DIR_IMG_GAME + EntityID.BULLET;
    public static final String TEX_POWERUP_STAR = DIR_IMG_GAME + EntityID.POWERUP_STAR;
    public static final String TEX_POWERUP_PLUS = DIR_IMG_GAME + EntityID.POWERUP_PLUS;

    // BLDG
    public static final String TEX_BLDG_LRG_ONE = DIR_IMG_BLDG + EntityID.BUILDING_LARGE_ONE;
    public static final String TEX_BLDG_ROUND_ONE = DIR_IMG_BLDG + EntityID.BUILDING_ROUND_ONE;
    public static final String TEX_VYROID_BEACON = DIR_IMG_BLDG + EntityID.VYROID_BEACON;

    // ENV
    public static final String TEX_RIFT_ONE = DIR_IMG_ENV + EntityID.RIFT_ONE;
    public static final String TEX_RIFT_TWO = DIR_IMG_ENV + EntityID.RIFT_TWO;

    public static final String TEX_THE_EDGE  = DIR_IMG_ENV + EntityID.THE_EDGE;
    public static final String TEX_BG_ARCADE = DIR_IMG_ENV + EntityID.BG_ARCADE;


    // Static Initializer
    static {
        // SFX_FILENAME_MAP
        String ext = ".wav";
        HashMap<SoundEffect, String> hashMap = new HashMap<SoundEffect, String>();
        hashMap.put(SoundEffect.AMMO_DEPLETED, DIR_SOUNDS + "ShootBlank" + ext);
        hashMap.put(SoundEffect.CELL_HIT, DIR_SOUNDS + "Hit" + ext);
        hashMap.put(SoundEffect.EXPLOSION, DIR_SOUNDS + "Explosion" + ext);
        hashMap.put(SoundEffect.LASER, DIR_SOUNDS + "LaserShot" + ext);
        hashMap.put(SoundEffect.LOSE, DIR_SOUNDS + "ShieldDown" + ext);
        hashMap.put(SoundEffect.PLAYER_HIT, DIR_SOUNDS + "PlayerHurt" + ext);
        hashMap.put(SoundEffect.POWERUP_PICKUP, DIR_SOUNDS + "Select" + ext); // TODO sound effect
        hashMap.put(SoundEffect.UI_BACK, DIR_SOUNDS + "UIBack" + ext);
        hashMap.put(SoundEffect.UI_BACK_LONG, DIR_SOUNDS + "UIBackLong" + ext);
        hashMap.put(SoundEffect.UI_CONFIRM, DIR_SOUNDS + "UIConfirm" + ext);
        SFX_FILENAME_MAP = Collections.unmodifiableMap(hashMap);

        // ANIM_PLAYER
        String prefix = DIR_IMG_GAME + EntityID.PLAYER + "/";
        ArrayList<String> playerAnim = new ArrayList<String>();
        playerAnim.add(prefix + "0");
        playerAnim.add(prefix + "1");
        playerAnim.add(prefix + "2");
        playerAnim.add(prefix + "3");
        playerAnim.add(prefix + "4");
        playerAnim.add(prefix + "5");
        playerAnim.add(prefix + "6");
        playerAnim.add(prefix + "7");
        playerAnim.add(prefix + "8");
        playerAnim.add(prefix + "9");
        ANIM_PLAYER = Collections.unmodifiableList(playerAnim);

        // ANIM_PLAYER_SHIELD
        prefix = DIR_IMG_GAME + EntityID.PLAYER_SHIELD + "/";
        ArrayList<String> playerShieldAnim = new ArrayList<String>();
        playerShieldAnim.add(prefix + "25p");
        playerShieldAnim.add(prefix + "50p");
        playerShieldAnim.add(prefix + "75p");
        playerShieldAnim.add(prefix + "100p");
        ANIM_PLAYER_SHIELD = Collections.unmodifiableList(playerShieldAnim);

        // ANIM_CIV1_BLINKER
        prefix = DIR_IMG_GAME + EntityID.CIV_ONE_BLINKER + "/";
        ArrayList<String> civ1BlinkerAnim = new ArrayList<String>();
        civ1BlinkerAnim.add(prefix + "0");
        civ1BlinkerAnim.add(prefix + "1");
        ANIM_CIV1_BLINKER = Collections.unmodifiableList(civ1BlinkerAnim);

        // ANIM VYRAPUFFER
        prefix = DIR_IMG_GAME + EntityID.VYRAPUFFER + "/";
        ArrayList<String> vyrapufferAnim = new ArrayList<String>();
        vyrapufferAnim.add(prefix + "0");
        vyrapufferAnim.add(prefix + "1");
        vyrapufferAnim.add(prefix + "2");
        ANIM_VYRAPUFFER = Collections.unmodifiableList(vyrapufferAnim);
    }

    public static String getSfxPath(SoundEffect effect) {
        return SFX_FILENAME_MAP.get(effect);
    }
}
