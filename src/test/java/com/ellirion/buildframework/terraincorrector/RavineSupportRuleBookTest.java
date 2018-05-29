package com.ellirion.buildframework.terraincorrector;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import org.junit.Test;
import com.ellirion.buildframework.terraincorrector.rulebook.RavineSupportsRuleBook;

import static org.junit.Assert.*;

public class RavineSupportRuleBookTest {

    private static final String minHX = "minHoleX";
    private static final String minX = "minX";
    private static final String maxHX = "maxHoleX";
    private static final String maxX = "maxX";
    private static final String minHZ = "minHoleZ";
    private static final String minZ = "minZ";
    private static final String maxHZ = "maxHoleZ";
    private static final String maxZ = "maxZ";
    private static final FactMap facts = new FactMap();

    private void setKeysAndFacts(RavineSupportsRuleBook ruleBook, int hMinX, int xMin, int hMaxX, int xMax, int hMinZ,
                                 int zMin, int hMaxZ, int zMax) {
        ruleBook.setKeys(minHX, minX, maxHX, maxX, minHZ, minZ, maxHZ, maxZ);

        facts.setValue(minHX, hMinX);
        facts.setValue(minX, xMin);
        facts.setValue(maxHX, hMaxX);
        facts.setValue(maxX, xMax);
        facts.setValue(minHZ, hMinZ);
        facts.setValue(minZ, zMin);
        facts.setValue(maxHZ, hMaxZ);
        facts.setValue(maxZ, zMax);
    }

    @Test
    public void ruleBook_whenRavineOnXAxis_shouldReturnZero() {
        // Arrange
        RavineSupportsRuleBook ruleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();

        setKeysAndFacts(ruleBook, 0, 0, 3, 3, 1, 0, 2, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 0);
    }

    @Test
    public void ruleBook_whenRavineOnZAxis_shouldReturnOne() {
        // Arrange
        RavineSupportsRuleBook ruleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();

        setKeysAndFacts(ruleBook, 1, 0, 2, 3, 0, 0, 3, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 1);
    }

    @Test
    public void ruleBook_whenHoleFacingNorth_shouldReturnTwo() {
        // Arrange
        RavineSupportsRuleBook ruleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();

        setKeysAndFacts(ruleBook, 0, 0, 3, 3, 0, 0, 2, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 2);

        // Arrange
        setKeysAndFacts(ruleBook, 1, 0, 2, 3, 0, 0, 2, 3);

        // Act
        ruleBook.run(facts);
        outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 2);
    }

    @Test
    public void ruleBook_whenHoleOnEastSide_shouldReturnThree() {
        // Arrange
        RavineSupportsRuleBook ruleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();

        setKeysAndFacts(ruleBook, 1, 0, 3, 3, 0, 0, 3, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 3);

        // Arrange
        setKeysAndFacts(ruleBook, 1, 0, 3, 3, 1, 0, 2, 3);

        // Act
        ruleBook.run(facts);
        outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 3);
    }

    @Test
    public void ruleBook_whenHoleOnSouthSide_shouldReturnFour() {
        // Arrange
        RavineSupportsRuleBook ruleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();

        setKeysAndFacts(ruleBook, 0, 0, 3, 3, 1, 0, 3, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 4);

        // Arrange
        setKeysAndFacts(ruleBook, 1, 0, 2, 3, 1, 0, 3, 3);

        // Act
        ruleBook.run(facts);
        outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 4);
    }

    @Test
    public void ruleBook_whenHoleOnWestSide_shouldReturnFive() {
        // Arrange
        RavineSupportsRuleBook ruleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();

        setKeysAndFacts(ruleBook, 0, 0, 2, 3, 0, 0, 3, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 5);

        // Arrange
        setKeysAndFacts(ruleBook, 0, 0, 2, 3, 1, 0, 2, 3);

        // Act
        ruleBook.run(facts);
        outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 5);
    }

    @Test
    public void ruleBook_whenHoleOnNorthEastCorner_shouldReturnSix() {
        // Arrange
        RavineSupportsRuleBook ruleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();

        setKeysAndFacts(ruleBook, 1, 0, 3, 3, 0, 0, 2, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 6);
    }

    @Test
    public void ruleBook_whenHoleOnSouthEastCorner_shouldReturnSeven() {
        // Arrange
        RavineSupportsRuleBook ruleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();

        setKeysAndFacts(ruleBook, 1, 0, 3, 3, 1, 0, 3, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 7);
    }

    @Test
    public void ruleBook_whenHoleOnSouthWestCorner_shouldReturnEight() {
        // Arrange
        RavineSupportsRuleBook ruleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();

        setKeysAndFacts(ruleBook, 0, 0, 2, 3, 1, 0, 3, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 8);
    }

    @Test
    public void ruleBook_whenHoleOnNorthWestCorner_shouldReturnNine() {
        // Arrange
        RavineSupportsRuleBook ruleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();

        setKeysAndFacts(ruleBook, 0, 0, 2, 3, 0, 0, 2, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 9);
    }
}
