import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import static akka.actor.Actors.*;


public class Jail extends UntypedActor {
	private Hashtable<Integer,ActorRef> prisoners;
	
	private int shutdownCounter;

	public Jail() {
		this.shutdownCounter = 0;
		this.prisoners = new Hashtable<Integer,ActorRef>();
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof String && ((String)msg).equals("LINE STOPPED")) {
			this.shutdownCounter++;
			if(shutdownCounter == Main.NUM_LINES) {
				getContext().tell(poisonPill(), getContext());
			}
		}
		else if(msg instanceof EnterJail) {
			EnterJail message = (EnterJail)msg;			
			System.out.println("Jail recieves Passenger " + message.getPassengerId());
			this.prisoners.put(message.getPassengerId(),message.getPassenger());
		}
		else {
			unhandled(msg);
		}
	}
	
	@Override
	public void postStop() {
		Set<Integer>keys = prisoners.keySet();
		for(Integer i : keys) {
			System.out.println("Jail sends Passenger " + i + " to the Permanent Detention");
			prisoners.get(i).tell(poisonPill());
		}		
		System.out.println("Jail stopped.");
	}
	
	//
	// Messages
	//
	static class EnterJail {
		private final ActorRef passenger;
		private final int passengerId;
		public EnterJail(final ActorRef passenger, final int passengerId) {
			this.passenger = passenger;
			this.passengerId = passengerId;
		}
		public ActorRef getPassenger() {
			return this.passenger;
		}
		public int getPassengerId() {
			return this.passengerId;
		}
	}
}
