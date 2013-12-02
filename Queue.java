import java.util.LinkedList;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;


public class Queue extends UntypedActor {
	private final java.util.Queue<ActorRef> passengers;
	private final ActorRef bodyScanner;
	private boolean scannerReady;
	private int lineId;

	public Queue(final ActorRef bodyScanner, int lineId) {
		this.passengers = new LinkedList<ActorRef>();
		this.bodyScanner = bodyScanner;
		this.scannerReady = false;
		this.lineId = lineId;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof EnterQueue) {
			ActorRef passenger = ((EnterQueue)msg).getPassenger();
			if(this.scannerReady) {
				System.out.println("Passenger entering queue. Scanner is ready. Send them.");
				passenger.tell(new Passenger.ProceedToBodyScan(this.bodyScanner), getContext());
				this.scannerReady = false;
			} else {
				System.out.println("(Line " +lineId+  ") Passenger added to queue.");
				this.passengers.add(passenger);
			}
		}
		else if(msg instanceof Line.BodyScanner.Ready) {
			if(this.passengers.isEmpty()) {
				this.scannerReady = true;
			} else {
				this.passengers.remove().tell(new Passenger.ProceedToBodyScan(this.bodyScanner), getContext());
			}
		}
		else { 
			unhandled(msg);
		}
	}

	@Override
	public void postStop() {
		System.out.println("(Line " +lineId+  ") Queue stopped.");
	}
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
