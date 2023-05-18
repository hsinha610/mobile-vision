
# Mobile Vision App - CameraX + Google ML Kit

App extracts English text from images & allows you to translate the text to Hindi.

## Project overview
- Project contains 1 activity & 3 fragments.
    - **MainActivity** - Handles permission checks required for using camera & fragment transactions.
    - **ImageCaptureFragment** - shows preview of the back camera & gives option to capture image. We get captured image Wrapped in ImageProxy which we convert to Bitmap & pass to next fragment.
    - **ImageTextExtractionFragment** - performs text extraction on image & displays both image & extracted text.
    - **TextTranslationFragment** - translates the extracted text & shows side by side the original & translated text.

## Libraries used

    1. CameraX
    2. Google ML Kit

## Articles for reference

<a href="https://developer.android.com/codelabs/camerax-getting-started#0">Getting started with CameraX</a>

<a href="https://joebirch.co/android/exploring-camerax-on-android-use-cases/">Exploring CameraX on Android: Use Cases
</a>

<a href="https://developers.google.com/ml-kit/vision/text-recognition/v2/android">Text Recognition</a>

<a href="https://developers.google.com/ml-kit/language/translation/android">Text Translation</a>
