from flask import Flask, render_template,request
from werkzeug import secure_filename
import os
import cv2 as cv2
import pytesseract
from googletrans import Translator
import numpy as np


app = Flask(__name__)
UPLOAD_FOLDER = os.path.join('static','Images')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

tesseract_dict = {'English':'eng','Hindi':'hin','Marathi':'mar','Russian':'rus','Japanese':'jpn','Telugu':'tel','Urdu':'urd','Gujrati':'guj'}
googletrans_dict = {'English':'en','Hindi':'hi','Marathi':'mr','Russian':'ru','Japanese':'ja','Telugu':'te','Urdu':'ur','Gujrati':'gu'}

@app.route('/')
def index():
    return render_template('upload_1.html')

@app.route('/result',methods=['POST','GET'])
def uploaded():
    # if data is given by user 
    if request.method == 'POST':
        # obtaining image
        f = request.files['file']  

        # obtaining languages from user  
        data = request.form
        source_language = data['source_language']
        destination_language = data['destination_language']

        # saving the uploaded file
        name = secure_filename(f.filename)
        image_path = os.path.join(app.config['UPLOAD_FOLDER'],name)
        f.save(image_path)

        # calling the function for processing the image
        data_to_be_passed = extract_and_translate(image_path,source_language,destination_language)

        # returning the result of processing
        return render_template('final_result_1.html',data = data_to_be_passed)  

    else:
        return render_template('upload.html')   

def extract_and_translate(image_path,source_language,destination_language):
    data_to_be_passed = {}
    data_to_be_passed['source_language'] = source_language
    data_to_be_passed['destination_language'] = destination_language

    destination_language = googletrans_dict[destination_language]
    source_language = tesseract_dict[source_language]

    data_to_be_passed['image_name'] = image_path

    img = cv2.imread(image_path)

    img = cv2.resize(img, None, fx=2, fy=2, interpolation=cv2.INTER_CUBIC)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    img = cv2.threshold(img, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)[1]
    img = cv2.medianBlur(img, 3)
    img = cv2.bilateralFilter(img,9,75,75)
    cv2.adaptiveThreshold(img, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, 31, 2)

    text_img = pytesseract.image_to_string(img,lang=source_language)
    data_to_be_passed['extracted_text'] = text_img

    translator = Translator() 
    translated = translator.translate(text_img,dest=destination_language)
    data_to_be_passed['translated_text'] = translated.text

    return data_to_be_passed


