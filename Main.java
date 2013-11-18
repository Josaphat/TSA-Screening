import java.util.ArrayList;
import java.util.List;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

public class Main {
	/** The number of security lines in the simulation */
	public static final int NUM_LINES = 4;
	/** Unit of simulated time */
	public static final long ONE_MINUTE = 10L;
	/* Length of the "day" being simulated */
	private static final long SIMULATION_LENGTH = 480L * ONE_MINUTE;
	
	/** Percent chance of failing the document check. */
	public static final double DOCUMENT_FAIL_CHANCE = 0.20;
	/** Percent chance of failing the baggage scan. */
	public static final double BAGGAGE_FAIL_CHANCE = 0.20;
	/** Percent chance of failing the body scan. */
	public static final double BODY_FAIL_CHANCE = 0.20;
	
	private static long startTime = 0L;
	
	/**
	 * Starts the system, generates passengers, keeps track of the time of day,
	 * and stops the system at the end of the day.
	 * @param args unused
	 */
	public static void main(String[] args) {
		System.out.println("Starting Jail");
		final ActorRef jail = Actors.actorOf(Jail.class);
		jail.start();
		
		final List<ActorRef> lines = new ArrayList<ActorRef>();
		System.out.println("Starting " + Main.NUM_LINES + " lines.");
		for(int i = 0; i < Main.NUM_LINES; i++) {
			System.out.println("\tStarting line " + i);
			final ActorRef lineRef = Actors.actorOf(new UntypedActorFactory() {

				@Override
				public UntypedActor create() {
					return new Line(jail);
				}
			});
			lineRef.start();
			lines.add(lineRef);
		}
		
		System.out.println("Starting the DocumentChecker.");
		final ActorRef docChecker = Actors.actorOf(new UntypedActorFactory() {
			@Override
			public Actor create() {
				return new DocumentChecker(lines);
			}
		});
		docChecker.start();
		
		System.out.println("Opening Airport");
		Main.startTime = System.currentTimeMillis();
		System.out.println("Generate Passengers.");
		while(System.currentTimeMillis() - Main.startTime <= Main.SIMULATION_LENGTH) {
			if(System.currentTimeMillis() % Main.ONE_MINUTE == 0) {
				// 50% chance of generating a passenger per simulated minute
				if(Math.random() < .50) {
					ActorRef passenger = Actors.actorOf(new UntypedActorFactory(){
						@Override
						public UntypedActor create() {
							return new Passenger();
						}
					});
					passenger.start();
					passenger.tell(new Passenger.ProceedToDocumentChecker(docChecker));
				}
			}
		}
		System.out.println("Simulation over.");
		// Shut down the document checker. It notifies the line to shut down. Lines notify the jail.
		docChecker.tell(Actors.poisonPill());
		
		// FIXME Passengers should die once they leave the security line or at the end of the day (when the jail kills them)
		Actors.registry().shutdownAll();
	}
}