import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Line {
	private final ActorRef jail;	
	private final ActorRef queue;
	private final ActorRef baggageScanner;
	private final ActorRef bodyScanner;
	private final ActorRef securityStation;
	private int lineId;
	
	public Line(final ActorRef jail, final ActorRef securityStation,
				 final ActorRef bodyScanner, final ActorRef baggageScanner,
				  final ActorRef queue, int lineId) {
		this.jail = jail;
		
		this.securityStation = securityStation;
		
		this.bodyScanner = bodyScanner;
		
		this.baggageScanner = baggageScanner;
		
		this.queue = queue;

		this.lineId = lineId;
	}
	
	public ActorRef getQueue() {
		return this.queue;
	}
	
	public ActorRef getBaggageScanner() {
		return this.baggageScanner;
	}
	
	public ActorRef getBodyScanner() {
		return this.bodyScanner;
	}
	
	public ActorRef getSecurityStation() {
		return this.securityStation;
	}
	
	public void shutDown() {
		// TODO Coordinate killing the actors
		// Necessary?
	}
	
	//
	// "Child" actors
	//
	
	static class BaggageScanner extends UntypedActor {
		private final ActorRef securityStation;
		private int lineId;
		
		public BaggageScanner(final ActorRef securityStation, int lineId) {
			this.securityStation = securityStation;
			this.lineId = lineId;
		}
		
		@Override
		public void onReceive(Object msg) throws Exception {
			if(msg instanceof Baggage) {
				Baggage message = (Baggage)msg;
				System.out.println("(Line " +lineId+  ") Scanning bag...");
				this.securityStation.tell(new SecurityStation.SecurityReport(getContext().getSender().get(), message.isSafe()),getContext());
			}
			else {
				unhandled(msg);
			}
		}
		
		static class Baggage {
			private final boolean isSafe;
			public Baggage() {
				this.isSafe = Math.random() >= Main.BAGGAGE_FAIL_CHANCE;
			}
			public boolean isSafe() {
				return this.isSafe;
			}
		}
	}
	
	static class BodyScanner extends UntypedActor {
		private final ActorRef securityStation;
		private ActorRef queue;
		private int lineId;
		
		public BodyScanner(final ActorRef securityStation,int lineId) {
			this.securityStation = securityStation;
			this.lineId = lineId;
		}
		
		@Override
		public void onReceive(Object msg) throws Exception {
			if(msg instanceof GiveQueue) {
				this.queue = ((GiveQueue)msg).getQueue();
				this.queue.tell(new Ready(), getContext());
			}
			else if(msg instanceof Passenger.Body) {
				Passenger.Body message = (Passenger.Body)msg;
				System.out.println("(Line " +lineId+  ") Scanning passenger body...");
				this.securityStation.tell(new SecurityStation.SecurityReport(getContext().getSender().get(), message.isSafe()), getContext());
				this.queue.tell(new Ready(), getContext());
			} else {
				unhandled(msg);
			}
		}
		
		static class Ready {
			
		}
		
		static class GiveQueue {
			private final ActorRef queue;
			public GiveQueue(final ActorRef queue) {
				this.queue = queue;
			}
			public ActorRef getQueue() {
				return this.queue;
			}
		}
	}
}
