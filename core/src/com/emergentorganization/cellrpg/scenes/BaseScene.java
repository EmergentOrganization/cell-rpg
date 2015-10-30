package com.emergentorganization.cellrpg.scenes;

import com.badlogic.gdx.ScreenAdapter;
import com.emergentorganization.cellrpg.PixelonTransmission;

/**
 * Created by orelb on 10/30/2015.
 */
public class BaseScene extends ScreenAdapter {

    protected PixelonTransmission pt;

    public void setPt(PixelonTransmission pt){
        this.pt = pt;
    }

}
