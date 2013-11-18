import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;

public class Line extends UntypedActor {
	private final ActorRef jail;
	
	private final ActorRef queue;
	private final ActorRef baggageScanner;
	private final ActorRef bodyScanner;
	private final ActorRef securityStation;
	
	public Line(final ActorRef jail) {
		this.jail = jail;
		this.queue = Actors.actorOf(Queue.class).start();
		this.baggageScanner = Actors.actorOf(BaggageScanner.class).start();
		this.bodyScanner = Actors.actorOf(BodyScanner.class).start();
		this.securityStation = Actors.actorOf(SecurityStation.class).start();
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		// TODO Implement Line message handling
		if(msg instanceof EnterQueue) {
			// TODO Log message received
			// TODO Log messages sent
			this.queue.tell(msg, getContext().getSender().get()); // Forward down to the Queue
			getContext().getSender().get().tell(new Passenger.ProceedToBagScan(this.baggageScanner), getContext());
		}
		else if(msg instanceof String && ((String)msg).equals("SHUT DOWN")) {
			// TODO Log message received
			// TODO Log message sent
			getContext().tell(Actors.poisonPill(), getContext());
		}
		else {
			unhandled(msg);
		}
	}
	
	@Override
	public void postStop() {
		System.out.println("Line stopped.");
		this.jail.tell("LINE STOPPED");
	}
	
	
	//
	// "Child" actors
	//
	static class Queue extends UntypedActor {
		private final List<ActorRef> passengers;
		
		public Queue() {
			this.passengers = new ArrayList<ActorRef>();
		}

		@Override
		public void onReceive(Object msg) throws Exception {
			// TODO Implement Queue message handling
			if(msg instanceof EnterQueue) {
				// TODO Log message received
				ActorRef passenger = ((EnterQueue)msg).getPassenger();
				this.passengers.add(passenger);
				System.out.println("Passenger added to queue.");
			}
			else { 
				unhandled(msg);
			}
		}
		
	}
	
	static class BaggageScanner extends UntypedActor {

		@Override
		public void onReceive(Object msg) throws Exception {
			if(msg instanceof Baggage) {
				// TODO Log message received
				System.out.println("Scanning bag...");
				// TODO Send baggage scan report
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

		@Override
		public void onReceive(Object msg) throws Exception {
			// TODO Implement BodyScanner message handling
			unhandled(msg);
		}
		
	}
	
	static class SecurityStation extends UntypedActor {

		@Override
		public void onReceive(Object msg) throws Exception {
			// TODO Implement SecurityStation message handling
			unhandled(msg);
		}
		
	}
	
	//
	// Messages
	//
	static class EnterQueue {
		private final ActorRef passenger;
		
		public EnterQueue(final ActorRef passenger) {
			this.passenger = passenger;
		}
		
		public ActorRef getPassenger() {
			return this.passenger;
		}
	}
}
