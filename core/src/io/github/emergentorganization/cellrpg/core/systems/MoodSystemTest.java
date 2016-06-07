package io.github.emergentorganization.cellrpg.core.systems;

import io.github.emergentorganization.cellrpg.managers.EventManager;
import org.junit.Test;

/**
 */
public class MoodSystemTest {

    @Test
    public void testIntensityLevelScorerMax() {
        MoodSystem moodSystem = new MoodSystem(new EventManager());
        moodSystem.intensity = moodSystem.MAX_INTENSITY;
        int res = moodSystem.scoreIntensityLevelOutOf(10);
        assert res == 10;
    }

    @Test
    public void testIntensityLevelScorerGreaterThanMax() {
        MoodSystem moodSystem = new MoodSystem(new EventManager());
        moodSystem.intensity = moodSystem.MAX_INTENSITY * 2;
        int res = moodSystem.scoreIntensityLevelOutOf(10);
        assert res == 10;
    }

    @Test
    public void testIntensityLevelScorerMiddle() {
        MoodSystem moodSystem = new MoodSystem(new EventManager());
        moodSystem.intensity = moodSystem.MAX_INTENSITY / 2;
        int res = moodSystem.scoreIntensityLevelOutOf(10);
        assert res <= 6 && res >= 4;  // anything 4-6 is fine
    }
}