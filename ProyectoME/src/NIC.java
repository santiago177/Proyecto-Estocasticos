import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class NIC implements Runnable{
	
	private static double time_per_unit = 0.2;
	long start, dur;
	String type; //true = client, false = server	
	int port;
	int id;
	int target;
	
	NIC(String type, int port, int id, long start, long dur, int target) {
		this.type = type;
		this.port = port;
		this.id = id;
		this.start = start;
		this.dur = dur;
		this.target = target;
	}
	
	@Override
	public void run() {
		System.out.println("at nic run");
		try {
			if(type.equals("client")) {
				Socket client = new Socket("127.0.0.1", port);
				BufferedReader bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
				System.out.println("waiting");
				String msg;
				while((msg = bf.readLine()) != null && System.currentTimeMillis() < start + dur) {
					//System.out.printf("port %d Received %s id %d\n", port, msg, id);
					HostData data = Simulation.data[id];
					//System.out.println(System.currentTimeMillis() - start+" in client added");
					Simulation.data[id].holding_times.add(System.currentTimeMillis() - start);
					double net_delay = data.hold_delay;
					long r1 = (long)(Simulation.exp_random(1.0/net_delay));
					long r2 = (long)(Agent.distance(data.position, Simulation.data[data.target].position)*time_per_unit);
					//System.out.printf("r1 %d r2 %d\n", r1, r2);
					long delay = (long)(r1 + r2);
					//System.out.printf("%d client delay %d\n", id, delay);
					Thread.sleep(delay);
				}
				//System.out.printf("%d client closed\n", id);
				client.close();
			}
			else { // Server
				System.out.println("at server");
				ServerSocket ss = new ServerSocket(port);
				Socket server = ss.accept();
				PrintWriter pw = new PrintWriter(server.getOutputStream(), true);
				String msg = "something";
				
				while(System.currentTimeMillis() < start + dur) {
					pw.println(msg);
					//System.out.printf("port %d Sent %d id %d\n", port, port, id);							
					HostData data = Simulation.data[id];
					//System.out.println(System.currentTimeMillis() - start+" in server added");
					Simulation.data[target].arrival_times.add(System.currentTimeMillis() - start);
					double net_delay = data.send_delay;
					long delay = (long)(Simulation.exp_random(1.0/net_delay) + Agent.distance(data.position, Simulation.data[data.target].position)*time_per_unit);
					//System.out.printf("%d server delay %d\n", id, delay);
					Thread.sleep(delay);							
				}
				//System.out.printf("%d server closed\n", id);
				server.close();
				ss.close();
			}				
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}		

}
