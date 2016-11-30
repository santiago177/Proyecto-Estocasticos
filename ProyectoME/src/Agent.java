
public class Agent implements Runnable {
	int id;
	long start, dur;
	static final double[] prob = {0.3, 0.3, 0.3, 0.1};//0 = accelerate, 1 = deaccelerate, 2 = change direction, 3 = do nothing
	static final double max_dist = 500;
	static final double tol = 50;
	
	Agent(int id, long start, long dur) {
		this.id = id;
		this.start = start;
		this.dur = dur;
	}
	
	public static double distance(Vec a, Vec b) {
		return Math.sqrt((a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y));
	}
	
	public static double uniform_random(double a, double b) {
		return a + Math.random()*(b - a);
	}
	
	public boolean outOfScreen() {
		Vec pos = Simulation.data[id].position;
		int max_x, max_y;
		max_x = Simulation.max_x;
		max_y = Simulation.max_y;
		if(pos.x > max_x/2 || pos.x < -max_x/2 || pos.y > max_y/2 || pos.y < -max_y/2)
			return true;
		return false;
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
			if(outOfScreen()) {
				if(n == 2)
					return 1;
				else
					return 0;
			}
			if(max >= max_dist) {
				if(n == 0)
					return uniform_random(0, 0.2);
				if(n == 1)
					return uniform_random(0, 0.5);
				if(n == 2)
					return uniform_random(0, 0.7);
			}
			else {
				if(n == 0)
					return uniform_random(0, 0.6);
				if(n == 1)
					return uniform_random(0, 0.3);
				if(n == 2)
					return uniform_random(0, 0.1);
			}
		}
		return uniform_random(0, 0.4);
	}
	
	boolean doubleComp(double a, double b) {
		return Math.abs(a-b) < 0.00000000001;
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
			if(doubleComp(new_speed.x, 0) && doubleComp(new_speed.y, 0)) {
				new_speed.x = 0.1;
				new_speed.y = 0.1;
			}
			double initial_norm = Vec.norm(data.speed);
			new_speed = new_speed.normalize().mult(initial_norm);
			double reb = 3;
			if(Simulation.data[id].position.x > Simulation.max_x/2-tol) {
				//System.out.println("here");
				new_speed.x = -reb;
			}
			if(Simulation.data[id].position.x < -Simulation.max_x/2+tol) {
				new_speed.x = reb;
			}
			if(Simulation.data[id].position.y > Simulation.max_y/2-tol) {
				new_speed.y = -reb;
			}
			if(Simulation.data[id].position.y < -Simulation.max_y/2+tol) {
				new_speed.y = reb;
			}
			Simulation.data[id].speed = new_speed;			
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
