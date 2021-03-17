reset;set logscale y; 
set term wxt font "Monospace,12" size 600,400; 
set xlabel "Iterationsschritt"; 
set ylabel "Abstand und Breite des Intervalls x_n";
set xrange[0:2000]

plot \
"2^-100"  using 1:2 with lines lt rgb "#3288BD" title "ε = 2^{-100}",\
"tm100"  using 1:2 with lines lt rgb "black" notitle "Radius x_0 = 2^{-100}",\
"2^-500" using 1:2 with lines lt rgb "#D53E4F" title  "ε = 2^{-500}",\
"tm500" using 1:2 with lines lt rgb "black" notitle "Radius x_0 = = 2^{-500}",\
"2^-1000" using 1:2 with lines lt rgb "#42f5f5" title "ε = 2^{-1000}",\
"tm1000" using 1:2 with lines lt rgb "black" notitle "Radius x_0 = = 2^{-1000}",\
"2^-1500" using 1:2 with lines lt rgb "#66C2A5" title "ε = 2^{-1500}",\
"tm1500" using 1:2 with lines lt rgb "black" notitle "Radius x_0 = = 2^{-1500}",\
"2^-2000" using 1:2 with lines lt rgb "#E6F598" title "ε = 2^{-2000}",\
"tm2000" using 1:2 with lines lt rgb "black" notitle "Radius x_0 = = 2^{-2000}"


