# Face Tracker
face tracking using openCV

Napotki za zagon
----------------
Aplikacija uporablja knjižnici OpenJS 3.0 in JFreeChart 1.0.19.
Na začetku datoteke Main.java je potrebno spremeniti poti do OpenJS 
komponent. Za zagon kamere podamo kot vhodni parameter število 0, 
za predvajanje datoteke pa pot do nje.

Opis funkcionalnosti
--------------------
Aplikacija v realnem času prikazuje statistike videa v obliki grafov,
in sicer: pozicijo, vektorje premikov, velikosti ter število obrazov.

Opis delovanja
--------------
main() metoda razreda Main po inicializaciji vseh potrebnih komponent 
začne z izvajanjem mainLoop() metode. Ta metoda v vsakem ciklu prvo
zajame sliko ter jo pošlje klasifikatorju, ki ji vrne nazaj množico
pravokotnikov. Te pravokotnike nato pošlje razredu FaceLogger, ki
hrani pozicije obrazov. Vsak pravokotnik se ali dodeli že obstoječemu
objektu Face, ali pa se po potrebi ustvari novi (če je pravokotnik
preveč oddaljen on že obstoječih obrazov). Nato se kliče metoda
updateCharts(), ki pobere sveže podatke iz faceLoggerja, jih
povpreči, ter pošlje grafom. Nato še pošlje na začetku pridobljeno 
sliko facelogerju da ji le-ta doriše pravokotnike, ter pošlje to sliko
na izhod.   
