
public class Agent implements Runnable {
	int id;
	long start, dur;
	static final double[] prob = {0.3, 0.3, 0.3, 0.1};//0 = accelerate, 1 = deaccelerate, 2 = change direction, 3 = do nothing
	static final double max_dist = 300;
	
	Agent(int id, long start, long dur) {
		this.id = id;
		this.start = start;
		this.dur = dur;
	}
	
	public static double distance(Vec a, Vec b) {
		return Math.sqrt((a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y));
	}
	
	public double uniform_random(double a, double b) {
		return a + Math.random()*(b - a);
	}
	
	public double util(int n) {
		if(n < 4) {
			double max, min;
			max = 0;
			for(int i = 0; i < Simulation.data.length; i++) {
				if(i == id)
					continue;
				max = Math.max(max, distance(Simulation.data[id].position, Simulation.data[i].position));			
			}
			//System.out.printf("id %d max %f\n", id, max);
			if(max >= max_dist) {
				if(n == 0)
					return uniform_random(0, 0.2);
				if(n == 1)
					return uniform_random(0, 0.5);
				if(n == 3)
					return uniform_random(0, 0.7);
			}
			else {
				if(n == 0)
					return uniform_random(0, 0.5);
				if(n == 1)
					return uniform_random(0, 0.2);
				if(n == 3)
					return uniform_random(0, 0.3);
			}
		}
		return uniform_random(0, 0.4);
	}
	
	public synchronized void execute(int n) {
		//System.out.printf("id %d executing %d\n", id, n);
		HostData data = Simulation.data[id];		
		if(n == 0) {			
			data.speed.x += data.accel.x;
			data.speed.y += data.accel.y;
		}
		else if(n == 1) {
			data.speed.x -= data.accel.x;
			data.speed.y -= data.accel.y;
		}
		else if(n == 2) {
			Vec new_speed = new Vec(0, 0);
			for(int i = 0; i < Simulation.data.length; i++) {
				new_speed.x += Simulation.data[i].position.x - data.position.x;
				new_speed.y += Simulation.data[i].position.y - data.position.y;
			}
			//System.out.printf("new speed %f %f\n", new_speed.x, new_speed.y);			
			double initial_norm = Vec.norm(data.speed);
			Simulation.data[id].speed = new_speed.normalize().mult(initial_norm);
			//System.out.printf("checking %f %f pos %f %f\n", Simulation.data[id].speed.x, Simulation.data[id].speed.y, data.position.x, data.position.y);
		}
	}
	
	@Override
	public void run() {
		try {
			HostData[] data = Simulation.data;
			while(System.currentTimeMillis() < start + dur) {				
				int best = 0;
				double val = 0;
				for(int i = 0; i <= 3; i++) {
					double u = util(i);
					double p = prob[i];
					if(u*p > val) {
						val = u*p;
						best = i;
					}
				}
				//System.out.println("best "+best);
				execute(best);
				Thread.sleep(50);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.printf("%d agent closed\n", id);
	}

}
