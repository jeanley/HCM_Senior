select	a.numemp,
		a.nomfun,
		a.datadm,
		b.titcar,
		sum(c.hormes/60) as carga_horaria,
		a.codban,
		a.codage,
		a.conban
from	r034fun a
		inner join r024car b on b.estcar = a.estcar and b.codcar = a.codcar
		inner join r171apf c on c.numemp = a.numemp and c.tipcol = a.tipcol and c.numcad = a.numcad
where	a.tipcol = 1
and		a.datadm > '2025-09-30'
and		a.tipcon = 10
group   by a.numemp, a.nomfun,
		a.datadm,
		b.titcar,
		a.codban,
		a.codage,
		a.conban

UNION
select	a.numemp, a.nomfun,
		a.datadm,
		b.titcar,
		c.hormes as carga_horaria,
		a.codban,
		a.codage,
		a.conban
from	r034fun a
		inner join r024car b on b.estcar = a.estcar and b.codcar = a.codcar
		inner join r006esc c on c.codesc = a.codesc
where	a.tipcol = 1
and		a.datadm > '2025-09-30'
and		a.tipcon <> 10
--group   by a.nomfun,
--		a.datadm,
--		b.titcar,
--		a.codban,
--		a.codage,
--		a.conban
