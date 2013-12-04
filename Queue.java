import java.util.LinkedList;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;


public class Queue extends UntypedActor {
	private final java.util.Queue<ActorRef> passengers;
	private final ActorRef bodyScanner;
	private boolean scannerReady;
	private final int lineId;

	public Queue(final ActorRef bodyScanner, int lineId) {
		this.passengers = new LinkedList<ActorRef>();
		this.bodyScanner = bodyScanner;
		this.scannerReady = false;
		this.lineId = lineId;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof EnterQueue) {
			System.out.println("Queue<Line "+lineId+"> recieves EnterQueue message");
			ActorRef passenger = ((EnterQueue)msg).getPassenger();
			if(this.scannerReady) {
				System.out.println("Body Scanner<Line "+lineId+"> is ready. Send next Passenger.");
				passenger.tell(new Passenger.ProceedToBodyScan(this.bodyScanner), getContext());
				this.scannerReady = false;
			} else {
				System.out.println("Passenger added to Queue<Line "+lineId + ">.");
				this.passengers.add(passenger);
			}
		}
		else if(msg instanceof Line.BodyScanner.Ready) {
			System.out.println("Queue<Line "+lineId+"> recieves BodyScannerReady message");
			if(this.passengers.isEmpty()) {
				this.scannerReady = true;
			} else {
				System.out.println("Passenger at front of Queue<Line "+lineId+"> instructed to proceed to Body Scanner");
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
		this.bodyScanner.tell(new Line.ShutDown(), null);
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
