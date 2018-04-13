package com.ellirion.buildframework.terraincorrector;

import com.ellirion.buildframework.BuildFramework;

public class TerrainValidator {
    /***
     *
     * @return returns whether the terrain allows terrain generation
     */
    public boolean validate() {
        if (!validateNotFloating()) {
            return false;
        }
        if (!validateConnectedToGround()) {
            return false;
        }
        if (!validateNotSandwiched()) {
            return false;
        }
        if (!validateBlocksChangedDoesNotExceedThreshold()) {
            return false;
        }

        return true;
    }

    private boolean validateBlocksChangedDoesNotExceedThreshold() {
        return true;
    }

    private boolean validateNotSandwiched() {
        return true;
    }

    private boolean validateConnectedToGround() {
        final BuildFramework buildFramework = BuildFramework.getInstance();
        final double connectionPercentage = buildFramework.getConfig().getDouble("GroundConnectionPercentage", 0.80);
        return true;
    }

    private boolean validateNotFloating() {
        return true;
    }
}
