# Baking App Project (UDACITY Android Developer Nanodegree)

![Phone](https://imgur.com/Kbk5U9u.gif)![Tablet](https://imgur.com/RSNUD2Y.gif)

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](LICENSE.txt)[![Platform Android](https://img.shields.io/badge/platform-Android-blue.svg)](https://www.android.com)

## Project Overview

The aim of this project was to utilize a json source of data from the network and create an app based on this data, which is in a production ready state, capable of finding and handling error cases, adding accessibility features, allowing for localization, adding a widget, and adding a library.

## What is exhibited in this project?
In this project there is:
* Usage of MediaPlayer/Exoplayer to display videos.
* Handling error cases in Android.
* Addition of a widget.
* Leverage of a third-party library.
* Usage of fragments to create a responsive design that works on phones and tablets.

## App Description
The task was to create a Android Baking App that allows Udacity’s resident baker-in-chief, Miriam, to share her recipes with the world. The user selects a recipe and see video-guided steps for how to complete it.

The recipe listing is located [here](https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json).

The JSON file contains the recipes' instructions, ingredients, videos and images. Some may have a video, an image, or no visual media at all. One of the skills demonstrated in this project is how to handle unexpected input in the data -- professional developers often cannot expect polished JSON data when building an app.

## Rubric

[BakingAppRubric](documentation/BakingAppRubric.pdf)


## How to build the app

1. Clone this repository on your local machine:

```
https://github.com/douggharvey/PopularMovies.git
```

2. Open Android Studio and open the project from `File > Open...`

## Languages, libraries and tools used

* [Java](https://docs.oracle.com/javase/8/)
* Android Support Libraries
* [Retrofit](https://github.com/square/retrofit)
* [Gson](https://github.com/google/gson)
* [Picasso](https://github.com/square/picasso)
* [Butterknife](https://github.com/JakeWharton/butterknife)
* [MaterialFavoriteButton](https://github.com/IvBaranov/MaterialFavoriteButton)
* [OKHttp Logging Interceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor)
* [ExoPlayer](https://github.com/google/ExoPlayer)
* [Espresso](https://developer.android.com/training/testing/espresso/index.html)

## Requirements

* JDK 1.8
* [Android SDK](https://developer.android.com/studio/index.html)
* Android O ([API 27](https://developer.android.com/about/versions/oreo/))

## License

[MIT license](LICENSE.txt)
Copyright 2018 Douglas Gordon Harvey


