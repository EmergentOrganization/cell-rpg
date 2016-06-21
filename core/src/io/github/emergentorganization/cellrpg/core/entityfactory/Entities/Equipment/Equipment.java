package io.github.emergentorganization.cellrpg.core.entityfactory.Entities.Equipment;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import io.github.emergentorganization.cellrpg.components.Charge;
import io.github.emergentorganization.cellrpg.components.Weapon.Powerup;
import io.github.emergentorganization.cellrpg.core.components.Bounds;
import io.github.emergentorganization.cellrpg.core.components.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract class which describes an equipment entity.
 *
 *      !IMPORTANT!
 * Equipments must have zero-argument constructor which will be used when loading from save file,
 * so instead of using a constructor, @Override setup() and/or create() methods.
 *
 * setup() and create() perform construction instead and return this object for easy method chaining.
 * setup() is used to set values in code and is not called when loading from file (since values are set when loaded).
 * create() MUST be called following construction and load.
 *
 * Thus, construction might something like this:
 *      Equipment myEquip = new Equipment().setup().create();
 *
 * read() and write() provide default serialization; inheritors may need to @Override these to
 * add their additional attributes if they must be saved to file.
 */

public abstract class Equipment implements Json.Serializable{
    public String name;
    private String description;
    public EquipmentType type = EquipmentType.CONTROLLER;
    int parentId = -1;
    protected Entity ent;

    public boolean damaged = false;

    // energy units
    public int baseEnergy = 0;
    public int energySlots = 0;
    public int powerFilled = 0;

    // equipment stats (probably only use one of these according to type)
    int attackStat = 0;
    int shieldStat = 0;
    int moveStat = 0;
    int satStat = 0;


    public Equipment setup(String name, String description, int baseEnergy, int energySlots){
        // Constructs the equipment. (not using constructor b/c zero-argument constructor needed for save/loading)
        // Equipment *might* work without calling this, but not recommended.
        this.name = name;
        this.description = description;
        this.baseEnergy = baseEnergy;
        this.energySlots = energySlots;
        return this;
    }

    public Equipment setChargeStats(int initCharge, int rechargeRate, int maxCharge){
        return this;
    }

    public Equipment create(World world, Vector2 pos, int parentId) {
        // instantiates the equipment Entity as child of parentId
        // use to initialize on construction or after load from file.
        // MUST be called before using the equipment.
        this.parentId = parentId;
        buildEntity();

        // TODO: call setupEvents?
        return this;
    }

    public abstract void buildEntity();
    // builds Entity and sets ent

    public int attackStat() {
        return attackStat * powerLevel();
    }

    public int shieldStat() {
        return shieldStat * powerLevel();
    }

    public int moveStat() {
        return moveStat * powerLevel();
    }

    public int satStat() {
        return satStat * powerLevel();
    }

    int powerLevel() {
        // returns to what level this equipment is powered (above base power).
        if (powerFilled - baseEnergy > 0) {
            return powerFilled - baseEnergy;
        } else {
            return 0;
        }
    }

    public boolean powerIsFull() {
        // return true if no more room for more power
        return baseEnergy + energySlots <= powerFilled;
    }

    public boolean powerIsEmpty() {
        // return true if no power at all allocated
        return powerFilled > 0;
    }

    public boolean isPowered() {
        return powerFilled > baseEnergy;  // must be greater else just enough power to run but not to apply anything
    }

    public void recharge() {
        // energy management functions for the equipment. Called by EnergySystem.
        // recharge weapon
        if (isPowered()) {
            ent.getComponent(Charge.class).recharge(powerLevel());
            logger.trace("recharge " + type);
        }
        // TODO: take some charge from the energySystem and give it to the equipment
    }

    public void powerUp(Powerup type) {
        // powers up the equipment (if applicable) with given powerup
    }

    public void updatePosition(ComponentMapper<Bounds> boundsMapper, ComponentMapper<Position> posMapper) {
        // movement management functions for the equipment. Acts to keep equipment entity next to parent.
        // Called by MovementSystem.
        // Default behavior is to center the equipment on the parent, @Override if different behavior desired.
        centerOnParent(boundsMapper, posMapper);
    }

    public void write (Json json) {
        // writes the values needed to reconstruct the equipment

        // equipment instance details (things typically passed to constructor)
        json.writeValue("name", name);
        json.writeValue("desc", description);
        json.writeValue("enSl", energySlots);
        json.writeValue("baEn", baseEnergy);
        json.writeValue("atSt", attackStat);
        json.writeValue("shSt", shieldStat);
        json.writeValue("moSt", moveStat);
        json.writeValue("saSt", satStat);

        // equipment state
        json.writeValue("poFi", powerFilled);
        json.writeValue("dama", damaged);
    }

    public void read (Json json, JsonValue jsonMap) {
        // read in saved values
        name = jsonMap.get("name").asString();
        description = jsonMap.get("desc").asString();
        energySlots = jsonMap.get("enSl").asInt();
        baseEnergy = jsonMap.get("baEn").asInt();
        attackStat = jsonMap.get("atSt").asInt();
        shieldStat = jsonMap.get("shSt").asInt();
        moveStat = jsonMap.get("moSt").asInt();
        satStat = jsonMap.get("saSt").asInt();

        // load equipment state
        powerFilled = jsonMap.get("poFi").asInt();
        damaged = jsonMap.get("dama").asBoolean();

        // NOTE: must call create() following deserialization
    }

    public void centerOnParent(ComponentMapper<Bounds> boundsMapper, ComponentMapper<Position> posMapper){
        if (ent.getId() > -1) {
            posMapper.get(ent.getId()).centerOnParent(
                    boundsMapper.get(ent.getId()),
                    posMapper.get(parentId), boundsMapper.get(parentId)
            );
        } else {
            logger.error("cannot updatePos of equip#" + ent.getId() + " of ent#" + parentId);
        }
    }

    private final Logger logger = LogManager.getLogger(getClass());
}
