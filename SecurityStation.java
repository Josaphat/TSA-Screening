import java.util.HashMap;
import java.util.Map;

import com.eaio.uuid.UUID;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.Actors;

/**
 * The SecurityStation receives reports from the scanners and decides to send a
 * passenger to jail or lets them go to their gate.
 * Once both reports arrive for a particular passenger, a message is sent to that passenger.
 *
 */
public class SecurityStation extends UntypedActor {
	private final ActorRef jail;
	private final Map<UUID, Boolean> log;
	private final int lineId;
	private int shutDown = 0;
	
	public SecurityStation(final ActorRef jail, int lineId) {
		this.jail = jail;
		this.log = new HashMap<UUID, Boolean>();
		this.lineId = lineId;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof SecurityReport) {
			System.out.println("Security Station<Line "+lineId+"> recieves SecurityReport");
			SecurityReport report = (SecurityReport)msg;
			if(this.log.containsKey(report.getPassenger().getUuid())) {
				if(report.getScanPassed() && this.log.get(report.getPassenger().getUuid())) {
					System.out.println("Security Station<Line "+lineId+"> tells passenger both scans passed.");
					report.getPassenger().tell(new Passenger.SecurityCheckPassed(), getContext());
				} else {
					System.out.println("Security Station<Line "+lineId+"> tells passenger to go to jail.");
					report.getPassenger().tell(new Passenger.ProceedToJail(this.jail), getContext());
				}
			} else {
				// Add this report to the map
				this.log.put(report.getPassenger().getUuid(), report.getScanPassed());
			}
		} else if (msg instanceof Line.ShutDown){
			shutDown++;
			if(shutDown > 1){
				getContext().tell(Actors.poisonPill(),null);
			}
		}
		else {
			unhandled(msg);
		}
	}
	
	@Override
	public void postStop(){
		String msg = "LINE STOPPED";
		this.jail.tell(msg);
	}
	//
	// Messages
	//
	static class SecurityReport {
		private final ActorRef passenger;
		private final boolean scanPassed;
		
		public SecurityReport(final ActorRef passenger, final boolean scanPassed) {
			this.passenger = passenger;
			this.scanPassed = scanPassed;
		}
		
		public ActorRef getPassenger() {
			return this.passenger;
		}
		
		public boolean getScanPassed() {
			return this.scanPassed;
		}
	}
}
