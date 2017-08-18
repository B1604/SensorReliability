set title "Heatmap P3D2"
set size ratio 0.5
set yrange [99:2599]
set xrange [0:23]
set xlabel "Time of day (24hr)"
set ylabel "Delta T (msec)"
set xtics 0,2,23
set ytics 99,100,2599
set cbrange [1:10]
unset cbtics
set tic scale 0
set grid
set palette rgbformulae 22,13,10
set view map 
unset dgrid3d
set pm3d interpolate 10,10
splot 'p3d2_final.dat' using 1:2:3 with pm3d