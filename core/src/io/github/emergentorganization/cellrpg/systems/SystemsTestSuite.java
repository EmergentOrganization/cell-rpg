package io.github.emergentorganization.cellrpg.systems;

import io.github.emergentorganization.cellrpg.systems.CASystems.CAGenerationSystemTest;
import io.github.emergentorganization.cellrpg.systems.CASystems.CAInteractionSystemTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CAGenerationSystemTest.class, CAInteractionSystemTest.class})
public final class SystemsTestSuite {
}
