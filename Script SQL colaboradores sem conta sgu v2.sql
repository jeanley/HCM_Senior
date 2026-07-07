select	distinct a.numemp,
        a.tipcol,
		a.numcad,
		a.nomfun,
		b.codusu as usuario,
		c.usu_login,
		d.codusu
from	r034fun (nolock) a
		left join r034usu (nolock) b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad
		left join usu_tmpusu c on cast(c.usu_numcpf as varchar) = cast(a.numcpf as varchar)
		left join r999usu d on d.nomusu = c.usu_login
where	1 = 1
and		a.tipcol = 1
and		a.sitafa not in (7,18)
and		b.codusu is null
and		c.usu_login is not null
order   by a.numemp, a.nomfun
