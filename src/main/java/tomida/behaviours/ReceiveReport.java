package tomida.behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import tomida.agents.ShopAgent;

public class ReceiveReport extends CyclicBehaviour {

  private final MessageTemplate template;

  public ReceiveReport(final Agent agent) {
    super(agent);

    template = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
  }

  @Override
  public void action() {
    final ShopAgent agent = (ShopAgent) myAgent;
    final ACLMessage message = agent.receive(template);

    if (message != null) {
      final String[] report = message.getContent()
          .split(",");
      final int price = Integer.parseInt(report[1]);
      final String spyName = message.getSender()
          .getLocalName();

      System.out.printf("[%s] report from %s (%s: %d)\n",
          agent.getLocalName(),
          spyName,
          report[0],
          price
      );
      agent.setRivalPrice(report[0], price);
    }
  }
}
