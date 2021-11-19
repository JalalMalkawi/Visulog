#!/usr/bin/env bash
echo "Welcome " $USER ", please enter your git repository link or just press enter :"
read rea
if [ "$rea" = "" ]; then
  echo "Running Visulog on this Visulog project..."
  ./gradlew run
else
  echo "Running Visulog on ${rea}"
  ./gradlew run --args=${rea}
fi;
