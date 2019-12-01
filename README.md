# 키친포스

## 요구 사항

- 치킨집에 포스 시스템을 구현한다.

- Product
  - [ ] Product를 등록할 수 있다.
  - [ ] Product의 가격은 0 이상이어야 한다.
  - [ ] 전체 목록을 확인할 수 있다.
    
- Menu
  - [ ] Menu를 등록할 수 있다.
  - [ ] Menu에 등록되는 가격은 0이상이다.
  - [ ] Menu는 Group에 속해 있어야한다.
  - [ ] Menu Product의 가격 * 수량에 의해 합산한다.
  - [ ] Menu의 가격은 Menu Product들의 가격 합산보다 클 수 없다
  
  - [ ] 전체 목록을 확인할 수 있다.
  - [ ] Menu 전체 목록에서 Product의 목록을 확인할 수 있다.
    
- Menu Group
  - [ ] Menu Group을 등록할 수 있다.
  - [ ] Menu Group은 이름이 있어야 등록할 수 있다.
  
  - [ ] Menu Group의 전체 목록을 조회할 수 있다.
  
- Order Table
  - [ ] Order Table을 등록할 수 있다
  - [ ] Order Table은 사용여부와 사용자수를 갖는다.
  
  - [ ] Order Table의 비어있는 상태로 변경할 수 있다.  
  - [ ] Order Table은 Order Table Group에 속하면 안된다.
  - [ ] Order Table의 비어있는 상태로 변경시, Order Table Group에 속하지 않고 COMPLETION 상태여야만 한다.
  
  - [ ] Order Table에 변경하고자하는 인원이 0명 이상인 경우만 변경가능하다.
  - [ ] Order Table이 비어있는 상태면 안된다.
  
  
- Order Table Group
  - [ ] Order Table Group을 등록할 수 있다.
  - [ ] Group에 속한 Order Table의 목록이 2개 이상이어야 한다.
  - [ ] Order Table들은 다른 Order Table Group에 속해 있으면 안된다.
  - [ ] Order Table Group의 생성시점을 저장한다.
  
  - [ ] Order Table Group을 삭제할 수 있다.  
  - [ ] Order Table Group에 속한 Order Table은 COMPLETION 상태여야 한다.


- Order
  - [ ] 전체 목록을 조회할 수 있다.
  
  - [ ] Order는 Order item이 있는 경우 생성할 수 있다.
  - [ ] Order Table이 비어있으면 안된다.
  - [ ] Order Table Group에 속한 경우 현재 Order를 Order Table Group에 추가한다.
  - [ ] Order는 COOKING 상태이며 현재 주문시간을 저장한다.
  - [ ] Order item들은 Order에 속한다.
  
  - [ ] Order 상태를 변경할 수 있다.
  - [ ] COOKING, MEAL 상태의 경우만 변경할 수 있다.
  - [ ] COMPLETION인 경우 변경할 수 없다.
  
## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |

## 모델링

- 
