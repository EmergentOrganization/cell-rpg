package io.github.emergentorganization.cellrpg.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Profile;
import com.artemis.systems.IntervalEntityProcessingSystem;
import io.github.emergentorganization.cellrpg.components.EnergyLevel;
import io.github.emergentorganization.cellrpg.components.EquipmentList;
import io.github.emergentorganization.cellrpg.tools.profiling.EmergentProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * System for managing energyLevels based on environment and recharging equipments.
 */
@Profile(using = EmergentProfiler.class, enabled = true)
public class EnergySystem extends IntervalEntityProcessingSystem {
    private static final int FREQ = 1;
    private final Logger logger = LogManager.getLogger(getClass());
    private ComponentMapper<EquipmentList> equipMapper;

    public EnergySystem() {
        super(Aspect.all(EnergyLevel.class), FREQ);
    }

    public void process(Entity ent) {
        //EnergyLevel energyLevel = ent.getComponent(EnergyLevel.class);
        // TODO: change energyLevel based on environment (amount of flux in region)...
        // TODO: will need to implement flux levels in regions first.

        // Update equipment charges
        EquipmentList equipmentList = ent.getComponent(EquipmentList.class);
        if (equipmentList != null) {
            equipmentList.rechargeEquipment();
        }
    }
}