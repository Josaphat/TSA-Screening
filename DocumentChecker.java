import java.util.List;

import akka.actor.UntypedActor;
import akka.actor.ActorRef;


public class DocumentChecker extends UntypedActor {
	private final List<Line> lines;
	private int nextIndex;
	
	public DocumentChecker(List<Line> lines) {
		this.lines = lines;
		this.nextIndex = 0;
	}
	
	private Line nextLine() {
		Line line = this.lines.get(nextIndex);
		nextIndex = (++nextIndex) % lines.size();
		return line;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof TravelDocuments) {
			TravelDocuments docs = (TravelDocuments)msg;
			if(docs.areValid()) {
				Line nextLine = this.nextLine();
				System.out.println("Document Checker assigns Passenger" + docs.getId() + " to line " + nextLine.getId() + ".");
				getContext().getSender().get().tell(new Passenger.DocumentsPassed(nextLine.getBaggageScanner(), nextLine.getQueue()), getContext());
			} else {
				System.out.println("Document Checker rejects Passenger" + docs.getId() + "'s travel documents.");
				getContext().getSender().get().tell(new Passenger.DocumentsFailed(), getContext());
			}
		} else {
			unhandled(msg);
		}
	}
	
	@Override
	public void postStop() {
		for(Line line : this.lines) {
			line.shutDown();
			//line.tell("SHUT DOWN");
		}
	}

	///
	/// Messages
	///
	static class TravelDocuments {
		private final boolean areValid;
		private final int passengerId;
		public TravelDocuments(int passengerId) {
			this.passengerId = passengerId;
			this.areValid = Math.random() >= Main.DOCUMENT_FAIL_CHANCE;
		}
		public boolean areValid() {
			return this.areValid;
		}
		public int getId(){
			return passengerId;
		}
	}
}
