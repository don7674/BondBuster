# BondBuster
BondBuster is a Java desktop application. It allows you to monitor your mortgage bond in terms of balance outstanding, monthly payments, and interest paid per month or per day.

Importantly it illustrates the substantial effect interest has on the total amount repaid.
  For example, on a 20 year mortgage you can easily be paying back double the purchase price of the house!
  
## Features:
1.  Bond details, payments, and interest rates are stored in simple xml files, allowing for easy updating with your favourite editor (eg. Notepad for Windows or gedit for Linux). These files are located in the data sub-folder.
Interest rate can be either fixed or variable (relative to prime). Interest is amortised daily based on the rate effective on each day

2.  Scenario planning: You can change the values and hit Calculate to see the effects. In particular you can make "ad-hoc" payments and see how dramatically these can reduce the debt and lifetime of the bond. 

3.  A bar chart that illustrates the portion of your annual payments allocated to interest versus capital.


## Build:
```
javac -cp lib/jcommon-1.0.16.jar:lib/jdom.jar:lib/jfreechart-1.0.13.jar -d ./bin src/*.java
```

## Run:
1. In bin/data folder, edit the various xml files (with any editor) to suit your needs.

2. Run as follows:
```
    cd bin
    
    export CLASSPATH=../lib/jcommon-1.0.16.jar:../lib/jfreechart-1.0.13.jar:../lib/jdom.jar:./
    
    java bondcalculator.Main
```
