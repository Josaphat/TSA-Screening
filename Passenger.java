import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;

public class Passenger extends UntypedActor {

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof ProceedToDocumentChecker) {
			// TODO Log message received
			// TODO Log message sent
			ProceedToDocumentChecker message = (ProceedToDocumentChecker)msg;
			message.getDocumentChecker().tell(new DocumentChecker.TravelDocuments(), getContext());
		} else if(msg instanceof DocumentsPassed) {
			// TODO Log message received
			// TODO Log message sent
			ActorRef line = ((DocumentsPassed)msg).getLine();
			line.tell(new Line.EnterQueue(getContext()), getContext());
		} else if(msg instanceof DocumentsFailed) {
			// TODO Log message received
			System.out.println("Documents failed the document check. Passenger is leaving.");
			getContext().tell(Actors.poisonPill(), getContext());
		} else if(msg instanceof ProceedToBagScan) {
			// TODO Log message received
			// TODO Log message sent
			System.out.println("Passenger should place their bag on the scanner.");
			ProceedToBagScan message = (ProceedToBagScan)msg;
			message.getBagScanner().tell(new Line.BaggageScanner.Baggage(), getContext());
		} else {
			unhandled(msg);
		}
	}
	
	///
	/// Messages
	///
	static class ProceedToDocumentChecker {
		private final ActorRef documentChecker;
		
		public ProceedToDocumentChecker(ActorRef documentChecker) {
			this.documentChecker = documentChecker;
		}
		
		public ActorRef getDocumentChecker() { return this.documentChecker; }
	}
	
	static class DocumentsPassed {
		private final ActorRef line;
		public DocumentsPassed(final ActorRef line) {
			this.line = line;
		}
		public ActorRef getLine() {
			return this.line;
		}
	}
	
	static class DocumentsFailed { }
	
	static class ProceedToBagScan {
		private final ActorRef bagScanner;
		public ProceedToBagScan(final ActorRef bagScanner) {
			this.bagScanner = bagScanner;
		}
		public ActorRef getBagScanner() {
			return this.bagScanner;
		}
	}
}
