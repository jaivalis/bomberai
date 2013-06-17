default:
	rm -f *.aux *.toc *.log *.out *.pdf
	pdflatex report.tex
	pdflatex report.tex
	bibtex report.aux
	pdflatex report.tex
	pdflatex report.tex

clean:
	rm -f *.aux *.toc *.log *.out

realclean:
	rm -f *.aux *.toc *.log *.out *.pdf

