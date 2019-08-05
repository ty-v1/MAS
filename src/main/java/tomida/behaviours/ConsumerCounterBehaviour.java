package tomida.behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import tomida.agents.ShopAgent;

public class ConsumerCounterBehaviour extends CyclicBehaviour {

  private final MessageTemplate template;

  public ConsumerCounterBehaviour(final Agent agent) {
    super(agent);
    template = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
  }

  @Override
  public void action() {
    final ShopAgent agent = (ShopAgent) myAgent;
    final ACLMessage message = agent.receive(template);

    if (message != null) {
      agent.incrementCounter();

      final String cname = message.getSender()
          .getLocalName();
      System.out.printf("[%s] %s visited (Total: %d)\n",
          agent.getLocalName(),
          cname,
          agent.getCount()
      );
    }
  }
}
