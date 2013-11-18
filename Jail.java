import akka.actor.Actors;
import akka.actor.UntypedActor;


public class Jail extends UntypedActor {
	private int shutdownCounter = 0;

	@Override
	public void onReceive(Object msg) throws Exception {
		// TODO Implement Jail message handling
		if(msg instanceof String && ((String)msg).equals("LINE STOPPED")) {
			this.shutdownCounter++;
			if(shutdownCounter == Main.NUM_LINES) {
				getContext().tell(Actors.poisonPill());
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
