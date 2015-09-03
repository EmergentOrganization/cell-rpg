package com.emergentorganization.cellrpg.tools.postprocessing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by BrianErikson on 9/3/15.
 */
public class TronShader implements PostProcessor {
    public static final String horizontalVertexShader = Gdx.files.internal("shaders/Tron_Vert_Horizontal.glsl").readString();
    public static final String verticalVertexShader = Gdx.files.internal("shaders/Tron_Vert_Vertical.glsl").readString();
    public static final String horizontalFragShader = Gdx.files.internal("shaders/Tron_Frag_Horizontal.glsl").readString();
    public static final String verticalFragShader = Gdx.files.internal("shaders/Tron_Frag_Vertical.glsl").readString();
    private ShaderProgram horizontalBlurProgram = new ShaderProgram(horizontalVertexShader, horizontalFragShader);
    private ShaderProgram verticalBlurProgram = new ShaderProgram(verticalVertexShader, verticalFragShader);

    private SpriteBatch batch = new SpriteBatch(1);

    public TronShader() throws ShaderException {
        if (!horizontalBlurProgram.isCompiled()) {
            throw new ShaderException(horizontalBlurProgram.getLog());
        }
        else if (!verticalBlurProgram.isCompiled()) {
            throw new ShaderException(verticalBlurProgram.getLog());
        }
    }

    @Override
    public void render(FrameBuffer frameBuffer) {
        Texture cb = frameBuffer.getColorBufferTexture();
        TextureRegion fboRegion = new TextureRegion(cb, 0, 0, cb.getWidth(), cb.getHeight());
        fboRegion.flip(false, true); // FBO uses lower left, TextureRegion uses upper-left

        frameBuffer.begin();
        batch.setShader(horizontalBlurProgram);
        batch.begin();
        batch.draw(fboRegion, 0, 0);

        //batch.setShader(verticalBlurProgram);
        //batch.draw(fboRegion, 0, 0);
        batch.end();
        frameBuffer.end();
    }
}
