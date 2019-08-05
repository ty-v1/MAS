package tomida.behaviours;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import tomida.agents.ConsumerAgent;

public class PriceRequest extends DelayBehaviour {

  private final MessageTemplate template;
  private final BiConsumer<ACLMessage, Integer> afterTimeout;

  public PriceRequest(final Agent agent, final long timeOut,
      final BiConsumer<ACLMessage, Integer> afterTimeout) {
    super(agent, timeOut);

    this.afterTimeout = afterTimeout;
    template = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
  }

  @Override
  protected void handleElapsedTimeout() {
    final ACLMessage response = myAgent.receive(template);

    if (response == null) {
      return;
    }

    // 値段を取り出す
    final int price = Integer.parseInt(response.getContent());
    final String shopName = response.getSender()
        .getLocalName();
    System.out.printf("[%s] got price $%d from %s",
        myAgent.getLocalName(),
        price,
        shopName
    );

    afterTimeout.accept(response, price);
  }
}
