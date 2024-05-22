# Ear Alarm

<p align="center">
  <a href="https://github.com/p-chanmin"><img alt="Profile" src="https://img.shields.io/badge/GitHub-p--chanmin-informational?logo=github"/></a>
  <a href="https://android-arsenal.com/api?level=26"><img alt="API" src="https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat"/></a><br> 
    <a><img src="https://img.shields.io/badge/Android%20Studio-Hedgehog | 2023.1.1-%233DDC84?logo=Android%20Studio"/></a>
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
| <img src="https://github.com/p-chanmin/EarAlarmApp/assets/87304360/df4ecbc6-e60d-44e7-b76f-3bc71537d2a3" width="220"> | <img src="https://github.com/p-chanmin/EarAlarmApp/assets/87304360/701e4e5d-c8d6-424b-8ba0-3696bf0a2494" width="220"> | <img src="https://github.com/p-chanmin/EarAlarmApp/assets/87304360/71b73765-6b2b-42ef-9590-3d9fc48ee9bf" width="220"> | <img src="https://github.com/p-chanmin/EarAlarmApp/assets/87304360/de4d0c3e-a2d2-417e-b9bc-0066261694b3" width="220"> |

<br>

**EarAlarm**은 다국어 지원이 되어 있어 전 세계의 사용자들이 이용할 수 있습니다.

현재 **영어(en), 한국어(ko), 스페인어(es), 프랑스어(fr), 인도네시아어(in), 일본어(ja), 중국어(간체)(zh-CN), 중국어(번체)(zh-TW)** 의 언어로 사용 가능합니다. 

<br>

## 📲 Download

[**Google Play Store**](https://play.google.com/store/apps/details?id=kr.ac.tukorea.android.earalarm) 에서 최신 버전의 앱을 다운로드 하실 수 있습니다.

혹은 **[Releases](https://github.com/p-chanmin/EarAlarmApp/releases/latest)** 에서 최신 apk를 다운로드 하실 수 있습니다.

<br>



## 📢 Trouble Shooting

- 

<br>

## 📄 기술 스택

| 분류                    | Stack                                            |
| ----------------------- | ------------------------------------------------ |
| App Architechture       | Google Recommended app architecture              |
| Design Pattern          | MVVM                                             |
| DI                      | Hilt                                             |
| UI                      | XML                                              |
| Local Storage           | DataStore                                        |
| serializer/deserializer | Gson                                             |
| Asynchronous            | Coroutines, Flow                                 |
| Background              | Service, AlarmManager, AudioManager, MediaPlayer |
| Notification            | NotificationManager                              |

