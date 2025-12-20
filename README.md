# Date Picker

AI 기반 개인화 데이트 코스 추천 Android 애플리케이션

## 프로젝트 소개

Date Picker는 사용자의 선호도, 예산, 지역을 기반으로 AI가 맞춤형 데이트 코스를 자동 생성해주는 스마트 여행 가이드 애플리케이션입니다. Google OAuth 인증, 실시간 날씨 정보, 위치 기반 서비스를 통합하여 완벽한 데이트 계획을 지원합니다.

## 주요 기능

### 사용자 인증
- Google OAuth 2.0 기반 안전한 로그인
- 자동 토큰 관리 및 세션 유지

### 개인화 설정
- **관심사 선택**: 식당/카페, 영화/공연, 산책/야경, 전시/미술관, 액티비티, 쇼핑, 랜덤 코스
- **예산 설정**: 슬라이더 및 프리셋 (4만원, 8만원, 12만원)
- **지역 선택**: 사전 정의된 위치 또는 커스텀 입력
- **현위치 감지**: GPS 기반 자동 지역 인식

### AI 코스 생성
- 사용자 선호도 기반 맞춤형 코스 자동 생성
- 각 장소별 상세 설명 및 외부 링크 제공
- 실시간 날씨 정보 반영

### 코스 관리
- 추천 코스 / 인기 코스 / 최신 코스 탐색
- 북마크 기능으로 좋아하는 코스 저장
- 조회수 추적 및 인기도 반영

### 날씨 정보
- Open-Meteo API 기반 실시간 날씨 조회
- 지역별 날씨 정보 및 한글 설명
- 시각적 날씨 이모지 표시

### 공유 기능
- 웹 기반 코스 공유 (https://date-picker-share-web.vercel.app)
- 시스템 공유 인텐트 지원

## 기술 스택

### Android
- **언어**: Java 11
- **SDK**: compileSdk 36, minSdk 29, targetSdk 36
- **UI**: Material Design 3, View Binding
- **Components**: AndroidX (AppCompat, RecyclerView, CardView)

### 네트워킹
- **HTTP Client**: Retrofit 2, OkHttp 3
- **JSON**: Gson
- **인증**: Google Sign-In (Play Services Auth)

### 빌드 도구
- Gradle 8.13 (Kotlin DSL)
- Android Gradle Plugin

### 외부 API
- **백엔드**: https://datepicker-api-server.vercel.app
- **날씨**: Open-Meteo API
- **인증**: Google OAuth 2.0

## 프로젝트 구조

```
date-picker/
├── app/
│   ├── src/main/
│   │   ├── java/com/abjin/date_picker/
│   │   │   ├── *Activity.java          # 13개의 Activity
│   │   │   ├── api/                     # API 인터페이스 및 클라이언트
│   │   │   │   ├── ApiClient.java
│   │   │   │   ├── AuthApiService.java
│   │   │   │   ├── DateCourseApiService.java
│   │   │   │   ├── UserApiService.java
│   │   │   │   ├── WeatherApiClient.java
│   │   │   │   ├── WeatherApiService.java
│   │   │   │   └── models/              # 데이터 모델 (10개)
│   │   │   ├── auth/                    # 인증 관리
│   │   │   │   ├── GoogleAuthManager.java
│   │   │   │   └── TokenManager.java
│   │   │   ├── preferences/             # 사용자 선호도 관리
│   │   │   │   └── UserPreferenceManager.java
│   │   │   └── weather/                 # 날씨 관리
│   │   │       └── WeatherManager.java
│   │   └── res/
│   │       ├── layout/                  # XML 레이아웃
│   │       ├── drawable/                # 이미지 리소스
│   │       └── values/                  # 색상, 문자열, 테마
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 화면 구성 (13개 Activity)

| Activity | 설명 |
|----------|------|
| SplashActivity | 초기 로딩 화면 및 자동 네비게이션 |
| LoginActivity | Google OAuth 로그인 |
| Onboarding1Activity | 온보딩 화면 |
| InterestSelectActivity | 관심사 선택 (그리드 레이아웃) |
| BudgetSelectActivity | 예산 선택 (슬라이더 + 프리셋) |
| LocationSelectActivity | 지역 선택 (현위치 감지 포함) |
| HomeActivity | 메인 홈화면 (추천/인기/최신 코스) |
| CourseGenerateActivity | AI 코스 생성 (로딩 화면) |
| CourseResultActivity | 코스 상세 보기 + 북마크/공유 |
| BookmarkedCoursesActivity | 저장한 코스 목록 |
| PopularCoursesActivity | 인기 코스 목록 |
| RecentCoursesActivity | 최신 코스 목록 |
| MyPageActivity | 마이페이지 (설정 편집/로그아웃) |

## API 엔드포인트

### DateCourseApiService
```
POST   /date-courses                      # 코스 생성
POST   /date-courses/{id}/bookmark        # 코스 북마크
GET    /date-courses/bookmarks            # 북마크된 코스 목록
POST   /date-courses/{id}/views           # 조회수 증가
GET    /date-courses?sortBy=&limit=       # 코스 목록 조회
```

### AuthApiService
```
POST   /auth/google-login                 # Google 로그인
```

### UserApiService
```
POST   /user/preferences                  # 사용자 선호도 업데이트
```

### WeatherApiService
```
GET    /forecast?latitude=&longitude=&current_weather=true  # 날씨 조회
```

## 설치 및 실행

### 요구사항
- Android Studio Hedgehog (2023.1.1) 이상
- JDK 11 이상
- Android SDK 29 이상

### 빌드 방법

1. 프로젝트 클론
```bash
git clone https://github.com/your-username/date-picker.git
cd date-picker
```

2. Android Studio에서 프로젝트 열기
```
File > Open > date-picker 폴더 선택
```

3. Gradle 동기화
```
프로젝트 열기 시 자동 동기화 또는
File > Sync Project with Gradle Files
```

4. 빌드 및 실행
```bash
./gradlew assembleDebug
```
또는 Android Studio에서 Run 버튼 클릭

### 권한 설정

앱 실행 시 다음 권한이 필요합니다:
- `INTERNET`: API 통신
- `ACCESS_FINE_LOCATION`: 정확한 위치 감지
- `ACCESS_COARSE_LOCATION`: 대략적 위치 감지
- `ACCESS_NETWORK_STATE`: 네트워크 상태 확인

## 사용자 플로우

### 초기 설정
1. **Splash Screen** → 토큰 확인
2. **미로그인 시**: Onboarding → Login → 선호도 설정 (관심사 → 예산 → 지역)
3. **로그인 시**: 바로 HomeActivity로 이동

### 코스 생성
1. **Home 화면**에서 "새 코스 생성" 선택
2. **AI 코스 생성** (사용자 선호도 + 실시간 날씨 반영)
3. **코스 상세 보기** (장소 목록, 설명, 링크)
4. **북마크 또는 공유**

### 코스 탐색
- **추천 코스**: 사용자 맞춤 추천
- **인기 코스**: 조회수 상위 5개
- **최신 코스**: 최근 생성된 5개
- **북마크**: 저장한 코스 모아보기

## 아키텍처 및 패턴

### 인증 및 토큰 관리
- **TokenManager (Singleton)**: SharedPreferences 기반 로컬 토큰 저장
- **AuthInterceptor**: 모든 API 요청에 Authorization 헤더 자동 추가

### 사용자 선호도 관리
- **UserPreferenceManager (Singleton)**: 로컬 저장 + 서버 동기화

### API 통신
- **Retrofit**: REST API 통신
- **Gson**: 자동 직렬화/역직렬화
- **Callback 패턴**: 비동기 처리

### UI 패턴
- **RecyclerView Adapter**: 리스트 표시
- **Material Design Components**: 최신 디자인 시스템
- **Multi-Activity Navigation**: Activity 기반 화면 전환

## 주요 라이브러리

```gradle
// Android
implementation 'androidx.appcompat:appcompat:1.7.0'
implementation 'androidx.material:material:1.12.0'
implementation 'androidx.activity:activity:1.9.3'
implementation 'androidx.constraintlayout:constraintlayout:2.2.0'

// Networking
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.9.1'

// Google Auth
implementation 'com.google.android.gms:play-services-auth:21.3.0'

// Testing
testImplementation 'junit:junit:4.13.2'
androidTestImplementation 'androidx.test.ext:junit:1.2.1'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.6.1'
```

## 배포

### 백엔드 서버
- **URL**: https://datepicker-api-server.vercel.app
- **플랫폼**: Vercel

### 공유 웹사이트
- **URL**: https://date-picker-share-web.vercel.app

## 개발 환경

```
Gradle: 8.13
Android Gradle Plugin: 8.8.0
Java: 11
Kotlin DSL: build.gradle.kts
```
