import numpy as np
import pandas as pd
import tensorflow.compat.v2 as tf
import tensorflow_hub as hub
from PIL import Image
import cv2


class FoodRecognizer:

    def __init__(self):
        self.m = hub.KerasLayer('https://tfhub.dev/google/aiy/vision/classifier/food_V1/1')


    def predict(self, image):
        labelmap_url = "https://www.gstatic.com/aihub/tfhub/labelmaps/aiy_food_V1_labelmap.csv"
        input_shape = (224, 224)

        img = Image.open(image).convert("RGB")
        img = np.asarray(img)
        img = cv2.resize(img, input_shape)
        img = img / img.max()

        images = np.expand_dims(img, 0)
        output = self.m(images)
        predicted_index = output.numpy().argmax()
        classes = list(pd.read_csv(labelmap_url)["name"])
        return classes[predicted_index]


if __name__ == '__main__':
    rec = FoodRecognizer()
    print(rec)
    print(rec.model.modules())
