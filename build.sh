#!/bin/bash
# Property Tax Checker v1.0.0 — lightweight manual APK build (no Gradle)
set -e
export JAVA_HOME="$HOME/workspace/jdk/jdk-17.0.20.1+1"
export PATH="$JAVA_HOME/bin:$PATH"
SDK="$HOME/workspace/android-sdk"
ANDROID_JAR="$SDK/platforms/android-34/android.jar"
BT="$SDK/build-tools/34.0.0"
PROJ="$HOME/workspace/apk-build/property-tax"
OUTDIR="$HOME/workspace/your_files/property-tax-app"
cd "$PROJ"
rm -rf build && mkdir -p build/classes build/dex build/gen

echo "== keystore =="
if [ ! -f "$PROJ/debug.keystore" ]; then
  keytool -genkeypair -keystore "$PROJ/debug.keystore" -alias androiddebugkey \
    -storepass android -keypass android -keyalg RSA -keysize 2048 -validity 10000 \
    -dname "CN=PropertyTax Debug" >/dev/null 2>&1
fi
KS="$PROJ/debug.keystore"

echo "== aapt2 compile =="
"$BT/aapt2" compile --dir res -o build/res.zip

echo "== aapt2 link (APK) =="
"$BT/aapt2" link -o build/base.apk -I "$ANDROID_JAR" \
  --manifest AndroidManifest.xml --java build/gen build/res.zip

echo "== javac =="
javac -encoding UTF-8 -source 8 -target 8 -nowarn \
  -cp "$ANDROID_JAR" -d build/classes \
  build/gen/com/hatch/propertytax/R.java \
  java/com/hatch/propertytax/*.java

echo "== d8 =="
"$BT/d8" --min-api 24 --output build/dex $(find build/classes -name "*.class")

echo "== add dex (APK) =="
(cd build/dex && zip -q ../base.apk classes.dex)

echo "== add assets (Poppins fonts) =="
if [ -d "$PROJ/assets" ]; then (cd "$PROJ" && zip -q -r build/base.apk assets); fi

echo "== zipalign =="
"$BT/zipalign" -f 4 build/base.apk build/aligned.apk

echo "== apksigner (APK) =="
"$BT/apksigner" sign --ks "$KS" \
  --ks-pass pass:android --key-pass pass:android \
  --out build/final.apk build/aligned.apk

echo "== verify APK =="
"$BT/apksigner" verify --print-certs build/final.apk | head -6
"$BT/aapt2" dump badging build/final.apk | grep -E "package:|launchable-activity:|application-label:"

mkdir -p "$OUTDIR"
cp build/final.apk "$OUTDIR/property-tax-checker-v1.7.0.apk"
ls -la "$OUTDIR/property-tax-checker-v1.7.0.apk"
echo BUILD_OK
