package com.emergentorganization.cellrpg.tools.postprocessing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
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
    public static final String alphaMaskVertexShader = Gdx.files.internal("shaders/Tron_Vert_AlphaMask.glsl").readString();
    public static final String alphaMaskFragShader = Gdx.files.internal("shaders/Tron_Frag_AlphaMask.glsl").readString();
    private final TextureRegion maskRegion;
    private ShaderProgram alphaMaskProgram = new ShaderProgram(alphaMaskVertexShader, alphaMaskFragShader);
    private ShaderProgram horizontalBlurProgram = new ShaderProgram(horizontalVertexShader, horizontalFragShader);
    private ShaderProgram verticalBlurProgram = new ShaderProgram(verticalVertexShader, verticalFragShader);
    private FrameBuffer maskBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

    private SpriteBatch batch = new SpriteBatch(1);

    public TronShader() throws ShaderException {
        if (!horizontalBlurProgram.isCompiled()) {
            throw new ShaderException(horizontalBlurProgram.getLog());
        }
        else if (!verticalBlurProgram.isCompiled()) {
            throw new ShaderException(verticalBlurProgram.getLog());
        }
        else if (!alphaMaskProgram.isCompiled()) {
            throw new ShaderException(alphaMaskProgram.getLog());
        }
        horizontalBlurProgram.setAttributef("a_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0f, 0f);
        verticalBlurProgram.setAttributef("a_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0f, 0f);

        Texture mask = maskBuffer.getColorBufferTexture();
        maskRegion = new TextureRegion(mask, 0, 0, mask.getWidth(), mask.getHeight());
        maskRegion.flip(false, true); // FBO uses lower left, TextureRegion uses upper-left
    }

    @Override
    public void render(FrameBuffer frameBuffer) {

        Texture cb = frameBuffer.getColorBufferTexture();
        TextureRegion fboRegion = new TextureRegion(cb, 0, 0, cb.getWidth(), cb.getHeight());
        fboRegion.flip(false, true); // FBO uses lower left, TextureRegion uses upper-left

        // draw blur
        maskBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setShader(alphaMaskProgram);
        batch.begin();
        batch.draw(fboRegion, 0, 0);

        batch.setShader(horizontalBlurProgram);
        batch.draw(maskRegion, 0, 0);

        batch.setShader(verticalBlurProgram);
        batch.draw(maskRegion, 0, 0);
        batch.end();
        maskBuffer.end();

        // blend
        frameBuffer.begin();
        batch.begin();
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_SRC_ALPHA);
        batch.draw(maskRegion, 0, 0);
        batch.end();
        frameBuffer.end();
    }
}
