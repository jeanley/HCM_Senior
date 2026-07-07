select	a.numemp,
		a.numcad,
		b.codfil,
		b.nomfun,
		a.codeve,
		c.deseve,
		a.refeve,
		a.valeve
from	r046ver a
		inner join r034fun b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad
		inner join r008evc c on c.codtab = a.tabeve and c.codeve = a.codeve
where	a.tipcol = 1
and		a.numemp = 2
and		a.codcal = 21334
order	by a.numcad, a.codeve
