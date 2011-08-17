package nz.ubermouse.uberutils.helpers;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 7/11/11
 * Time: 9:11 PM
 * Package: nz.uberutils.helpers;
 */

import com.rsbuddy.script.methods.Skills;

/**
 * Enum for storing potions and there attributes
 */
public enum Potions
{
    EXTREME_ATTACK("Extreme attack", Skills.ATTACK),
    EXTREME_STRENGTH("Extreme strength", Skills.STRENGTH),
    EXTREME_DEFENCE("Extreme defence", Skills.DEFENSE),
    SUPER_ATTACK("Super attack", Skills.ATTACK),
    SUPER_STRENGTH("Super strength", Skills.STRENGTH),
    SUPER_DEFENCE("Super defence", Skills.DEFENSE),
    ATTACK_POTION("Attack potion", Skills.ATTACK),
    STRENGTH_POTION("Strength potion", Skills.STRENGTH),
    DEFENCE_POTION("Defence potion", Skills.DEFENSE),
    COMBAT_POTION("Combat potion", Skills.ATTACK),
    OVERLOAD("Overload (", Skills.ATTACK);
    private String name;
    private int    skill;

    Potions(String name, int skill) {
        this.name = name;
        this.skill = skill;
    }

    public String getName() {
        return name;
    }

    public int skill() {
        return skill;
    }
}
