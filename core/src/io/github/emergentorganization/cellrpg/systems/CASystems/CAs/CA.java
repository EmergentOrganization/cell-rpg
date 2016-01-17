package io.github.emergentorganization.cellrpg.systems.CASystems.CAs;

import java.util.EnumMap;


public enum CA {
    NO_BUFFER, BUFFERED, GENETIC, DECAY;

//    TODO: use this instead of getCAMap(). also do the same in CellRenderer.
//    public static iCA getCA(CA ca){
//        return caMap.get(ca);
//    }

    public static EnumMap<CA, iCA> getCAMap() {
        // maps enums to interface implementers.
        EnumMap<CA, iCA> ca
                = new EnumMap<CA, iCA>(CA.class);

        ca.put(CA.NO_BUFFER, new NoBufferCA());
        ca.put(CA.BUFFERED, new BufferedCA());
        ca.put(CA.DECAY, new DecayCA());
        ca.put(CA.GENETIC, new GeneticCA());

        return ca;
    }
}
