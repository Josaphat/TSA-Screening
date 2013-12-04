import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import static akka.actor.Actors.*;

public class Passenger extends UntypedActor {

	private final int passengerId;

	public Passenger(int passengerId){
		this.passengerId = passengerId;
	}

	public int getId(){
		return passengerId;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof ProceedToDocumentChecker) {
			System.out.println("Passenger " + passengerId + " recieves ProceedToDocumentChecker message");
			ProceedToDocumentChecker message = (ProceedToDocumentChecker)msg;
			System.out.println("Passenger " + passengerId + " gives documents to document checker." );
			message.getDocumentChecker().tell(new DocumentChecker.TravelDocuments(passengerId), getContext());
		}
		else if(msg instanceof DocumentsPassed) {
			System.out.println("Passenger " + passengerId + " recieves DocumentsPassed message");
			DocumentsPassed message = (DocumentsPassed)msg;
			
			// Put baggage on the scanner
			ActorRef baggageScanner = message.getBaggageScanner();
			System.out.println("Passenger " + passengerId + " places baggage on conveyor belt." );
			baggageScanner.tell(new Line.BaggageScanner.Baggage(), getContext());
			
			// Enter the queue
			ActorRef queue = message.getQueue();
			System.out.println("Passenger " + passengerId + " enters the body scan queue." );
			queue.tell(new Queue.EnterQueue(getContext()), getContext());
		}
		else if(msg instanceof DocumentsFailed) {
			System.out.println("Passenger " + passengerId + " recieves DocumentsFailed message");
			System.out.println("Passenger " + passengerId + " leaves airport." );
			getContext().tell(poisonPill(), getContext());
		}
		else if(msg instanceof ProceedToBodyScan) {
			System.out.println("Passenger " + passengerId + " recieves ProceedToBodyScan message");
			ProceedToBodyScan message = (ProceedToBodyScan)msg;
			System.out.println("Passenger " + passengerId + " proceeds to Body Scanner." );
			message.getBodyScanner().tell(new Body(), getContext());
		}
		else if(msg instanceof ProceedToJail) {
			System.out.println("Passenger " + passengerId + " recieves ProceedToJail message");
			ProceedToJail message = (ProceedToJail)msg;
			System.out.println("Passenger " + passengerId + " goes to jail!");
			message.getJail().tell(new Jail.EnterJail(getContext(),passengerId), getContext());
		}
		else if(msg instanceof SecurityCheckPassed){
			System.out.println("Passenger " + passengerId + " recieves SecurityCheckPassed message");
			System.out.println("Passenger " + passengerId + " leaves the security area");
			getContext().tell(poisonPill(),getContext());
		}
		else {
			unhandled(msg);
		}
	}
	
	@Override
	public void postStop() {
		System.out.println("Passenger " + passengerId + " stopped.");
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
	
	static class DocumentsFailed {}
	
	static class ProceedToJail {
		private final ActorRef jail;
		public ProceedToJail(final ActorRef jail) {
			this.jail = jail;
		}
		public ActorRef getJail() {
			return this.jail;
		}
	}

	static class SecurityCheckPassed {}
}
