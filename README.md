Java solutions for the [Advent of Code](https://adventofcode.com/) daily challenges
([2020](https://adventofcode.com/2020/) and [2021](https://adventofcode.com/2021)).

Goals:

* The code is readable and logical, includes meaningful comments and debug statements, and is easy to comprehend
* Efficiency in terms of execution time and memory allocation
* Learning, in particular modern Java features and algorithms
* No external dependencies other than a logging framework (tinylog)
* Attempt to solve entirely independently; if stuck look on Reddit for tips on general approaches to
solving the problem. As a last resort, look at other Java solution implementations.

Warning - includes answers to enable self verification!

## References and Credits

* [Circular Linked List in Java](https://www.baeldung.com/java-circular-linked-list) with associated [implementation](https://github.com/eugenp/tutorials/blob/master/data-structures/src/main/java/com/baeldung/circularlinkedlist/CircularLinkedList.java)
* Stack Abuse [Graph Theory and Graph-Related Algorithm's Theory and Implementation](https://stackabuse.com/graph-theory-and-graph-related-algorithms-theory-and-implementation/)
* [Dijkstra shortest path](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm) algorithm.
    * Baeldung article on how to [implement with Java](https://www.baeldung.com/java-dijkstra) with associated [implementation](https://github.com/eugenp/tutorials/blob/master/algorithms-miscellaneous-2/src/main/java/com/baeldung/algorithms/ga/dijkstra/Dijkstra.java)
    * Stack Abuse Graphs in Java [article and implementation](https://stackabuse.com/graphs-in-java-dijkstras-algorithm/)
* [A* search](https://en.wikipedia.org/wiki/A*_search_algorithm) algorithm
    * Baeldung article on how to [implement with Java](https://www.baeldung.com/java-a-star-pathfinding) with associated [implementation](https://github.com/eugenp/tutorials/blob/master/algorithms-miscellaneous-2/src/test/java/com/baeldung/algorithms/astar/RouteFinder.java)
    * Stack Abuse Graphs in Java [article and implementation](https://stackabuse.com/graphs-in-java-a-star-algorithm/)
* [Dijkstra vs. A* for Pathfinding](https://www.baeldung.com/cs/dijkstra-vs-a-pathfinding)

## Tricky Challenges

### 2019

#### Day 7

Nice progressive use of the Intcode Virtual Machine. This challenge is relatively straightforward
by linking the amplifiers together with a blocking queue such that the output from one amplifier is
used as the input for the next. The slight annoyance here is that the BlockingQueue put / take
methods both throw the checked InterruptedException which doesn't play well with lambda expressions.
Using a blocking queue allows the VMs to execute in parallel until they receive their HALT
instruction. A fixed thread pool executor service provides a neat solution to running all amplifiers
in parallel and waiting until all have terminated.

All that is then left is to stream the permutations of the phase settings, calculate the output
signal for each permutation and extract the maximum output signal. Streaming the permutations means
that the list of all possible permutations does not need to calculated up front and stored in memory
in a single list.

Solutions for parts 1 and 2 are then identical as the phase settings for part 1 result in all of the
amplifiers terminating immediately after just one pass, whereas part 2 requires a number of
iterations.

### 2020

#### Day 13

First time I'd encountered the [Chinese Remainder Theorem](https://en.wikipedia.org/wiki/Chinese_remainder_theorem).

#### Day 14

Use of LongStream.concat() to recursively process all possible memory locations.

#### Day 18

Parsing mathematical formulas with differing precedence rules.

#### Day 19

Huge and complex regular expressions - learning about non-capturing groups "(?:)". Still need to see
if there is a way to match number of pattern matches to handle the second recursive rule.

#### Day 20

Not too difficult but very labour intensive once you realise that edges are unique, hence corner
tiles are guaranteed to have exactly two matching edges. All possible orientations can be made by
applying these transformations in order:

1. Rotate 90,
1. Flip Horizontal,
1. Rotate 90,
1. Flip Vertical,
1. Rotate 90,
1. Flip Horizontal,
1. Rotate 90,
1. Flip Vertical.

First build a list of all tiles and map each edge to the corresponding neighbouring tile. Then
select any corner (I use the first corner tile but any would work) and orient it such that it has
neighbours to the right and bottom. Then build each row by orienting tiles column by column.

#### Day 23

Fairly simple with a Circular Linked List, would be nice to find a way to speed it up - 1 million
cups and 10 million moves.

### 2021

#### Day 1

Sliding windows - (t[0] + t[1] + t[2]) < (t[1] + t[2] + t[3]) = t[0] < t[3].

#### Day 6

The literal solution in part 1 doesn't scale to the number of days in part 2 so a different approach
was needed. Simply had to maintain buckets for each lanternfish internal timer, rather than
individuals.

#### Day 9

Interesting challenge. In part 2, recurse from each low point to calculate the basin size.

#### Day 12

Need to revisit and use either the generic Dijkstra or
[A* Pathfinding](https://www.baeldung.com/java-a-star-pathfinding) algorithm implementation. Could
be tricky given the rules on how many times you can visit a cave based on its size.

#### Day 15

Introduction to Dijkstra and A\* shortest path algorithms. Also implemented with A\* - approximately
six times faster.

#### Day 16

Tricky challenge! Nice use of bit masks and recursion.

#### Day 17

Relatively straightforward once you realise that vertical drag isn't modelled, therefore downward
velocity and the vertical overlaps with the target are entirely predictable. First, calculate the
maximum y velocity that would pass through the target area, completely ignoring x. For any
`yVel > 0, y' = -yVel - 1`. We need to maximise yVel without y' moving past yMin. This maximum is
reached at: `yVel = -yMin - 1`.

#### Day 18

Took a long time over this one. Basically used a tree structure and the solution was okay once I
worked out `addToFirstNumberToTheLeft` and `addToFirstNumberToTheRight` which search downwards and then
upwards. Could a [binary tree](https://github.com/eugenp/tutorials/blob/master/data-structures/src/main/java/com/baeldung/tree/BinaryTree.java) provide a more general solution?

#### Day 19

Another difficult one! There are twenty four possible cube face orientations - each of the six faces
can have four rotational positions (0, 90, 180, and 270 degrees). Solved by iteratively finding the
alignment that produces at least 12 common translations from one scanner to all others.

#### Day 20

And again!

#### Day 21

Even harder...

#### Day 22

Algorithms for intersecting cuboids - adding and removing overlapping 3D spaces. Made my brain hurt
calculating what the unique result when two overlapping cuboids are broken down into smaller
individual, non-overlapping cuboids. Also how to remove a partially overlapping cuboid from another.

See `UnboundedReactorCore.addOrRemove(cuboid)`.

#### Day 23

I found this very challenging, implementation based on the
[A* search](https://en.wikipedia.org/wiki/A*_search_algorithm) algorithm. Need to see if it can be
refactored to use the generic implementation.

#### Day 24

Interesting challenge, cannot be solved by simply parsing and executing the instructions due to the
size of the possible numbers (2^14). Had to be solved by analysing the instructions themselves and
discovering that they actually contain 14 repeating blocks, one for each input number. The follwing
input number simply needs to match that expected.

```
                   |   Maximum    |   Minimum    |    Wrong
-------------------+--------------+--------------+-------------
 Z  Div   X  Y inc | Input      Z | Input      Z | Input      Z
-------------------+--------------+--------------+-------------
  1   1  11      6 |   9       15 |   9       15 |   9       15
  2   1  13     14 |   9      413 |   2      406 |   2      406
  3   1  15     14 |   3   10,755 |   1   10,571 |   1   10,571
  4  26  -8     10 |   9      413 |   7      406 |   7      406
  5   1  13      9 |   4   10,751 |   1   10,566 |   1   10,566
  6   1  15     12 |   8  279,546 |   1  274,729 |   1  274,729
  7  26 -11      8 |   9   10,751 |   2   10,566 |   2   10,566
  8  26  -4     13 |   9      413 |   6      406 |   6      406
  9  26 -15     12 |   8       15 |   1       15 |   1       15
 10   1  14      6 |   9      405 |   3      399 |   3      399
 11   1  14      9 |   1   10,540 |   1   10,384 |   1   10,384
 12  26  -1     15 |   9      405 |   9      399 |   8   10,397
 13  26  -8      4 |   7       15 |   1       15 |   1   10,379
 14  26 -14     10 |   1        0 |   1        0 |   1   10,385

 If Div != 1 then prev Z % 26 + X must equal Input.
 E.g. Max row 4, prev z = 10,755; 10,755 % 26 = 17; 17 + -8 = 9 == Input (9)
 E.g. Wrong row 12, prev z = 10,384; 10,384 % 26 = 10; 10 + -1 = 9 != Input (8)
```
