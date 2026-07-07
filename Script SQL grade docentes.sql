select	a.numemp,
		a.tipcol,
		b.codfil,
		upper(d.nomfil) as nomfil,
		a.numcad,
		upper(b.nomfun) as nomfun,
		a.datini,
		case
			when a.datfim = '1900-12-31 00:00:00.000' then null
			else a.datfim
		end as datfim,
		a.seqapf,
		a.codapf,
		c.desapf,
		(a.hormes/60) as horasMes,
		a.valapf
from	R171apf a
		inner join r034fun b on b.numemp = a.numemp and b.numcad = a.numcad and b.tipcol = a.tipcol
		inner join r171ati c on c.codapf = a.codapf
		inner join r030fil d on d.numemp = b.numemp and d.codfil = b.codfil
where	b.tipcol = 1
and		b.sitafa not in (7,18)
--and		a.datfim = '1900-12-31 00:00:00.000'
order   by a.numemp, b.codfil, b.nomfun
