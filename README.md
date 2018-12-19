[![API](https://img.shields.io/badge/API-16%2B-red.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![Build Status](https://travis-ci.org/wupdigital/android-maven-publish.svg?branch=master)](https://github.com/Krunal-Kevadiya/RxPhotoPicker)
[ ![Download](https://api.bintray.com/packages/kevadiyakrunalk/MyFramework/rxphotopicker/images/download.svg) ](https://bintray.com/kevadiyakrunalk/MyFramework/rxphotopicker/_latestVersion) 
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](https://opensource.org/licenses/Apache-2.0)

# RxPhotoPicker

* you can pick image from GALLERY or CAMERA with three type of transformers like URI, BITMAP and FILE.
* there are two type of Gallery image pick single and multiple with max limit.
* also it's handle the drive/google photo pick.
* Code :-
```java
// - Single image pick
   RxPhotoPicker
    .getInstance(this)
    .pickSingleImage(
      Sources.GALLERY /*you have use source as a pick from gallery or camera*/, 
      Transformers.URI /*you have set Transformers as your actual image getting format like Uri, Bitmap or File*/, 
      new PhotoInterface<
       Uri/*Set argument based on your transformers for example Transformers.URI to Uri, Transformers.BITMAP to Bitmap*/>(){
         @Override
         public void onPhotoResult(Uri uri) {
           //here is your output based on Transformers Like URI, BITMAP or FILE.
         }
       }, 
       Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) /*[Option param] this param only use when you have pick image as a file format*/);
// - Single image pick with crop option and default setting
   RxPhotoPicker
    .getInstance(this)
    .pickSingleImage(
      Sources.GALLERY /*you have use source as a pick from gallery or camera*/, 
      Transformers.URI /*you have set Transformers as your actual image getting format like Uri, Bitmap or File*/, 
      true /*you have set crop then pass true and use with the default setting*/,
      new PhotoInterface<
       Uri/*Set argument based on your transformers for example Transformers.URI to Uri, Transformers.BITMAP to Bitmap*/>(){
         @Override
         public void onPhotoResult(Uri uri) {
           //here is your output based on Transformers Like URI, BITMAP or FILE.
         }
       }, 
       Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) /*[Option param] this param only use when you have pick image as a file format*/); 
// - Single image pick with crop option and custom setting
   CropOption.Builder builder = new CropOption.Builder();
        builder.setOutputHW(690, 690);
        builder.setAspectRatio(3, 2);
        builder.setScale(true);
        
   RxPhotoPicker
    .getInstance(this)
    .pickSingleImage(
      Sources.GALLERY /*you have use source as a pick from gallery or camera*/, 
      Transformers.URI /*you have set Transformers as your actual image getting format like Uri, Bitmap or File*/, 
      true /*you have set crop then pass true and use with the default setting*/,
      builder /*crop option in custom setting*/,
      new PhotoInterface<
       Uri/*Set argument based on your transformers for example Transformers.URI to Uri, Transformers.BITMAP to Bitmap*/>(){
         @Override
         public void onPhotoResult(Uri uri) {
           //here is your output based on Transformers Like URI, BITMAP or FILE.
         }
       }, 
       Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) /*[Option param] this param only use when you have pick image as a file format*/);  
// - Multiple image pick
   RxPhotoPicker
    .getInstance(this)
    .pickMultipleImage(
      Transformers.URI /*you have set Transformers as your actual image getting format like Uri, Bitmap or File*/,
      new PhotoInterface<List<Uri
       /*Set argument based on your transformers for example Transformers.URI to Uri, Transformers.BITMAP to Bitmap*/>>() { 
        @Override
        public void onPhotoResult(List<Uri> uri) {
          //here is your output based on Transformers Like URI, BITMAP or FILE.
        }
     }, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
```
-> Gradle
```groovy
//add dependencies for app level build.gradle
repositories {
    jcenter()
}
dependencies {
  compile 'com.kevadiyakrunalk:rxphotopicker:2.7@aar'
  compile 'com.kevadiyakrunalk:commonutils:1.1@aar'
}
```
-> Maven
```xml
<dependencies>
  <dependency>
    <groupId>com.kevadiyakrunalk</groupId>
    <artifactId>rxphotopicker</artifactId>
    <version>2.7</version>
    <type>pom</type>
  </dependency>
  <dependency>
    <groupId>com.kevadiyakrunalk</groupId>
    <artifactId>commonutils</artifactId>
    <version>1.1</version>
    <type>pom</type>
  </dependency>
</dependencies>
```
