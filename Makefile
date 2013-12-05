all: build

build: *.java
	javac *.java

run: build
	java Main

docs: Design.tex
	pdflatex Design.tex

submission: build docs
	cp README.md README.txt
	zip tsa.zip *.java README.txt Design.pdf Makefile
	rm README.txt

clean:
	rm *.class
