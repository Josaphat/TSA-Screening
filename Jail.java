import akka.actor.Actors;
import akka.actor.UntypedActor;


public class Jail extends UntypedActor {
	private int shutdownCounter = 0;

	@Override
	public void onReceive(Object msg) throws Exception {
		// TODO Implement Jail message handling
		if(msg instanceof String && ((String)msg).equals("LINE STOPPED")) {
			// TODO Log message received
			this.shutdownCounter++;
			if(shutdownCounter == Main.NUM_LINES) {
				// TODO Log message sent
				getContext().tell(Actors.poisonPill(), getContext());
			}
		} else {
			unhandled(msg);
		}
	}
	
	@Override
	public void postStop() {
		// TODO Take passengers to real jail. (i.e. Shut the actors down)
		System.out.println("Jail stopped.");
	}
}
