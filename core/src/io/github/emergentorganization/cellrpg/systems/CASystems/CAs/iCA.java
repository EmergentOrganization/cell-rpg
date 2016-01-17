package io.github.emergentorganization.cellrpg.systems.CASystems.CAs;

import io.github.emergentorganization.cellrpg.components.CAGridComponents;

/**
 * Defines interface to be used to compute cellular automata generations.
 */
public interface iCA {
    void generate(CAGridComponents gridComps);
    // computes the next ca generation for the given grid
}
