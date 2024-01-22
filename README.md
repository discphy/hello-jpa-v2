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