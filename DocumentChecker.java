import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;


public class DocumentChecker extends UntypedActor {
	private final List<ActorRef> lines;
	
	public DocumentChecker(List<ActorRef> lines) {
		this.lines = lines;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		// TODO Implement DocumentChecker message handling
		unhandled(msg);
	}
	
	@Override
	public void postStop() {
		for(ActorRef line : this.lines) {
			line.tell("SHUT DOWN");
		}
	}

}
