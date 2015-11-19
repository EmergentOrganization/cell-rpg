package com.emergentorganization.cellrpg.tools;

import com.emergentorganization.cellrpg.core.SoundEffect;

import java.util.HashMap;

/**
 * Created by brian on 11/19/15.
 */
public class Resources {
    private static HashMap<SoundEffect, String> sfxFileNameMap = new HashMap<SoundEffect, String>();

    // Static Initializer
    static {
        final String prefix = FileStructure.RESOURCE_DIR + "sounds/";
        final String ext = ".wav";
        sfxFileNameMap.put(SoundEffect.AMMO_DEPLETED, prefix + "ShootBlank" + ext);
        sfxFileNameMap.put(SoundEffect.CELL_HIT, prefix + "Hit" + ext);
        sfxFileNameMap.put(SoundEffect.EXPLOSION, prefix + "Explosion" + ext);
        sfxFileNameMap.put(SoundEffect.LASER, prefix + "LaserShot" + ext);
        sfxFileNameMap.put(SoundEffect.LOSE, prefix + "ShieldDown" + ext);
        sfxFileNameMap.put(SoundEffect.PLAYER_HIT, prefix + "PlayerHurt" + ext);
        sfxFileNameMap.put(SoundEffect.POWERUP_PICKUP, prefix + "Select" + ext); // TODO sound effect
        sfxFileNameMap.put(SoundEffect.UI_BACK, prefix + "UIBack" + ext);
        sfxFileNameMap.put(SoundEffect.UI_BACK_LONG, prefix + "UIBackLong" + ext);
        sfxFileNameMap.put(SoundEffect.UI_CONFIRM, prefix + "UIConfirm" + ext);
    }

    public static String getSfxPath(SoundEffect effect) {
        return sfxFileNameMap.get(effect);
    }

    public static HashMap<SoundEffect, String> getSfxFileNameMap() {
        return sfxFileNameMap;
    }
}
