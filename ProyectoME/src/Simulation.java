import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import processing.core.*;

class Color {
	float r, g, b;
	Color(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
}

public class Simulation extends PApplet{
	
	public volatile static HostData[] data;
	static Host hosts[];
	static int max_x = 1000;
	static int max_y = 700;
	static Color colors[];
	static long sim_time = 180000; // 180000 ms = 3 minutos
	static long extra = 2000;
	static long st;
	static int agents = 3;
	
	@Override
    public void settings() {
        size(max_x, max_y);
        
    }
	
	@Override
    public void draw() {    
		float r = 30;
        background(0);
        noStroke();
        translate(max_x/2, max_y/2);
        for(int i = 0; i < data.length; i++) {
        	fill(colors[(i+3)%7].r, colors[(i+3)%7].g, colors[(i+3)%7].b);
        	//System.out.printf("here %f %f %f %f\n", (float)data[i].position.x, (float)data[i].position.y, data[i].position.x, data[i].position.y);
        	ellipse((float)data[i].position.x, (float)data[i].position.y, r, r);        	
        }
    }
	
	public static void setConnection(Host src, Host dst, int port) {
		src.out.add(port);
		src.target.add(dst.id);
		dst.in.add(port);
	}
	
	public static void init() {
		long start = System.currentTimeMillis();
		colors = new Color[7];
		int[] r = {1, 0, 0, 1, 0, 1, 1};
        int[] g = {0, 1, 0, 1, 1, 0, 1};
        int[] b = {0, 0, 1, 1, 1, 1, 0};
        for(int i = 0; i < 7; i++) {
        	colors[i] = new Color(255*r[i], 255*g[i], 255*b[i]);
        }
		
		hosts = new Host[agents];
		data = new HostData[agents];
		st = start;
		data[0] = new HostData(new Vec(0, 0), new Vec(0, 0.2), new Vec(0.1, 0.1), 15, 10); //proceso llegada
		data[1] = new HostData(new Vec(0, 0), new Vec(0.1, -0.2), new Vec(0.1, 0.1), 20, 23);
		data[2] = new HostData(new Vec(0, 0), new Vec(0.1, 0.3), new Vec(0.1, 0.1), 18, 15);
		hosts[0] = new Host(0, start, sim_time);	
		hosts[1] = new Host(1, start, sim_time);
		hosts[2] = new Host(2, start, sim_time);
		setConnection(hosts[0], hosts[1], 9003);
		setConnection(hosts[1], hosts[2], 9004);
		setConnection(hosts[2], hosts[0], 9005);
	}
	
	public static double exp_random(double lambda) {
		double r = -Math.log(1-Math.random());
		//System.out.printf("log val %f res %f lambda %f\n", r, r/lambda, lambda);
		return r/lambda;
	}
	
	private static void write_report() {
		for(int i = 0; i < data.length; i++) {
			try {
					System.out.println("writing "+i);
					File f = new File(String.format("host%d.txt", i));
					PrintWriter pw = new PrintWriter(new FileWriter(f));
					pw.println("Holding times");
					for(long l: data[i].holding_times) {
						pw.println(l);
				}
				pw.println("Arrival times");
				if(data[i] == null)
					System.out.printf("%d data null\n");
				if(data[i].arrival_times == null)
					System.out.printf("%d arrival_times null\n");
				for(long l: data[i].arrival_times) {
					pw.println(l);
				}
				pw.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {		
		Thread threads[] = new Thread[agents];
		init();
		
		//setConnection(hosts[1], hosts[0], 9001);
		//hosts[2] = new Host(9091, true);
		//hosts[3] = new Host(9091, false);
		for(int i = 0; i < threads.length; i++) {
			if(hosts[i] == null)
				break;
			threads[i] = new Thread(hosts[i]);
			threads[i].start();
		}
		
		PApplet.main(Simulation.class.getCanonicalName());
		
		while(System.currentTimeMillis() < st + sim_time + extra) {
			
		}
		
		write_report();			
		
		System.out.println("out");
	}

}
