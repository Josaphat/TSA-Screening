import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;

public class Line extends UntypedActor {
	private final ActorRef jail;
	
	public Line(final ActorRef jail) {
		this.jail = jail;
		// TODO Construct the line
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		// TODO Implement Line message handling
		if(msg instanceof String && ((String)msg).equals("SHUT DOWN")) {
			getContext().tell(Actors.poisonPill());
		} else {
			unhandled(msg);
		}
	}
	
	@Override
	public void postStop() {
		System.out.println("Line stopped.");
		this.jail.tell("LINE STOPPED");
	}
}
