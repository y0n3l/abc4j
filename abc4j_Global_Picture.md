# Introduction #

abc4j is designed to ease the manipulation of abc music notation but you can also use it as a pure way to represent music without carrying about abc notation if you're not interested in ascii based music notation.
The split between components is done in such a way that you can deal with different level of interfaces (roughly: low = the abc parsing itself / high = tune/tunebook OO representation) depending on your needs.

# Details #
Here's a global picture of abc4j :
```
  music as     parsing     Object Oriented                 UIs like Swing score diplay
abc notation ===========> music representation  ========>  Midi playback
                                                           Tunes manipulation
```