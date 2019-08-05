package tomida.behaviours;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class GCBehaviour extends Behaviour {

  @Override
  public void action() {
    // キューに溜まっているメッセージを処理
    while (this.myAgent.getCurQueueSize() > 0) {
      final ACLMessage message = this.myAgent.receive();
      if (message == null) {
        break;
      }
    }
  }

  @Override
  public boolean done() {
    return this.myAgent.getCurQueueSize() == 0;
  }
}
