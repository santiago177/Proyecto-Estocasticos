import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Host implements Runnable{
	
	long start, dur;
	boolean isserver;
	int port;
	int id;
	static int val = 0;
	Host another;
	ArrayList<Integer> out = new ArrayList<>();
	ArrayList<Integer> target = new ArrayList<>();
	ArrayList<Integer> in = new ArrayList<>();
	
	public Host(int id, long start, long dur) {
		this.id = id;
		this.start = start;
		this.dur = dur;
	}
		
	
	@Override
	public void run() { 
		try {			
			Thread agent_thread = new Thread(new Agent(id, start, dur));
			agent_thread.start();
			
			for(int i = 0; i < out.size(); i++) {
				System.out.println("here");
				NIC nic = new NIC("server", out.get(i), id, start, dur, target.get(i));
				Thread t = new Thread(nic);
				t.setPriority(Thread.MAX_PRIORITY);
				t.start();
			}
			
			for(int i = 0; i < in.size(); i++) {
				NIC nic = new NIC("client", in.get(i), id, start, dur, 0);
				Thread t = new Thread(nic);
				t.setPriority(Thread.MAX_PRIORITY);
				t.start();
			}
			
			while(System.currentTimeMillis() < start + dur) {
				synchronized (this) {
					Vec pos = Simulation.data[id].position;
					Vec speed = Simulation.data[id].speed;
					pos.x += speed.x;
					pos.y += speed.y;
					System.out.printf("id %d position  %f %f speed %f %f\n", id, pos.x, pos.y, speed.x, speed.y);
				}
				Thread.sleep(30);				
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.printf("%d host closed\n", id);
	}

}
