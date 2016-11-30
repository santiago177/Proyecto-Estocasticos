import java.util.*;
public class HostData {
	Vec position;
	Vec speed;
	Vec accel;
	int read_packets;
	int sent_packets;
	double hold_delay;
	double send_delay;
	int target;
	ArrayList<Long> holding_times;
	ArrayList<Long> arrival_times;
	ArrayList<Long> random_holding;
	ArrayList<Long> random_arrival;
	
	public HostData(Vec position, Vec speed, Vec accel, double hold_delay, double send_delay) {
		this.position = position;
		this.speed = speed;
		this.accel = accel;
		read_packets = 0;
		sent_packets = 0;
		this.hold_delay = hold_delay;
		this.send_delay = send_delay;
		holding_times = new ArrayList<>();
		arrival_times = new ArrayList<>();
		random_arrival = new ArrayList<>();
		random_holding = new ArrayList<>();
	}
}
