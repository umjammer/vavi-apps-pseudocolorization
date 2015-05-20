# なに？

 マンガミーヤで行われていた自動着色を Android で行なうデモアプリです。

* Before

<img src="http://farm8.staticflickr.com/7194/6868888845_e796a3eaa6_m.jpg" />

* After

<img src="http://farm8.staticflickr.com/7188/6868864913_a4208be509_m.jpg" />

# 使い方

## 起動前に

 SD カードに以下の2ファイルをコピーしておいてください。

* イメージの ZIP ファイルを `/mnt/sdcard/pseudocoloriztion/test.zip` (名前固定でゴメンなさい...) にコピー
* 疑似四色刷り旧形式アルファ補正付き.cur を `/mnt/sdcard/pseudocoloriztion/test.cur` (名前固定でゴメンなさい...) にコピー

 疑似四色刷りファイルは<a href="http://www1.axfc.net/uploader/File/so/File_56252.zip">こちら</a>

## 実行

* .apk ファイルをインストールして
* vavi-apps-pseudocoloriztion を実行

# TODO

~~自動着色ロジックが遅すぎですね。 JNI にしないと...~~

`set/getPixel` をループから出して `set/getPixels` に<a href="https://github.com/umjammer/vavi-apps-pseudocolorization/commit/4f9ce54df43602c48bf69acf38c82bc824bbd01c#diff-336237e6cf1498512c051d172fcd73ee">したら</a>だいぶましになった。

あと、日本のコミックはページ送りが逆だった...orz

# GPL

このリポジトリには KLab iPhoroid UI のソースコードが含まれておりません。
KLab iPhoroid UI のソースコードは<a href="https://www.klab.jp/iphoroid/download.html">ここ</a>から取得してください。

# JSE version

* https://github.com/umjammer/vavi-image/blob/master/vavi-image-sandbox/src/main/java/vavix/awt/image/color/ColorCurveOp.java
* https://github.com/umjammer/vavi-image/blob/master/vavi-image-sandbox/src/test/java/vavix/awt/image/color/ColorCurveOpTest.java
