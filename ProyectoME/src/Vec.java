
public class Vec {
	double x, y;
	Vec(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public static double norm(Vec a) {
		return Math.sqrt(a.x*a.x + a.y*a.y);
	}
	
	public Vec mult(double n) {
		return new Vec(x*n, y*n);
	}
	
	public Vec normalize() {
		double n = norm(this);
		//System.out.println("norm "+n);
		return new Vec(x/n, y/n);
	}
}
