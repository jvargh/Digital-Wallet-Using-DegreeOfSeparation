#!/usr/bin/env bash

# example of the run script for running the fraud detection algorithm with a python file,
# but could be replaced with similar files from any major language

# I'll execute my programs, with the input directory paymo_input and output the files in the directory paymo_output
#python ./src/antifraud.py ./paymo_input/batch_payment.txt ./paymo_input/stream_payment.txt ./paymo_output/output1.txt ./paymo_output/output2.txt ./paymo_output/output3.txt

java  -cp out\production\FinalSubmission_DigitalWallet AntiFraud
java  -cp out\production\FinalSubmission_DigitalWallet;libs\junit-4.12.jar;libs\hamcrest-all-1.3.jar org.junit.runner.JUnitCore AntiFraud_Your_Own_Test