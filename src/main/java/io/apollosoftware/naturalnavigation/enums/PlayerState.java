package io.apollosoftware.naturalnavigation.enums;

import lombok.Getter;

/**
 * Class created by xenojava on 8/30/2015.
 */
public enum PlayerState {
    PRE(NavigationType.START), PLAYING(NavigationType.NORMAL), DEAD(NavigationType.DEATH);

    @Getter
    private NavigationType correspondingType;

    PlayerState(NavigationType correspondingType) {
        this.correspondingType = correspondingType;
    }
}
