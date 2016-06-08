package io.github.emergentorganization.cellrpg.tools.postprocessing;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;


interface PostProcessor {

    /**
     * Renders the post-process to a given framebuffer
     *
     * @param frameBuffer The FrameBuffer to write to
     */
    void render(FrameBuffer frameBuffer);

    void dispose();
}
