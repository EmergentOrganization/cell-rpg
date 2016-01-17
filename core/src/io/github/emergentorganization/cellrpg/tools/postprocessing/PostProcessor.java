package com.emergentorganization.cellrpg.tools.postprocessing;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;


public interface PostProcessor {

    /**
     * Renders the post-process to a given framebuffer
     *
     * @param frameBuffer
     */
    void render(FrameBuffer frameBuffer);

    void dispose();
}
