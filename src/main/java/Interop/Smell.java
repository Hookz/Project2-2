package Interop;

import Interop.Geometry.Distance;
import Interop.Percept.Smell.SmellPerceptType;

public class Smell {
    private SmellPerceptType type;
    private Distance radius;

    public Smell(SmellPerceptType type, Distance radius) {
        this.type = type;
        this.radius = radius;
    }

    public SmellPerceptType getType() {
        return type;
    }

    public Distance getRadius() {
        return radius;
    }
}
