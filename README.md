# MettaScript-Editor

A General Purpose Calculator and Programming Frontend with a variety of export options.

![A Recent Screenshot](http://zakfenton.com/user/pages/07.projects/sdk/Screenshot%20from%202016-11-20%2019-59-16.png)

## About This Repository

This repository is the GitHub home for the public release of the MettaScript Editor.

Development of the MettaScript Editor mostly happens in a private Fossil repository, so the version uploaded here may not be the most up-to-date or well-documented.

## Binaries & News

The most up-to-date source of binary builds and news relating to MettaScript and the MettaScript Editor is my website's SDK section:

http://zakfenton.com/projects/sdk

## Primer

MettaScript is half way between a computer algebra system and a programming language. It will be most useful to users who are already familiar with Scheme/LISP and Smalltalk (it's similar to those, but with a more traditional syntax based on plain old mathematics, and with looser semantic rules allowing for more intuitive operation).

Every operation in MettaScript is conceptually a binary operation (like `1 + 1`, with a left-hand-side, an operator, and a right-hand-side). This simplifies the language model a great deal while generally coinciding with traditional mathematical syntax.

Examples:

    x + y

(The left-hand-side is `x`, the operator is `+` and the right-hand-side is `y`.)

    "abc" isUnicodeCompatible

(The left-hand-side is `"abc"`, the operator is `isUnicodeCompatible` and the right-hand-side is an implicit empty value.)

    "abc" isUnicodeCompatible()

(Equivalent to the previous example, but with the empty right-hand-side made explicit.)

    Fibonacci 20

(The left-hand-side is `Fibonacci`, the operator is an implicit `@` meaning "applied to" and the right-hand-side is `20`.)

    Fibonacci @ 20

(Equivalent to the previous example, but with the `@` operator made explicit.)

Every value in MettaScript is conceptually a function. Most binary operations are reduced to a function call, where the left-hand-side is the function (more precisely, the intended target of the operation), and the operator and right-hand-side are the parameters. This is compatible with Object-Oriented Programming (in particular, the original Smalltalk model) but is simpler, because objects and classes would otherwise rely on functions anyway (this way, less special rules and value types are required).

User-defined functions in MettaScript always accept exactly three parameters, named `.` (the left-hand-side), `!` (the operator) and `?` (the right-hand-side). If multiple right-hand-side parameters are required, they are simply combined into a single value and then split when the individual values are needed. Object-oriented programming can be implemented by using the left-hand-side as a "self" identifier (and using a special system function to make "super calls" to a different function with the same left-hand-side).

Examples:

    x = [? + 1]; x 2

(The result is `3`.) 

    addMul = [(x,y,z)=?; x + y * z]; addMul(1,2,3)

(The result is `7`.)

More examples can be found in the editor itself.
