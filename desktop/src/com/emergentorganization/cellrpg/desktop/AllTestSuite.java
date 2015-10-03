package com.emergentorganization.cellrpg.desktop;

import com.emergentorganization.cellrpg.entities.ca.CATestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TesterTest.class, CATestSuite.class} )
public final class AllTestSuite {}
