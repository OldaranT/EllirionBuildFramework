package com.ellirion.buildframework.util.worldhelper;

import lombok.Getter;
import lombok.Setter;
import com.ellirion.buildframework.model.BlockChange;
import com.ellirion.buildframework.util.async.IPromiseFinisher;

public class PendingBlockChange {

    @Getter private BlockChange change;
    @Getter @Setter private IPromiseFinisher<Boolean> finisher;

    PendingBlockChange(final BlockChange change) {
        this.change =  change;
    }

}
