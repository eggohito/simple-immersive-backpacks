package com.github.eggohito.simple_immersive_bags.duck;

import com.github.eggohito.simple_immersive_bags.util.BagUpdateStatus;

public interface EntityBagUpdateStatus {
    BagUpdateStatus sib$getStatus();
    void sib$setStatus(BagUpdateStatus updateStatus);
}
