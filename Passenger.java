import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Passenger extends UntypedActor {

	@Override
	public void onReceive(Object msg) throws Exception {
		// TODO Implement Passenger message handling
		if(msg instanceof ProceedToDocumentChecker) {
			ProceedToDocumentChecker message = (ProceedToDocumentChecker)msg;
			System.out.println("Received message: " + message.getClass() + " from " + getContext().getSender());
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
}
