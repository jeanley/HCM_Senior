select	a.numemp,
		a.numcad,
		a.sitafa,
		upper(a.nomfun),
		a.numcpf,
		b.codusu,
		c.usu_login,
		c.usu_numcpf,
		upper(c.usu_nome),
		d.nomusu,
		d.codusu,
		(select count(codusu) from r034usu where codusu = d.codusu) as total
from	r034fun a
		left join r034usu b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad
		left join usu_tmpusu c on cast(c.usu_nome as varchar) = cast(a.nomfun as varchar)
		left join r999usu d on d.nomusu = c.usu_login
where	a.tipcol = 1
and		a.sitafa not in (7,18)
and		b.codusu is null
and		a.numemp < 5
--and     b.codusu not in (select codusu from r034usu)
order	by a.numemp, a.nomfun


--select * from r034usu where codusu = 12870
