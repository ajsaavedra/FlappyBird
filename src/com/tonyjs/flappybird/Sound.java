package com.tonyjs.flappybird;

import javafx.scene.media.AudioClip;

/**
 * Created by tonysaavedra on 6/23/16.
 */
public class Sound {
    private AudioClip soundEffect;

    public Sound(String filePath) {
        soundEffect = new AudioClip(getClass().getResource(filePath).toExternalForm());
    }

    public void playClip() {
        soundEffect.play();
    }
}
