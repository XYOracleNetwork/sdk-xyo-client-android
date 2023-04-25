[![logo][]](https://xyo.network)

# sdk-xyo-client-android

[![jitpack-badge][]][jitpack-link]
[![codacy-badge][]][codacy-link]
[![codeclimate-badge][]][codeclimate-link]
[![snyk-badge][]][snyk-link]

> The XYO Foundation provides this source code available in our efforts to advance the understanding of the XYO Procotol and its possible uses. We continue to maintain this software in the interest of developer education. Usage of this source code is not intended for production.

## Table of Contents

-   [Title](#sdk-xyo-client-android)
-   [Description](#description)
-   [Instructions](#instructions)
-   [Maintainers](#maintainers)
-   [License](#license)
-   [Credits](#credits)

## Description

Primary SDK for using the XYO Protocol 2.0 from Android.

## Instructions

### Adding to Project
[See instructions on JitPack](https://jitpack.io/#xyoraclenetwork/sdk-xyo-client-android)

### Configure XYO Panel
```kotlin
var panel = XyoPanel(context, "test", listOf(NodeClient), listOf(XyoSystemInfoWitness()))
```

### Generate BoundWitness report
```kotlin
var bw = panel.reportAsync().bw
```

### Proguard Issues

> There seems to be issues using proguard with the SDK, or more specifically, Moshi, where it will remove generated classes
> We strongly recommend that you use R8 over Proguard.

## Maintainers

-   Arie Trouw

## License

See the [LICENSE](LICENSE) file for license details

## Credits

Made with 🔥 and ❄️ by [XYO](https://xyo.network)

[logo]: https://cdn.xy.company/img/brand/XYO_full_colored.png

[jitpack-badge]: https://jitpack.io/v/xyoraclenetwork/sdk-xyo-client-android.svg
[jitpack-link]: https://jitpack.io/#xyoraclenetwork/sdk-xyo-client-android

[codacy-badge]: https://app.codacy.com/project/badge/Grade/e5647b5338044a958e18c0fe91b4ed4f
[codacy-link]: https://www.codacy.com/gh/XYOracleNetwork/sdk-xyo-client-swift/dashboard?utm_source=github.com&utm_medium=referral&utm_content=XYOracleNetwork/sdk-xyo-client-android&utm_campaign=Badge_Grade

[codeclimate-badge]: https://api.codeclimate.com/v1/badges/127abaccfe85048dcf38/maintainability
[codeclimate-link]: https://codeclimate.com/github/XYOracleNetwork/sdk-xyo-client-android/maintainability

[snyk-badge]: https://snyk.io/test/github/XYOracleNetwork/sdk-xyo-client-android/badge.svg
[snyk-link]: https://snyk.io/test/github/XYOracleNetwork/sdk-xyo-client-android
