# MAS
Jadeのお勉強（レポート課題）

## 要件
`>= Java8`

## 動作方法
1. JadeのGUIを起動
```bash
# linux, mac
./gradlew runGui

# windows
.\gradlew.bat runGui
```
2. Agentを起動
```bash
# linux, mac
./gradlew runContainer -Pshops="20" -Pconsumers="100"

# windows
.\gradlew.bat runContainer -Pshops="20" -Pconsumers="100"
```

## 引数
- shops: ShopAgentの数（1以上）
- consumers: ConsumerAgentの数(1以上)

