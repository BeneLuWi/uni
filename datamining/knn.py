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

CB.append(T[0])

for t in T:
	max = 0
	maxCase = []
	print(CB)
	for c in CB:
		
		if t[4] != c[4]:			
			if sim(t, c) > max:
				maxCase = c
				max = sim(t,c)
	
	if (len(maxCase) > 0) and (maxCase[3] != t[3]):
		CB.append(t)
		
		
print(CB)