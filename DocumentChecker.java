import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;


public class DocumentChecker extends UntypedActor {
	private final List<ActorRef> lines;
	private int nextIndex;
	
	public DocumentChecker(List<ActorRef> lines) {
		this.lines = lines;
		this.nextIndex = 0;
	}
	
	private ActorRef nextLine() {
		ActorRef line = this.lines.get(nextIndex);
		nextIndex = (++nextIndex) % lines.size();
		return line;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		// TODO Implement DocumentChecker message handling
		if(msg instanceof TravelDocuments) {
			// TODO Log message received
			// TODO Log message sent
			TravelDocuments docs = (TravelDocuments)msg;
			if(docs.areValid()) {
				getContext().getSender().get().tell(new Passenger.DocumentsPassed(this.nextLine()));
			} else {
				getContext().getSender().get().tell(new Passenger.DocumentsFailed());
			}
		} else {
			unhandled(msg);
		}
	}
	
	@Override
	public void postStop() {
		for(ActorRef line : this.lines) {
			line.tell("SHUT DOWN");
		}
	}

	///
	/// Messages
	///
	static class TravelDocuments {
		private final boolean areValid;
		public TravelDocuments() {
			this.areValid = Math.random() >= Main.DOCUMENT_FAIL_CHANCE;
		}
		public boolean areValid() {
			return this.areValid;
		}
	}
}
