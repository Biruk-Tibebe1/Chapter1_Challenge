# Chapter1_Challenge_1_2: The Lottery Number Analyzer

## Problem Description
Process a hardcoded String array of lottery numbers (e.g., "12-34-56-78-90") using a for loop: strip "-" with replace(), convert string to char[] then int[] digits via for-each and Character.getNumericValue(), sum with another for-each, calc avg, print per ticket, and track/announce the max-avg winner.

## Example Input/Output
Hardcoded—no user input.  
**Output:**  
Analyzing: 12-34-56-78-90  
Digit Sum: 45, Digit Average: 4.5  
Analyzing: 33-44-11-66-22  
Digit Sum: 30, Digit Average: 3.0  
Analyzing: 01-02-03-04-05  
Digit Sum: 15, Digit Average: 1.5  
The winning number with the highest average is: 12-34-56-78-90 with an average of 4.5

## Reflection
String-to-array conversion was smoother than expected—replace() and for-each loops handled the heavy lifting without fuss. The max-tracking if inside the for was a clean way to avoid extra passes. Debugged a pesky package mismatch, but testing with varied avgs confirmed robustness. Excited for more loops in 1.3—GitHub's growing!