# Chapter1_Challenge_1_3: The Dungeon Game

## Problem Description
A simple text dungeon crawler: 5 rooms (for loop), random event per room (switch on 1-3): trap (-20 health), potion (+15, cap 100), monster (do-while guesses 1-5 to match random). Break early on <=0 health; announce victory/defeat.

## Example Input/Output
Random—varies per run. Example:
Entering room 1...
You found a healing potion! Health is now 100 (capped to 100).
Entering room 2...
A monster appears! Guess a number (1-5) to defeat it:
3
Wrong! Try again: 2
Wrong! Try again: 4
You defeated the monster!
...
Entering room 3...
A trap sprung! Health is now 0.
You have been defeated in room 3.

OR full clear: You cleared the dungeon! Victorious with 80 health!

## Reflection
Do-while was ideal for persistent guesses until correct—feels game-like! Switch kept events clean, and break + flag handled early exit elegantly. Randomness made testing replayable; capped health avoided over-healing bugs. Tough: Ensuring death check post-event but pre-next room. Loving the flow—1.3's interactive!