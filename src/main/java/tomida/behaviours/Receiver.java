package tomida.behaviours;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Receiver extends SimpleBehaviour {

  private final MessageTemplate template;
  private final long timeOut;
  private ACLMessage message = null;

  private long wakeupTime;
  private boolean finished;

  public Receiver(final Agent agent, final long timeOut, final MessageTemplate mt) {
    super(agent);
    this.timeOut = timeOut;
    template = mt;
  }

  public ACLMessage getMessage() {
    return message;
  }

  @Override
  public void onStart() {
    wakeupTime = (timeOut < 0 ? Long.MAX_VALUE
        : System.currentTimeMillis() + timeOut);
  }

  @Override
  public void action() {
    System.out.println();
    message = (template != null) ? myAgent.receive(template) : myAgent.receive();

    if (message != null) {
      finished = true;
      handle(message);
      return;
    }

    final long dt = wakeupTime - System.currentTimeMillis();
    if (dt > 0) {
      block(dt);
    } else {
      finished = true;
      handle(this.message);
    }
  }

  @Override
  public boolean done() {
    return finished;
  }

  @Override
  public void reset() {
    message = null;
    finished = false;
    super.reset();
  }

  public void handle(final ACLMessage message) {
    /* can be redefined in subclass */
  }
}
