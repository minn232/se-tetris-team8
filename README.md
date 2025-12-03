# SE-Tetris Team 8 🎮

![Java](https://img.shields.io/badge/Java-17-orange)
![Gradle](https://img.shields.io/badge/Gradle-8.5-blue)

**클래식 테트리스를 현대적으로 재해석한 멀티플레이 테트리스 게임**

## 📋 목차
- 주요 기능
- 게임 모드
- 시스템 요구사항
- 설치 및 실행
- 게임 조작법
- 기술 스택
- 프로젝트 구조
- 팀 정보   

## ✨ 주요 기능

### 🎯 다양한 게임 모드
- **싱글 플레이**: Soft Mode, Item Mode
- **로컬 대전**: 한 화면에서 2인 대전 (P2P Battle)
- **네트워크 대전**: 온라인 멀티플레이 지원

### 🎨 풍부한 게임 요소
- **아이템 블록**: 
  - 💣 **Bomb Block**: 주변 블록 폭발
  - ⚖️ **Weight Block**: 무거운 블록으로 하강
  - 🐌 **Slow Block**: 상대방 속도 감소
  - 📏 **Line Block**: 특정 라인 삭제
  - 🔄 **Transform Block**: 랜덤 블록으로 변환
- **난이도 선택**: Easy, Normal, Hard
- **랭킹 시스템**: 모드별 점수 저장 및 관리

### 🎵 사운드 & 비주얼
- 배경 음악 및 효과음 지원
- 볼륨 조절 기능
- 키보드 커스터마이징
- 플래시 애니메이션 효과

## 🎮 게임 모드

### 1. Soft Mode (일반 모드)
클래식 테트리스를 즐기는 기본 모드입니다.
- 난이도별 속도 조절
- 점수 기반 랭킹 시스템
- 라인 클리어에 따른 속도 증가

### 2. Item Mode (아이템 모드)
특수 아이템 블록이 등장하는 모드입니다.
- 5가지 특수 아이템 블록
- 전략적인 아이템 사용
- 아이템 전용 랭킹

### 3. P2P Battle (로컬 대전)
한 컴퓨터에서 2명이 대결하는 모드입니다.
- Player 1 vs Player 2
- 2줄 이상 클리어 시 상대방 공격
- 실시간 대전

### 4. Network Battle (네트워크 대전)
온라인으로 다른 플레이어와 대결합니다.
- Host/Client 방식
- 실시간 채팅 기능
- 보드 상태 동기화

## 💻 시스템 요구사항

- **OS**: Windows, macOS, Linux
- **Java**: JDK 17 이상
- **메모리**: 최소 512MB RAM
- **디스플레이**: 800x600 이상 해상도

## 🚀 설치 및 실행

### Gradle로 빌드 및 실행

```bash
# 저장소 클론
git clone https://github.com/yourusername/se-tetris-team8.git
cd se-tetris-team8

# 빌드
./gradlew build

# 실행
./gradlew run
```

### JAR 파일로 실행

```bash
# JAR 생성
./gradlew jar

# 실행
java -jar build/libs/se-tetris-team8.jar
```

## 🎹 게임 조작법

### 싱글 플레이 (기본 설정)
| 동작 | 키보드 (WASD) | 키보드 (방향키) |
|------|---------------|-----------------|
| 왼쪽 이동 | A | ← |
| 오른쪽 이동 | D | → |
| 아래 이동 | S | ↓ |
| 회전 | W | ↑ |
| 하드 드롭 | Space | Enter |

### 로컬 대전 모드
**Player 1 (WASD):**
- 이동: A(왼쪽), D(오른쪽), S(아래)
- 회전: W
- 하드 드롭: Space

**Player 2 (방향키):**
- 이동: ←(왼쪽), →(오른쪽), ↓(아래)
- 회전: ↑
- 하드 드롭: Enter

### 공통 키
- **P**: 일시정지
- **ESC**: 메뉴/뒤로가기

## 🛠 기술 스택

### Core
- **Java 17**: 주 개발 언어
- **Swing**: GUI 프레임워크
- **Gradle 8.5**: 빌드 도구

### Network
- **Java Socket**: 네트워크 대전 구현
- **ObjectInputStream/ObjectOutputStream**: 객체 직렬화

### Audio
- **Java Sound API**: 배경음악 및 효과음

### Testing
- **JUnit 5**: 단위 테스트
- **JaCoCo**: 코드 커버리지

## 📁 프로젝트 구조

```
se-tetris-team8/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── core/          # 게임 핵심 로직
│   │   │   ├── items/         # 아이템 블록
│   │   │   ├── network/       # 네트워크 통신
│   │   │   ├── ranking/       # 랭킹 시스템
│   │   │   └── screens/       # UI 화면
│   │   └── resources/
│   │       ├── images/        # 이미지 리소스
│   │       └── sounds/        # 사운드 파일
│   └── test/                  # 테스트 코드
├── build.gradle               # Gradle 설정
└── README.md
```

## 👥 팀 정보

- 프로젝트 기간: 2025년 9 ~ 12월
- 팀 정보: beannowbean, minn232, RShiro003

### 주요 기능 담당
- **게임 엔진**: 핵심 테트리스 로직 구현
- **UI/UX**: Swing 기반 화면 디자인
- **네트워크**: P2P 대전 시스템
- **아이템 시스템**: 특수 블록 구현
- **사운드**: 배경음악 및 효과음


**즐거운 게임 되세요! 🎮**