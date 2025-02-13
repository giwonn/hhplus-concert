# 콘서트 예약 서비스

## 요구사항 분석 보고서
### [마일스톤](https://github.com/users/giwonn/projects/5)
### [플로우 차트](./docs/flow_chart.md)
### [시퀀스 다이어그램](./docs/sequence_diagram.md)

## 설계 자료
### [ERD](./docs/erd.md)
### [API 명세서](https://allens-personal-organization.gitbook.io/hhplus/step6/api-docs)
### [API Swagger](./docs/swagger.md)

## 성능 개선 및 고도화 보고서
### [콘서트 예약 시스템에서의 동시성 문제 및 해결 방안](./docs/concurrency.md)
### [Redis를 이용한 성능 개선 보고서](./docs/performance_improvement_by_redis.md)

## 개념 정리
### [Cache](./docs/cache.md)
### [MSA 전환시 분산 트랜잭션 전략](./docs/msa-architecture-improvement.md)

## 프로젝트

## Getting Started

### Prerequisites

#### Running Docker Containers

`local` profile 로 실행하기 위하여 인프라가 설정되어 있는 Docker 컨테이너를 실행해주셔야 합니다.

```bash
docker-compose up -d
```

### 부하테스트
```
1. docker compose -f docker-compose.load-test.yml up --build -d // 부하테스트에 필요한 환경 세팅
2. http://localhost:3000/login 접속 및 로그인 (ID: admin, PW: admin) // Grafana 로그인
3. k6 Load Testing Results 대시보드 선택
3. docker compose -f docker-compose.k6.yml run --rm k6 -- waiting-queue.js // 대기열 부하테스트 실행
```
