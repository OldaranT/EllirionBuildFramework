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
        assertEquals(0, outcome);
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
        assertEquals(1, outcome);
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
        assertEquals(2, outcome);
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
        assertEquals(2, outcome);
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
        assertEquals(3, outcome);
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
        assertEquals(3, outcome);
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
        assertEquals(4, outcome);
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
        assertEquals(4, outcome);
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
        assertEquals(5, outcome);
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
        assertEquals(5, outcome);
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
        assertEquals(6, outcome);
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
        assertEquals(7, outcome);
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
        assertEquals(8, outcome);
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
        assertEquals(9, outcome);
    }

    private void setKeysAndFacts(int hMinX, int xMin, int hMaxX, int xMax, int hMinZ,
                                 int zMin, int hMaxZ, int zMax) {
        facts.setValue(RavineSupportsRuleBook.getMinHoleX(), hMinX);
        facts.setValue(RavineSupportsRuleBook.getMinX(), xMin);
        facts.setValue(RavineSupportsRuleBook.getMaxHoleX(), hMaxX);
        facts.setValue(RavineSupportsRuleBook.getMaxX(), xMax);
        facts.setValue(RavineSupportsRuleBook.getMinHoleZ(), hMinZ);
        facts.setValue(RavineSupportsRuleBook.getMinZ(), zMin);
        facts.setValue(RavineSupportsRuleBook.getMaxHoleZ(), hMaxZ);
        facts.setValue(RavineSupportsRuleBook.getMaxZ(), zMax);
    }
}
