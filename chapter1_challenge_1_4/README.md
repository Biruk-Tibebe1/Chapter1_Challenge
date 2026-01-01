# Chapter1_Challenge_1_4: The Robust File Config Reader

## Problem Description
Load config.txt via BufferedReader: Parse line1 as int version (throw Exception if <2), line2 as path (throw IOException if !exists). Multiple specific catches (FileNotFound/NumberFormat/IO), fallback Exception; finally always executes. Graceful, non-crashing I/O.

## Example Input/Output
config.txt (old version + bad path):
1
C:\nonexistent_folder\nonexistent_file.txt

**Output:**  
Error: Config version too old!  
Config read attempt finished.

- No file: "Error: Config file not found." + finally  
- Line1="abc": "Error: Invalid number in config version." + finally  
- Version=2 + bad path: "Error: Target file does not exist!" + finally

## Reflection
Multiple catches in order was key—specifics snag before general, keeping msgs precise. Throwing custom Exceptions turned validation into clean flow; finally nailed "always execute." Testing by mangling the file hammered home checked vs unchecked. Chapter 1 done—exceptions demystified, GitHub's a beast!