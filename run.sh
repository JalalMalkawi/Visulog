#!/usr/bin/env bash
echo "[Visulog] Welcome " $USER ","
while true; do
  echo "[Visulog] Please enter your git repository link or just press enter :"
  read rea
  if [ "$rea" = "" ]; then
    echo "[Visulog] Running Visulog on this Visulog project... (Advanced analysis)"
    ./gradlew run
  else
    echo "[Visulog] Please enter 'y' if you want an advanced analysis (it will take more time !), else for a simple analysis enter 'n' :"
    read ana
    echo "[Visulog] Running Visulog on ${rea}...";
    if [ "$ana" = "y" ]; then
      echo -e "\t\t(Advanced analysis)";
      ./gradlew run --args="--advancedMode=1 ${rea}";
    else
      echo -e "\t\t(Simple analysis)";
      ./gradlew run --args=${rea}
    fi
  fi;

  read -p "[Visulog] Do you want to stop doing analysis ? ('y'/'n')" yn
  case $yn in
        [Nn]* ) ;;
        [Yy]* ) echo "[Visulog] Thanks for using Visulog !"; exit;;
        * ) ;;
  esac
done