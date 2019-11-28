import math

pi = 5
ni = 3


def h(p, n):
	if n==0 or p==0 :
		return 0
	return - (p / (p + n)) * math.log2((p / (p + n))) - (n / (p + n)) * math.log2((n / (p + n))) 

def r_i(n, amount, pos, neg):
	return (amount/n) * h(pos, neg)


# attributes = [(#vorkommen dieses wertes, #positive beispiele bei diesem wert, #negative beispiele bei diesem wert )]
print(h(5,3))
#industry
print( h(5,3) - (r_i(8, 3, 3, 0) + r_i(8, 3, 1, 2) + r_i(8, 2, 1, 1)))
#size
print(h(5,3) - (r_i(8, 2, 1, 1) + r_i(8, 2, 1, 1) + r_i(8, 4, 3, 1)))
#per
print(h(5,3) - (r_i(8, 4, 3, 1) + r_i(8, 4, 2, 2)))