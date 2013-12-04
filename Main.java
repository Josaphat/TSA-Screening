import java.util.ArrayList;
import java.util.List;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Actors;
import static akka.actor.Actors.*;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

public class Main {
	/** The number of security lines in the simulation */
	public static final int NUM_LINES = 4;
	/** Unit of simulated time */
	public static final int ONE_MINUTE = 5;  //Number of miliseconds in a minute
	private static final int ONE_HOUR = 60 * ONE_MINUTE; // Number of milliseconds in an hour
	/* Length of the "day" being simulated */
	private static final long SIMULATION_LENGTH = 1L * ONE_HOUR;
	
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
		
		final List<Line> lines = new ArrayList<Line>();
		System.out.println("Starting " + Main.NUM_LINES + " lines.");
		for(int i = 0; i < Main.NUM_LINES; i++) {
			System.out.println("\tStarting line " + i);
			final int lineId = i +1;
			final ActorRef securityStation = Actors.actorOf(new UntypedActorFactory() {
				@Override
				public Actor create() {
					return new SecurityStation(jail,lineId);
				}
			});
			securityStation.start();
			
			final ActorRef bodyScanner = Actors.actorOf(new UntypedActorFactory() {
				@Override
				public Actor create() {
					return new Line.BodyScanner(securityStation,lineId);
				}
			});
			bodyScanner.start();
			
			final ActorRef bagScanner = Actors.actorOf(new UntypedActorFactory(){
				@Override
				public Actor create() {
					return new Line.BaggageScanner(securityStation,lineId);
				}
			});
			bagScanner.start();
			
			final ActorRef queue = Actors.actorOf(new UntypedActorFactory() {
				@Override
				public Actor create() {
					return new Queue(bodyScanner,lineId);
				}
			});
			queue.start();
			bodyScanner.tell(new Line.BodyScanner.GiveQueue(queue));
			
			Line line = new Line(jail, securityStation, bodyScanner, bagScanner, queue,i+1);
			lines.add(line);
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
		int passCount = 1;
		while(System.currentTimeMillis() - Main.startTime <= Main.SIMULATION_LENGTH) {
			// 50% chance of generating a passenger per simulated minute
			if(Math.random() < .5){
				final int passengerId = passCount;
				ActorRef passenger = Actors.actorOf(new UntypedActorFactory(){
					@Override
					public UntypedActor create() {
						return new Passenger(passengerId);
					}
				});
				passCount++;
				passenger.start();
				System.out.println("Passenger " + (passCount -1) + " Started");
				passenger.tell(new Passenger.ProceedToDocumentChecker(docChecker));
			}
			try{
				Thread.sleep(ONE_MINUTE);
			} catch (InterruptedException e){}
		}
		try{
			Thread.sleep(1000);
			} catch (InterruptedException e){}
		System.out.println("Simulation over.");
		// Shut down the document checker. It notifies the line to shut down. Lines notify the jail.
		docChecker.tell(poisonPill());
		System.out.println("*******************.");
	}
}
