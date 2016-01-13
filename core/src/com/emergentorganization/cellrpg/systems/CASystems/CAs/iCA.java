package com.emergentorganization.cellrpg.systems.CASystems.CAs;

import com.emergentorganization.cellrpg.components.CAGridComponents;

/**
 * Defines interface to be used to compute cellular automata generations.
 */
public interface iCA {
    void generate(CAGridComponents gridComps);
    // computes the next ca generation for the given grid
}
