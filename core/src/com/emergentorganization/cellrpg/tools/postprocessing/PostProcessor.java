package com.emergentorganization.cellrpg.tools.postprocessing;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;

/**
 * Created by BrianErikson on 9/3/15.
 */
public interface PostProcessor {

    /**
     * Renders the post-process to a given framebuffer
     * @param frameBuffer
     */
    void render(FrameBuffer frameBuffer);

    void dispose();
}
