from flask import Flask

from ingredients_recognizer import IngredientsRecognizer

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello World!'


@app.route('/ingredient')
def classify_ingredient():
    return 'potato'


if __name__ == '__main__':
   app.run(host='0.0.0.0', port=5000)

