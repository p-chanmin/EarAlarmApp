# Ear Alarm

<p align="center">
  <a href="https://github.com/p-chanmin"><img alt="Profile" src="https://img.shields.io/badge/GitHub-p--chanmin-informational?logo=github"/></a>
  <a href="https://android-arsenal.com/api?level=26"><img alt="API" src="https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat"/></a><br> 
    <a><img src="https://img.shields.io/badge/Android%20Studio-Meerkat | 2024.3.2-%233DDC84?logo=Android%20Studio"/></a>
    <a><img src="https://img.shields.io/github/languages/top/p-chanmin/EarAlarmApp"/></a>
  <a><img src="https://img.shields.io/github/last-commit/p-chanmin/EarAlarmApp"/></a>
  <a><img alt="Profile" src="https://img.shields.io/github/v/tag/p-chanmin/EarAlarmApp"/></a> 
</p>


<br>

<p align="center"><img src="https://user-images.githubusercontent.com/87304360/175567236-57d0aec2-8bec-424f-9fc9-2a30d3ee72e1.png" width="800"/>
</p>

<br>


## 🔔 이어폰에서 울리는 타이머 알람 앱

**EarAlarm**은 이어폰에서 직접 울리는 타이머 알람 앱으로, 조용한 환경에서도 이어폰으로 알람을 들을 수 있도록 도와줍니다. 이 앱은 특히 도서관, 사무실, 지하철, 버스, 기차 등 **조용한 환경**에서 유용합니다.

**EarAlarm**은 기본적으로 **미디어 볼륨을 사용**하기 때문에, 타이머 알람이 울릴 때 이어폰을 착용하는 것을 권장합니다. 이어폰을 착용하면 타이머 알람이 이어폰을 통해 울리며, 주변 사람들에게 방해가 되지 않습니다.

**만약 이어폰이 연결되어 있지 않은 경우**, EarAlarm은 기본 알람과 동일하게 동작하여, **기기의 스피커를 통해 알람 소리가 울립니다.** 이 경우에는 공공장소나 조용한 환경에서 타이머 알람 소리가 울리지 않도록 주의해 주세요.

<br>

|                          알람 설정                           |                          알람 해제                           |                          background                          |                             설정                             |
| :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| <img src="https://github.com/user-attachments/assets/84d05b2a-726d-4e77-b6dc-b15d368a5a19" width="200"> | <img src="https://github.com/user-attachments/assets/6167b58f-addd-4826-bf46-a85e52dd2dee" width="200"> | <img src="https://github.com/user-attachments/assets/044b2451-501f-4d4a-aa76-3ce83b759237" width="200"> | <img src="https://github.com/user-attachments/assets/68d4b37e-dc8a-440c-b820-7c32d0484c56" width="200"> |

<br>

**EarAlarm**은 다음 언어를 지원합니다.

**사용 가능 언어**

- **영어(en)**
- **한국어(ko)**
- **스페인어(es)**
- **프랑스어(fr)**
- **인도네시아어(in)**
- **일본어(ja)**
- **중국어(간체)(zh-CN)**
- **중국어(번체)(zh-TW)**

<br>

## 📲 Download

[**Google Play Store**](https://play.google.com/store/apps/details?id=kr.ac.tukorea.android.earalarm) 에서 최신 버전의 앱을 다운로드 하실 수 있습니다.

혹은 **[Releases](https://github.com/p-chanmin/EarAlarmApp/releases/latest)** 에서 최신 apk를 다운로드 하실 수 있습니다.

<br>



## 📚 Document

- [**타이머 서비스 분석 및 알람 로직 개선**](https://oldogz7358.tistory.com/1)
- [**Google Play In-App Review API 연결하기**](https://oldogz7358.tistory.com/3)
- [**Google Play In-App updates 연결하기**](https://oldogz7358.tistory.com/4)
- [**Restoring the Navigation back stack failed**](https://oldogz7358.tistory.com/5)
- [**Attempting to launch an unregistered ActivityResultLauncher with contract**](https://oldogz7358.tistory.com/6)



<br>

## 📄 기술 스택

| 분류                     | Stack                              |
| ------------------------ | ---------------------------------- |
| **Architecture**         | App Architecture, MVVM             |
| **UI**                   | Jetpack Compose, Material Design 3 |
| **Navigation**           | Navigation Compose                 |
| **Dependency Injection** | Hilt                               |
| **Async**                | Coroutines, Flow                   |
| **Local Storage**        | DataStore                          |
| **Serialization**        | Kotlinx Serialization              |
| **Testing**              | JUnit4, Kotest, MockK, Turbine     |
| **Firebase**             | Analytics, Crashlytics             |
| **Play Services**        | In-App Review, In-App Update, Ads  |

