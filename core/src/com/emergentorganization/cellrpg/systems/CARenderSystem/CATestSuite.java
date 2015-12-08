package com.emergentorganization.cellrpg.systems.CARenderSystem;

import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.GeneticCellTest;
import com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.DGRN4j.DGRNTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DGRNTestSuite.class, GeneticCellTest.class} )
public final class CATestSuite {}
