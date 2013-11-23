import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;

public class Passenger extends UntypedActor {

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof ProceedToDocumentChecker) {
			ProceedToDocumentChecker message = (ProceedToDocumentChecker)msg;
			message.getDocumentChecker().tell(new DocumentChecker.TravelDocuments(), getContext());
		}
		else if(msg instanceof DocumentsPassed) {
			DocumentsPassed message = (DocumentsPassed)msg;
			
			// Put baggage on the scanner
			ActorRef baggageScanner = message.getBaggageScanner();
			baggageScanner.tell(new Line.BaggageScanner.Baggage(), getContext());
			
			// Enter the queue
			ActorRef queue = message.getQueue();
			queue.tell(new Queue.EnterQueue(getContext()), getContext());
		}
		else if(msg instanceof DocumentsFailed) {
			getContext().tell(Actors.poisonPill(), getContext());
		}
		else if(msg instanceof ProceedToBodyScan) {
			ProceedToBodyScan message = (ProceedToBodyScan)msg;
			message.getBodyScanner().tell(new Body(), getContext());
		}
		else if(msg instanceof ProceedToJail) {
			ProceedToJail message = (ProceedToJail)msg;
			System.out.println("Going to jail... :(");
			message.getJail().tell(new Jail.EnterJail(getContext()), getContext());
		}
		else {
			unhandled(msg);
		}
	}
	
	@Override
	public void postStop() {
		//System.out.println("Passenger stopped.");
	}
	
	///
	/// Messages
	///
	
	static class Body {
		private final boolean isSafe;
		public Body() {
			this.isSafe = Math.random() >= Main.BODY_FAIL_CHANCE;
		}
		public boolean isSafe() {
			return this.isSafe;
		}
	}
	
	static class ProceedToDocumentChecker {
		private final ActorRef documentChecker;
		
		public ProceedToDocumentChecker(ActorRef documentChecker) {
			this.documentChecker = documentChecker;
		}
		
		public ActorRef getDocumentChecker() { return this.documentChecker; }
	}
	
	static class ProceedToBodyScan {
		private final ActorRef bodyScanner;
		public ProceedToBodyScan(final ActorRef bodyScanner) {
			this.bodyScanner = bodyScanner;
		}
		public ActorRef getBodyScanner() {
			return this.bodyScanner;
		}
	}
	
	static class DocumentsPassed {
		private final ActorRef baggageScanner;
		private final ActorRef queue;
		public DocumentsPassed(final ActorRef baggageScanner, final ActorRef queue) {
			this.baggageScanner = baggageScanner;
			this.queue = queue;
		}
		public ActorRef getBaggageScanner() {
			return this.baggageScanner;
		}
		public ActorRef getQueue() {
			return this.queue;
		}
	}
	
	static class DocumentsFailed { }
	
	static class ProceedToJail {
		private final ActorRef jail;
		public ProceedToJail(final ActorRef jail) {
			this.jail = jail;
		}
		public ActorRef getJail() {
			return this.jail;
		}
	}
}
