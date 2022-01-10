# Visulog

*Tool for analysis and visualization of git logs*

## Presentation

Visulog a tool made for analyzing contributions from the members of a team working as a same given project hosted on a git repository. Its goal is to assist teachers for individual grading of students working as a team.
It requires a UNIX system like macOS.

This tool can:

- compute a couple of relevant indicators such as:
    - number of lines or characters added/deleted/changed
    - number of commits
    - number of merge commits
    - number of authors
- visualize the indicators as charts (histograms, pie charts, etc.) embedded in a generated web page.
- to have an exhaustive list of the function, see the following section

## Analyzes

This tool will show you :
- The total of commits & merge commits
    - which can be used to see the importance of the work
- The number of modified lines since the beginning of the project
    - Used for similar reason
- Average number of modified lines per day (include in the advanced analysis)
    - It allows you to have an idea of the speed of the project's growth

- The number of commits per day & per month
    - Used to compare when there are more commits
  
- The number of commits per hour
    - To inspect at which hour is there more commits, independently of the day : indirectly, it informs when collaborators are more efficiency.

- The number of authors
    - More authors implies a more important project
- The number of commits per author
    - It informs of the authors' names, and the importance of their contributions in the project
  
- The number of modified lines per day (include in the advanced analysis)
    - Useful to have a better view about the daily contribution of the team
- The number of modified lines per author (include in the advanced analysis)
  - To see the contribution of each author in the code
- The number of modified lines per author per day(include in the advanced analysis)
    - To see the daily contribution of each author in the code
## Already existing similar tools

- [gitstats](https://pypi.org/project/gitstats/)


## Technical means

- The charts are generated by a third party library (R).
- The data to analyze can be obtained using calls to the git CLI. For instance "git log", "git diff --numstat", and so on.

## Architecture

Visulog contains the following modules:

- data types for storing raw data directly extracted from git history, with relevant parsers
- a generator of numerical series (for the indicators mentioned above)
- a generator of web pages
- a command line program that calls the other modules using the provided command line parameters
- a shared module for configuration object definitions

## Usage

### Building the project

1. Clone the repository
    ```
    git clone https://gaufre.informatique.univ-paris-diderot.fr/benmouff/visulog.git
    ```
2. Only if you are on a SCRIPT computer (in one of the TP rooms):
    ```
    source SCRIPT/envsetup
    ```
   *This will setup the GRADLE_OPTS environment variable so that gradle uses the SCRIPT proxy for downloading its dependencies. It will also use a custom trust store (the one installed in the system is apparently broken... ).*
3. (Optional) Run gradle wrapper (it will download all dependencies including gradle itself, except R)
    ```
    ./gradlew build
    ```
4. Install R

    See https://www.r-project.org to install R
   
5. (Optional) Register your other account(s)
   In the file `./AuthorName.txt` in this format : `MainAccountName=YourSecondAccountName=YourThirdAccountName=..`". (Only your main account name will be displayed by the graphs).
    

### Running the software

Open a terminal window and set your working directory to here : ```/visulog```
Then, run through your terminal
```
./run
```
(It may ask you to allow permission of execution on this file, in which case just do ```chmod u+x ./run.sh```).

Then follow the command usage :
```
[Visulog] Welcome User !
[Visulog] Usage: ./run [-a | -s] <link/path>
                 ./run [-t]
                 ./run [-h]
Options :
   -s   for a simple analysis (it includes stats on Commits Per
         Month/Day/Hour/Author and the Total of (merged) commits)
   -a   for an advanced analysis (it could take more time! 
        It includes all plugins, see README.md for an exhaustive list)
   -h   to display this

   -t   to run an interactive script (useful if you have differents analysis to do)

Examples :
   ./run -a  https://github.com/Homebrew/install 
        to run an advanced analysis on homebrew-install repository
   ./run -s . 
        to run a simple analysis on this repository

Remarks :
   Writting both -s and -a will automatically let Visulog choose the last one
   See README.md if any errors occurs

Authors :
   LN Supremacy Team

```


 
#### Errors

To properly run this tool, you should have :
- make sure there is no space in the pathname of the visulog directory.
- set a default browser
- allow this application to write files
- 
  
If you didn't already cloned the concerned git repository :
- have an internet connexion
- as wrote above, running this tool on a public github/gitlab project