package io.github.emergentorganization.cellrpg.tools.postprocessing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.emergentorganization.cellrpg.core.WorldFactory;
import io.github.emergentorganization.cellrpg.core.entityfactory.EntityFactory;
import io.github.emergentorganization.cellrpg.tools.FileStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BackgroundShader implements PostProcessor {
    public static final String vertexShader = Gdx.files.internal(FileStructure.RESOURCE_DIR + "shaders/background/Background_Vert.glsl").readString();
    public static final String fragShader = Gdx.files.internal(FileStructure.RESOURCE_DIR + "shaders/background/Background_Frag.glsl").readString();
    private final Vector3 color;
    private ShaderProgram program = new ShaderProgram(vertexShader, fragShader);

    private final Logger logger = LogManager.getLogger(WorldFactory.class);
    private SpriteBatch batch = new SpriteBatch(1, program);
    private Vector2 pos;
    private long startTime = TimeUtils.millis();

    public BackgroundShader() {
        this(new Vector3(1f, 1f, 1f));
    }

    public BackgroundShader(Vector3 color) {
        this.color = color;

        try {
            if (!program.isCompiled()) {
                throw new ShaderException(program.getLog());
            }
        } catch (ShaderException e1) {
            e1.printStackTrace();
        }
    }

    public void setWorldPosition(Vector2 pos) {
        this.pos = pos;
        pos.x /= Gdx.graphics.getWidth() / EntityFactory.SCALE_BOX_TO_WORLD;
        pos.y /= Gdx.graphics.getHeight() / EntityFactory.SCALE_BOX_TO_WORLD;
    }

    @Override
    public void render(FrameBuffer frameBuffer) {
        Texture cb = frameBuffer.getColorBufferTexture();
        TextureRegion fboRegion = new TextureRegion(cb, 0, 0, cb.getWidth(), cb.getHeight());
        fboRegion.flip(false, true); // FBO uses lower left, TextureRegion uses upper-left

        program.begin();
        program.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        program.setUniformf("u_color", color.x, color.y, color.z);
        float time = (float) ((TimeUtils.millis() - startTime) * 0.001);
        program.setUniformf("u_time", time);
        program.setAttributef("a_worldPos", pos.x, pos.y, 0f, 0f);
        program.end();

        batch.begin();
        batch.draw(fboRegion, 0, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        program.dispose();
    }
}