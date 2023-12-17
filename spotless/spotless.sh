#!/bin/bash

echo "Running spotless"
./gradlew spotlessApply
git add .
