# 키친포스

## 요구 사항

- 간단한 포스 프로그램을 구현한다.
- 상품
    - [ ] 상품을 등록할 수 있다.
        - [ ] 상품 등록 시 상품 아이템의 정보가 있어야 한다.
        - [ ] 상품의 가격은 0원 이상이어야 한다.
    - [ ] 등록된 상품 목록을 볼 수 있다.
- 메뉴 그룹
    - [ ] 메뉴 그룹을 등록할 수 있다.
        - [ ] 메뉴 그룹 등록 시 메뉴 그룹 아이템의 정보가 있어야 한다.
    - [ ] 등록된 메뉴 그룹 목록을 볼 수 있다.
- 메뉴
    - [ ] 메뉴를 등록할 수 있다.
        - [ ] 메뉴 등록 시 메뉴 아이템의 정보가 있어야 한다.
        - [ ] 메뉴의 가격은 0원 이상이어야 한다.
        - [ ] 메뉴 그룹을 가지고 있어야 한다.
        - [ ] 메뉴 상품 가격의 총합이 메뉴 가격보다 크면 안 된다.
    - [ ] 등록된 메뉴 목록을 볼 수 있다.
- 테이블 그룹
    - [ ] 테이블 그룹을 등록할 수 있다.
        - [ ] 주문 테이블 목록은 2개 이상이어야 한다.
        - [ ] 주문 테이블 각각이 빈 테이블이어야 한다.
    - [ ] 테이블 그룹을 삭제할 수 있다.
        - [ ] 주문 테이블의 상태가 조리 중이거나 식사 중이면 안된다.  
- 주문 테이블
    - [ ] 주문 테이블 목록을 볼 수 있다.
    - [ ] 주문 테이블을 등록 할 수 있다.
    - [ ] 주문 테이블의 빈 상태 여부를 변경할 수 있다.
        - [ ] 테이블 그룹 번호를 가지고 있다.
        - [ ] 주문 테이블의 상태가 조리 중이거나 식사 중이면 안 된다.
    - [ ] 주문 테이블의 인원수를 변경할 수 있다.
        - [ ] 인원수가 0명 이상이어야 한다.
        - [ ] 주문 테이블이 비어 있는 상태이면 안 된다.
- 주문
    - [ ] 주문 목록을 볼 수 있다.
    - [ ] 주문을 등록할 수 있다.
        - [ ] 주문 등록 시 주문 테이블의 식별자, 주문 내역 목록을 필수로 입력해야 한다.
        - [ ] 주문 내역 목록이 비어 있어서는 안 된다.
        - [ ] 존재하는 주문 테이블 식별자를 사용해야 하며 주문 테이블이 빈 상태이면 안 된다.
        - [ ] 주문 테이블이 테이블 그룹에 속해있으면, 테이블 그룹 중 가장 낮은 주문 테이블 식별자를 가진 주문 테이블의 정보가 등록된다.  
        - [ ] 주문 상태는 조리 중, 주문 시간은 현재 시간으로 등록된다.
    - [ ] 주문 상태를 변경할 수 있다.
        - [ ] 변경 시 주문 식별자와 주문 정보의 주문 상태를 필수로 입력해야 한다.
        - [ ] 등록된 주문 식별자를 사용해야 한다.
        - [ ] 주문의 상태가 완료이어서는 안 된다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |

## 모델링

- 
