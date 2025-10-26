# SOL-Assistant - 프로디지털아카데미 6기 파이널 프로젝트
주식이 어려운 초보자를 위한 개인 맞춤형 대시보드 서비스(인앱서비스)

## 프로젝트 개요
### 문제 인식
<img width="1037" height="582" alt="image" src="https://github.com/user-attachments/assets/f5e7e810-4010-4a75-840e-733a9c7fec97" />

<img width="1035" height="582" alt="image" src="https://github.com/user-attachments/assets/f82ac73a-b1b9-43d4-8ea7-26bfcf06932d" />

### 개발 기간
2025.09.01. ~ 2025.10.24. (2개월) 

### 팀 소개
|![이지환](https://github.com/takeitEasyhwan.png)|![곽예경](https://github.com/kyun9-cloud.png)|![조정현](https://github.com/CISXO.png)|![한정우](https://github.com/hanuuuuU.png)|
| :-------------------------------------------------------------------------------: | :--------------------------------------------: | :---------------------------------------------------: | :----------------------------------------------: |
|**이지환**|**곽예경**|**조정현**|**한정우**|
|[@takeitEasyhwan](https://github.com/takeitEasyhwan)|[@kyun9-cloud](https://github.com/kyun9-cloud)|[@CISXO](https://github.com/CISXO)|[@hanuuuuU](https://github.com/hanuuuuU)|
|PM / BE|FE|BE / Infra|FE|
| MSA-external-service 파트(뉴스, 차트) 제작<br>MSA-internal-service 파트(매매 기록, 사용자 대시보드) 제작 |       디자인<br>대시보드 페이지 제작<br>뉴스, 섹터 페이지 제작      | MSA 인프라 설계<br>MSA-internal-service 파트(고수 데이터, 로그인) 제작 | 행동 데이터 트래킹 파트 제작<br>대시보드 페이지 제작<br>고수 히트맵 페이지 제작 |

## 주요 개발 내용
### 아키텍처 도식도
<img width="1036" height="580" alt="image" src="https://github.com/user-attachments/assets/88be6c29-0714-4830-b50b-0a300a2cad43" />

<img width="1035" height="581" alt="image" src="https://github.com/user-attachments/assets/91ad8b85-bf1e-4079-a48d-119f4c681cb1" />

### ERD
<img width="1112" height="773" alt="image" src="https://github.com/user-attachments/assets/9132dfc2-892a-4288-85aa-b4be68b1861d" />

### API 명세
<img width="1034" height="579" alt="image" src="https://github.com/user-attachments/assets/afc3d471-48d3-4e18-bb58-f8816ea8c6dc" />

### FE
<div style="display:inline;">
<img src="https://img.shields.io/badge/Next.js-000000?style=flat-square&logo=nextdotjs&logoColor=white"/>
<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black"/>
<img src="https://img.shields.io/badge/TypeScript-3178C6?style=flat-square&logo=typescript&logoColor=white"/>
<img src="https://img.shields.io/badge/React-61DAFB?style=flat-square&logo=react&logoColor=black"/>
</div>

### BE
<div style="display:inline;">
<img src="https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=spring&logoColor=white"/>
<img src="https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white"/>
</div>

### Infra / DevOps
<div style="display:inline;">
<img src="https://img.shields.io/badge/Amazon AWS-FF9900?style=flat-square&logo=amazonaws&logoColor=white"/>
<img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white"/>
<img src="https://img.shields.io/badge/Apache Kafka-231F20?style=flat-square&logo=apachekafka&logoColor=white"/>
</div>

## 메인 기능
### 1. 내 종목 모아보기

보유 종목의 주가 정보, 해당 섹터 뉴스, 같은 투자 성향고수의 관심, 매매신호 등을 한눈에 확인할 수 있는 통합 화면입니다.
데이터를 실시간으로 캐싱해 빠른 로딩 속도를 유지하며, 사용자의 보유 자산 변동을 직관적으로 보여줍니다.
<img width="900" src="https://github.com/user-attachments/assets/d1a4b348-4c0a-4292-ae6b-cf3b5d082d68" />
### 2. 유저 행동 데이터 트래킹 

사용자의 클릭, 체류 시간, 페이지 이동 패턴을 Redis → Kafka → RDS 파이프라인으로 수집합니다.
이를 통해 투자 성향 및 관심 종목을 분석해 개인 맞춤형 콘텐츠 제공에 활용합니다.

<img width="900" src="https://github.com/user-attachments/assets/a34257a3-295a-4b56-819c-160f3f1cd883" />

### 3. 고수의Pick

투자 성향 별 상위 투자자들의 종목 선택 데이터, 행동 데이터를 분석해 거래 추세와 포트폴리오 구성을 시각화합니다.
유사한 투자 성향의 유저를 추천받거나 학습 자료로 활용할 수 있습니다.
<img width="900" src="https://github.com/user-attachments/assets/2336eebe-273d-4b76-91a9-c6c30894d707" />
### 4. 유사 차트

과거 차트 데이터를 기반으로 현재 종목과 패턴이 유사한 종목을 추천합니다.
이동평균선, RSI, ROC, 볼린저 밴드 등을 종합적으로 비교해 시각적 학습 효과를 높였습니다.
<img width="900" src="https://github.com/user-attachments/assets/1604ade6-104a-4bdf-883e-45f26ed039f5" />
### 5. 위험종목 / 정리종목 시각화

보유 종목의 위험도를 한눈에 확인하고 즉시 대응할 수 있도록 시각화된 경고 시스템을 제공합니다.
<img width="900" src="https://github.com/user-attachments/assets/46f33fae-fc95-47a3-a06c-b9d8d543907a" />

### 시연영상

