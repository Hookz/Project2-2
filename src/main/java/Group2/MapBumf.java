package Group2;
/**
 * Processes data from given knowledge base into information regarding map.
 * Absolutely not cheating :)
 */

import Interop.Geometry.Direction;
import Interop.Geometry.Point;

public class MapBumf {
    /**
     * Finds target position relative to intruder in unit basis (u: delta position)
     * @param a initial angle to target (0u, 0u)
     * @param b new angle to target after x location change (1u, 0u)
     * @param c new angle to target after y location change (0u, 1u)
     * @return target point represented in u units
     */
    public static Point targetLocationInUnits(Direction a, Direction b, Direction c) {
        //calculate x distance {a, b}
        // -(cot(b) * tan(a) - 1)
        double uDivB = -((1 / Math.tan(b.getRadians())) * Math.tan(a.getRadians()) - 1);
        double x = 1 / uDivB;

        //calculate y distance {a, c}
        // -(cot(a) * tan(c) - 1)
        double uDivC = -((1 / Math.tan(a.getRadians())) * Math.tan(c.getRadians()) - 1);
        double y = 1 / uDivC;

        return new Point(x, y);
    }
}
