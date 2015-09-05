package com.emergentorganization.cellrpg.tools.postprocessing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by BrianErikson on 9/3/15.
 */
public class TronShader implements PostProcessor {
    public static final String horizontalVertexShader = Gdx.files.internal("shaders/tron/Tron_Vert_Horizontal.glsl").readString();
    public static final String verticalVertexShader = Gdx.files.internal("shaders/tron/Tron_Vert_Vertical.glsl").readString();
    public static final String horizontalFragShader = Gdx.files.internal("shaders/tron/Tron_Frag_Horizontal.glsl").readString();
    public static final String verticalFragShader = Gdx.files.internal("shaders/tron/Tron_Frag_Vertical.glsl").readString();
    public static final String colorMaskVertexShader = Gdx.files.internal("shaders/tron/Tron_Vert_ColorMask.glsl").readString();
    public static final String colorMaskFragShader = Gdx.files.internal("shaders/tron/Tron_Frag_ColorMask.glsl").readString();
    private final TextureRegion maskRegion;
    private ShaderProgram colorMaskProgram = new ShaderProgram(colorMaskVertexShader, colorMaskFragShader);
    private ShaderProgram horizontalBlurProgram = new ShaderProgram(horizontalVertexShader, horizontalFragShader);
    private ShaderProgram verticalBlurProgram = new ShaderProgram(verticalVertexShader, verticalFragShader);
    private FrameBuffer maskBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

    private SpriteBatch batch = new SpriteBatch(2);
    private int passes;

    public TronShader(int passes) {
        this(new Vector3(1f, 1f, 1f), passes);
    }

    public TronShader(Vector3 targetColor, int passes) {
        this.passes = passes;

        try {
            if (!horizontalBlurProgram.isCompiled()) {
                throw new ShaderException(horizontalBlurProgram.getLog());
            }
            else if (!verticalBlurProgram.isCompiled()) {
                throw new ShaderException(verticalBlurProgram.getLog());
            }
            else if (!colorMaskProgram.isCompiled()) {
                throw new ShaderException(colorMaskProgram.getLog());
            }
        } catch (ShaderException e) {
            e.printStackTrace();
        }

        colorMaskProgram.begin();
        colorMaskProgram.setUniformf("maskColor", targetColor.x, targetColor.y, targetColor.z);
        colorMaskProgram.end();
        horizontalBlurProgram.begin();
        horizontalBlurProgram.setAttributef("a_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0f, 0f);
        horizontalBlurProgram.end();
        verticalBlurProgram.begin();
        verticalBlurProgram.setAttributef("a_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0f, 0f);
        verticalBlurProgram.end();

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
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setShader(colorMaskProgram);
        batch.begin();
        batch.draw(fboRegion, 0, 0);

        for (int i = 0; i < passes; i++) {
            batch.setShader(horizontalBlurProgram);
            batch.draw(maskRegion, 0, 0);

            batch.setShader(verticalBlurProgram);
            batch.draw(maskRegion, 0, 0);
        }

        batch.end();
        maskBuffer.end();

        // blend
        frameBuffer.begin();
        batch.begin();
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.draw(maskRegion, 0, 0);
        batch.end();
        frameBuffer.end();
    }
}
