all: build

build: *.java
	javac -cp $(CLASSPATH) *.java

run: build
	java -cp .:$(CLASSPATH) Main

docs: Design.tex
	pdflatex Design.tex

clean:
	rm *.class
