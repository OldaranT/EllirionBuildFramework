package com.ellirion.buildframework.terraincorrector.rulebook;

import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

public class RavineSupportsRuleBook extends CoRRuleBook<Integer> {

    private static String MIN_HOLE_X = "minHoleX";
    private static String MIN_X = "minX";
    private static String MAX_HOLE_X = "maxHoleX";
    private static String MAX_X = "maxX";
    private static String MIN_HOLE_Z = "minHoleZ";
    private static String MIN_Z = "minZ";
    private static String MAX_HOLE_Z = "maxHoleZ";
    private static String MAX_Z = "maxZ";

    /**
     * @return the max x of the hole.
     */
    public static String getMaxHoleX() {
        return MAX_HOLE_X;
    }

    /**
     * @return the max z of the hole.
     */
    public static String getMaxHoleZ() {
        return MAX_HOLE_Z;
    }

    /**
     * @return the max x.
     */
    public static String getMaxX() {
        return MAX_X;
    }

    /**
     * @return the max z.
     */
    public static String getMaxZ() {
        return MAX_Z;
    }

    /**
     * @return the min x of the hole.
     */
    public static String getMinHoleX() {
        return MIN_HOLE_X;
    }

    /**
     * @return the min z of the hole.
     */
    public static String getMinHoleZ() {
        return MIN_HOLE_Z;
    }

    /**
     * @return the min x.
     */
    public static String getMinX() {
        return MIN_X;
    }

    /**
     * @return the min z.
     */
    public static String getMinZ() {
        return MIN_Z;
    }

    /*
     * WithResultType == what kind of object you return
     * when == if
     * then == action
     * stop == stop going through the rules if when == true
     * build == create the rule
     * */

    @Override
    public void defineRules() {
        // Check if the hole runs straight from one side to the other.
        // Build on the z-axis
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X) &&
                                       facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X)) &&
                                      !(facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z) ||
                                        facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z)))
                        .then((facts, result) -> result.setValue(0))
                        .stop()
                        .build());
        // Build on the x-axis
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z) &&
                                       facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z)) &&
                                      !(facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X) ||
                                        facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X)))
                        .then((facts, result) -> result.setValue(1))
                        .stop()
                        .build());
        // Check if hole is not a corner and faces north
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z) &&
                                       facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X) &&
                                       facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X) &&
                                       !(facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z))) ||
                                      (facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z) &&
                                       !(facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X) ||
                                         facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X) ||
                                         facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z))))
                        .then((facts, result) -> result.setValue(2))
                        .stop()
                        .build());
        // Check if hole is not a corner and faces east
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X) &&
                                       facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z) &&
                                       facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z) &&
                                       !(facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X))) ||
                                      (facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X) &&
                                       !(facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z) ||
                                         facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X) ||
                                         facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z))))
                        .then((facts, result) -> result.setValue(3))
                        .stop()
                        .build());
        // Check if hole is not a corner and faces south
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z) &&
                                       facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X) &&
                                       facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X) &&
                                       !(facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z))) ||
                                      (facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z) &&
                                       !(facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X) ||
                                         facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X) ||
                                         facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z))))
                        .then((facts, result) -> result.setValue(4))
                        .stop()
                        .build());
        // Check if hole is not a corner and faces west
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X) &&
                                       facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z) &&
                                       facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z) &&
                                       !(facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X))) ||
                                      (facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X) &&
                                       !(facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X) ||
                                         facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z) ||
                                         facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z))))
                        .then((facts, result) -> result.setValue(5))
                        .stop()
                        .build());
        // Check if the hole is a corner and faces north east
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z) &&
                                       facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X)) &&
                                      !(facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X) &&
                                        facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z)))
                        .then((facts, result) -> result.setValue(6))
                        .stop()
                        .build());
        // Check if the hole is a corner and faces south east
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z) &&
                                       facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X)) &&
                                      !(facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X) &&
                                        facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z)))
                        .then((facts, result) -> result.setValue(7))
                        .stop()
                        .build());
        // Check if the hole is a corner and faces south west
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z) &&
                                       facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X)) &&
                                      !(facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X) &&
                                        facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z)))
                        .then((facts, result) -> result.setValue(8))
                        .stop()
                        .build());
        // Check if the hole is a corner and faces north west
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(MIN_HOLE_Z) <= facts.getIntVal(MIN_Z) &&
                                       facts.getIntVal(MIN_HOLE_X) <= facts.getIntVal(MIN_X)) &&
                                      !(facts.getIntVal(MAX_HOLE_X) >= facts.getIntVal(MAX_X) &&
                                        facts.getIntVal(MAX_HOLE_Z) >= facts.getIntVal(MAX_Z)))
                        .then((facts, result) -> result.setValue(9))
                        .stop()
                        .build());
    }
}
