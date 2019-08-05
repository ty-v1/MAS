package tomida.behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import tomida.agents.ShopAgent;

public class PriceResponseBehaviour extends CyclicBehaviour {

  private final MessageTemplate template;

  public PriceResponseBehaviour(final Agent agent) {
    super(agent);
    template = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
  }

  @Override
  public void action() {
    final ShopAgent agent = (ShopAgent) myAgent;
    final ACLMessage message = agent.receive(template);

    if (message != null) {
      final String cname = message.getSender()
          .getLocalName();
      System.out.printf("[%s] query from %s\n",
          myAgent.getLocalName(),
          cname
      );
      final ACLMessage reply = message.createReply();
      reply.setPerformative(ACLMessage.INFORM);
      reply.setContent(String.valueOf(agent.getPrice()));
      this.myAgent.send(reply);
    }
  }
}
