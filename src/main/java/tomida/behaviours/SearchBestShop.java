package tomida.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import tomida.agents.ConsumerAgent;

public class SearchBestShop extends OneShotBehaviour {

  public SearchBestShop(final Agent agent) {
    super(agent);
  }

  @Override
  public void action() {
    final ConsumerAgent agent = (ConsumerAgent) myAgent;
    final ACLMessage bestResponse = agent.getBestResponse();
    final int bestPrice = agent.getBestPrice();

    if (bestResponse != null) {
      System.out.format("[%s] best price $%d\n",
          agent.getLocalName(),
          bestPrice
      );

      final ACLMessage reply = bestResponse.createReply();
      reply.setPerformative(ACLMessage.AGREE);
      myAgent.send(reply);
    } else {
      System.out.format("[%s] not found\n",
          agent.getLocalName()
      );
    }
  }
}
