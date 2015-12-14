package com.emergentorganization.cellrpg.systems.CARenderSystem;

import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.core.entityfactory.CALayerFactory;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.GeneticCell;
import com.emergentorganization.cellrpg.systems.CARenderSystem.CACell.GeneticCellTest;
import com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.DGRN4j.DGRNTestSuite;
import com.emergentorganization.cellrpg.systems.CARenderSystem.GeneticCells.GeneticCellBuilders.TestCell2;
import com.emergentorganization.cellrpg.systems.CARenderSystem.layers.CALayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.Arrays;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CAGenerationSystemTest.class, DGRNTestSuite.class, GeneticCellTest.class} )
public final class CATestSuite {}
