# member-service
[lab]회원 관리 서비스

## DB schema

로컬 MySQL 연동 예시는 [src/main/resources/application-local.properties.example](/Users/qnada/Documents/GitHub/goorm/2nd-Team04/member-service/src/main/resources/application-local.properties.example)에 있습니다.
회원 테이블 수동 스키마는 [src/main/resources/schema.sql](/Users/qnada/Documents/GitHub/goorm/2nd-Team04/member-service/src/main/resources/schema.sql)에 정의되어 있습니다.
현재 예제 설정은 다음 기준으로 동작합니다.
- `schema.sql`로 `members` 테이블을 초기화합니다.
- `spring.jpa.hibernate.ddl-auto=validate`로 엔티티와 스키마 일치 여부만 검증합니다.
