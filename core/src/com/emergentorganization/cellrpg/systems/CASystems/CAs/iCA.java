package com.emergentorganization.cellrpg.systems.CASystems.CAs;

import com.emergentorganization.cellrpg.components.CAGridComponents;
import com.emergentorganization.cellrpg.systems.CASystems.CAs.CACell.BaseCell;

/**
 * Defines interface to be used to compute cellular automata generations.
 *
 * Created by 7yl4r on 12/16/2015.
 */
public interface iCA {
    void generate(CAGridComponents gridComps);
    // computes the next ca generation for the given grid
}
