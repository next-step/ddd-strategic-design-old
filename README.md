# 키친포스

## 요구 사항

- 치킨집에 포스 시스템을 구현한다.

- 상품
  - [ ] 상품를 등록할 수 있다.
  - [ ] 상품의 가격은 0 이상이어야 한다.
  - [ ] 전체 목록을 확인할 수 있다.
    
- 메뉴
  - [ ] 메뉴를 등록할 수 있다.
  - [ ] 메뉴에 등록되는 가격은 0이상이다.
  - [ ] 메뉴는 그룹에 속해 있어야한다.
  - [ ] 메뉴상품의 가격 * 수량에 의해 합산한다.
  - [ ] 메뉴의 가격은 메뉴상품들의 가격 합산보다 클 수 없다
  
  - [ ] 전체 목록을 확인할 수 있다.
  - [ ] 메뉴 전체 목록에서 메뉴상품의 목록을 확인할 수 있다.
    
- 메뉴그룹
  - [ ] 메뉴그룹은 이름이 있어야 등록할 수 있다.
  - [ ] 메뉴그룹의 전체 목록을 조회할 수 있다.
  
- 주문테이블
  - [ ] 주문테이블을 등록할 수 있다
  
  - [ ] 주문테이블의 비어있는 상태로 변경할 수 있다.  
  - [ ] 주문테이블은 테이블그룹에 속하면 안된다.
  - [ ] 주문테이블의 비어있는 상태로 변경시, 테이블그룹에 속하지 않고 완료상태여야만 한다.
  
  - [ ] 주문테이블에 변경하고자하는 인원이 0명 이상인 경우만 변경가능하다.
  - [ ] 주문테이블이 사용자 변경시 비어있는 상태면 안된다.
  
  
- 테이블그룹
  - [ ] 테이블그룹을 등록할 수 있다.
  - [ ] 그룹에 속한 주문테이블의 목록이 2개 이상이어야 한다.
  - [ ] 주문테이블들은 다른 테이블그룹에 속해 있으면 안된다.
  - [ ] 테이블그룹의 생성시점을 저장한다.
  
  - [ ] 테이블그룹을 삭제할 수 있다.  
  - [ ] 테이블그룹에 속한 주문테이블은 완료상태여야 한다.


- 주문
  - [ ] 전체 목록을 조회할 수 있다.
  
  - [ ] 주문은 주문상품이 있는 경우 생성할 수 있다.
  - [ ] 주문테이블이 비어있으면 안된다.
  - [ ] 테이블그룹에 속한 경우 현재 주문을 테이블그룹에 추가한다.
  
  - [ ] 주문상태를 변경할 수 있다.
  - [ ] 요리 준비, 식사  상태의 경우만 변경할 수 있다.
  
## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 사용자 | Guest | 매장에 방문하는 대상을 이야기하며 메뉴를 주문할 수 있다. |
| 상품 | Product | 상품명과 가격을 갖는 대상을 말한다. |
| 상품ID | Product ID | 상품의 고유 ID |
| 메뉴상품 | Menu Product | 상품정보와 수량을 포함하며 메뉴에 속해 있는 대상을 말한다. |
| 메뉴 | Menu | 메뉴명, 메뉴상품 및 메뉴그룹에 속하며 메뉴상품의 합산보다 작은 가격을 갖는 주문의 대상을 말한다. |
| 메뉴ID | Menu ID | 메뉴의 고유 ID |
| 메뉴그룹 | Menu Group | 그룹명이 있는 메뉴의 묶음을 말한다. |
| 메뉴그룹ID | Menu Group ID | 메뉴그룹의 고유 ID |
| 주문테이블 | Order Table | 테이블그룹ID, 사용자, 현재 상태를 포함하며 사용자들에게 부여되는 공간(단위)을 말한다.
| 주문테이블ID | Order Table ID | 주문테이블의 고유 ID |
| 테이블그룹 | Table Group | 주문테이블의 묶음을 말한다. |
| 테이블그룹ID | Table Group ID | 테이블그룹의 고유 ID |
| 주문상품 | OrderLineItem | 주문한 메뉴와 수량을 포함한 대상을 말한다. |
| 주문 | Order | 테이블그룹ID, 주문상태, 주문시간, 주문상품을 갖는 행위를 말한다. |
| 주문ID | OrderID | 주문의 고유 ID |
| 주문상태 | OrderStatus | 요리, 식사, 완료라는 상태를 말한다. |

## 모델링

- `Product`은 고유한 `Product ID`를 가지고 있다.
- `Product`은 명칭, 가격을 가지고 있다.
- `Product`은 생성시 0이상의 가격을 갖을때만 생성할 수 있다.
- `Product`의 전체목록을 볼 수 있다.

- `Menu`는 고유한 `Menu ID`를 가진다.
- `Menu`는 `Menu Group ID`, 가격 및 다수의 `Menu Product`을 가진다. ( 0이상의 가격을 갖는다. )
- `Menu`생성 시 `Order Table ID`를 포함해야하며 다수의 `OrderLineItem`를 등록할 수 있다.
- `Menu`전체목록을 확인하면 `Menu`에 속한 다수의 `Menu Product`를 확인할 수 있다.

- `Menu Product`은 `Menu ID`, `Menu Group ID`, 수량을 가진다.

- `Order`은 고유한 `OrderID`를 가진다.
- `Order`은 `Order Table ID`, `OrderStatus`, 주문시간 및 다수의 `OrderLineItem`을 가진다.
- `Order`는 `OrderStatus`가 `COMPLETION`일때는 변경할 수 없다.
- `Order`전체목록을 확인하면 `Order`에 속한 다수의 `OrderLineItem`를 확인할 수 있다.

- `OrderLineItem`은 `Menu ID`, 수량, `OrderID`을 가진다.

- `Order Table`은 `Table Group ID`, `Guest`, 현재 상태를 가진다.
- `Order Table`를 비어있는 상태로 변경할 시 `Order Table`이 없거나 `OrderStatus`가 `COMPLETION`이면 불가능하다.
- `Order Table`를 `Guest` 수를 변경할 시 `Order Table`이 비어있는 상태면 불가능하다.
- `Order Table`의 전체목록을 볼 수 있다.

- `Table Group`은 고유한 `Table Group ID`을 가진다.
- `Table Group`은 생성시간 및 다수의 `Order Table`을 가진다.
- `Table Group` 생성시 다수의 `Order Table`을 포함하며 `Order Table`은 `Table Group ID`을 가진다.
- `Table Group`삭제시 `Order Table`의 `OrderStatus`가 `COMPLETION`이면 불가능하다.

- `OrderStatus`는 `COOKING`, `MEAL`, `COMPLETION`의 상태를 갖는다.
