TSA-Airport-Screening
=====================

Project 2 for SWEN-342

# Documentation
The submission design document is written in LaTeX. LaTeX is a system for writing documents which handles the typesetting and appearance of the document, leaving you, the writer, to focus on the structure and content of your document. It is similar in spirit to the markdown format that this README is written in.

## What You Need to Know
The specifics of how LaTeX works aren't terribly important. All you need to know is that it's a plaintext format which allows you to focus on the content and structure and not the appearance.

The easiest way for you to get started with LaTeX is to look at a very simple sample:

```latex
\documentclass[12pt,letterpaper]{scrartcl}

\usepackage{listings} % Provides commands for generating code listings
\usepackage[margin=1in]{geometry} % Make 1-inch margins

\title{Sample Document}
\author{Joe Schmoe \and Jane Blaine}
\date{November 2013}

\begin{document}

\maketitle % Creates the title page (or title section) and displays it in the document

\section{Hello World}
The section command creates a section.
LaTeX automatically keeps track of the numbers for you so that if you insert new sections you won't have to fix the numbering scheme yourself (or any references to them).

\subsection{More About Sectioning}
Use sections in LaTeX to logically separate the document.
You can also use subsections and even sub-subsections: `\textbackslash\,subsection\{title\}' and `\textbackslash\,subsubsection\{title\}'.
LaTeX automatically handles the numbering scheme for you.

If you need to include a section (or subsection) which should not be numbered in the document, use:
`\textbackslash\,section*\{Un-numbered section\}' (note the asterisk `*').

\section{Other stuff}
\subsection{Lists}
There are two kinds of lists: bulleted and numbered lists.

Create a numbered list with the `enumerate' environment like this:
\begin{enumerate}
    \item The first item.
    \item The second item.
    \item The third item.
\end{enumerate}

Create a bulleted list with the `itemize' environment like this:
\begin{itemize}
    \item First bullet point
    \item Second bullet point
    \item Third bullet point
\end{itemize}

\subsection{Paragraphs}
Paragraphs are created by leaving a blank space between lines.
This is not the beginning of a paragraph.

This is a new paragraph. More than one blank line will be treated as a single blank line by LaTeX.

\subsection{Quotation Marks}
The only annoying thing about LaTeX (other than dealing with complex tables of data), is the way it handles quotation marks.
Rest assured there are good reasons for the way it is, but note that the correct way to make quote marks is to use the backtick character ` for the left quote and the apostrophe character ' for the right quote.
For double quotes just double the characters: ``This is properly quoted with double quotes''.
`This is properly quoted with single quotes.'

\subsection{Special Characters}
You may have noticed some special characters.
The following ten characters have special meaning in LaTeX:
\verb| & % $ # _ \{ } ~ ^ \ |

Outside of `\textbackslash\,verb' (short for \emph{verbatim}) you can type the first seven by just preceding them with a backslash and for the other three you can use the macros \texttt{\textbackslash\,textasciitilde, \textbackslash\,textasciicircum, and \textbackslash\,textbackslash}.

\end{document}
```

Note that the output of the document above is included as `Sample.pdf` in the repository.

Hopefully the explanation above is enough to get you started just typing plain text into a file.
If you need any help, contact Josaphat and he'll help you out (with stuff like including diagrams and the like).
A quick google search for `latex <thing I need help on>` will almost certainly give you back good results, too.

For a more complete introduction to LaTeX, its syntax, and its capabilities, check out http://en.wikibooks.org/wiki/LaTeX/Basics

If you want to install LaTeX yourself on Windows, I recommend the protext distribution of MiKTeX which you can find at http://www.tug.org/protext . All you really need in order to contribute to the documentation is a text editor (like notepad). Syntax highlighting for LaTeX can be really useful as well.
