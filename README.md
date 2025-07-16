# Orllewin website and site generator

This repo contains the website for orllewin.uk in Markdown format together with the Kotlin Script parser which converts it to HTML.

## Dependencies

Kotlin and a JVM, and ImageMajick. 

## Features

### Image handling

Any .jpg or .jpeg files are heavily compressed, often to < 10Kb, with a link to the original file. .png images are left alone.


### Embedding html

Html can be embedded by using the `embedHtml:` command followed by a path to a Html snippet. The Html file should end with `html.html`. .html files are ignored in `.gigignore` but there's an exception for files ending in `html.html`, this means handwritten .html files can be saved in the repository alongside Markdown.

Why would you want to do that? See [algorithms/](https://orllewin.uk/algorithms/)
