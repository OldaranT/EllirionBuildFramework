package com.ellirion.buildframework.terraincorrector.rulebook;

import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

public class RavineSupportsRuleBook extends CoRRuleBook<Integer> {

    public static String minHoleX = "minHoleX";
    public static String minX = "minX";
    public static String maxHoleX = "maxHoleX";
    public static String maxX = "maxX";
    public static String minHoleZ = "minHoleZ";
    public static String minZ = "minZ";
    public static String maxHoleZ = "maxHoleZ";
    public static String maxZ = "maxZ";

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
                                      (facts.getIntVal(minHoleX) <= facts.getIntVal(minX) &&
                                       facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX)) &&
                                      !(facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) ||
                                        facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ)))
                        .then((facts, result) -> result.setValue(0))
                        .stop()
                        .build());
        // Build on the x-axis
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) &&
                                       facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ)) &&
                                      !(facts.getIntVal(minHoleX) <= facts.getIntVal(minX) ||
                                        facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX)))
                        .then((facts, result) -> result.setValue(1))
                        .stop()
                        .build());
        // Check if hole is not a corner and faces north
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) &&
                                       facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX) &&
                                       facts.getIntVal(minHoleX) <= facts.getIntVal(minX) &&
                                       !(facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ))) ||
                                      (facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) &&
                                       !(facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX) ||
                                         facts.getIntVal(minHoleX) <= facts.getIntVal(minX) ||
                                         facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ))))
                        .then((facts, result) -> result.setValue(2))
                        .stop()
                        .build());
        // Check if hole is not a corner and faces east
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX) &&
                                       facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) &&
                                       facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ) &&
                                       !(facts.getIntVal(minHoleX) <= facts.getIntVal(minX))) ||
                                      (facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX) &&
                                       !(facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) ||
                                         facts.getIntVal(minHoleX) <= facts.getIntVal(minX) ||
                                         facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ))))
                        .then((facts, result) -> result.setValue(3))
                        .stop()
                        .build());
        // Check if hole is not a corner and faces south
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ) &&
                                       facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX) &&
                                       facts.getIntVal(minHoleX) <= facts.getIntVal(minX) &&
                                       !(facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ))) ||
                                      (facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ) &&
                                       !(facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX) ||
                                         facts.getIntVal(minHoleX) <= facts.getIntVal(minX) ||
                                         facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ))))
                        .then((facts, result) -> result.setValue(4))
                        .stop()
                        .build());
        // Check if hole is not a corner and faces west
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(minHoleX) <= facts.getIntVal(minX) &&
                                       facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) &&
                                       facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ) &&
                                       !(facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX))) ||
                                      (facts.getIntVal(minHoleX) <= facts.getIntVal(minX) &&
                                       !(facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX) ||
                                         facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) ||
                                         facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ))))
                        .then((facts, result) -> result.setValue(5))
                        .stop()
                        .build());
        // Check if the hole is a corner and faces north east
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) &&
                                       facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX)) &&
                                      !(facts.getIntVal(minHoleX) <= facts.getIntVal(minX) &&
                                        facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ)))
                        .then((facts, result) -> result.setValue(6))
                        .stop()
                        .build());
        // Check if the hole is a corner and faces south east
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ) &&
                                       facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX)) &&
                                      !(facts.getIntVal(minHoleX) <= facts.getIntVal(minX) &&
                                        facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ)))
                        .then((facts, result) -> result.setValue(7))
                        .stop()
                        .build());
        // Check if the hole is a corner and faces south west
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ) &&
                                       facts.getIntVal(minHoleX) <= facts.getIntVal(minX)) &&
                                      !(facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX) &&
                                        facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ)))
                        .then((facts, result) -> result.setValue(8))
                        .stop()
                        .build());
        // Check if the hole is a corner and faces north west
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) &&
                                       facts.getIntVal(minHoleX) <= facts.getIntVal(minX)) &&
                                      !(facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX) &&
                                        facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ)))
                        .then((facts, result) -> result.setValue(9))
                        .stop()
                        .build());
    }
}
