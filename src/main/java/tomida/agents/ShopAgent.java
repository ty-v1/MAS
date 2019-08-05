package tomida.agents;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import tomida.behaviours.ConsumerCounterBehaviour;
import tomida.behaviours.DelayBehaviour;
import tomida.behaviours.PriceResponseBehaviour;
import tomida.behaviours.ReceiveReport;

public class ShopAgent extends Agent {

  private int price = 100;
  private int yesterdayConsumerCount = 0;
  private int consumerCounter = 0;

  private static final int COST = 100;
  private final Random random = new Random((int) (Math.random() * 100));
  private final Map<String, Integer> shopToPrice = new HashMap<>();

  @Override
  protected void setup() {
    price = (random.nextInt(500) + 100) + COST;

    // 商品価格の回答と来店数のカウントを並列に処理する
    final ParallelBehaviour parallelBehaviour = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
    addBehaviour(parallelBehaviour);

    // 商品価格を答える
    parallelBehaviour.addSubBehaviour(new PriceResponseBehaviour(this));

    // 来店数を数える
    parallelBehaviour.addSubBehaviour(new ConsumerCounterBehaviour(this));

    // スパイからの情報を取得する
    parallelBehaviour.addSubBehaviour(new ReceiveReport(this));

    // 1日(2000ms)ごとに値段を更新する
    parallelBehaviour.addSubBehaviour(new DelayBehaviour(this, 2000) {
      @Override
      protected void handleElapsedTimeout() {
        if (yesterdayConsumerCount > consumerCounter) {
          // 来店数が少ないときは来店数を増やして利益を得るために
          // ライバルよりも低く設定する
          shopToPrice.entrySet()
              .stream()
              .filter(e -> !e.getKey()
                  .equals(getLocalName()))
              .mapToInt(Entry::getValue)
              .min()
              .ifPresent(min -> setPrice(min - random.nextInt(9) - 1));

          System.err.printf("[%s] new price $%d\n",
              myAgent.getLocalName(),
              price);
        } else {
          // 来店数が増えた時は利益を維持するために何もしない
          System.err.printf("[%s] does not change price $%d\n",
              myAgent.getLocalName(),
              price);
        }
        yesterdayConsumerCount = consumerCounter;
        consumerCounter = 0;
        reset(timeout);
      }

      @Override
      public boolean done() {
        return false;
      }
    });
  }

  public int getCount() {
    return consumerCounter;
  }

  public void incrementCounter() {
    consumerCounter++;
  }

  public int getPrice() {
    return price;
  }

  public void setRivalPrice(final String shopName, final int price) {
    shopToPrice.put(shopName, price);
  }

  private void setPrice(final int price) {
    // 原価よりも高ければ更新できる
    if (COST >= price) {
      return;
    }
    this.price = price;
  }
}