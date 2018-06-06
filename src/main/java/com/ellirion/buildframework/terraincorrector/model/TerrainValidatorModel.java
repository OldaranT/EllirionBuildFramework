package com.ellirion.buildframework.terraincorrector.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class TerrainValidatorModel {

    @Getter private List<String> errors = new ArrayList<>();
    @Setter @Getter private boolean succeeded;

    /**
     * Construct the return model for the {@link com.ellirion.buildframework.terraincorrector.TerrainValidator}.
     * @param succeeded the default value for succeeded
     */
    public TerrainValidatorModel(final boolean succeeded) {
        this.succeeded = succeeded;
    }
}
