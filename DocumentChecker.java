import java.util.List;

import akka.actor.UntypedActor;


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
				getContext().getSender().get().tell(new Passenger.DocumentsPassed(nextLine.getBaggageScanner(), nextLine.getQueue()), getContext());
			} else {
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
		public TravelDocuments() {
			this.areValid = Math.random() >= Main.DOCUMENT_FAIL_CHANCE;
		}
		public boolean areValid() {
			return this.areValid;
		}
	}
}
