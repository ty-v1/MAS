package tomida.behaviours;

import java.util.Map;
import java.util.Map.Entry;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import tomida.agents.SpyAgent;

public class ReportBehaviour extends OneShotBehaviour {

  private final String shopName;

  public ReportBehaviour(final Agent agent, final String shopName) {
    super(agent);
    this.shopName = shopName;
  }

  @Override
  public void action() {
    final SpyAgent agent = getSpyAgent();
    final Map<String, Integer> shopToPrice = agent.getShopToPrice();

    // スパイした結果を報告する
    for (final Entry<String, Integer> shopAndPrice : shopToPrice.entrySet()) {
      final ACLMessage report = new ACLMessage(ACLMessage.QUERY_REF);
      report.setPerformative(ACLMessage.INFORM);
      report.addReceiver(new AID(shopName, AID.ISLOCALNAME));
      report.setContent(shopAndPrice.getKey() + "," + shopAndPrice.getValue());
      agent.send(report);
    }
  }

  private SpyAgent getSpyAgent() {
    return (SpyAgent) myAgent;
  }
}
