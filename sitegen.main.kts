#!/usr/bin/env kotlin

//https://github.com/JetBrains/markdown
@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("org.jetbrains:markdown:0.1.45")
@file:DependsOn("org.jsoup:jsoup:1.21.1")

import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import org.jsoup.Jsoup
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess


var header: String? = null
val HEADER = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>__title__</title>
  <style>
    body{
      font-family: Arial, Helvetica, sans-serif;
      padding-bottom: 12px;
    }
    .tinytext { font-size: x-small; }
    details { font-size: small; }
    footer{
      font-size: small;
      position: fixed;
      bottom: 0;
      width: 100%;
      background-color: white;
      padding: 4px;
    }
    ul { padding-left: 16px; }
    li p{ margin: 0px; }
    hr { border: 0.5px solid #ababab; }
    img { max-width: 100%; }
    a:link, a:visited { color: crimson; }
    a:hover { background: crimson; color: white; }
  </style>
</head>
<body>
<main>
""".trimIndent()
val FOOTER = """
    </main>
    <footer>
	© Orllewin 2025. Made in Todmorden, Yorkshire.
	</footer>
  </body>
</html>
""".trimIndent()

/**
 * Simple script to generate a website, uses Pandoc and Imagemajick
 */

var pendingEmbed = false
val flavour = CommonMarkFlavourDescriptor()

when {
    args.isEmpty() || args.size > 1 -> {
        //quit("A single path argument is expected")
        inspectDirectory(File("."))
    }

    else -> {
        inspectDirectory(File(args.first()))
        log("\nfinished\n")
    }
}

var dir: File = File("")

fun inspectDirectory(directory: File) {
    if (directory.isDirectory) {
        dir = directory
        directory.listFiles()?.forEach { file ->
            when {
                file.extension.lowercase() == "md" -> convertMarkdown(directory, file)
            }
        }
        directory.listFiles()?.forEach { file ->
            when {
                file.isDirectory -> inspectDirectory(file)
            }
        }
    } else {
        quit("Argument is not a directory")
    }
}

var tree: ASTNode? = null

fun convertMarkdown(directory: File, mdFile: File) {
    log("Processing: ${mdFile.path}")
    val sb = StringBuilder()
    val markdown = mdFile.readText()
    tree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdown)
    tree?.children?.forEach { node: ASTNode ->
        processMarkdown(directory, markdown, node, sb)
    }

    val html = HEADER.replace("__title__", header ?: "") + sb.toString() + FOOTER
    val pHtml = Jsoup.parse(html).html()
    //println("Output:")
    //println(pHtml)

    val outputFile = File(mdFile.path.replace(".md", ".html"))
    if (outputFile.exists()) outputFile.delete()
    outputFile.createNewFile()
    outputFile.writeText(pHtml)
}

fun processMarkdown(directory: File, markdown: String, node: ASTNode, sb: StringBuilder) {
    log("processMarkdown: ${node.type}")
    when {
        node.type.name.startsWith("ATX_") -> parseHeader(markdown, node, sb)
        node.type.name == "EOL" -> sb.append("\n")
        node.type.name == "BR" -> sb.append("<br>\n")
        node.type.name == ":" -> if(!pendingEmbed) sb.append(":")
        node.type == MarkdownElementTypes.PARAGRAPH -> parseParagraph(directory, markdown, node, sb)
        node.type == MarkdownElementTypes.IMAGE -> parseImage(directory, markdown, node, sb)
        node.type == MarkdownElementTypes.UNORDERED_LIST -> parseUnorderedList(directory, markdown, node, sb)
        node.type == MarkdownElementTypes.INLINE_LINK || node.type.toString() == "Markdown:INLINE_LINK" -> parseInlineLink(
            markdown,
            node,
            sb
        )

        node.type.name == "WHITE_SPACE" -> parseWhitespace(node, sb)
        node.type.name == "TEXT" -> parseText(markdown, node, sb)
        node.type.name == "HTML_TAG" -> parseHtml(markdown, node, sb)
        node.type == MarkdownElementTypes.CODE_FENCE -> parseCodeFence(markdown, node, sb)
        node.type.name == "HORIZONTAL_RULE" -> sb.append("\n<hr>\n")
        node.type.name == "CODE_SPAN" -> parseCodeSpan(markdown, node, sb)
        else -> {
            log("Unhandled node type: ${node.type.name}")
            //sb.append(markdown.substring(node.startOffset, node.endOffset))
        }
    }
}

fun parseCodeSpan(markdown: String, node: ASTNode, sb: StringBuilder) {
    synchronized(this) {
        sb.append("<code>")
        node.children.forEach { codeChild ->
            when (codeChild.type.name) {
                "BACKTICK" -> {
                    //do nothing
                }

                "TEXT" -> sb.append(markdown.substring(codeChild.startOffset, codeChild.endOffset))
                else -> sb.append(markdown.substring(codeChild.startOffset, codeChild.endOffset))
            }
        }

        sb.append("</code>")
    }
}

fun parseCodeFence(markdown: String, node: ASTNode, sb: StringBuilder) {
    synchronized(this) {
        sb.append("<pre><code>")
        node.children.forEach { codeChild ->
            when (codeChild.type.name) {
                "CODE_FENCE_CONTENT" -> sb.append(markdown.substring(codeChild.startOffset, codeChild.endOffset))
                "EOL" -> sb.append("\n")
                "FENCE_LANG" -> {
                    val codeLang = markdown.substring(codeChild.startOffset, codeChild.endOffset)
                    log("Code lang: $codeLang")
                }
            }
        }

        sb.append("</code></pre>")
    }
}

fun parseHtml(markdown: String, node: ASTNode, sb: StringBuilder) {
    synchronized(this) {
        sb.append(markdown.substring(node.startOffset, node.endOffset))
    }
}

fun parseText(markdown: String, node: ASTNode, sb: StringBuilder) {
    node.children.forEach { textChild ->
        log("textChild: ${textChild.type.name}")
    }
    val text = markdown.substring(node.startOffset, node.endOffset)
    if(text.startsWith("embedHtml")){
        pendingEmbed = true
    }else{
        if(pendingEmbed){
            val embedLocation = "${dir.absolutePath}/$text"
            println("EMBED FILE: $embedLocation")
            val embedFile = File(embedLocation)
            sb.append(embedFile.readText())
            pendingEmbed = false
        }else{
            log("Adding text: $text")
            sb.append(text)
        }

    }
}

fun parseWhitespace(node: ASTNode, sb: StringBuilder) {
    synchronized(this) {
        repeat(node.endOffset - node.startOffset) {
            sb.append(" ")
        }
    }
}

fun parseUnorderedList(directory: File, markdown: String, node: ASTNode, sb: StringBuilder) {
    synchronized(this) {
        sb.append("<!-- unordered list -->")
        log("${node.type}")
        sb.append("<ul>")
        node.children.forEach { listItem ->
            if (listItem.type.name == "LIST_ITEM") {
                sb.append("<li>")
                listItem.children.forEach { listItemChild ->
                    processMarkdown(directory, markdown, listItemChild, sb)
                }
                sb.append("</li>")
            } else if (listItem.type.name == "EOL") {
                sb.append("\n")
            }
        }

        sb.append("</ul>")
    }
}

fun parseInlineLink(markdown: String, node: ASTNode, sb: StringBuilder) {
    log("Parsing link: ${markdown.substring(node.startOffset, node.endOffset)}")
    synchronized(this) {
        var text = ""
        var destination = ""

        node.children.forEach { child ->
            log("Iline link child: ${child.type.name}")
            when (child.type.name) {
                "LINK_TEXT" -> {
                    child.children.forEach { linkTextChild ->
                        log("Inline link, text node child: ${linkTextChild.type.name}")
                        if (linkTextChild.type.name == "TEXT") {
                            text += markdown.substring(linkTextChild.startOffset, linkTextChild.endOffset)
                        }else if(linkTextChild.type.name == "WHITE_SPACE"){
                            text += markdown.substring(linkTextChild.startOffset, linkTextChild.endOffset)
                        }else if(linkTextChild.type.name.length == 1){
                            val otherChar =  markdown.substring(linkTextChild.startOffset, linkTextChild.endOffset)
                            if(otherChar != "[" && otherChar  != "]"){
                                text += otherChar
                            }
                        }
                    }
                }

                "LINK_DESTINATION" -> destination =
                    markdown.substring(child.startOffset, child.endOffset).replace(".md", ".html")
            }
        }

        sb.append("<a href=\"$destination\">$text</a>")
    }
}

fun parseImage(directory: File, markdown: String, node: ASTNode, sb: StringBuilder) {
    synchronized(sb) {
        var text: String? = null
        var destination = ""

        node.children.forEach { child ->
            when (child.type) {
                MarkdownElementTypes.INLINE_LINK -> {
                    child.children.forEach { linkChild ->
                        when (linkChild.type.name) {
                            "LINK_TEXT" -> text =
                                markdown.substring(linkChild.startOffset, linkChild.endOffset).drop(1).dropLast(1)

                            "LINK_DESTINATION" -> destination =
                                markdown.substring(linkChild.startOffset, linkChild.endOffset)
                        }
                    }
                }
            }
        }

        val filename = when {
            destination.contains("/") -> destination.substring(destination.lastIndexOf("/") + 1, destination.length)
            else -> destination
        }

        val imageFile = File(destination)

        val target = "${imageFile.path.replace(imageFile.name, "")}${imageFile.nameWithoutExtension}_c.jpg"

        if (destination.lowercase().endsWith(".png")) {
          //No conversion for pngs
          sb.append("<img src=\"$destination\" alt=\"$text\" loading=\"lazy\">\n")
        }else {
          if (destination.lowercase().endsWith(".jpg") || destination.lowercase().endsWith(".jpeg")) {
            log("")
            log("CONVERTING $destination to $target")

            val origPath = "${directory.path}/$destination".replace("/images/images/", "/images/")
            val targetPath = "${directory.path}/$target".replace("/images/images/", "/images/")
            val imageCommand = "magick $origPath -quality 6 -colorspace Gray $targetPath"
            log("imageCommand: $imageCommand")
            shell(imageCommand)
            log("")

            val convertedFile = File(targetPath)
            val convertedPath = Paths.get(convertedFile.path)
            val convertedSize = Files.size(convertedPath)
            val sizeLabel = bytesLabel(convertedSize.toDouble())

            val originalFile = File(origPath)
            val originalPath = Paths.get(originalFile.path)
            val originalSize = Files.size(originalPath)
            val originalSizeLabel = bytesLabel(originalSize.toDouble())

            when {
                text != null -> sb.append("<img src=\"$target\" alt=\"$text\" loading=\"lazy\">\n<br>\n<span class=\"tinytext\">$filename: $text (${sizeLabel})")
                else -> sb.append("<img src=\"$target\" loading=\"lazy\">\n<br>\n<span class=\"tinytext\">$filename (${sizeLabel})")
            }

            sb.append(" - <a href=\"$destination\">view original $originalSizeLabel</a></span>")
          }else{
          }
        }
    }
}

fun parseParagraph(directory: File, markdown: String, node: ASTNode, sb: StringBuilder) {
    sb.append("<p>")
    node.children.forEach { child ->
        log("THROWING: ${child.type}")
        processMarkdown(directory, markdown, child, sb)
    }
    sb.append("</p>")
}

fun parseHeader(markdown: String, node: ASTNode, sb: StringBuilder) {
    synchronized(this) {
        val tag = node.type.name.replace("ATX_", "h")
        sb.append("\t<$tag>")
        node.children.forEach { child ->
            if (child.type.name == "ATX_CONTENT") {
                val text = markdown.substring(child.startOffset, child.endOffset).trim()
                sb.append(text)
                if (header == null) header = text
            }
        }
        sb.append("</$tag>")
    }
}

fun shell(command: String) {
    val process = ProcessBuilder("/bin/bash", "-c", command).inheritIO().start()
    process.waitFor()
}

fun bytesLabel(bytes: Double) = when {
    bytes >= 1 shl 20 -> "%.1fMB".format(bytes / (1 shl 20))
    bytes >= 1 shl 10 -> "%.0fkB".format(bytes / (1 shl 10))
    else -> "$bytes bytes"
}


fun log(message: String) {
    println(message)
}

fun quit(message: String) {
    println("\n$message\n")
    exitProcess(0)
}