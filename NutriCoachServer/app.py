from flask import Flask, request

from food_recognizer import FoodRecognizer

app = Flask(__name__)


@app.route('/', methods=['POST'])
def hello_world():
    model = FoodRecognizer()
    result = model.predict(request.files.get('image').stream)
    return result


@app.route('/ingredient', methods=['POST', 'GET'])
def classify_ingredient():
    return "hi"


if __name__ == '__main__':
   app.run(host='0.0.0.0', port=5000, debug=True, debugger=False)

