# Orllewin website and site generator

This repo contains the website for orllewin.uk in Markdown format together with the Kotlin Script parser which converts it to HTML.

## Dependencies

Kotlin and a JVM, and ImageMajick. 

## Features

Any .jpg or .jpeg files are heavily compressed, often to < 10Kb, with a link to the original file. .png images are left alone.

Html can be embedded by using the `embedHtml:` commad followed by a path to a Html snippet.
