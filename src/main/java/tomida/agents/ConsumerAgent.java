package tomida.agents;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import tomida.helper.AgentsHelper;
import tomida.behaviours.DelayBehaviour;
import tomida.behaviours.GCBehaviour;
import tomida.behaviours.PriceRequest;
import tomida.behaviours.SearchBestShop;

public class ConsumerAgent extends Agent {

  private final Random random
      = new Random(System.currentTimeMillis() + toString().hashCode());
  private int bestPrice = 10000;
  private ACLMessage bestResponse = null;

  @Override
  protected void setup() {
    // 最安値をリセット
    reset();

    final int numberOfShop = AgentsHelper.getNumberOfShops(this);
    final ACLMessage priceRequest = new ACLMessage(ACLMessage.QUERY_REF);
    priceRequest.setContent("price");

    for (int i = 1; i <= numberOfShop; i++) {
      priceRequest.addReceiver(new AID("shop" + i, AID.ISLOCALNAME));
    }
    send(priceRequest);

    final SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
    addBehaviour(sequentialBehaviour);

    // いくつかの店で値段をチェックする
    // 訪問する順は毎回ランダム
    for (final int i : generateRandomUniqueNumbers(numberOfShop)) {
      // チェックにかかる時間
      final long timeout = (long) (random.nextInt(10 * i) + 10);

      // 価格をチェックする
      final BiConsumer<ACLMessage, Integer> afterTimeout = (response, price) -> {
        if (bestPrice > price) {
          bestPrice = price;
          bestResponse = response;
        }
      };
      sequentialBehaviour.addSubBehaviour(new PriceRequest(this, timeout, afterTimeout));

      // (30 + 10*i)%でやめる
      if (random.nextInt(10) < 3 + i) {
        break;
      }
    }

    // ゴミ掃除
    sequentialBehaviour.addSubBehaviour(new GCBehaviour());

    // 一番安い店に行く
    sequentialBehaviour.addSubBehaviour(new SearchBestShop(this));

    sequentialBehaviour.addSubBehaviour(new DelayBehaviour(this, 2000) {
      @Override
      protected void handleElapsedTimeout() {
        setup();
      }
    });
  }

  private void reset() {
    bestResponse = null;
    bestPrice = 10000;
  }

  public int getBestPrice() {
    return bestPrice;
  }

  public ACLMessage getBestResponse() {
    return bestResponse;
  }

  private int[] generateRandomUniqueNumbers(final int size) {
    if (size <= 0) {
      return new int[0];
    }

    final List<Integer> uniqueNumbers = IntStream.rangeClosed(1, size)
        .boxed()
        .collect(Collectors.toList());
    Collections.shuffle(uniqueNumbers, random);

    // プリミティブintに変換してから配列に変換
    return uniqueNumbers.stream()
        .mapToInt(e -> e)
        .toArray();
  }
}
