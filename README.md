# food-detector

This is my personal portfolio project of native Android application. Food Detector allows you to recognise food based on it's foto by
leveraging machine learning image classification model.

You can install it on your device using links below. 
During installation you will get warning that developer of this application is unknown. 
It's OK since I didn't upload my signing key to Google Play. For me it's unnecesarry hassle. 
Also, releasing app in Google Play require to pay a fee. 

|Release | Sha256 digest|
|--------------------------------------------------------------------------------------------------|--------------|
|[FoodDetector1.0.apk](https://github.com/lukbast/food-detector/raw/main/app/release/FoodDetector1.0.apk)|AF16194DAC2D3F943755617CAE45F9D7038A029D761D02FE642FAA97AFE2BD20|

## List of recognised foods

Since machine learning model can detect objects based on data that it was trained on, application detect only 101 different foods. 
I trained machine learning model used in this app on [Food-101](https://data.vision.ee.ethz.ch/cvl/datasets_extra/food-101/) dataset which contains images of
[these foods](https://github.com/lukbast/food-detector/blob/main/classes1.0.txt). 
If you are interested, [this notebook](https://github.com/lukbast/tensorflow/blob/main/08_food_vision.ipynb) contains detailed process of training model used in this app.

## Back-end

Backend of this application consists of [API](https://github.com/lukbast/food_detector_api) written in Python and 
Tensorflow Serving [model server](https://github.com/lukbast/fd_model_server). They are containerised and deployed on Google Cloud platform using Kubernetes. 
Both have their own repos which contains more information about them.

