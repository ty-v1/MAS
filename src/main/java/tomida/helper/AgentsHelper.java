package tomida.helper;

import java.util.Arrays;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAException;

public class AgentsHelper {

  private AgentsHelper() {
  }

  /**
   * ShopAgentを探す
   */
  public static int getNumberOfShops(final Agent agent) {
    try {
      final SearchConstraints constraints = new SearchConstraints();
      constraints.setMaxResults(-1L); //-1: ALL

      return (int) Arrays.stream(AMSService.search(agent, new AMSAgentDescription(), constraints))
          .map(AMSAgentDescription::getName)
          .map(AID::getLocalName)
          .filter(e -> e.startsWith("shop"))
          .count();
    } catch (FIPAException e) {
      e.printStackTrace();
      return 0;
    }
  }
}
