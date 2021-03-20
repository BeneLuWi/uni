set term wxt font "Monospace,12" size 600,400; set xlabel "Präzision [Bits]"; 
set ylabel "Iterationen"; 
set yrange [0:1500] ; 
set key right bottom;
plot \
"clean.dat" using 1:2 with lines lt rgb "#3288BD" lw 3 title "Mit Cleaning,  1 Fehlersymbol",\
"clean.dat" using 1:4 with lines lt rgb "#42f5f5" lw 3 title "Mit Cleaning,  3 Fehlersymbole",\
"clean.dat" using 1:3 with lines lt rgb "#D53E4F" lw 3 title "Ohne Cleaning, 1 Fehlersymbol",\
"clean.dat" using 1:5 with lines lt rgb "#66C2A5" lw 3 title "Ohne Cleaning, 3 Fehlersymbole"
