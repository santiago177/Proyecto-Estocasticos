import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Simulation {
	
	public volatile static HostData[] data;
	static Host hosts[];
	static long sim_time = 180000; // 180000 ms = 3 minutos
	static long extra = 2000;
	static long st;
	static int agents = 3;
	
	public static void setConnection(Host src, Host dst, int port) {
		src.out.add(port);
		src.target.add(dst.id);
		dst.in.add(port);
	}
	
	public static void init() {
		long start = System.currentTimeMillis();
		hosts = new Host[agents];
		data = new HostData[agents];
		st = start;
		data[0] = new HostData(new Vec(0, 0), new Vec(0, 2), new Vec(1, 1), 15, 10); //proceso llegada
		data[1] = new HostData(new Vec(0, 0), new Vec(1, -2), new Vec(1, 1), 20, 23);
		data[2] = new HostData(new Vec(0, 0), new Vec(1, 3), new Vec(1, 1), 18, 15);
		hosts[0] = new Host(0, start, sim_time);	
		hosts[1] = new Host(1, start, sim_time);
		hosts[2] = new Host(2, start, sim_time);
		setConnection(hosts[0], hosts[1], 9000);
		setConnection(hosts[1], hosts[2], 9001);
		setConnection(hosts[2], hosts[0], 9002);
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
		
		while(System.currentTimeMillis() < st + sim_time + extra) {
			
		}
		
		write_report();
		
		System.out.println("out");
	}

}
