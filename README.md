# 키친포스

## 요구 사항

- Menu
    - [ ] Menu를 추가할 수 있다
    - [ ] 모든 Menu를 조회할 수 있다
    - [ ] price가 필수고 음수가 될 수 없다
    - [ ] Menu의 price는 메뉴별 MenuProduct 가격의 총합보다 클 수 없다
    - [ ] MenuGroupId는 필수다
    - [ ] Menu는 MenuProduct 목록을 가지고 있다

- MenuGroup
    - [ ] MenuGroup을 추가할 수 있다
    - [ ] 모든 MenuGroup를 조회할 수 있다

- Order
    - [ ] 새로운 Order를 받을 수 있다
    - [ ] Order는 OrderLineItem을 한 개 이상 갖고 있어야 한다
    - [ ] Order의 OrderTable이 비어있으면 안된다
    - [ ] Order의 OrderTable이 TableGroup이면 가장 작은 OrderTableId가 Order의 OrderTable이다
    - [ ] 새로운 Order 상태는 COOKING이 된다
    - [ ] 모든 Order를 조회할 수 있다
    - [ ] OrderStatus를 변경할 수 있다
    - [ ] 동일한 OrderStatus로 변경할 수 없다
    - [ ] OrderStatus는 COOKING, MEAL, COMPLETION 값을 갖는다

- OrderLineItem
    - [ ] MenuId와 OrderId를 가지고 있다
    - [ ] 주문한 수량을 가지고 있다

- OrderTable
    - [ ] OrderTable을 추가할 수 있다
    - [ ] 모든 OrderTable을 조회할 수 있다
    - [ ] TableGroupId를 가지고 있다
    - [ ] Guest 인원을 가지고 있다
    - [ ] Guest 인원은 음수가 될 수 없다
    - [ ] Guest 인원을 변경할 수 있다
    - [ ] 빈 OrderTable인지 확인할 수 있다
    - [ ] TableGroup에 포함되지 않고 COOKING 또는 MEAL 상태가 아니면 OrderTable를 비울 수 있다

- Product
    - [ ] Product를 추가할 수 있다
    - [ ] price는 음수면 안 된다
    - [ ] 모든 Product를 조회할 수 있다

- TableGroup
    - [ ] OrderTable 목록을 가지고 있다
    - [ ] TableGroup을 추가할 수 있다
    - [ ] TableGroup 추가는 OrderTable 2개 이상만 가능하다
    - [ ] TableGroup 추가할 때 OrderTable이 비어있지 않고 소속된 TableGroup이 없어야 한다
    - [ ] TableGroup에 속한 모든 OrderTable이 COOKING 또는 MEAL 상태가 아니면 TableGroup을 삭제할 수 있다

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |

## 모델링

- 
