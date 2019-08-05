package tomida.agents;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import tomida.helper.AgentsHelper;
import tomida.behaviours.DelayBehaviour;
import tomida.behaviours.GCBehaviour;
import tomida.behaviours.PriceRequest;
import tomida.behaviours.ReportBehaviour;

public class SpyAgent extends Agent {

  private final Map<String, Integer> shopToPrice = new HashMap<>();
  private final Random random = new Random(System.currentTimeMillis());

  @Override
  protected void setup() {
    // 客の振りをして商品価格を調べる
    final ACLMessage priceRequest = new ACLMessage(ACLMessage.QUERY_REF);
    priceRequest.setContent("price");

    final int numberOfShop = AgentsHelper.getNumberOfShops(this);
    for (int i = 1; i <= numberOfShop; i++) {
      priceRequest.addReceiver(new AID("shop" + i, AID.ISLOCALNAME));
    }
    send(priceRequest);

    final SequentialBehaviour sequentialBehaviour = new SequentialBehaviour(this);
    addBehaviour(sequentialBehaviour);

    // いくつかの店で値段をチェックする
    for (int i = 1; i <= numberOfShop; i++) {
      // チェックにかかる時間
      final long timeout = (long) (random.nextInt(10 * i) + 10);

      // 価格をチェックする
      final BiConsumer<ACLMessage, Integer> afterTimeout = (response, price) -> {
        final String shopName = response.getSender()
            .getLocalName();
        shopToPrice.put(shopName, price);

      };
      sequentialBehaviour.addSubBehaviour(new PriceRequest(this, timeout, afterTimeout));
    }

    // ゴミ掃除
    sequentialBehaviour.addSubBehaviour(new GCBehaviour());

    // 自分の所属する店に値段を報告する
    sequentialBehaviour.addSubBehaviour(new ReportBehaviour(this, getShopName()));

    // 1000ms 後に再びスパイ活動をする
    sequentialBehaviour.addSubBehaviour(new DelayBehaviour(this, 1000) {
      public void handleElapsedTimeout() {
        setup();
      }
    });
  }

  public Map<String, Integer> getShopToPrice() {
    return shopToPrice;
  }

  private String getShopName() {
    final String localName = getLocalName();
    return "shop" + localName.replace("spy", "");
  }
}
