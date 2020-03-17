package Interop;
import java.awt.geom.Point2D;
public class Line2DExtended extends java.awt.geom.Line2D.Double {
    private double slope, intercept;
    public Line2DExtended(double x1, double y1, double x2, double y2){
        super(x1,y1,x2,y2);
        this.slope = (y2-y1)/(x2-x1);
        this.intercept = this.y1 - this.slope * this.x1;
    }
    public Point2D evalAtX(double x){
        return new Point2D.Double(x,this.slope*x+this.intercept);
    }

    public double getIntercept() {
        return intercept;
    }

    public double getSlope() {
        return slope;
    }
}
