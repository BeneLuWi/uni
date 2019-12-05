import math

def sim(x,y):
	
	sum = 0
	for i in range(0, 2):
		sum += 1 if x[i] == y[i] else 0
		
	return sum * (1/3)
	
CB = []

#[Weather, Humidity, Wind, Class, Number]
T = [
	["c", "h","s", 0, 1], 
	["c", "n", "w", 0, 2],
	["s", "h", "w", 1, 3],
	["s", "h", "s", 1, 4],
	["c", "h", "w", 0, 5],
	["r", "h", "w" , 0, 6],
	["r", "h", "s", 1, 7],
	["r", "n", "w", 0, 8],
	["r", "n", "s", 1, 9],
	["c", "n", "s", 0, 10],
]

for t in T:
	max = 0
	maxCase = []
	classCounter = [0,0]
	
	#Get the Maximum Similarity
	for c in CB:
		if t[4] != c[4]:			
			if sim(t, c) > max:
				maxCase = c
				max = sim(t,c)
				
	#Count Cases from CB with equal similarity as the max sim
	for c in CB:	
		if t[4] != c[4]:			
			if sim(t, c) == max:
				classCounter[c[3]] += 1	
	print("t: " + str(t))
	print("Case Base: " + str(CB))
	print("Closest: " + str(maxCase))
	print(classCounter)
	
	if ((classCounter[0] > classCounter[1] and t[3] == 1) or
		(classCounter[0] < classCounter[1] and t[3] == 0) or
		(classCounter[0] == classCounter[1])):
		CB.append(t)
	
print(CB)