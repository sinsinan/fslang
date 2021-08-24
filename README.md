# FSlang

## A simple lambda calculus interpreter

FSlang is a lambda calculus interpreter written in java. It is a port of lazy lambda calulus interpreter written by L. Allison of Monash University. Full article about the same is available at https://users.monash.edu.au/~lloyd/tildeFP/Lambda

## Examples
**Function to add two numbers**
```
let add = lambda x.lambda y.x+y
in add 2 3
```
Output:
```
5
```
**Function to find factorial of a number**
```
let rec factorial = lambda n.
    if n=0 then
        1
    else
        n*factorial(n-1)
in factorial 3
```
Output:
```
6
```
