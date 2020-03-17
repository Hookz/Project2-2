package Interop;

import Interop.Geometry.Distance;
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Sound.SoundPerceptType;

public class Sound{
    private SoundPerceptType type;
    private Distance radius;
    private int duration = 3;

    public Sound(SoundPerceptType type, Distance radius) {
        this.type = type;
        this.radius = radius;
    }

    public SoundPerceptType getType() {
        return type;
    }

    public Distance getRadius() {
        return radius;
    }

    public void decaySound(){
        duration--;
    }

    public int getDuration() {
        return duration;
    }
}


