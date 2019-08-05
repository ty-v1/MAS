package tomida.behaviours;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

public class DelayBehaviour extends SimpleBehaviour {

  protected long timeout, wakeupTime;
  private boolean finished;

  public DelayBehaviour(final Agent agent, long timeout) {
    super(agent);
    this.timeout = timeout;
    finished = false;
  }

  public final void onStart() {
    wakeupTime = System.currentTimeMillis() + timeout;
  }

  public final void action() {
    final long dt = wakeupTime - System.currentTimeMillis();
    if (dt <= 0) {
      finished = true;
      handleElapsedTimeout();
    } else {
      block(dt);
    }
  }

  protected void handleElapsedTimeout() {
  }

  public final void reset(final long timeout) {
    wakeupTime = System.currentTimeMillis() + timeout;
    finished = false;
  }

  public boolean done() {
    return finished;
  }
}