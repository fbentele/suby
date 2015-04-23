#!/bin/bash
echo "Start downloading nightly.zip"
wget http://suby.flokus.ch/nightly.zip
echo "Finished downloading nightly.zip"
echo "Remove old jar"
rm ./Suby.jar
echo "Extract archive"
unzip ./nightly.zip && rm ./nightly.zip
echo "all good"
