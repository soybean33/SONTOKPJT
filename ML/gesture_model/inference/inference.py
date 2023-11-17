import tensorflow as tf
import numpy as np
import os
import pandas as pd

file_root = input("test data set root : ")
test_data_files = os.listdir(file_root)
model_path = input("model dir path : ")
models = os.listdir(model_path)
save_path = input("save path : ")

for model in models:
    # Load the TFLite model and allocate tensors.
    interpreter = tf.lite.Interpreter(model_path=model_path+model)
    interpreter.allocate_tensors()

    # Get input and output tensors.
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    # Test the model on random input data.
    input_shape = input_details[0]['shape']
    for test_file in test_data_files:
        
        outputs = []

        input_data = np.load(file_root+test_file)
        # if int(model[-8]) >2:   
        input_data = input_data.astype(np.float32)
        # else:
        #     input_data = input_data.astype(np.float16)
        # input_data = input_data[:, :, :-1]

        print('input shape: ', input_shape)
        print('input data shape: ', input_data[0:1].shape)

        for idx in range(len(input_data)):
            interpreter.set_tensor(input_details[0]['index'], [input_data[idx]])
            interpreter.invoke()

            # The function `get_tensor()` returns a copy of the tensor data.
            # Use `tensor()` in order to get a pointer to the tensor.
            output_data = interpreter.get_tensor(output_details[0]['index'])
            print(output_data)
            outputs.append(*output_data)
        df = pd.DataFrame(outputs)
        print("csv 미리보기", df, sep="\n")
        df.to_csv(f'{save_path}/{model}_{test_file}.csv', index=False, header=False)
