# Introduction #
.....


# Details #
**JScoreComponent**

This is the main entry point (from user perspective) for score rendition.
From implementation perspective, this class is more or less a facade that redirects calls to the encapsulated JTune instance that actually implements the score rendition logic.

**ScoreMetrics**

This class encapsulates the font(s) instance(s) used to display the score. Based on properties of the used music font (current one is sonora.ttf) this class is able to provide reference pixel sizes of music components to be used when displaying / scaling a score.
Proceeding like this enables an easy way to scale music components when changing the font size.

**JTune**

This class implements the high level logic to render music scores. Its role is to manage the global layout of the page and organize the way sub components are organized in the score.
To achieve this, the JTune iterates through all the music and non music elements contained in the music part of a tune (getMusic() method on Tune class )

Add your content here.  Format your content with:
  * Text in **bold** or _italic_
  * Headings, paragraphs, and lists
  * Automatic links to other wiki pages