# Chapter1_Challenge_1_1: The Cryptic Message Decoder

## Problem Description
As a secret agent, I intercepted a cryptic message that's a positive integer (like 13579). The real info is hidden, so I need to decode it by extracting the first and last digits, multiplying them for a product; then grabbing the second and second-last digits, adding them for a sum; and finally concatenating the product and sum as a string (e.g., "9" + "10" = "910"). No if statements or loops allowed—just variables, operators, division, modulus, and Math tricks.

## Example Input/Output
**Input:** 13579  
**Output:**  
Enter the cryptic message (positive integer): 13579  
The decrypted code is: 910

(For 24680: Product of 2*0=0, Sum of 4+8=12 → "012")

## Reflection
This challenge was a great intro to mathematical digit manipulation! I learned how to dynamically find the number of digits with Math.log10 and extract the first one using pow for shifting—super clever without loops. The tricky part was handling the second/second-last digits for non-5-digit numbers (the example assumes 5, so I hardcoded /1000 and /10, but it sparked ideas for more general formulas). Testing with edges like 4-digit inputs showed why robustness matters. Overall, it solidified operators and type conversion; my GitHub now has clean, commented code I'm proud of!