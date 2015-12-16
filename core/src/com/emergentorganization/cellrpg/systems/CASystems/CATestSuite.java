package com.emergentorganization.cellrpg.systems.CASystems;

import com.emergentorganization.cellrpg.components.CAGridComponentsTest;
import com.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.GeneticCellTest;
import com.emergentorganization.cellrpg.systems.CASystems.GeneticCells.DGRN4j.DGRNTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CAGridComponentsTest.class, CAGenerationSystemTest.class, DGRNTestSuite.class, GeneticCellTest.class} )
public final class CATestSuite {}
