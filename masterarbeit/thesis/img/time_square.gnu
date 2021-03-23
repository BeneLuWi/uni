set term wxt font "Monospace,12" size 600,400; 
set xlabel "Gradreduktion"; 
set ylabel "Iterationen"; 
set yrange [0:10];
set xtics(0,1,2,3,4); 
plot \
"time_square.dat" using 1:2 with lines lt rgb "#E6F598" lw 3 title "n = 0",\
"time_square.dat" using 1:3 with lines lt rgb "#D53E4F" lw 3 title "n = 1",\
"time_square.dat" using 1:4 with lines lt rgb "#F46D43" lw 3 title "n = 2",\
"time_square.dat" using 1:5 with lines lt rgb "#66C2A5" lw 3 title "n = 3",\
"time_square.dat" using 1:6 with lines lt rgb "#3288BD" lw 3 title "n = 4"
