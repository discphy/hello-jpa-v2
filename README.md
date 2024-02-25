### 영속성 컨텍스트

---

- 하나의 트랜잭션 안에서 1차 캐시된 것을 먼저 조회 후 없으면 DB를 조회한다. 
- `em.persist()` 시에 데이터베이스에 보내지 않고 **커밋** 시점에 보냄 
- `@Entity`는 반드시 기본 생성자 필요
- `JPA`는 자바 컬렉션 개념 (데이터 변경 후 다시 컬렉션에 집어넣지 않음) - 변경감지와 관련

### 플러시 

---

- 영속성 컨텍스트를 비우진 않음 
- 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화 

### 스키마 자동생성

---

`hibernate.hbm2ddl.auto`

- `create` : 스키마 생성 - 개발기에서 사용 X (`DROP + CREATE`)
- `create-drop` : 종료시점에 `DROP`

`create` 또는 `update` 개발 초기 단계

`update` 또는 `validate` 테스트 서버 (되도록이면 지양)

`validate` 또는 `none` 스테이징과 운영 서버  

### 컬럼 매핑 

---

- `@Temporal` : 날짜 매핑 - `LocalDate`, `LocalDateTime`의 경우에는 사용 안해도 된다. 
- `@Transient` : 엔티티 생성 X
- `@Column`의 `unique`에서는 잘 안쓰고 `@Table`에서 사용 (여러개 복합 및 키 이름 지정 가능)
- `@Enumerated` : 반드시 `EnumType.STRING`로 사용하는 편 

### 기본 키 매핑

---

- `IDENTITY` : 기본 키 생성을 데이터베이스에 위임 - INSERT 쿼리 이후 ID를 알 수 있음
그러나, H2 Database가 업데이트 되었는지 ID를 지정 하지 않으면 다음과 같은 에러 발생
```log
ERROR: NULL not allowed for column "ID"; SQL statement:
```

- 기본키 제약 조건 : null 아님, 변하면 안된다.
- `@SequenceGenerator` : `allocationSize` 해당 값 만큼 `nextValue`를 확보하고 시퀀스 값을 세팅한다. 
(동시성 문제 해결)

### `mappedBy`

---

- 객체와 테이블간에 연관관계를 맺는 차이를 이해해야 한다. 
  - 객체 연관관계 2개 
  - 테이블 연관관계 1개

- 객체의 양방향 관계는 사실 양방향 관계가 아니라 서로 다른 단방향 관계 2개다.

### 연관관계의 주인 

---

- 연관관계의 주인만이 외래 키를 관리(등록 및 수정
- 주인이 아닌쪽은 읽기만 가능 
- `mappedBy` 속성 사용 X 
- 외래 키가 있는 곳을 주인으로 정해라  
- 다(`N`) 쪽인 부분이 연관관계 주인이 되면 된다. `=` 연관관계의 주인은 외래 키의 위치를 기준으로 정해야함

### 양방향 매핑

---

- 양방향 매핑 시에는 연관관계 주인에 값을 입력 (쉽게 이해하면, 둘 다 넣어준다고 생각하면 간단하다. - 연관관계 편의 메소드 작성 `add**` OR `chage**`)
- 양방향 매핑시 무한루프 조심 : `toString()`(쓰지말 것!), `lombok`, `JSON` 생성 라이브러리(`Controller` 에서 `Entity` 반환 금지)
- 단방향 매핑만으로도 이미 연관관계 매핑은 완료 (처음 설계 시, 단방향 매핑만으로 설계 해야함!)

### 다중성

---

- 다대다 : `@ManyToMany` 는 실무에서 쓰면 안됨 (연결테이블로 `@OneToMany`, `@ManyToOne` 으로 설계하는 것을 권장)
- 일대다 단방향은 실무에서 사용하지 말 것 -> 다대일 양방향 매핑을 사용
- 일대일에서 대상테이블에 외래키 사용 시, 기능의 한계로 즉시 로딩을 하는 단점이 있다.
- `@ManyToOne`은 `mappedBy`가 없어 연관관계 주인이 되어야 한다.

### 고급 매핑 

---

- 상속관계 매핑 : 구현방법은 3가지

  - 조인 전략(`@Inheritance(strategy = JOINED)`) : 정석적인 방법이다. 
  - 단일 테이블 전략(`@Inheritance(strategy = SINGLE_TABLE)`) : `NULL` 허용에 대한 문제 
  - 구현 클래스마다 테이블 전략(`@Inheritance(strategy = TABLE_PER_CLASS)`) : 조회 시, `union`을 사용하기 때문에 성능 저하 > 쓰면 안됨..

- `@MappedSuperclass`
  
  - 상속관계 매핑 X 
  - 엔티티 X 
  - 추상 클래스 권장

---

### 프록시 

- `em.getReference()` : 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회
- 특징

  - 프록시 객체는 처음 사용할 때 한번만 초기화 
  - 프록시 객체를 초기화 할때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아님
  - 프록시 객체는 원본 엔티티를 상속받음, 타입 체크시 주의필요 (`instance of`로 확인, `==` 으로 비교하면 안됨)
  - 영속성 컨텍스트에 엔티티가 이미 있으면 `em.getReference()`를 호출해도 실제 엔티티 반환
  - 프록시 `==` 엔티티 항상 `true`

---

### 즉시로딩과 지연로딩 

- 가급적 지연 로딩만 사용(특히 실무에서)
- 즉시로딩은 `JPQL`에서 N + 1 문제를 일으킨다
- `@ManyToOne`, `@OneToOne`은 기본이 즉시 로딩 -> `LAZY`로 설정

---

### CASCADE : 영속성 전이

- 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없음

---

### 고아객체 

- `orphanRemoval = true`
- 참조하는 곳이 하나일 때 사용해야함 
- **특정 엔티티가 개인 소유할 때만 사용**

---

### 기본값 타입

  - 엔티티 타입
    - 데이터가 변해도 식별자로 지속해서 추적 가능
        

  - 값 타입
    - 식별자가 없고 값만 있으므로 변깅시 추적 불가
    - **생명주기를 엔티티의 의존**
    - 공유하면 안됨 수정하면 여러 개의 엔티티에 들어간 객체의 모든 값이 변경 
    - 변경 시에는 객체를 다시 생성해서 바꿔야함
    - 값 타입을 비교할 때는 `equals`를 생성

--- 

### 임베디드 타입 

  - `ORM`을 잘 설계하면 테이블 수보다 클래수 수가 더 많음

--- 

### 불변 객체 

  - 객체 타입을 수정할 수 없게 만듦 
  - 값 타입은 불변 객체로 설계해야함  

---

### 값 타입 컬렉션 

  - 값 타입을 하나 이상 저장할 때 사용
  - `@ElementCollection`, `@CollectionTable` 사용
  - 값 타입 컬렉션도 지연 로딩 전략 사용 
  - 값 타입 컬렉션은 영속성 전이(Cascade) - 고아 객체 제거 기능을 필수로 가진다고 볼 수 있다. 
  - **값 수정**은 주의 필요
  - 값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다.
  - 추적 필요가 없을 때만 사용 (정말 단순한것)
  - 실무에서는 상황에 따라 **값 타입 컬렉션 대신에 일대다 관계를 고려**
