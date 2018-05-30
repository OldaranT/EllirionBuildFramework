package com.ellirion.buildframework.terraincorrector.rulebook;

import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

public class RavineSupportsRuleBook extends CoRRuleBook<Integer> {

    private String minHoleX = "minHoleX";
    private String minX = "minX";
    private String maxHoleX = "maxHoleX";
    private String maxX = "maxX";
    private String minHoleZ = "minHoleZ";
    private String minZ = "minZ";
    private String maxHoleZ = "maxHoleZ";
    private String maxZ = "maxZ";

    /*
     * withResultType == what kind of object you return
     * when == if
     * then == action
     * stop == stop going through the rules if when == true
     * build == create the rule
     * */

    @Override
    public void defineRules() {
        // check if the hole runs straight from one side to the other.
        // build on the Z AXIS
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(minHoleX) <= facts.getIntVal(minX) &&
                                       facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX)) &&
                                      !(facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) ||
                                        facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ)))
                        .then((facts, result) -> result.setValue(0))
                        .stop()
                        .build());
        // build on the X AXIS
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) &&
                                       facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ)) &&
                                      !(facts.getIntVal(minHoleX) <= facts.getIntVal(minX) ||
                                        facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX)))
                        .then((facts, result) -> result.setValue(1))
                        .stop()
                        .build());
        // check if hole is not a corner and faces north
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
        // check if hole is not a corner and faces east
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
        // check if hole is not a corner and faces south
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
        // check if hole is not a corner and faces west
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
        // check if the hole is a corner and faces north east
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ) &&
                                       facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX)) &&
                                      !(facts.getIntVal(minHoleX) <= facts.getIntVal(minX) &&
                                        facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ)))
                        .then((facts, result) -> result.setValue(6))
                        .stop()
                        .build());
        // check if the hole is a corner and faces south east
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ) &&
                                       facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX)) &&
                                      !(facts.getIntVal(minHoleX) <= facts.getIntVal(minX) &&
                                        facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ)))
                        .then((facts, result) -> result.setValue(7))
                        .stop()
                        .build());
        // check if the hole is a corner and faces south west
        addRule(RuleBuilder.create().withResultType(Integer.class)
                        .when(facts ->
                                      (facts.getIntVal(maxHoleZ) >= facts.getIntVal(maxZ) &&
                                       facts.getIntVal(minHoleX) <= facts.getIntVal(minX)) &&
                                      !(facts.getIntVal(maxHoleX) >= facts.getIntVal(maxX) &&
                                        facts.getIntVal(minHoleZ) <= facts.getIntVal(minZ)))
                        .then((facts, result) -> result.setValue(8))
                        .stop()
                        .build());
        // check if the hole is a corner and faces north west
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

    /**
     * @param minHoleX key for minHoleX
     * @param minX key for minX
     * @param maxHoleX key for maxHoleX
     * @param maxX key for maxX
     * @param minHoleZ key for minHoleZ
     * @param minZ key for minZ
     * @param maxHoleZ key for maxHoleZ
     * @param maxZ key for maxZ
     */
    public void setKeys(String minHoleX, String minX, String maxHoleX, String maxX, String minHoleZ, String minZ,
                        String maxHoleZ, String maxZ) {
        this.minHoleX = minHoleX;
        this.minX = minX;
        this.maxHoleX = maxHoleX;
        this.maxX = maxX;
        this.minHoleZ = minHoleZ;
        this.minZ = minZ;
        this.maxHoleZ = maxHoleZ;
        this.maxZ = maxZ;
    }
}
