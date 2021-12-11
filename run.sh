#!/usr/bin/env bash

echo "[Visulog] Welcome ${USER} !"

help()
{
  echo "[Visulog] Usage: ./run.sh [-a | -s] <link/path>"
  echo "                 ./run.sh [--tool | -t]"
  ehco "                 ./run.sh [-h]"
  echo -e "Options :"
  echo -e "   -s  \n\tfor a simple analysis (it includes stats on \n\tCommits Per Month/Day/Hour/Author and the Total of (merged) commits)"
  echo -e "   -a \n\tfor an advanced analysis (it could take more time! \n\tIt includes all plugins, see README.md for an exhaustive list)"
  echo -e "   -h \n\tto display this"
  echo ""
  echo -e "   -t \n\tto run an interactive script (useful if you have differents analysis to do)"
  echo -e ""
  echo -e "Examples :"
  echo -e "   ./run.sh -a  https://github.com/Homebrew/install \n\tto run an advanced analysis on homebrew-install repository"
  echo -e "   ./run.sh -s . \n\tto run a simple analysis on this repository"
  echo ""
  echo -e "Remarks :"
  echo -e "   Not writting if you prefer -s or -a,  will automatically choose the -a option (advanced mode)"
  echo -e "   Writting both -s and -a will automatically let Visulog choose the last one"
  echo -e "   See README.md if any errors occurs"
  echo ""
  echo -e "Authors :"
  echo -e "   LN Supremacy Team"
}
def="\033[0m"
err="\033[0;31m"
s="-1"
t="-1"

while getopts ":hsat:" option; do
   case $option in
        h ) help exit;;
        s ) s="1" ;;
        a ) s="0" ;;
        t ) t="1"; ./tool.sh; exit;; # Appel à un script auxiliaire comme l'ancien mais avec les paths
        [^hsat]* ) help exit;;
  esac
done
if [ "$t" = "1" ]; then
  ./tool.sh
  exit
fi;

if [ "$1" = "" ] || [ "$2" = "" ]; then
  echo -e "${err}[Visulog] Please respect syntax${def}"
  help
  exit 1
else
  if [ "$s" = "1" ]; then
    echo "[Visulog] Running Visulog on \"${2}\" using simple mode"
    ./gradlew run --args="${2}"
  else
    echo "[Visulog] Running Visulog on \"${2}\" using advanced mode"
    ./gradlew run --args="--advancedMode=1 ${2}"
  fi
fi