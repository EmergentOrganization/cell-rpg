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
import com.emergentorganization.cellrpg.tools.FileStructure;


public class TronShader implements PostProcessor {
    public static final String blurVertexShader = Gdx.files.internal(FileStructure.RESOURCE_DIR + "shaders/tron/Tron_Vert_Blur.glsl").readString();
    public static final String blurFragShader = Gdx.files.internal(FileStructure.RESOURCE_DIR + "shaders/tron/Tron_Frag_Blur.glsl").readString();
    public static final String colorMaskVertexShader = Gdx.files.internal(FileStructure.RESOURCE_DIR + "shaders/Vert_Passthrough.glsl").readString();
    public static final String colorMaskFragShader = Gdx.files.internal(FileStructure.RESOURCE_DIR + "shaders/tron/Tron_Frag_ColorMask.glsl").readString();
    private final TextureRegion maskRegion;
    private ShaderProgram colorMaskProgram = new ShaderProgram(colorMaskVertexShader, colorMaskFragShader);
    private ShaderProgram blurProgram = new ShaderProgram(blurVertexShader, blurFragShader);
    private FrameBuffer maskBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

    private SpriteBatch batch = new SpriteBatch(2);

    public TronShader() {
        this(new Vector3(1f, 1f, 1f));
    }

    public TronShader(Vector3 targetColor) {
        try {
            if (!blurProgram.isCompiled()) {
                throw new ShaderException(blurProgram.getLog());
            }
            else if (!colorMaskProgram.isCompiled()) {
                throw new ShaderException(colorMaskProgram.getLog());
            }
        } catch (ShaderException e) {
            e.printStackTrace();
        }

        colorMaskProgram.begin();
        colorMaskProgram.setUniformf("u_maskColor", targetColor.x, targetColor.y, targetColor.z);
        colorMaskProgram.end();
        blurProgram.begin();
        blurProgram.setAttributef("a_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0f, 0f);
        blurProgram.end();

        Texture mask = maskBuffer.getColorBufferTexture();
        maskRegion = new TextureRegion(mask, 0, 0, mask.getWidth(), mask.getHeight());
        maskRegion.flip(false, true); // FBO uses lower left, TextureRegion uses upper-left
    }

    @Override
    public void render(FrameBuffer frameBuffer) {
        Texture cb = frameBuffer.getColorBufferTexture();
        TextureRegion fboRegion = new TextureRegion(cb, 0, 0, cb.getWidth(), cb.getHeight());
        fboRegion.flip(false, true); // FBO uses lower left, TextureRegion uses upper-left

        // wide blur pass
        maskBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setShader(colorMaskProgram);
        batch.begin();
        batch.draw(fboRegion, 0, 0);
        batch.end();

        batch.setShader(blurProgram);
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
        batch.begin();

        blurProgram.setUniformf("u_radius", 7f);
        blurProgram.setUniformf("u_brightness", 0.07f);
        blurProgram.setUniformf("u_isVertical", 0f);
        batch.draw(maskRegion, 0, 0);
        batch.flush();
        blurProgram.setUniformf("u_isVertical", 1f);
        batch.draw(maskRegion, 0, 0);

        batch.end();
        maskBuffer.end();

        frameBuffer.begin();
        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.draw(maskRegion, 0, 0);
        batch.end();
        frameBuffer.end();

        // narrow blur pass
        maskBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setShader(colorMaskProgram);
        batch.begin();
        batch.draw(fboRegion, 0, 0);
        batch.end();

        batch.setShader(blurProgram);
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
        batch.begin();

        blurProgram.setUniformf("u_radius", 1f);
        blurProgram.setUniformf("u_brightness", 0.2f);
        blurProgram.setUniformf("u_isVertical", 0f);
        batch.draw(maskRegion, 0, 0);
        batch.flush();
        blurProgram.setUniformf("u_isVertical", 1f);
        batch.draw(maskRegion, 0, 0);

        batch.end();
        maskBuffer.end();

        // blend
        frameBuffer.begin();
        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.draw(maskRegion, 0, 0);
        batch.end();
        frameBuffer.end();
    }

    @Override
    public void dispose() {
        maskRegion.getTexture().dispose();
        colorMaskProgram.dispose();
        blurProgram.dispose();
        maskBuffer.getColorBufferTexture().dispose();
        maskBuffer.dispose();
        batch.dispose();
    }
}
