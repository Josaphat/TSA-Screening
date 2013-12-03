import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;


public class Jail extends UntypedActor {
	private final List<ActorRef> prisoners;
	
	private int shutdownCounter;

	public Jail() {
		this.shutdownCounter = 0;
		this.prisoners = new ArrayList<ActorRef>();
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof String && ((String)msg).equals("LINE STOPPED")) {
			this.shutdownCounter++;
			if(shutdownCounter == Main.NUM_LINES) {
				getContext().tell(Actors.poisonPill(), getContext());
			}
		}
		else if(msg instanceof EnterJail) {
			EnterJail message = (EnterJail)msg;
			this.prisoners.add(message.getPassenger());
			System.out.println("Throwing passenger in jail.");
		}
		else {
			unhandled(msg);
		}
	}
	
	@Override
	public void postStop() {
		System.out.println("Jail stopped.");
		for(ActorRef prisoner : this.prisoners) {
			prisoner.tell(Actors.poisonPill());
		}
	}
	
	//
	// Messages
	//
	static class EnterJail {
		private final ActorRef passenger;
		public EnterJail(final ActorRef passenger) {
			this.passenger = passenger;
		}
		public ActorRef getPassenger() {
			return this.passenger;
		}
	}
}
