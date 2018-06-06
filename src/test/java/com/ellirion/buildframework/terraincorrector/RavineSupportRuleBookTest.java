package com.ellirion.buildframework.terraincorrector;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import org.junit.Before;
import org.junit.Test;
import com.ellirion.buildframework.terraincorrector.rulebook.RavineSupportsRuleBook;

import static org.junit.Assert.*;

public class RavineSupportRuleBookTest {

    private static final FactMap facts = new FactMap();
    private RavineSupportsRuleBook ruleBook;

    @Before
    public void setup() {
        ruleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();
    }

    @Test
    public void ruleBook_whenRavineOnXAxis_shouldReturnZero() {
        // Arrange
        setKeysAndFacts(0, 0, 3, 3, 1, 0, 2, 3);

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
        setKeysAndFacts(1, 0, 2, 3, 0, 0, 3, 3);

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
    public void ruleBook_whenHoleFacingNorthAndBothSidesEmpty_shouldReturnTwo() {
        // Arrange
        setKeysAndFacts(0, 0, 3, 3, 0, 0, 2, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 2);
    }

    @Test
    public void ruleBook_whenHoleFacingNorthAndSidesAreSolid_shouldReturnTwo() {
        // Arrange
        setKeysAndFacts(1, 0, 2, 3, 0, 0, 2, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 2);
    }

    @Test
    public void ruleBook_whenHoleOnEastSideAndBothSidesAreEmpty_shouldReturnThree() {
        // Arrange
        setKeysAndFacts(1, 0, 3, 3, 0, 0, 3, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 3);
    }

    @Test
    public void ruleBook_whenHoleOnEastSideAndBothSideAreSolid_shouldReturnThree() {
        // Arrange
        setKeysAndFacts(1, 0, 3, 3, 1, 0, 2, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 3);
    }

    @Test
    public void ruleBook_whenHoleOnSouthSideAndBothSidesEmpty_shouldReturnFour() {
        // Arrange
        setKeysAndFacts(0, 0, 3, 3, 1, 0, 3, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 4);
    }

    @Test
    public void ruleBook_whenHoleOnSouthSideAndBothSidesAreSolid_shouldReturnFour() {
        // Arrange
        setKeysAndFacts(1, 0, 2, 3, 1, 0, 3, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 4);
    }

    @Test
    public void ruleBook_whenHoleOnWestSideAndBothSidesAreEmpty_shouldReturnFive() {
        // Arrange
        setKeysAndFacts(0, 0, 2, 3, 0, 0, 3, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
        if (ruleBook.getResult().isPresent()) {
            Result result = ruleBook.getResult().get();
            outcome = (int) result.getValue();
        }

        // Assert
        assertTrue(outcome == 5);
    }

    @Test
    public void ruleBook_whenHoleOnWestSideAndBothSidesAreSolid_shouldReturnFive() {
        // Arrange
        setKeysAndFacts(0, 0, 2, 3, 1, 0, 2, 3);

        // Act
        ruleBook.run(facts);
        int outcome = Integer.MAX_VALUE;
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
        setKeysAndFacts(1, 0, 3, 3, 0, 0, 2, 3);

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
        setKeysAndFacts(1, 0, 3, 3, 1, 0, 3, 3);

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
        setKeysAndFacts(0, 0, 2, 3, 1, 0, 3, 3);

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
        setKeysAndFacts(0, 0, 2, 3, 0, 0, 2, 3);

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

    private void setKeysAndFacts(int hMinX, int xMin, int hMaxX, int xMax, int hMinZ,
                                 int zMin, int hMaxZ, int zMax) {

        facts.setValue(RavineSupportsRuleBook.minHoleX, hMinX);
        facts.setValue(RavineSupportsRuleBook.maxX, xMin);
        facts.setValue(RavineSupportsRuleBook.maxHoleX, hMaxX);
        facts.setValue(RavineSupportsRuleBook.maxX, xMax);
        facts.setValue(RavineSupportsRuleBook.minHoleZ, hMinZ);
        facts.setValue(RavineSupportsRuleBook.minZ, zMin);
        facts.setValue(RavineSupportsRuleBook.maxHoleZ, hMaxZ);
        facts.setValue(RavineSupportsRuleBook.maxZ, zMax);
    }
}
