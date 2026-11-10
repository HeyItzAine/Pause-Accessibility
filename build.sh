#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
: "${ANDROID_JAR:?Set ANDROID_JAR to an Android API 35 android.jar}"
: "${BUILD_TOOLS:?Set BUILD_TOOLS to Android build-tools 35 directory}"
: "${ECJ_JAR:?Set ECJ_JAR to Eclipse ecj 3.40.0 jar}"
mkdir -p build/gen build/classes build/dex build/tests
"$BUILD_TOOLS/aapt2" compile --dir res -o build/resources.zip
"$BUILD_TOOLS/aapt2" link -o build/base.apk --manifest AndroidManifest.xml -I "$ANDROID_JAR" --java build/gen build/resources.zip
java -jar "$ECJ_JAR" -8 -classpath "$ANDROID_JAR" -d build/classes src/app/accesspause/*.java build/gen/app/accesspause/R.java
java -jar "$ECJ_JAR" -8 -d build/tests src/app/accesspause/ToggleEngine.java tests/ToggleEngineTest.java
java -cp build/tests ToggleEngineTest
"$BUILD_TOOLS/d8" --lib "$ANDROID_JAR" --min-api 24 --output build/dex build/classes/app/accesspause/*.class
cp build/base.apk build/unsigned.apk
(cd build/dex && zip -q ../unsigned.apk classes.dex)
"$BUILD_TOOLS/zipalign" -f 4 build/unsigned.apk build/aligned.apk
# Use your own private signing key. APKs with a different key cannot update the supplied build in place.
: "${SIGNING_KEYSTORE:?Set SIGNING_KEYSTORE to your own signing keystore}"
"$BUILD_TOOLS/apksigner" sign --ks "$SIGNING_KEYSTORE" --out build/Access-Pause.apk build/aligned.apk
"$BUILD_TOOLS/apksigner" verify --verbose build/Access-Pause.apk
