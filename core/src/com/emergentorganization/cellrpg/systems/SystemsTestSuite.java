package com.emergentorganization.cellrpg.systems;

import com.emergentorganization.cellrpg.systems.CASystems.CAGenerationSystemTest;
import com.emergentorganization.cellrpg.systems.CASystems.CAInteractionSystemTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CAGenerationSystemTest.class, CAInteractionSystemTest.class} )
public final class SystemsTestSuite {}
